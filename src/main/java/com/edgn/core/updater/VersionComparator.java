package com.edgn.core.updater;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionComparator {
    
    private static final Pattern VERSION_PATTERN = Pattern.compile("^[vV]?([0-9]+(\\.[0-9]+)*)");

    public static int compare(String version1, String version2) {
        String v1 = extractVersion(version1);
        String v2 = extractVersion(version2);
        
        if (v1 == null || v2 == null) {
            return 0;
        }
        
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        
        int maxLength = Math.max(parts1.length, parts2.length);
        
        for (int i = 0; i < maxLength; i++) {
            int num1 = i < parts1.length ? parseIntSafe(parts1[i]) : 0;
            int num2 = i < parts2.length ? parseIntSafe(parts2[i]) : 0;
            
            if (num1 < num2) return -1;
            if (num1 > num2) return 1;
        }
        
        return 0;
    }

    public static boolean isNewer(String newVersion, String currentVersion) {
        return compare(newVersion, currentVersion) > 0;
    }
    
    private static String extractVersion(String versionString) {
        if (versionString == null) return null;
        
        Matcher matcher = VERSION_PATTERN.matcher(versionString.trim());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    private static int parseIntSafe(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}