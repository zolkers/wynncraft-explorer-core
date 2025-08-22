package com.edgn.core.minecraft.system.command;

import com.edgn.core.minecraft.system.command.builder.ArgumentBuilder;
import com.edgn.core.minecraft.system.command.builder.CommandBuilder;
import com.edgn.core.minecraft.system.command.builder.EdgnLiteralArgumentBuilder;
import com.edgn.core.minecraft.system.command.builder.EdgnRequiredArgumentBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class CommandHelpGenerator {

    public static void generateHelpFor(CommandBuilder cmd, CommandContext context) {
        String commandBase = "/" + CommandManager.getCommandPrefix() + " " + cmd.getName();

        context.sendRawMessage(CommandTextUtil.createSeparator());
        context.sendRawMessage(Text.literal("Help for command: ").formatted(Formatting.GOLD)
                .append(Text.literal(commandBase).formatted(Formatting.YELLOW)));
        context.sendRawMessage(CommandTextUtil.createSeparator());

        context.sendRawMessage(Text.literal(cmd.getDescription()).formatted(Formatting.WHITE));

        if (!cmd.getAliases().isEmpty()) {
            context.sendRawMessage(Text.literal("Aliases: ").formatted(Formatting.GRAY)
                    .append(String.join(", ", cmd.getAliases())));
        }

        context.sendRawMessage(Text.literal(""));
        context.sendRawMessage(CommandTextUtil.createSubtitle("Available usages:"));
        
        List<String> usages = findUsagePaths(cmd);
        if (usages.isEmpty()) {
            context.sendRawMessage(Text.literal("  " + commandBase).formatted(Formatting.GREEN));
        } else {
            for (String usage : usages) {
                String clickableUsage = usage.replaceFirst("/" + CommandManager.getCommandPrefix() + " ", "");
                context.sendRawMessage(Text.literal("  ").append(CommandTextUtil.createClickableCommand(clickableUsage)));
            }
        }

        context.sendRawMessage(Text.literal(""));
        context.sendRawMessage(CommandTextUtil.createBackButton());
        context.sendRawMessage(CommandTextUtil.createSeparator());
    }

    private static List<String> findUsagePaths(CommandBuilder cmd) {
        List<String> paths = new ArrayList<>();
        String prefix = "/" + CommandManager.getCommandPrefix() + " " + cmd.getName();

        if (cmd.getExecutor() != null) {
            paths.add(prefix);
        }
        
        for (ArgumentBuilder<?> argument : cmd.getArguments()) {
            recursivePathFinder(prefix, argument, paths);
        }
        return paths;
    }

    private static void recursivePathFinder(String currentPath, ArgumentBuilder<?> node, List<String> paths) {
        String nodeRepresentation;

        if (node instanceof EdgnLiteralArgumentBuilder) {
            nodeRepresentation = node.getName();
        } else if (node instanceof EdgnRequiredArgumentBuilder) {
            nodeRepresentation = "<" + node.getName() + ">";
        } else {
            nodeRepresentation = "[?]";
        }

        String newPath = currentPath + " " + nodeRepresentation;

        if (node.getExecutor() != null) {
            paths.add(newPath);
        }

        for (ArgumentBuilder<?> child : node.getChildren()) {
            recursivePathFinder(newPath, child, paths);
        }
    }
}