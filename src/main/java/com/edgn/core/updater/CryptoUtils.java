package com.edgn.core.updater;

import com.edgn.Main;
import net.minecraft.client.MinecraftClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

public final class CryptoUtils {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HMAC_SALT = "WE-2024-ULTRA-SECURE-SALT-v1";

    public static String calculateSecureFileHash(Path filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] fileBytes = Files.readAllBytes(filePath);
            byte[] hashBytes = digest.digest(fileBytes);
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            Main.LOGGER.error("Failed to calculate file hash for {}: {}", filePath, e.getMessage());
            return "";
        }
    }

    public static String calculateHashFromBytes(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(data);
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            Main.LOGGER.error("Failed to calculate hash from bytes: {}", e.getMessage());
            return "";
        }
    }

    public static boolean verifySignature(String data, String signatureBase64, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
            boolean isValid = signature.verify(signatureBytes);
            Main.LOGGER.debug("Signature verification result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            Main.LOGGER.error("Signature verification failed: {}", e.getMessage());
            return false;
        }
    }

    public static PublicKey parsePublicKeyFromPem(String pemKey) {
        try {
            String publicKeyContent = pemKey
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyContent);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey key = keyFactory.generatePublic(spec);
            Main.LOGGER.debug("Successfully parsed RSA public key");
            return key;
        } catch (Exception e) {
            Main.LOGGER.error("Error parsing public key: {}", e.getMessage());
            return null;
        }
    }

    public static String signRequest(String requestData) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(HMAC_SALT.getBytes(java.nio.charset.StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(secretKey);
            byte[] hmacBytes = mac.doFinal(requestData.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            String hmacHex = bytesToHex(hmacBytes);
            Main.LOGGER.debug("Request signed with HMAC");
            return hmacHex;
        } catch (Exception e) {
            Main.LOGGER.error("Failed to sign request: {}", e.getMessage());
            return "";
        }
    }

    public static boolean verifyHmacSignature(String data, String receivedSignature) {
        try {
            String expectedSignature = signRequest(data);
            boolean isValid = MessageDigest.isEqual(
                    expectedSignature.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    receivedSignature.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );
            Main.LOGGER.debug("HMAC verification result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            Main.LOGGER.error("HMAC verification failed: {}", e.getMessage());
            return false;
        }
    }

    public static String generateClientId() {
        try {
            Path clientIdFile = Path.of(".we_client_id");
            if (Files.exists(clientIdFile)) {
                String existingId = Files.readString(clientIdFile).trim();
                if (!existingId.isEmpty() && isValidUUID(existingId)) {
                    return existingId;
                }
            }
            String newId = UUID.randomUUID().toString();
            Files.writeString(clientIdFile, newId);
            Main.LOGGER.info("Generated new client ID");
            return newId;
        } catch (Exception e) {
            Main.LOGGER.error("Failed to generate/read client ID: {}", e.getMessage());
            return UUID.randomUUID().toString();
        }
    }

    public static Path saveTemporaryFile(String moduleId, byte[] data) {
        try {
            Path tempDir = getUpdatesDir(); // << config/wynncraft-explorer/updates
            Files.createDirectories(tempDir);

            String fileName = moduleId + "_" + System.currentTimeMillis() + "_update.jar";
            Path tempFile = tempDir.resolve(fileName);

            Files.write(tempFile, data);

            byte[] readBack = Files.readAllBytes(tempFile);
            if (!MessageDigest.isEqual(data, readBack)) {
                throw new IOException("File integrity check failed after write");
            }

            Main.LOGGER.info("Saved temporary update file: {}", tempFile);
            return tempFile;
        } catch (Exception e) {
            Main.LOGGER.error("Failed to save temporary file for {}: {}", moduleId, e.getMessage());
            return null;
        }
    }

    public static void cleanupOldTempFiles() {
        try {
            Path tempDir = getUpdatesDir(); // << même dossier
            if (!Files.exists(tempDir)) return;

            long cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000);

            Files.list(tempDir)
                    .filter(path -> {
                        try {
                            return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                            Main.LOGGER.debug("Cleaned up old temp file: {}", path);
                        } catch (IOException e) {
                            Main.LOGGER.warn("Failed to delete old temp file: {}", path);
                        }
                    });
        } catch (Exception e) {
            Main.LOGGER.warn("Error during temp file cleanup: {}", e.getMessage());
        }
    }

    private static Path getUpdatesDir() {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            File base = (mc != null && mc.runDirectory != null) ? mc.runDirectory : new File(".");
            return base.toPath().resolve("config").resolve("wynncraft-explorer").resolve("updates");
        } catch (Throwable t) {
            return Path.of("config", "wynncraft-explorer", "updates");
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) result.append(String.format("%02x", b));
        return result.toString();
    }

    private static boolean isValidUUID(String uuid) {
        try { UUID.fromString(uuid); return true; } catch (IllegalArgumentException e) { return false; }
    }

    public static Path getCurrentJarPath() {
        try {
            return Path.of(CryptoUtils.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
        } catch (Exception e) {
            Main.LOGGER.error("Failed to get current JAR path: {}", e.getMessage());
            return Path.of("mods", "wynncraft-explorer.jar");
        }
    }

    public static boolean isDevelopmentMode() {
        String classPath = System.getProperty("java.class.path");
        return classPath.contains("build/classes") || classPath.contains("out/production");
    }
}
