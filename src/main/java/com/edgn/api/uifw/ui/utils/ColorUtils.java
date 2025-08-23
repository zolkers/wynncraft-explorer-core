package com.edgn.api.uifw.ui.utils;

import java.util.Locale;

/**
 * Utility class for colors
 * @author EDGN
 */
@SuppressWarnings({"unused"})
public class ColorUtils {

    private ColorUtils() {/* utility class */}

    /**
     * NamedColor represents commonly used colors (which are named obvious no ?)
     */
    public enum NamedColor {
        ALICEBLUE(0xFFF0F8FF),
        ANTIQUEWHITE(0xFFFAEBD7),
        AQUA(0xFF00FFFF),
        AQUAMARINE(0xFF7FFFD4),
        AZURE(0xFFF0FFFF),
        BEIGE(0xFFF5F5DC),
        BISQUE(0xFFFFE4C4),
        BLACK(0xFF000000),
        BLANCHEDALMOND(0xFFFFEBCD),
        BLUE(0xFF0000FF),
        BLUEVIOLET(0xFF8A2BE2),
        BROWN(0xFFA52A2A),
        BURLYWOOD(0xFFDEB887),
        CADETBLUE(0xFF5F9EA0),
        CHARCOAL(0xFF4A4A4A),
        CHARTREUSE(0xFF7FFF00),
        CHOCOLATE(0xFFD2691E),
        CORAL(0xFFFF7F50),
        CORNFLOWERBLUE(0xFF6495ED),
        CORNSILK(0xFFFFF8DC),
        CRIMSON(0xFFDC143C),
        CYAN(0xFF00FFFF),
        DARKBLUE(0xFF00008B),
        DARKCYAN(0xFF008B8B),
        DARKGOLDENROD(0xFFB8860B),
        DARKGRAY(0xFFA9A9A9),
        DARKGREEN(0xFF006400),
        DARKKHAKI(0xFFBDB76B),
        DARKMAGENTA(0xFF8B008B),
        DARKOLIVEGREEN(0xFF556B2F),
        DARKORANGE(0xFFFF8C00),
        DARKORCHID(0xFF9932CC),
        DARKRED(0xFF8B0000),
        DARKSALMON(0xFFE9967A),
        DARKSEAGREEN(0xFF8FBC8F),
        DARKSLATEBLUE(0xFF483D8B),
        DARKSLATEGRAY(0xFF2F4F4F),
        DARKTURQUOISE(0xFF00CED1),
        DARKVIOLET(0xFF9400D3),
        DEEPPINK(0xFFFF1493),
        DEEPSKYBLUE(0xFF00BFFF),
        DIMGRAY(0xFF696969),
        DODGERBLUE(0xFF1E90FF),
        FIREBRICK(0xFFB22222),
        FLORALWHITE(0xFFFFFAF0),
        FORESTGREEN(0xFF228B22),
        FUCHSIA(0xFFFF00FF),
        GAINSBORO(0xFFDCDCDC),
        GHOSTWHITE(0xFFF8F8FF),
        GOLD(0xFFFFD700),
        GOLDENROD(0xFFDAA520),
        GRAY(0xFF808080),
        GREEN(0xFF008000),
        GREENYELLOW(0xFFADFF2F),
        HONEYDEW(0xFFF0FFF0),
        HOTPINK(0xFFFF69B4),
        INDIANRED(0xFFCD5C5C),
        INDIGO(0xFF4B0082),
        IVORY(0xFFFFFFF0),
        KHAKI(0xFFF0E68C),
        LAVENDER(0xFFE6E6FA),
        LAVENDERBLUSH(0xFFFFF0F5),
        LAWNGREEN(0xFF7CFC00),
        LEMONCHIFFON(0xFFFFFACD),
        LIGHTBLUE(0xFFADD8E6),
        LIGHTCORAL(0xFFF08080),
        LIGHTCYAN(0xFFE0FFFF),
        LIGHTGOLDENRODYELLOW(0xFFFAFAD2),
        LIGHTGRAY(0xFFD3D3D3),
        LIGHTGREEN(0xFF90EE90),
        LIGHTPINK(0xFFFFB6C1),
        LIGHTSALMON(0xFFFFA07A),
        LIGHTSEAGREEN(0xFF20B2AA),
        LIGHTSKYBLUE(0xFF87CEFA),
        LIGHTSLATEGRAY(0xFF778899),
        LIGHTSTEELBLUE(0xFFB0C4DE),
        LIGHTYELLOW(0xFFFFFFE0),
        LIME(0xFF00FF00),
        LIMEGREEN(0xFF32CD32),
        LINEN(0xFFFAF0E6),
        MAGENTA(0xFFFF00FF),
        MAROON(0xFF800000),
        MEDIUMAQUAMARINE(0xFF66CDAA),
        MEDIUMBLUE(0xFF0000CD),
        MEDIUMORCHID(0xFFBA55D3),
        MEDIUMPURPLE(0xFF9370DB),
        MEDIUMSEAGREEN(0xFF3CB371),
        MEDIUMSLATEBLUE(0xFF7B68EE),
        MEDIUMSPRINGGREEN(0xFF00FA9A),
        MEDIUMTURQUOISE(0xFF48D1CC),
        MEDIUMVIOLETRED(0xFFC71585),
        MIDNIGHTBLUE(0xFF191970),
        MINTCREAM(0xFFF5FFFA),
        MISTYROSE(0xFFFFE4E1),
        MOCCASIN(0xFFFFE4B5),
        NAVAJOWHITE(0xFFFFDEAD),
        NAVY(0xFF000080),
        OLDLACE(0xFFFDF5E6),
        OLIVE(0xFF808000),
        OLIVEDRAB(0xFF6B8E23),
        ORANGE(0xFFFFA500),
        ORANGERED(0xFFFF4500),
        ORCHID(0xFFDA70D6),
        PALEGOLDENROD(0xFFEEE8AA),
        PALEGREEN(0xFF98FB98),
        PALETURQUOISE(0xFFAFEEEE),
        PALEVIOLETRED(0xFFDB7093),
        PAPAYAWHIP(0xFFFFEFD5),
        PEACHPUFF(0xFFFFDAB9),
        PERU(0xFFCD853F),
        PINK(0xFFFFC0CB),
        PLUM(0xFFDDA0DD),
        POWDERBLUE(0xFFB0E0E6),
        PURPLE(0xFF800080),
        RED(0xFFFF0000),
        ROSYBROWN(0xFFBC8F8F),
        ROYALBLUE(0xFF4169E1),
        SADDLEBROWN(0xFF8B4513),
        SALMON(0xFFFA8072),
        SANDYBROWN(0xFFF4A460),
        SEAGREEN(0xFF2E8B57),
        SEASHELL(0xFFFFF5EE),
        SIENNA(0xFFA0522D),
        SILVER(0xFFC0C0C0),
        SKYBLUE(0xFF87CEEB),
        SLATEBLUE(0xFF6A5ACD),
        SLATEGRAY(0xFF708090),
        SNOW(0xFFFFFAFA),
        SPRINGGREEN(0xFF00FF7F),
        STEELBLUE(0xFF4682B4),
        TAN(0xFFD2B48C),
        TEAL(0xFF008080),
        THISTLE(0xFFD8BFD8),
        TOMATO(0xFFFF6347),
        TURQUOISE(0xFF40E0D0),
        VIOLET(0xFFEE82EE),
        WHEAT(0xFFF5DEB3),
        WHITE(0xFFFFFFFF),
        WHITESMOKE(0xFFF5F5F5),
        YELLOW(0xFFFFFF00),
        YELLOWGREEN(0xFF9ACD32),
        TRANSPARENT(0x00000000);

        private final int argb;

        NamedColor(int argb) { this.argb = argb; }

        /**
         * Gets the value of a named color
         * @return the Integer
         */
        public int toInt() { return argb; }

        /**
         * Get a NamedColor from the name
         * @param name the name of a color
         * @return the NamedColor
         */
        public static NamedColor fromName(String name) {
            if (name == null) return null;
            String n = name.trim().toUpperCase(Locale.ROOT).replace(" ", "");
            try { return NamedColor.valueOf(n); } catch (IllegalArgumentException ignored) { return null; }
        }

        /**
         * Returns all the names under a String of the NamedColors
         * @return names
         */
        public static String[] names() {
            NamedColor[] vals = values();
            String[] out = new String[vals.length];
            for (int i = 0; i < vals.length; i++) out[i] = vals[i].name().toLowerCase(Locale.ROOT);
            return out;
        }
    }

    /**
     * Sets the opacity of the method
     * @param color any kind of color as long as it is an Integer ( use NamedColor )
     * @param opacity a value between 0 and 1
     * @return the color with the changed opacity
     */
    public static int setOpacity(int color, float opacity) {
        float alpha = Math.clamp(opacity, 0.0f, 1.0f);
        int newAlpha = (int) (alpha * 255);
        return (newAlpha << 24) | (color & 0x00FFFFFF);
    }
}
