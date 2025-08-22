package com.edgn.wynncraft.encoders;

import com.edgn.Main;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class WynntilsItemEncoder {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final int PRIVATE_USE_AREA_A_START = 0xF0000;
    private static final int PRIVATE_USE_AREA_B_START = 0x100000;

    private static final byte ITEM_START_BLOCK = 0;
    private static final byte ITEM_END_BLOCK = (byte) 255;
    private static final byte ITEM_TYPE_BLOCK = 1;
    private static final byte ITEM_NAME_BLOCK = 2;

    private static final byte ITEM_TYPE = 0;


    private static final byte FORMAT_VERSION = 1;

    public static String encodeAsWynntilsItem(String message) {
        try {
            if (message == null || message.isEmpty()) {
                return null;
            }

            message = removeNonUnicodeCharacters(message);
            if (message.isEmpty()) {
                return null;
            }

            Main.OVERLAY_MANAGER.getLoggerOverlay().info(message, false);

            String encodedMessage = MessageEncoder.encode(message);
            if (encodedMessage == null) {
                return null;
            }

            byte[] messageBytes = encodedMessage.getBytes(StandardCharsets.UTF_8);

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            buffer.put(ITEM_START_BLOCK);
            buffer.put(FORMAT_VERSION);

            buffer.put(ITEM_TYPE_BLOCK);
            buffer.put(ITEM_TYPE);

            buffer.put(ITEM_NAME_BLOCK);

            String itemName = generateWynntilsItemName();
            buffer.put(itemName.getBytes(StandardCharsets.UTF_8));
            buffer.put((byte) 0);

            for (byte b : messageBytes) {
                buffer.put(b);
            }

            buffer.put(ITEM_END_BLOCK);

            byte[] itemData = new byte[buffer.position()];
            buffer.flip();
            buffer.get(itemData);

            return bytesToWynntilsUtf16(itemData);

        } catch (Exception e) {
            return null;
        }
    }

    private static String removeNonUnicodeCharacters(String input) {
        StringBuilder result = new StringBuilder(input.length());

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (!Character.isSurrogate(c) ||
                    (Character.isHighSurrogate(c) && i+1 < input.length() && Character.isLowSurrogate(input.charAt(i+1)))) {
                result.append(c);

                if (Character.isHighSurrogate(c) && i+1 < input.length() && Character.isLowSurrogate(input.charAt(i+1))) {
                    result.append(input.charAt(++i));
                }
            }
        }

        return result.toString();
    }

    public static String decodeFromWynntilsItem(String encodedItem) {
        try {
            if (encodedItem == null || encodedItem.isEmpty()) {
                return null;
            }


            byte[] itemData = wynntilsUtf16ToBytes(encodedItem);
            if (itemData.length < 5) {
                return null;
            }


            if (itemData[0] != ITEM_START_BLOCK || itemData[itemData.length - 1] != ITEM_END_BLOCK) {
                return null;
            }



            byte[] encodedMessageBytes = extractMessageBytesFromItemData(itemData);
            if (encodedMessageBytes == null || encodedMessageBytes.length == 0) {
                return null;
            }


            String encodedMessage = new String(encodedMessageBytes, StandardCharsets.UTF_8);


            return MessageEncoder.decode(encodedMessage);

        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] extractMessageBytesFromItemData(byte[] itemData) {

        int position = 2;


        while (position < itemData.length) {
            byte blockId = itemData[position++];

            if (blockId == ITEM_NAME_BLOCK) {

                while (position < itemData.length && itemData[position] != 0) {
                    position++;
                }
                position++;
                break;
            } else if (blockId == ITEM_END_BLOCK) {

                return null;
            }


            if (blockId == ITEM_TYPE_BLOCK) {
                position++;
            }
        }


        int endPosition = itemData.length - 1;


        if (position >= endPosition) {
            return null;
        }


        byte[] messageBytes = new byte[endPosition - position];
        System.arraycopy(itemData, position, messageBytes, 0, messageBytes.length);

        return messageBytes;
    }

    private static String bytesToWynntilsUtf16(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length / 2 + (bytes.length % 2 > 0 ? 1 : 0));

        for (int i = 0; i < bytes.length; i += 2) {
            int value = 0;
            value |= ((bytes[i] & 0xFF) << 8);
            if (i + 1 < bytes.length) value |= (bytes[i + 1] & 0xFF);

            builder.appendCodePoint(PRIVATE_USE_AREA_A_START + value);
        }

        return builder.toString();
    }


    private static byte[] wynntilsUtf16ToBytes(String string) {
        List<Byte> byteList = new ArrayList<>(string.length() * 2);

        int[] codePoints = string.codePoints().toArray();

        for (int codePoint : codePoints) {
            int value = codePoint - PRIVATE_USE_AREA_A_START;

            byteList.add((byte) ((value >> 8) & 0xFF));

            byteList.add((byte) (value & 0xFF));
        }

        while (!byteList.isEmpty() && byteList.getLast() == 0) {
            byteList.removeLast();
        }

        byte[] result = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            result[i] = byteList.get(i);
        }

        return result;
    }


    private static String generateWynntilsItemName() {
        String[] prefixes = {"Mythic", "Fabled"};
        String[] items = {"Helmet", "Chestplate", "Leggings", "Boots", "Spear", "Dagger", "Wand", "Bow", "Relik"};
        String[] suffixes = {"Epoch", "Divzer"};

        String prefix = prefixes[SECURE_RANDOM.nextInt(prefixes.length)];
        String item = items[SECURE_RANDOM.nextInt(items.length)];
        String suffix = suffixes[SECURE_RANDOM.nextInt(suffixes.length)];

        return prefix + " " + item + " " + suffix;
    }

    public static boolean looksLikeWynntilsItem(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        return text.codePoints().anyMatch(cp -> cp >= PRIVATE_USE_AREA_A_START && cp <= PRIVATE_USE_AREA_A_START + 0xFFFFFF);
    }
}