package com.edgn.wynncraft.encoders;

import com.edgn.Main;

public class EncodedMessageProcessor {
    
    private static final ItemEncoderConfig CONFIG = ItemEncoderConfig.getInstance();

    public static String encodeMessage(String message) {
        if (message == null || message.isEmpty()) {
            return null;
        }

        try {
            return switch (CONFIG.getEncodingType()) {
                case WYNNTILS -> WynntilsItemEncoder.encodeAsWynntilsItem(message);
                case ARTEMIS -> ItemEncoderWrapper.encodeAsItem(message);
                default -> MessageEncoder.encode(message);
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static String decodeMessage(String encodedMessage) {
        if (encodedMessage == null || encodedMessage.isEmpty()) {
            return null;
        }

        try {
            
            if (WynntilsItemEncoder.looksLikeWynntilsItem(encodedMessage)) {
                return WynntilsItemEncoder.decodeFromWynntilsItem(encodedMessage);
            } else if (ItemEncoderWrapper.looksLikeEncodedItem(encodedMessage)) {
                return ItemEncoderWrapper.decodeFromItem(encodedMessage);
            } else {
                
                return MessageEncoder.decode(encodedMessage);
            }
        } catch (Exception e) {
            Main.OVERLAY_MANAGER.getLoggerOverlay().error("Failed to decode message: " + e.getMessage(), false);
            return null;
        }
    }

    public static boolean canDecode(String message) {
        if (message == null || message.isEmpty()) {
            return false;
        }

        
        if (WynntilsItemEncoder.looksLikeWynntilsItem(message)) {
            return true;
        }

        if (ItemEncoderWrapper.looksLikeEncodedItem(message)) {
            return true;
        }

        
        String base64Fragment = MessageEncoder.extractBase64Fragment(message);
        return base64Fragment != null && !base64Fragment.isEmpty();
    }
    
    public static String formatForMinecraftChat(String encodedMessage, int maxLineLength) {
        
        if (WynntilsItemEncoder.looksLikeWynntilsItem(encodedMessage)) {
            return encodedMessage;
        }

        
        if (ItemEncoderWrapper.looksLikeEncodedItem(encodedMessage)) {
            return encodedMessage;
        }

        
        return MessageEncoder.formatForMinecraftChat(encodedMessage, maxLineLength);
    }
}