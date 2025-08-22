package com.edgn.core.minecraft.system.command.commands;

import com.edgn.core.minecraft.system.command.CommandContext;
import com.edgn.core.minecraft.system.command.CommandHelpGenerator;
import com.edgn.core.minecraft.system.command.CommandManager;
import com.edgn.core.minecraft.system.command.CommandTextUtil;
import com.edgn.core.minecraft.system.command.ICommand;
import com.edgn.core.minecraft.system.command.builder.Arguments;
import com.edgn.core.minecraft.system.command.builder.CommandBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements ICommand {

    private static final int COMMANDS_PER_PAGE = 7;

    @Override
    public CommandBuilder build() {
        return CommandBuilder.create("help")
                .description("Helping poor lost souls")
                .aliases("?")

                .executes(ctx -> showCommandList(ctx, 1))

                .argument(Arguments.integer("page", 1)
                        .executes(ctx -> {
                            int page = ctx.getArgument("page", Integer.class);
                            showCommandList(ctx, page);
                        })
                )

                .argument(Arguments.string("command_name")
                        .suggests(ctx -> CommandManager.getCommands().stream()
                                .map(CommandBuilder::getName)
                                .toArray(String[]::new))
                        .executes(ctx -> {
                            String cmdName = ctx.getArgument("command_name", String.class);

                            CommandManager.getCommands().stream()
                                    .filter(c -> c.getName().equalsIgnoreCase(cmdName) || c.getAliases().contains(cmdName.toLowerCase()))
                                    .findFirst()
                                    .ifPresentOrElse(
                                            targetCmd -> CommandHelpGenerator.generateHelpFor(targetCmd, ctx),
                                            () -> ctx.sendError("Unknown command: " + cmdName)
                                    );
                        })
                );
    }

    private void showCommandList(CommandContext context, int currentPage) {
        List<CommandBuilder> commandsList = new ArrayList<>(CommandManager.getCommands());
        int totalPages = (int) Math.ceil((double) commandsList.size() / COMMANDS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        currentPage = Math.max(1, Math.min(currentPage, totalPages));

        context.sendRawMessage(CommandTextUtil.createTitle("=== Wynn Explorer Commands (Page " + currentPage + "/" + totalPages + ") ==="));
        context.sendRawMessage(Text.of(""));

        int startIndex = (currentPage - 1) * COMMANDS_PER_PAGE;
        for (int i = 0; i < COMMANDS_PER_PAGE; i++) {
            int index = startIndex + i;
            if (index < commandsList.size()) {
                CommandBuilder command = commandsList.get(index);
                context.sendRawMessage(CommandTextUtil.createCommandListItem(command.getName(), command.getDescription()));
            } else {
                context.sendRawMessage(Text.of(""));
            }
        }

        context.sendRawMessage(Text.of(""));

        if (totalPages > 1) {
            sendPageNavigationButtons(context, currentPage, totalPages);
        }
    }

    private void sendPageNavigationButtons(CommandContext context, int currentPage, int totalPages) {
        MutableText navigationText = Text.literal("");
        String commandPrefix = "/" + CommandManager.getCommandPrefix();

        if (currentPage > 1) {
            navigationText.append(CommandTextUtil.createPrevPageButton(commandPrefix + " help " + (currentPage - 1)));
        } else {
            navigationText.append(Text.literal("                 "));
        }

        navigationText.append(Text.literal("  |  ").formatted(Formatting.GRAY));

        if (currentPage < totalPages) {
            navigationText.append(CommandTextUtil.createNextPageButton(commandPrefix + " help " + (currentPage + 1)));
        }

        context.sendRawMessage(navigationText);
    }
}