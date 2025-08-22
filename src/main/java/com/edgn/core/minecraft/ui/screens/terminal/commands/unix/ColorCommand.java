package com.edgn.core.minecraft.ui.screens.terminal.commands.unix;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;
import java.util.*;

public class ColorCommand extends TerminalCommand {

    public ColorCommand() {
        super("color", "Change terminal colors", "color [ELEMENT] [COLOR]");
    }

    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();

        if (args.length == 0) {
            output.add("Current terminal colors:");
            output.add("  text      : " + colorToName(context.getTextColor()) + " (0x" + Integer.toHexString(context.getTextColor()).toUpperCase() + ")");
            output.add("  background: " + colorToName(context.getBackgroundColor()) + " (0x" + Integer.toHexString(context.getBackgroundColor()).toUpperCase() + ")");
            output.add("  border    : " + colorToName(context.getBorderColor()) + " (0x" + Integer.toHexString(context.getBorderColor()).toUpperCase() + ")");
            output.add("  scrollbar : " + colorToName(context.getScrollbarColor()) + " (0x" + Integer.toHexString(context.getScrollbarColor()).toUpperCase() + ")");
            output.add("");
            output.add("Usage: color <element> <color>");
            output.add("Elements: text, background, border, scrollbar, all");
            output.add("Colors: white, black, red, green, blue, yellow, cyan, magenta, gray, orange, purple");
            output.add("Or use hex: #RRGGBB or 0xRRGGBB");
            
        } else if (args.length == 1) {
            String element = args[0].toLowerCase();
            if (isValidElement(element)) {
                output.add("Available colors for " + element + ":");
                output.add("Basic colors:");
                for (Map.Entry<String, Integer> entry : getBasicColors().entrySet()) {
                    String colorName = entry.getKey();
                    int colorValue = entry.getValue();
                    output.add("  " + colorName + " (0x" + Integer.toHexString(colorValue).toUpperCase() + ")");
                }
                output.add("");
                output.add("You can also use custom hex colors: #FF0000 or 0xFF0000");
            } else {
                output.add("Invalid element: " + element);
                output.add("Valid elements: text, background, border, scrollbar, all");
            }
            
        } else {
            String element = args[0].toLowerCase();
            String colorStr = args[1];
            
            if (!isValidElement(element)) {
                output.add("Invalid element: " + element);
                output.add("Valid elements: text, background, border, scrollbar, all");
                return output;
            }
            
            Integer color = parseColor(colorStr);
            if (color == null) {
                output.add("Invalid color: " + colorStr);
                output.add("Use color names (white, red, etc.) or hex (#FF0000, 0xFF0000)");
                return output;
            }
            
            if (element.equals("all")) {
                context.setTextColor(color);
                context.setBackgroundColor(0xFF000000);
                context.setBorderColor(adjustBrightness(color, 0.5f));
                context.setScrollbarColor(adjustBrightness(color, 0.3f));
                output.add("Set all colors to " + colorToName(color));
            } else {
                switch (element) {
                    case "text":
                        context.setTextColor(color);
                        output.add("Text color set to " + colorToName(color));
                        break;
                    case "background":
                        context.setBackgroundColor(color);
                        output.add("Background color set to " + colorToName(color));
                        break;
                    case "border":
                        context.setBorderColor(color);
                        output.add("Border color set to " + colorToName(color));
                        break;
                    case "scrollbar":
                        context.setScrollbarColor(color);
                        output.add("Scrollbar color set to " + colorToName(color));
                        break;
                }
            }
        }

        return output;
    }

    private boolean isValidElement(String element) {
        return Arrays.asList("text", "background", "border", "scrollbar", "all").contains(element);
    }

    private Map<String, Integer> getBasicColors() {
        Map<String, Integer> colors = new LinkedHashMap<>();
        colors.put("white", 0xFFFFFFFF);
        colors.put("black", 0xFF000000);
        colors.put("red", 0xFFFF0000);
        colors.put("green", 0xFF00FF00);
        colors.put("blue", 0xFF0000FF);
        colors.put("yellow", 0xFFFFFF00);
        colors.put("cyan", 0xFF00FFFF);
        colors.put("magenta", 0xFFFF00FF);
        colors.put("gray", 0xFF808080);
        colors.put("darkgray", 0xFF404040);
        colors.put("lightgray", 0xFFC0C0C0);
        colors.put("orange", 0xFFFFA500);
        colors.put("purple", 0xFF800080);
        colors.put("pink", 0xFFFFC0CB);
        colors.put("lime", 0xFF32CD32);
        colors.put("navy", 0xFF000080);
        return colors;
    }

    private Integer parseColor(String colorStr) {
        colorStr = colorStr.toLowerCase().trim();
        
        Map<String, Integer> basicColors = getBasicColors();
        if (basicColors.containsKey(colorStr)) {
            return basicColors.get(colorStr);
        }
        
        try {
            if (colorStr.startsWith("#")) {
                String hex = colorStr.substring(1);
                if (hex.length() == 6) {
                    return 0xFF000000 | Integer.parseInt(hex, 16);
                }
            } else if (colorStr.startsWith("0x")) {
                String hex = colorStr.substring(2);
                if (hex.length() == 6) {
                    return 0xFF000000 | Integer.parseInt(hex, 16);
                } else if (hex.length() == 8) {
                    return (int) Long.parseLong(hex, 16);
                }
            }
        } catch (NumberFormatException e) {
            return null;
        }
        
        return null;
    }

    private String colorToName(int color) {
        for (Map.Entry<String, Integer> entry : getBasicColors().entrySet()) {
            if (entry.getValue().equals(color)) {
                return entry.getKey();
            }
        }
        return "custom";
    }

    private int adjustBrightness(int color, float factor) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        
        r = Math.max(0, Math.min(255, (int) (r * factor)));
        g = Math.max(0, Math.min(255, (int) (g * factor)));
        b = Math.max(0, Math.min(255, (int) (b * factor)));
        
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    @Override
    public List<String> getCompletions(TerminalContext context, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0];
            String[] elements = {"text", "background", "border", "scrollbar", "all"};

            for (String element : elements) {
                if (element.startsWith(prefix)) {
                    completions.add(element);
                }
            }
        } else if (args.length == 2) {
            String prefix = args[1];

            for (String colorName : getBasicColors().keySet()) {
                if (colorName.startsWith(prefix)) {
                    completions.add(colorName);
                }
            }
        }

        Collections.sort(completions);
        return completions;
    }

    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
                "NAME", "    color - change terminal colors", "",
                "SYNOPSIS", "    color [ELEMENT] [COLOR]", "",
                "DESCRIPTION", "    Change the colors of terminal elements.",
                "",
                "ELEMENTS",
                "    text         Text color",
                "    background   Background color", 
                "    border       Border color",
                "    scrollbar    Scrollbar color",
                "    all          Set text and derived colors",
                "",
                "COLORS",
                "    Named: white, black, red, green, blue, yellow, cyan, magenta, gray, etc.",
                "    Hex: #RRGGBB or 0xRRGGBB",
                "",
                "EXAMPLES",
                "    color text white",
                "    color background black", 
                "    color all green",
                "    color text #FF6600"
        );
    }
}