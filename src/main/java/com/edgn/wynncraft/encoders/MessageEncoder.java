package com.edgn.wynncraft.encoders;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MessageEncoder {

    private static final EncoderConfig CONFIG = new EncoderConfig();

    private static final AtomicLong MESSAGE_COUNTER = new AtomicLong(0);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private static final int NONCE_SIZE = 12;

    private static final Pattern BASE64_PATTERN = Pattern.compile("[A-Za-z0-9+/=]{20,}");
    
    private static final List<String> RECENT_MESSAGES = new ArrayList<>();
    private static final int MAX_MESSAGES_HISTORY = 15;


    public static String encode(String message) {
        try {
            if (message == null || message.isEmpty()) {
                return null;
            }

            
            byte[] nonce = new byte[NONCE_SIZE];
            SECURE_RANDOM.nextBytes(nonce);

            
            long counter = MESSAGE_COUNTER.getAndIncrement();

            
            byte[] masterKey = generateMasterKey(nonce, counter);

            
            byte[] encryptionKey = generateEncryptionKey(masterKey);

            
            byte[] iv = generateIV(masterKey);

            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptionKey, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            
            byte[] encrypted = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

            
            byte[] hmac = calculateHmac(masterKey, encrypted);

            
            ByteBuffer buffer = ByteBuffer.allocate(NONCE_SIZE + 8 + 32 + encrypted.length);
            buffer.put(nonce);
            buffer.putLong(counter);
            buffer.put(hmac);
            buffer.put(encrypted);

            
            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatForMinecraftChat(String encodedMessage, int maxLineLength) {
        if (encodedMessage == null || encodedMessage.isEmpty()) {
            return "";
        }

        
        if (encodedMessage.length() <= maxLineLength) {
            return encodedMessage;
        }

        List<String> lines = new ArrayList<>();

        
        for (int i = 0; i < encodedMessage.length(); i += maxLineLength) {
            int end = Math.min(i + maxLineLength, encodedMessage.length());
            lines.add(encodedMessage.substring(i, end));
        }

        
        return String.join("\n", lines);
    }

    public static String decode(String encodedMessage) {
        if (encodedMessage == null || encodedMessage.isEmpty()) {
            return null;
        }

        try {
            
            addToRecentMessages(encodedMessage);

            
            String result = attemptDecode(encodedMessage);
            if (result != null) {
                return result;
            }

            
            String cleanedMessage = gentlyCleanMessage(encodedMessage);
            if (!cleanedMessage.equals(encodedMessage)) {
                result = attemptDecode(cleanedMessage);
                if (result != null) {
                    return result;
                }
            }

            
            cleanedMessage = aggressivelyCleanMessage(encodedMessage);
            if (!cleanedMessage.equals(encodedMessage) && !cleanedMessage.equals(gentlyCleanMessage(encodedMessage))) {
                result = attemptDecode(cleanedMessage);
                if (result != null) {
                    return result;
                }
            }

            
            String multilineMessage = handleMultilineMessage(encodedMessage);
            if (multilineMessage != null && !multilineMessage.equals(encodedMessage)) {
                result = attemptDecode(multilineMessage);
                if (result != null) {
                    return result;
                }
            }

            
            String reconstructedMessage = reconstructFromRecentMessages(encodedMessage);
            if (reconstructedMessage != null && !reconstructedMessage.equals(encodedMessage)) {
                result = attemptDecode(reconstructedMessage);
                return result;
            }

            
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static String handleMultilineMessage(String message) {
        if (!message.contains("\n")) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        String[] lines = message.split("\n");

        
        for (String line : lines) {
            
            String cleaned = aggressivelyCleanMessage(line.trim());
            if (!cleaned.isEmpty()) {
                result.append(cleaned);
            }
        }

        
        if (result.isEmpty() || result.toString().equals(message)) {
            return null;
        }

        return result.toString();
    }

    private static String attemptDecode(String message) {
        if (message == null || message.isEmpty()) {
            return null;
        }

        try {
            byte[] data = Base64.getDecoder().decode(message);

            
            if (data.length < NONCE_SIZE + 8 + 32) {
                return null;
            }

            ByteBuffer buffer = ByteBuffer.wrap(data);

            
            byte[] nonce = new byte[NONCE_SIZE];
            buffer.get(nonce);

            
            long counter = buffer.getLong();

            
            byte[] hmac = new byte[32]; 
            buffer.get(hmac);

            
            byte[] encrypted = new byte[buffer.remaining()];
            buffer.get(encrypted);

            
            byte[] masterKey = generateMasterKey(nonce, counter);

            
            byte[] calculatedHmac = calculateHmac(masterKey, encrypted);
            if (!MessageDigest.isEqual(hmac, calculatedHmac)) {
                
                return null;
            }

            
            byte[] encryptionKey = generateEncryptionKey(masterKey);

            
            byte[] iv = generateIV(masterKey);

            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptionKey, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            
            byte[] decrypted = cipher.doFinal(encrypted);

            
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            
            return null;
        }
    }

    private static String gentlyCleanMessage(String message) {
        if (message == null) return null;

        
        String clean = message.replaceAll("§[0-9a-fk-or]", "");

        
        clean = clean.replaceAll("&[0-9a-fk-or]", "");

        
        for (String symbol : CONFIG.getSymbolsToIgnore()) {
            clean = clean.replace(symbol, "");
        }

        
        clean = clean.replaceAll("\\s{2,}", " ");

        return clean;
    }

    private static String aggressivelyCleanMessage(String message) {
        if (message == null) return null;

        
        return message.replaceAll("[^A-Za-z0-9+/=]", "");
    }

    private static synchronized void addToRecentMessages(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        
        if (!RECENT_MESSAGES.contains(message)) {
            RECENT_MESSAGES.add(message);

            
            if (RECENT_MESSAGES.size() > MAX_MESSAGES_HISTORY) {
                RECENT_MESSAGES.removeFirst();
            }
        }
    }

    private static String reconstructFromRecentMessages(String currentMessage) {
        if (currentMessage == null || currentMessage.isEmpty()) {
            return null;
        }

        
        List<String> currentFragments = findBase64Fragments(currentMessage);
        if (currentFragments.isEmpty()) {
            return null;
        }

        
        if (currentMessage.contains("\n")) {
            StringBuilder allFragments = new StringBuilder();

            for (String line : currentMessage.split("\n")) {
                String cleaned = aggressivelyCleanMessage(line.trim());
                if (!cleaned.isEmpty()) {
                    allFragments.append(cleaned);
                }
            }

            String combinedMessage = allFragments.toString();
            if (!combinedMessage.isEmpty() && !combinedMessage.equals(currentMessage)) {
                
                if (couldBeBase64(combinedMessage)) {
                    return combinedMessage;
                }
            }
        }

        
        for (String recentMessage : RECENT_MESSAGES) {
            
            if (recentMessage.equals(currentMessage)) {
                continue;
            }

            List<String> recentFragments = findBase64Fragments(recentMessage);

            
            for (String currentFrag : currentFragments) {
                for (String recentFrag : recentFragments) {
                    

                    
                    if (recentFrag.contains(currentFrag) && recentFrag.length() > currentFrag.length()) {
                        return recentFrag;
                    }

                    
                    if (currentFrag.contains(recentFrag) && currentFrag.length() > recentFrag.length()) {
                        return currentFrag;
                    }

                    
                    String merged = tryMergeOverlappingFragments(currentFrag, recentFrag);
                    if (merged != null && !merged.equals(currentFrag) && !merged.equals(recentFrag)) {
                        return merged;
                    }
                }
            }
        }

        
        return null;
    }

    private static List<String> findBase64Fragments(String message) {
        if (message == null || message.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> fragments = new ArrayList<>();

        
        Matcher matcher = BASE64_PATTERN.matcher(message);
        while (matcher.find()) {
            fragments.add(matcher.group());
        }

        
        if (fragments.isEmpty()) {
            String cleaned = gentlyCleanMessage(message);
            if (!cleaned.equals(message)) {
                matcher = BASE64_PATTERN.matcher(cleaned);
                while (matcher.find()) {
                    fragments.add(matcher.group());
                }
            }
        }

        
        if (fragments.isEmpty()) {
            String cleaned = aggressivelyCleanMessage(message);
            if (!cleaned.equals(message) && !cleaned.isEmpty()) {
                matcher = BASE64_PATTERN.matcher(cleaned);
                while (matcher.find()) {
                    fragments.add(matcher.group());
                }
            }
        }

        return fragments;
    }

    private static String tryMergeOverlappingFragments(String a, String b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty()) {
            return null;
        }

        
        int minOverlap = Math.min(20, Math.min(a.length(), b.length()));

        
        for (int i = minOverlap; i < a.length(); i++) {
            String endA = a.substring(a.length() - i);
            String startB = b.substring(0, Math.min(i, b.length()));
            if (endA.equals(startB)) {
                return a + b.substring(i);
            }
        }

        
        for (int i = minOverlap; i < b.length(); i++) {
            String endB = b.substring(b.length() - i);
            String startA = a.substring(0, Math.min(i, a.length()));
            if (endB.equals(startA)) {
                return b + a.substring(i);
            }
        }

        
        double similarity = calculateSimilarity(a, b);
        if (similarity > 0.8) { 
            return a.length() > b.length() ? a : b;
        }

        return null;
    }

    private static double calculateSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return 0.0;
        }

        
        if (Math.abs(s1.length() - s2.length()) > s1.length() * 0.3) {
            return 0.0;
        }

        
        int commonChars = 0;
        for (int i = 0; i < Math.min(s1.length(), s2.length()); i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                commonChars++;
            }
        }

        return (double) commonChars / Math.max(s1.length(), s2.length());
    }

    public static String cleanMessage(String message) {
        return gentlyCleanMessage(message);
    }

    public static String extractBase64Fragment(String message) {
        if (message == null) return null;

        
        String cleaned = gentlyCleanMessage(message);

        
        Matcher matcher = BASE64_PATTERN.matcher(cleaned);
        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    private static boolean couldBeBase64(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        try {
            Base64.getDecoder().decode(input);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] generateMasterKey(byte[] nonce, long counter) throws Exception {
        
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(CONFIG.getRootSeed().getBytes(StandardCharsets.UTF_8));
        digest.update(nonce);
        digest.update(longToBytes(counter));

        return digest.digest();
    }

    private static byte[] generateEncryptionKey(byte[] masterKey) throws Exception {
        
        Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(masterKey, HMAC_ALGORITHM);
        hmac.init(keySpec);

        
        hmac.update("ENCRYPTION_KEY".getBytes(StandardCharsets.UTF_8));

        
        byte[] encryptionKey = new byte[16];
        System.arraycopy(hmac.doFinal(), 0, encryptionKey, 0, 16);

        return encryptionKey;
    }

    private static byte[] generateIV(byte[] masterKey) throws Exception {
        
        Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(masterKey, HMAC_ALGORITHM);
        hmac.init(keySpec);
        
        hmac.update("IV_GENERATOR".getBytes(StandardCharsets.UTF_8));

        byte[] iv = new byte[16];
        System.arraycopy(hmac.doFinal(), 0, iv, 0, 16);

        return iv;
    }

    private static byte[] calculateHmac(byte[] masterKey, byte[] data) throws Exception {
        Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(masterKey, HMAC_ALGORITHM);
        hmac.init(keySpec);
        hmac.update(data);
        return hmac.doFinal();
    }

    private static byte[] longToBytes(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(value & 0xFFL);
            value >>= 8;
        }
        return result;
    }
}