package com.edgn.core.minecraft.text;

import net.minecraft.text.Text;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextMatcher {
    private final Text wrappedText;
    
    public TextMatcher(Text text) {
        this.wrappedText = text;
    }

    public Text getText() {
        return wrappedText;
    }

    public boolean matches(Pattern pattern, PartStyle.StyleType styleType) {
        String textString = extractTextString(styleType);
        return pattern.matcher(textString).matches();
    }

    public Matcher getMatcher(Pattern pattern, PartStyle.StyleType styleType) {
        String textString = extractTextString(styleType);
        return pattern.matcher(textString);
    }

    public boolean matches(String patternString, PartStyle.StyleType styleType) {
        return matches(Pattern.compile(patternString), styleType);
    }

    public Matcher getMatcher(String patternString, PartStyle.StyleType styleType) {
        return getMatcher(Pattern.compile(patternString), styleType);
    }

    private String extractTextString(PartStyle.StyleType styleType) {
        return switch (styleType) {
            case LITERAL -> {
                String literalString = wrappedText.getLiteralString();
                yield literalString != null ? literalString : wrappedText.getString();
            }
            default -> wrappedText.getString();
        };
    }

    public static TextMatcher of(Text text) {
        return new TextMatcher(text);
    }

    public static TextMatcher of(String text) {
        return new TextMatcher(Text.literal(text));
    }
    
    @Override
    public String toString() {
        return "TextMatcher{" + wrappedText.getString() + "}";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TextMatcher that = (TextMatcher) obj;
        return wrappedText.equals(that.wrappedText);
    }
    
    @Override
    public int hashCode() {
        return wrappedText.hashCode();
    }
}