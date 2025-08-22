package com.edgn.core.utils;

public class TimeUtil {

    public static double millisecondsToSeconds(long milliseconds) {
        return milliseconds / 1000.0;
    }

    public static int millisecondsToSecondsInt(long milliseconds) {
        return (int) (milliseconds / 1000);
    }

    public static long secondsToMilliseconds(double seconds) {
        return (long) (seconds * 1000);
    }

    public static long secondsToMilliseconds(int seconds) {
        return seconds * 1000L;
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long currentTimeSeconds() {
        return millisecondsToSecondsInt(System.currentTimeMillis());
    }

    public static String formatDuration(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + "ms";
        }

        long seconds = milliseconds / 1000;
        long remainingMs = milliseconds % 1000;

        if (seconds < 60) {
            return seconds + "s" + (remainingMs > 0 ? " " + remainingMs + "ms" : "");
        }

        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;

        StringBuilder result = new StringBuilder();
        result.append(minutes).append("m");

        if (remainingSeconds > 0) {
            result.append(" ").append(remainingSeconds).append("s");
        }

        if (remainingMs > 0) {
            result.append(" ").append(remainingMs).append("ms");
        }

        return result.toString();
    }
}