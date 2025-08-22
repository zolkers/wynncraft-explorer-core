package com.edgn.core.minecraft.render;

import net.minecraft.util.Formatting;

//TODO: REWORK THIS HELL
public enum StyledText {

    INSTANCE;

    public enum CharacterMapping {
        ZERO("0", ""),
        ONE("1", ""),
        TWO("2", ""),
        THREE("3", ""),
        FOUR("4", ""),
        FIVE("5", ""),
        SIX("6", ""),
        SEVEN("7", ""),
        EIGHT("8", ""),
        NINE("9", ""),
        A("A", ""),
        B("B", ""),
        C("C", ""),
        D("D", ""),
        E("E", ""),
        F("F", ""),
        G("G", ""),
        H("H", ""),
        I("I", ""),
        J("J", ""),
        K("K", ""),
        L("L", ""),
        M("M", ""),
        N("N", ""),
        O("O", ""),
        P("P", ""),
        Q("Q", ""),
        R("R", ""),
        S("S", ""),
        T("T", ""),
        U("U", ""),
        V("V", ""),
        W("W", ""),
        X("X", ""),
        Y("Y", ""),
        Z("Z", ""),
        SPACE(" ", " "),
        PIPE("|", "|"),
        QUESTION_MARK("?", "");

        private final String character;
        private final String representation;

        CharacterMapping(String character, String representation) {
            this.character = character;
            this.representation = representation;
        }

        public String getCharacter() {
            return character;
        }

        public String getRepresentation() {
            return representation;
        }

        public static String getRepresentationFor(String character) {
            for (CharacterMapping mapping : values()) {
                if (mapping.getCharacter().equals(character)) {
                    return mapping.getRepresentation();
                }
            }
            return character;
        }
    }

    public enum Embellishment {
        FISHING("Ⓚ"),
        WOODCUTTING("Ⓒ"),
        MINING("Ⓑ"),
        FARMING("Ⓙ"),
        JEWELING("Ⓓ"),
        ARMOURING("Ⓗ"),
        WEAPONSMITHING("Ⓖ"),
        WOODWORKING("Ⓘ"),
        TAILORING("Ⓕ"),
        ALCHEMISM("Ⓛ"),
        SCRIBING("Ⓔ"),
        COOKING("Ⓐ"),
        WARRIOR("\ue030"),
        SHAMAN("\ue02f"),
        MAGE("\ue02e"),
        ASSASSIN("\ue02d"),
        ARCHER("\ue02c"),
        SILVERBULL("\ue02b"),
        NO_RANK("", Formatting.GRAY),
        VETERAN("\ue02a", Formatting.YELLOW),
        IRONMAN("\ue029", Formatting.GOLD),
        ULTIMATE_IRONMAN("\ue083", Formatting.AQUA),
        HUNTED("\ue028", Formatting.DARK_PURPLE),
        HARDCORE("\ue027", Formatting.RED),
        CRAFTSMAN("\ue026", Formatting.DARK_AQUA),
        WEBDEV("\ue025", Formatting.DARK_RED),
        VIPPLUS("\ue024", Formatting.LIGHT_PURPLE),
        VIP("\ue023", Formatting.GREEN),
        QA("\ue022", Formatting.DARK_AQUA),
        OWNER("\ue021", Formatting.DARK_RED),
        MUSIC("\ue020", Formatting.DARK_AQUA),
        MODERATOR("\ue01f", Formatting.GOLD),
        MEDIA("\ue01e", Formatting.LIGHT_PURPLE),
        ITEM("\ue01d", Formatting.DARK_AQUA),
        HYBRID("\ue01c", Formatting.DARK_AQUA),
        HERO("\ue01b", Formatting.LIGHT_PURPLE),
        GAME_MASTER("\ue01a", Formatting.DARK_AQUA),
        DEVELOPER("\ue019", Formatting.DARK_RED),
        CMD("\ue018", Formatting.DARK_AQUA),
        CHAMPION("\ue017", Formatting.YELLOW),
        BUILDER("\ue016", Formatting.DARK_AQUA),
        ARTIST("\ue015", Formatting.DARK_AQUA),
        ADMIN("\ue014", Formatting.DARK_RED),
        HEALTH("❤", Formatting.DARK_RED),
        NEUTRAL("✣", Formatting.GOLD),
        EARTH("✤", Formatting.DARK_GREEN),
        THUNDER("✦", Formatting.YELLOW),
        WATER("❉", Formatting.AQUA),
        FIRE("✹", Formatting.RED),
        AIR("❋", Formatting.WHITE);

        private final String representation;
        private final Formatting color;

        Embellishment(String representation) {
            this.representation = representation;
            this.color = Formatting.WHITE;
        }

        Embellishment(String representation, Formatting color) {
            this.representation = representation;
            this.color = color;
        }

        public String getRepresentation() {
            return representation;
        }

        public Formatting getColor() {
            return color;
        }
    }

    public String encase(String input, Formatting colorTxt, Formatting colorBanner) {
        StringBuilder texteEncased = new StringBuilder();

        texteEncased.append(colorBanner).append("⁤");

        for (char c : input.toCharArray()) {
            String charStr = String.valueOf(c);
            if ("".contains(charStr)) {
                texteEncased.append(colorBanner).append("⁤");
            } else {
                texteEncased.append(colorBanner).append("");
            }

            texteEncased.append(colorTxt).append(CharacterMapping.getRepresentationFor(charStr));
        }

        texteEncased.append(colorBanner).append("");

        return texteEncased.toString();
    }

    public String encaseWithGradient(String input, Formatting colorTxt, Formatting[] gradientColors) {
        StringBuilder texteEncased = new StringBuilder();

        if (gradientColors == null || gradientColors.length == 0) {
            return encase(input, colorTxt, Formatting.WHITE);
        }

        int totalLength = input.length() + 2;

        texteEncased.append(gradientColors[0]).append("⁤");

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            String charStr = String.valueOf(c);

            int colorIndex = (int) ((double) (i + 1) / totalLength * gradientColors.length);
            colorIndex = Math.min(colorIndex, gradientColors.length - 1);

            if ("".contains(charStr)) {
                texteEncased.append(gradientColors[colorIndex]).append("⁤");
            } else {
                texteEncased.append(gradientColors[colorIndex]);
            }

            texteEncased.append(colorTxt).append(CharacterMapping.getRepresentationFor(charStr));
        }

        texteEncased.append(gradientColors[gradientColors.length - 1]);

        return texteEncased.toString();
    }

    public String encaseWithSimpleGradient(String input, Formatting colorTxt, Formatting startColor, Formatting endColor) {
        StringBuilder texteEncased = new StringBuilder();

        int totalLength = input.length() + 2;

        Formatting[] gradientColors = interpolateFormattingColors(startColor, endColor, totalLength);

        texteEncased.append(gradientColors[0]).append("⁤");

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            String charStr = String.valueOf(c);

            Formatting currentColor = gradientColors[i + 1];

            if ("".contains(charStr)) {
                texteEncased.append(currentColor).append("⁤");
            } else {
                texteEncased.append(currentColor);
            }

            texteEncased.append(colorTxt).append(CharacterMapping.getRepresentationFor(charStr));
        }

        texteEncased.append(gradientColors[gradientColors.length - 1]);

        return texteEncased.toString();
    }

    private Formatting[] interpolateFormattingColors(Formatting start, Formatting end, int steps) {
        Formatting[] result = new Formatting[steps];

        Formatting[] colorSpectrum = {
                Formatting.BLACK, Formatting.DARK_BLUE, Formatting.DARK_GREEN,
                Formatting.DARK_AQUA, Formatting.DARK_RED, Formatting.DARK_PURPLE,
                Formatting.GOLD, Formatting.GRAY, Formatting.DARK_GRAY,
                Formatting.BLUE, Formatting.GREEN, Formatting.AQUA,
                Formatting.RED, Formatting.LIGHT_PURPLE, Formatting.YELLOW, Formatting.WHITE
        };

        int startIndex = findColorIndex(start, colorSpectrum);
        int endIndex = findColorIndex(end, colorSpectrum);

        for (int i = 0; i < steps; i++) {
            double progress = (double) i / (steps - 1);
            int currentIndex = (int) (startIndex + (endIndex - startIndex) * progress);
            currentIndex = Math.max(0, Math.min(currentIndex, colorSpectrum.length - 1));
            result[i] = colorSpectrum[currentIndex];
        }

        return result;
    }

    private int findColorIndex(Formatting color, Formatting[] spectrum) {
        for (int i = 0; i < spectrum.length; i++) {
            if (spectrum[i] == color) {
                return i;
            }
        }
        return 0;
    }

    public String style(String input, Formatting colorStl) {
        StringBuilder texteEncased = new StringBuilder();

        for (char c : input.toCharArray()) {
            String charStr = String.valueOf(c);
            texteEncased.append(colorStl).append(CharacterMapping.getRepresentationFor(charStr));
        }

        return texteEncased.toString();
    }
}