package com.edgn.wynncraft.encoders;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemEncoderWrapper {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final Pattern ITEM_ENCODED_PATTERN = Pattern.compile("\\[([A-Za-z0-9+/=]+)]");

    private static final byte START_BLOCK = 0;
    private static final byte END_BLOCK = (byte) 255;
    private static final byte TYPE_BLOCK = 1;
    private static final byte NAME_BLOCK = 2;
    private static final byte IDENTIFICATIONS_BLOCK = 3;
    private static final byte POWDER_BLOCK = 4;
    private static final byte REROLLS_BLOCK = 5;
    private static final byte SHINY_BLOCK = 6;

    private static final byte ITEM_TYPE = 0;

    private static final byte FORMAT_VERSION = 1;

    public static String encodeAsItem(String message) {
        try {

            String encodedMessage = MessageEncoder.encode(message);
            if (encodedMessage == null) {
                return null;
            }


            ByteBuffer buffer = ByteBuffer.allocate(1024);


            buffer.put(START_BLOCK);
            buffer.put(FORMAT_VERSION);

            buffer.put(TYPE_BLOCK);
            buffer.put(ITEM_TYPE);

            buffer.put(NAME_BLOCK);

            String fakeName = generateFakeItemName();
            buffer.put(fakeName.getBytes(StandardCharsets.UTF_8));
            buffer.put((byte) 0);

            buffer.put(IDENTIFICATIONS_BLOCK);


            byte[] messageBytes = encodedMessage.getBytes(StandardCharsets.UTF_8);



            int idCount = (int) Math.ceil(messageBytes.length / 2.0);
            buffer.put((byte) idCount);
            buffer.put((byte) 0);


            for (int i = 0; i < idCount; i++) {

                buffer.put((byte) (1 + (i % 127)));


                if (i * 2 < messageBytes.length) {
                    buffer.put(messageBytes[i * 2]);
                } else {
                    buffer.put((byte) 0);
                }


                if (i * 2 + 1 < messageBytes.length) {
                    buffer.put(messageBytes[i * 2 + 1]);
                }
            }

            buffer.put(POWDER_BLOCK);
            buffer.put((byte) 4);
            buffer.put((byte) 1);
            buffer.put((byte) 0);


            buffer.put(REROLLS_BLOCK);
            buffer.put((byte) 3);


            buffer.put(END_BLOCK);


            byte[] finalData = new byte[buffer.position()];
            buffer.flip();
            buffer.get(finalData);


            return "[" + Base64.getEncoder().encodeToString(finalData) + "]";

        } catch (Exception e) {
            return null;
        }
    }

    public static String decodeFromItem(String encodedItemMessage) {
        try {
            if (encodedItemMessage == null || encodedItemMessage.isEmpty()) {
                return null;
            }


            Matcher matcher = ITEM_ENCODED_PATTERN.matcher(encodedItemMessage);
            if (!matcher.find()) {
                return null;
            }


            String base64Content = matcher.group(1);


            byte[] itemData = Base64.getDecoder().decode(base64Content);


            ByteBuffer buffer = ByteBuffer.wrap(itemData);


            byte[] extractedMessageBytes = null;


            while (buffer.hasRemaining()) {
                byte blockId = buffer.get();

                if (blockId == IDENTIFICATIONS_BLOCK) {

                    int idCount = buffer.get() & 0xFF;
                    byte idTypeFlag = buffer.get();


                    ByteBuffer messageBuffer = ByteBuffer.allocate(idCount * 2);


                    for (int i = 0; i < idCount; i++) {

                        byte value = buffer.get();
                        messageBuffer.put(value);


                        if (idTypeFlag == 0 && buffer.hasRemaining()) {
                            value = buffer.get();
                            messageBuffer.put(value);
                        }
                    }


                    extractedMessageBytes = new byte[messageBuffer.position()];
                    messageBuffer.flip();
                    messageBuffer.get(extractedMessageBytes);

                } else if (blockId == END_BLOCK) {

                    break;
                } else {

                    skipBlock(buffer, blockId);
                }
            }


            if (extractedMessageBytes != null) {

                String encodedMessage = new String(extractedMessageBytes, StandardCharsets.UTF_8);


                return MessageEncoder.decode(encodedMessage);
            }

            return null;

        } catch (Exception e) {
            return null;
        }
    }

    private static void skipBlock(ByteBuffer buffer, byte blockId) {
        switch (blockId) {
            case START_BLOCK, TYPE_BLOCK, REROLLS_BLOCK:

                if (buffer.hasRemaining()) buffer.get();
                break;

            case NAME_BLOCK:

                while (buffer.hasRemaining()) {
                    if (buffer.get() == 0) break;
                }
                break;

            case POWDER_BLOCK:

                if (buffer.hasRemaining()) {
                    byte numBytes = buffer.get();
                    buffer.position(buffer.position() + numBytes);
                }

                break;

            case SHINY_BLOCK:

                if (buffer.hasRemaining()) {
                    buffer.get();

                    while (buffer.hasRemaining()) {
                        byte b = buffer.get();
                        if ((b & 0x80) == 0) break;
                    }
                }
                break;

            default:



                buffer.position(buffer.limit());
                break;
        }
    }

    private static String generateFakeItemName() {
        String[] prefixes = {"Ancient", "Mythic", "Legendary", "Divine", "Corrupted", "Enchanted", "Masterwork"};
        String[] items = {"Sword", "Bow", "Dagger", "Wand", "Helmet", "Chestplate", "Boots", "Ring", "Amulet"};
        String[] suffixes = {"of Power", "of Wisdom", "of the Titan", "of the Phoenix", "of Dominance", "of Eternity"};

        String prefix = prefixes[SECURE_RANDOM.nextInt(prefixes.length)];
        String item = items[SECURE_RANDOM.nextInt(items.length)];
        String suffix = suffixes[SECURE_RANDOM.nextInt(suffixes.length)];

        return prefix + " " + item + " " + suffix;
    }

    public static boolean looksLikeEncodedItem(String message) {
        if (message == null || message.isEmpty()) {
            return false;
        }

        return ITEM_ENCODED_PATTERN.matcher(message).find();
    }
}