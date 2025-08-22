package com.edgn.service.services;

import com.edgn.Main;
import com.edgn.event.listeners.ChatMessageAddListener;
import com.edgn.service.AbstractService;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public class ChatService extends AbstractService implements ChatMessageAddListener {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int MAX_MESSAGES = 1000;

    private final List<StoredChatMessage> messages = new CopyOnWriteArrayList<>();

    @Override
    public void init() {
        Main.EVENT_MANAGER.add(ChatMessageAddListener.class, this);
    }

    @Override
    public void onAddMessage(ChatMessageAddListener.ChatMessageAddEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Text finalMessage = event.isModified() ? event.getModifiedMessage() : event.getOriginalMessage();

        StoredChatMessage message = new StoredChatMessage(
                finalMessage,
                LocalDateTime.now()
        );

        messages.add(message);

        if (messages.size() > MAX_MESSAGES) {
            messages.removeFirst();
        }
    }

    public List<StoredChatMessage> getAllMessages() {
        return new ArrayList<>(messages);
    }

    public List<StoredChatMessage> getMessagesMatchingFilters(Set<String> filterIds) {
        if (filterIds == null || filterIds.isEmpty()) {
            return new ArrayList<>();
        }

        return messages.stream()
                .filter(msg -> filterIds.stream()
                        .anyMatch(filterId -> MessageType.fromFilterId(filterId).matches(msg)))
                .toList();
    }

    public record StoredChatMessage(Text text, LocalDateTime timestamp) {
        public String getFormattedTime() {
            return timestamp.format(TIME_FORMAT);
        }

        public MessageType getType() {
            return MessageType.detect(this);
        }

        public String getContent() {
            return text.getString();
        }

        public String getContentLowercase() {
            return getContent().toLowerCase();
        }

        public String getPlainContent() {
            return text.getString();
        }

        public boolean containsText(String searchText) {
            return getContentLowercase().contains(searchText.toLowerCase());
        }

        public boolean startsWithText(String prefix) {
            return getContentLowercase().startsWith(prefix.toLowerCase());
        }

        public boolean matchesText(Text otherText) {
            if (text.equals(otherText)) {
                return true;
            }
            return text.getString().equals(otherText.getString());
        }

        public boolean hasColorInText(int color) {
            return hasColorInTextRecursive(text, color);
        }

        private boolean hasColorInTextRecursive(Text text, int targetColor) {
            Style style = text.getStyle();
            if (style.getColor() != null && style.getColor().getRgb() == targetColor) {
                return true;
            }

            for (Text sibling : text.getSiblings()) {
                if (hasColorInTextRecursive(sibling, targetColor)) {
                    return true;
                }
            }
            return false;
        }

        public Integer getFirstColor() {
            return getFirstColorRecursive(text);
        }

        private Integer getFirstColorRecursive(Text text) {
            Style style = text.getStyle();
            if (style.getColor() != null) {
                return style.getColor().getRgb();
            }

            for (Text sibling : text.getSiblings()) {
                Integer color = getFirstColorRecursive(sibling);
                if (color != null) {
                    return color;
                }
            }
            return null;
        }

        public Set<Integer> getAllColors() {
            Set<Integer> colors = new HashSet<>();
            getAllColorsRecursive(text, colors);
            return colors;
        }

        private void getAllColorsRecursive(Text text, Set<Integer> colors) {
            Style style = text.getStyle();
            if (style.getColor() != null) {
                colors.add(style.getColor().getRgb());
            }

            for (Text sibling : text.getSiblings()) {
                getAllColorsRecursive(sibling, colors);
            }
        }

        public Integer getRootColor() {
            Style style = text.getStyle();
            return style.getColor() != null ? style.getColor().getRgb() : null;
        }

        public static String colorToHex(int rgbColor) {
            return String.format("#%06X", rgbColor);
        }

        public String getFirstColorHex() {
            Integer color = getFirstColor();
            return color != null ? colorToHex(color) : null;
        }

        public Set<String> getAllColorsHex() {
            return getAllColors().stream()
                    .map(StoredChatMessage::colorToHex)
                    .collect(java.util.stream.Collectors.toSet());
        }

        public boolean hasHexColor(String hexColor) {
            String cleanHex = hexColor.startsWith("#") ? hexColor.substring(1) : hexColor;
            try {
                int rgb = Integer.parseInt(cleanHex, 16);
                return hasColorInText(rgb);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        public boolean hasFormatting(Formatting... formattings) {
            Style style = text.getStyle();
            for (Formatting formatting : formattings) {
                switch (formatting) {
                    case BOLD:
                        if (!style.isBold()) return false;
                        break;
                    case ITALIC:
                        if (!style.isItalic()) return false;
                        break;
                    case UNDERLINE:
                        if (!style.isUnderlined()) return false;
                        break;
                    case STRIKETHROUGH:
                        if (!style.isStrikethrough()) return false;
                        break;
                    case OBFUSCATED:
                        if (!style.isObfuscated()) return false;
                        break;
                }
            }
            return true;
        }
    }

    public enum MessageType {
        DCHAT("dchat", msg -> msg.startsWithText("\uE010\u2064\uE00F\uE012\uE043\uE00F\uE012\uE042\uE00F\uE012\uE047\uE00F\uE012\uE040\uE00F\uE012\uE053\uE011")) ,
        GUILD("guild", msg -> {
            String content = msg.getPlainContent();

            if(msg.getFirstColor() == null) return false;
            if(msg.getFirstColor() != 5636095) return false;

            boolean prefix1 = content.startsWith("\uDAFF\uDFFC\uE006\uDAFF\uDFFF\uE002\uDAFF\uDFFE");
            boolean prefix2 = content.startsWith("\uDAFF\uDFFC\uE001\uDB00\uDC06");

            return prefix1 || prefix2;
        }),

        PARTY("party", msg -> {
            String content = msg.getPlainContent();
            if(msg.getFirstColor() == null) return false;
            if(msg.getFirstColor() != 16777045) return false;

            boolean prefix1 = content.startsWith("\uDAFF\uDFFC\uE005\uDAFF\uDFFF\uE002\uDAFF\uDFFE");
            boolean prefix2 = content.startsWith("\uDAFF\uDFFC\uE001\uDB00\uDC06");

            return prefix1 || prefix2;
        }),
        INFO("info", msg -> msg.startsWithText("[info]")),
        UNKNOWN("unknown", msg -> false);

        private final String filterId;
        private final Predicate<StoredChatMessage> matcher;

        MessageType(String filterId, Predicate<StoredChatMessage> matcher) {
            this.filterId = filterId;
            this.matcher = matcher;
        }

        public boolean matches(StoredChatMessage message) {
            return matcher.test(message);
        }

        public static MessageType detect(StoredChatMessage message) {
            for (MessageType type : values()) {
                if (type != UNKNOWN && type.matcher.test(message)) {
                    return type;
                }
            }
            return UNKNOWN;
        }

        public static MessageType fromFilterId(String filterId) {
            for (MessageType type : values()) {
                if (type.filterId.equalsIgnoreCase(filterId)) {
                    return type;
                }
            }
            return UNKNOWN;
        }

        public String getFilterId() {
            return filterId;
        }
    }

    public List<StoredChatMessage> findMessagesByContent(String searchText) {
        return messages.stream()
                .filter(msg -> msg.containsText(searchText))
                .toList();
    }

    public List<StoredChatMessage> getMessagesByType(MessageType type) {
        return messages.stream()
                .filter(msg -> msg.getType() == type)
                .toList();
    }

    public List<StoredChatMessage> findExactMatches(Text targetText) {
        return messages.stream()
                .filter(msg -> msg.matchesText(targetText))
                .toList();
    }

    public void clearMessagesByType(MessageType type) {
        messages.removeIf(msg -> msg.getType() == type);
    }

    public java.util.Map<MessageType, Long> getMessageTypeStats() {
        return messages.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        StoredChatMessage::getType,
                        java.util.stream.Collectors.counting()
                ));
    }


    public List<StoredChatMessage> findMessagesByColor(int rgbColor) {
        return messages.stream()
                .filter(msg -> msg.hasColorInText(rgbColor))
                .toList();
    }

    public List<StoredChatMessage> findMessagesByHexColor(String hexColor) {
        return messages.stream()
                .filter(msg -> msg.hasHexColor(hexColor))
                .toList();
    }

    public java.util.Map<String, Long> getColorStats() {
        return messages.stream()
                .flatMap(msg -> msg.getAllColorsHex().stream())
                .collect(java.util.stream.Collectors.groupingBy(
                        java.util.function.Function.identity(),
                        java.util.stream.Collectors.counting()
                ));
    }
}