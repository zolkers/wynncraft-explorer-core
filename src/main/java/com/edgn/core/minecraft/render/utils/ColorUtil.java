package com.edgn.core.minecraft.render.utils;

public enum ColorUtil {

    INSTANCE;

    /**
     * Black hex color (Darker and stronger pastel with slightly weaker alpha)
     */
    public final int BLACK = 0xCC808080;

    /**
     * Blue hex color (Stronger pastel with slightly weaker alpha)
     */
    public final int BLUE = 0xCC6FA0D2;

    /**
     * Brown hex color (Stronger pastel with slightly weaker alpha)
     */
    public final int BROWN = 0xCC9E7F4B;

    /**
     * Cyan hex color (Stronger pastel with slightly weaker alpha)
     */
    public final int CYAN = 0xCC54FCFC;

    /**
     * Dark Gray hex color (Stronger pastel with slightly weaker alpha)
     */
    public final int DKGRAY = 0xCC777777;

    /**
     * Gray hex color (Stronger pastel with slightly weaker alpha)
     */
    public final int GRAY = 0xCC999999;

    /**
     * Green hex color (Stronger pastel with slightly weaker alpha)
     */
    public final int GREEN = 0xCC54FC54;

    /**
     * Light Gray hex color (Stronger pastel with slightly weaker alpha)
     */
    public final int LTGRAY = 0xCCB0B0B0;

    public final int COMMON = 0xCCFCFCFC;
    public final int CRAFTED  = 0xCC00A8A8;

    /**
     * Magenta hex color (Stronger pastel with slightly weaker alpha)
     */
    public final int MAGENTA = 0xCCD45F9B;

    /**
     * Orange hex color (Stronger pastel with slightly weaker alpha)
     */
    public final int ORANGE = 0xCCFF9A07;

    /**
     * Pink hex color (Stronger pastel with slightly weaker alpha)
     */
    public final int PINK = 0xCCFC54FC;

    /**
     * Purple hex color (Original violet, kept as is, with slightly weaker alpha)
     */
    public final int PURPLE = 0xCCA800A8;

    /**
     * Red hex color (Stronger pastel with slightly weaker alpha)
     */
    public final int RED = 0xCCFC5454;

    /**
     * Violet hex color (Stronger pastel with slightly weaker alpha)
     */
    public final int VIOLET = 0xCC9A66CC;

    /**
     * White hex color (White stays unchanged, slightly weaker alpha)
     */
    public final int WHITE = 0xCCFFFFFF;

    /**
     * Yellow hex color (Stronger pastel with slightly weaker alpha)
     */
    public final int YELLOW = 0xCCFCFC54;

    /**
     * Light Blue hex color (Stronger pastel with slightly weaker alpha)
     */
    public final int LTBLUE = 0xCC54FCFC;

    /**
     * Dark Blue hex color (Stronger pastel with slightly weaker alpha)
     */
    public final int DKBLUE = 0xCC4C6FA0;

    public int rainbowColor() {
        int[] rainbowColors = {
                RED,
                ORANGE,
                YELLOW,
                GREEN,
                CYAN,
                BLUE,
                PURPLE
        };

        int colorIndex = (int) (System.currentTimeMillis() / 400) % rainbowColors.length;
        return rainbowColors[colorIndex];
    }
}
