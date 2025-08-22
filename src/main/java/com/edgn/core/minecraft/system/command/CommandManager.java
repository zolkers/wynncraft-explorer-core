package com.edgn.core.minecraft.system.command;

import com.edgn.core.minecraft.system.command.builder.ArgumentBuilder;
import com.edgn.core.minecraft.system.command.builder.CommandBuilder;
import com.edgn.core.minecraft.system.command.commands.*;
import com.edgn.core.minecraft.system.command.commands.RaidInformationCommand;
import com.edgn.core.minecraft.system.command.commands.api.PlayerInformationCommand;
import com.edgn.core.minecraft.system.command.commands.nbt.GetNBTFromDisplayEntitiesCommand;
import com.edgn.core.minecraft.system.command.commands.nbt.GetNBTFromEntityCommand;
import com.edgn.core.minecraft.system.command.commands.nbt.GetNBTFromItemCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private static final Map<String, CommandBuilder> commands = new HashMap<>();
    private static final String COMMAND_PREFIX = "we";
    private static LiteralCommandNode<FabricClientCommandSource> rootNode = null;

    public static void register(CommandBuilder commandBuilder) {
        commands.put(commandBuilder.getName().toLowerCase(), commandBuilder);
    }

    public static void register(ICommand command) {
        register(command.build());
    }

    public static void init() {
        register(new HelpCommand());
        register(new BurgerCommand());
        register(new GetUUIDFromNameCommand());
        register(new PlayerInformationCommand());
        register(new RaidInformationCommand());
        register(new GetNBTFromItemCommand());
        register(new GetNBTFromEntityCommand());
        register(new GetNBTFromDisplayEntitiesCommand());
    }

    public static void registerBrigadierCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> mainNode = LiteralArgumentBuilder.literal(COMMAND_PREFIX);

        mainNode.executes(ctx -> {
            CommandBuilder helpCmd = commands.get("help");
            if (helpCmd != null && helpCmd.getExecutor() != null) {
                helpCmd.getExecutor().accept(new BrigadierCommandContext(ctx));
            } else {
                ctx.getSource().sendError(Text.of("Help command not found."));
            }
            return 1;
        });

        for (CommandBuilder cmdBuilder : commands.values()) {
            registerCommandNode(mainNode, cmdBuilder);
        }

        dispatcher.register(mainNode);
    }

    private static void registerCommandNode(LiteralArgumentBuilder<FabricClientCommandSource> root, CommandBuilder cmdBuilder) {
        LiteralArgumentBuilder<FabricClientCommandSource> commandNode = LiteralArgumentBuilder.literal(cmdBuilder.getName());

        if (cmdBuilder.getExecutor() != null) {
            commandNode.executes(ctx -> {
                cmdBuilder.getExecutor().accept(new BrigadierCommandContext(ctx));
                return 1;
            });
        }

        for (ArgumentBuilder<?> arg : cmdBuilder.getArguments()) {
            commandNode.then(arg.build());
        }

        LiteralCommandNode<FabricClientCommandSource> builtCommandNode = commandNode.build();
        root.then(builtCommandNode);

        for (String alias : cmdBuilder.getAliases()) {
            root.then(LiteralArgumentBuilder.<FabricClientCommandSource>literal(alias).redirect(builtCommandNode));
        }
    }

    public static void registerFromExtension(CommandBuilder commandBuilder) {
        register(commandBuilder);
        if (rootNode != null) {
            LiteralArgumentBuilder<FabricClientCommandSource> tmp = LiteralArgumentBuilder.literal(COMMAND_PREFIX);
            registerCommandNode(tmp, commandBuilder);
            LiteralCommandNode<FabricClientCommandSource> built = tmp.build();
            for (com.mojang.brigadier.tree.CommandNode<FabricClientCommandSource> child : built.getChildren()) {
                rootNode.addChild(child);
            }
        }
    }

    public static void registerFromExtension(ICommand command) {
        register(command);
        if (rootNode != null) {
            LiteralArgumentBuilder<FabricClientCommandSource> tmp = LiteralArgumentBuilder.literal(COMMAND_PREFIX);
            registerCommandNode(tmp, command.build());
            LiteralCommandNode<FabricClientCommandSource> built = tmp.build();
            for (com.mojang.brigadier.tree.CommandNode<FabricClientCommandSource> child : built.getChildren()) {
                rootNode.addChild(child);
            }
        }
    }


    public static Collection<CommandBuilder> getCommands() {
        return commands.values();
    }

    public static String getCommandPrefix() {
        return COMMAND_PREFIX;
    }

    public static class BrigadierCommandContext implements com.edgn.core.minecraft.system.command.CommandContext {
        private final com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> context;

        public BrigadierCommandContext(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> context) {
            this.context = context;
        }

        @Override
        public void sendMessage(String message) {
            context.getSource().sendFeedback(Text.of("[COMMAND] " + message));
        }

        @Override
        public void sendError(String message) {
            context.getSource().sendError(Text.of(message));
        }

        @Override
        public void sendRawMessage(Text message) {
            context.getSource().sendFeedback(message);
        }

        @Override
        public ClientPlayerEntity getPlayer() {
            return context.getSource().getPlayer();
        }

        @Override
        public <T> T getArgument(String name, Class<T> clazz) {
            try {
                return context.getArgument(name, clazz);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        @Override
        public String getPartialInput() {
            try {
                String input = context.getInput();
                int cursor = context.getRange().getEnd();

                if (cursor == input.length() && input.endsWith(" ")) {
                    return "";
                }

                int lastSpaceIndex = input.lastIndexOf(' ', cursor - 1);

                if (lastSpaceIndex == -1) {
                    return input.substring(0, cursor);
                }

                return input.substring(lastSpaceIndex + 1, cursor);
            } catch (Exception e) {
                context.getSource().sendError(Text.of("Error during input aborting"));
                return "";
            }
        }
    }
}