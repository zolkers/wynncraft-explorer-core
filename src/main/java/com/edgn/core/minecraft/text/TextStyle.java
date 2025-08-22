package com.edgn.core.minecraft.text;

import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class TextStyle implements Iterator<TextStyle.StyledSegment>, Iterable<TextStyle.StyledSegment> {

    private final Text text;
    private final List<Text> segments;
    private int currentIndex = 0;

    private TextStyle(Text text) {
        this.text = text;
        this.segments = text != null ? text.getWithStyle(Style.EMPTY) : List.of();
    }

    public static TextStyle of(Text text) {
        return new TextStyle(text);
    }

    public String toLegacy() {
        if (text == null) return "";

        StringBuilder result = new StringBuilder();

        for (Text segment : segments) {
            Style style = segment.getStyle();
            String content = segment.getString();

            result.append(styleToLegacyCodes(style));
            result.append(content);
        }

        return result.toString();
    }

    public Text getText() {
        return text;
    }

    public String getPlain() {
        return text != null ? text.getString() : "";
    }

    public boolean isEmpty() {
        return text == null || text.getString().isEmpty();
    }

    public int segmentCount() {
        return segments.size();
    }

    @Override
    public boolean hasNext() {
        return currentIndex < segments.size();
    }

    @Override
    public StyledSegment next() {
        Text segment = segments.get(currentIndex++);
        return new StyledSegment(segment.getString(), segment.getStyle());
    }

    @Override
    public @NotNull Iterator<StyledSegment> iterator() {
        currentIndex = 0;
        return this;
    }

    private static String styleToLegacyCodes(Style style) {
        StringBuilder codes = new StringBuilder();

        TextColor color = style.getColor();
        if (color != null) {
            codes.append("§").append(getColorCode(color));
        }

        if (style.isObfuscated()) codes.append("§k");
        if (style.isBold()) codes.append("§l");
        if (style.isItalic()) codes.append("§o");
        if (style.isUnderlined()) codes.append("§n");
        if (style.isStrikethrough()) codes.append("§m");

        return codes.toString();
    }

    private static String getColorCode(TextColor color) {
        for (Formatting formatting : Formatting.values()) {
            if (formatting.getColorValue() != null &&
                    formatting.getColorValue().equals(color.getRgb())) {
                return String.valueOf(formatting.getCode());
            }
        }

        return String.format("#%06x", color.getRgb() & 0xFFFFFF);
    }

    @Override
    public String toString() {
        return toLegacy();
    }


    public static class StyledSegment {
        private final String content;
        private final Style style;

        StyledSegment(String content, Style style) {
            this.content = content;
            this.style = style;
        }

        public String getContent() {
            return content;
        }

        public Style getStyle() {
            return style;
        }

        public String toLegacy() {
            return styleToLegacyCodes(style) + content;
        }

        public boolean hasColor() {
            return style.getColor() != null;
        }

        public boolean hasFormatting() {
            return style.isBold() || style.isItalic() || style.isUnderlined() ||
                    style.isStrikethrough() || style.isObfuscated();
        }

        @Override
        public String toString() {
            return toLegacy();
        }
    }
}