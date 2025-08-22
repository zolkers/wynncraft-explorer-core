package com.edgn.core.utils;

import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class StringUtil {

    public static String TAB_SPACE = "    ";
    public static String SPACE = " ";
    public static String DOUBLE_SPACE = "  ";

    public static boolean copyToClipboard(String text) {
        try {
            MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            client.keyboard.setClipboard(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean matchSearchQuery(String str, String pattern) {
        if (str == null || pattern == null) return false;

        String lowerStr = str.toLowerCase();
        String lowerPattern = pattern.toLowerCase();

        if (lowerPattern.startsWith("*") && lowerPattern.endsWith("*")) {
            String middle = lowerPattern.substring(1, lowerPattern.length() - 1);
            return lowerStr.contains(middle);
        } else if (lowerPattern.startsWith("*")) {
            String suffix = lowerPattern.substring(1);
            return lowerStr.endsWith(suffix);
        } else if (lowerPattern.endsWith("*")) {
            String prefix = lowerPattern.substring(0, lowerPattern.length() - 1);
            return lowerStr.startsWith(prefix);
        } else {
            return lowerStr.equals(lowerPattern);
        }
    }

    public static boolean matchesAnyPattern(String itemName, List<String> patterns) {
        if (itemName == null || patterns == null || patterns.isEmpty()) {
            return false;
        }

        String lowerItemName = itemName.toLowerCase();
        return patterns.stream().anyMatch(pattern ->
                matchSearchQueryOptimized(lowerItemName, pattern)
        );
    }

    private static boolean matchSearchQueryOptimized(String lowerStr, String pattern) {
        if (pattern == null) return false;

        String lowerPattern = pattern.toLowerCase();

        if (lowerPattern.startsWith("*") && lowerPattern.endsWith("*")) {
            String middle = lowerPattern.substring(1, lowerPattern.length() - 1);
            return lowerStr.contains(middle);
        } else if (lowerPattern.startsWith("*")) {
            String suffix = lowerPattern.substring(1);
            return lowerStr.endsWith(suffix);
        } else if (lowerPattern.endsWith("*")) {
            String prefix = lowerPattern.substring(0, lowerPattern.length() - 1);
            return lowerStr.startsWith(prefix);
        } else {
            return lowerStr.equals(lowerPattern);
        }
    }

    public static class CompiledPatterns {
        private final Set<String> exactMatches = new HashSet<>();
        private final Set<String> prefixes = new HashSet<>();
        private final Set<String> suffixes = new HashSet<>();
        private final Set<String> contains = new HashSet<>();

        public CompiledPatterns(List<String> patterns) {
            for (String pattern : patterns) {
                if (pattern == null) continue;

                String lowerPattern = pattern.toLowerCase();

                if (lowerPattern.startsWith("*") && lowerPattern.endsWith("*")) {
                    contains.add(lowerPattern.substring(1, lowerPattern.length() - 1));
                } else if (lowerPattern.startsWith("*")) {
                    suffixes.add(lowerPattern.substring(1));
                } else if (lowerPattern.endsWith("*")) {
                    prefixes.add(lowerPattern.substring(0, lowerPattern.length() - 1));
                } else {
                    exactMatches.add(lowerPattern);
                }
            }
        }

        public boolean matches(String itemName) {
            if (itemName == null) return false;

            String lowerItemName = itemName.toLowerCase();

            return exactMatches.contains(lowerItemName) ||
                    prefixes.stream().anyMatch(lowerItemName::startsWith) ||
                    suffixes.stream().anyMatch(lowerItemName::endsWith) ||
                    contains.stream().anyMatch(lowerItemName::contains);
        }
    }
}