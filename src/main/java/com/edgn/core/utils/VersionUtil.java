package com.edgn.core.utils;

import com.edgn.Main;

public class VersionUtil {

    public static String getCurrentVersion() {
        return Main.VERSION;
    }

    public static boolean isVersionNewer(String newVersion, String currentVersion) {
        if (currentVersion.equals("unknown")) return true;

        try {
            String cleanNew = newVersion.startsWith("v") || newVersion.startsWith("V") ?
                    newVersion.substring(1) : newVersion;
            String cleanCurrent = currentVersion.startsWith("v") || currentVersion.startsWith("V") ?
                    currentVersion.substring(1) : currentVersion;

            String[] newParts = cleanNew.split("\\.");
            String[] currentParts = cleanCurrent.split("\\.");

            int maxLength = Math.max(newParts.length, currentParts.length);

            for (int i = 0; i < maxLength; i++) {
                int newPart = i < newParts.length ? Integer.parseInt(newParts[i]) : 0;
                int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;

                if (newPart > currentPart) return true;
                if (newPart < currentPart) return false;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}