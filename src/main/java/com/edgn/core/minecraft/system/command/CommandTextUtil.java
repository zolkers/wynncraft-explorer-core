package com.edgn.core.minecraft.system.command;

import com.edgn.core.minecraft.render.StyledText;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CommandTextUtil {

    public static final String HELP_TAG = StyledText.INSTANCE.style("[?]", Formatting.AQUA);

    private CommandTextUtil() { /* class utilitaire */ }

    public static MutableText createTitle(String text) {
        return Text.literal(text)
                .formatted(Formatting.GOLD, Formatting.BOLD);
    }

    public static MutableText createSubtitle(String text) {
        return Text.literal(text)
                .formatted(Formatting.YELLOW);
    }

    public static MutableText createInfo(String text) {
        return Text.literal(text)
                .formatted(Formatting.WHITE);
    }

    public static MutableText createSecondary(String text) {
        return Text.literal(text)
                .formatted(Formatting.GRAY);
    }

    public static MutableText createHelpButton(String commandName) {
        return Text.literal(HELP_TAG)
                .setStyle(Style.EMPTY
                        .withColor(Formatting.AQUA)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/" + CommandManager.getCommandPrefix() + " help " + commandName))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Text.literal("Show help of /" + CommandManager.getCommandPrefix() + " " + commandName))));
    }

    public static MutableText createPrevPageButton(String command) {
        return Text.literal("« Previous page")
                .setStyle(Style.EMPTY
                        .withColor(Formatting.AQUA)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Previous page"))));
    }

    public static MutableText createNextPageButton(String command) {
        return Text.literal("Next page »")
                .setStyle(Style.EMPTY
                        .withColor(Formatting.AQUA)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Next page"))));
    }

    public static MutableText createBackButton() {
        return Text.literal("[Back to CMD list]")
                .setStyle(Style.EMPTY
                        .withColor(Formatting.GREEN)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/" + CommandManager.getCommandPrefix() + " help"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Text.literal("Get back to the command list"))));
    }

    public static String formatCommandName(String commandName) {
        return "/" + CommandManager.getCommandPrefix() + " " + commandName;
    }

    public static MutableText createClickableCommand(String commandName) {
        String fullCommand = formatCommandName(commandName);
        return Text.literal(fullCommand)
                .setStyle(Style.EMPTY
                        .withColor(Formatting.GREEN)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, fullCommand + " "))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Text.literal("Click to use the command faster"))));
    }

    public static MutableText createCommandListItem(String commandName, String description) {
        MutableText text = Text.literal("");

        MutableText createButton = createHelpButton(commandName);
        if(createButton != null) text = text.append(createButton);
        text = text.append(" ");

        text = text.append(createClickableCommand(commandName));

        MutableText descriptionText = Text.literal(" - " + description)
                .formatted(Formatting.WHITE);

        text = text.append(descriptionText);

        return text;
    }

    public static MutableText createSeparator() {
        return Text.literal("-".repeat(40)).formatted(Formatting.DARK_GRAY);
    }
}