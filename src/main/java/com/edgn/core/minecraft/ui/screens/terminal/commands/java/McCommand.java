package com.edgn.core.minecraft.ui.screens.terminal.commands.java;

import com.edgn.Main;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;
import net.minecraft.client.MinecraftClient;
import java.util.*;

public class McCommand extends TerminalCommand {

    public McCommand() {
        super("mc", "Quick access to common Minecraft classes", "mc [CLIENT|WORLD|PLAYER|OPTIONS]");
    }

    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();

        if (args.length == 0) {
            output.add("Available Minecraft shortcuts:");
            output.add("  mc client   - MinecraftClient instance");
            output.add("  mc world    - Current world");
            output.add("  mc player   - Current player");
            output.add("  mc options  - Game options");
            return output;
        }

        String target = args[0].toLowerCase();

        try {
            switch (target) {
                case "client":
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client != null) {
                        context.setCurrentClass(MinecraftClient.class);
                        context.setCurrentInstance(client);
                        output.add("Switched to MinecraftClient instance");
                        output.add("Use 'inspect' to explore the client");
                    } else {
                        output.add("MinecraftClient instance not available");
                    }
                    break;

                case "world":
                    MinecraftClient mcClient = MinecraftClient.getInstance();
                    if (mcClient != null && mcClient.world != null) {
                        context.setCurrentInstance(mcClient.world);
                        context.setCurrentClass(mcClient.world.getClass());
                        output.add("Switched to current world: " + mcClient.world.getClass().getSimpleName());
                        output.add("World dimension: " + mcClient.world.getRegistryKey().getValue());
                    } else {
                        output.add("No world currently loaded");
                    }
                    break;

                case "player":
                    MinecraftClient mcClient2 = MinecraftClient.getInstance();
                    if (mcClient2 != null && mcClient2.player != null) {
                        context.setCurrentInstance(mcClient2.player);
                        context.setCurrentClass(mcClient2.player.getClass());
                        output.add("Switched to current player: " + mcClient2.player.getClass().getSimpleName());
                        output.add("Player name: " + mcClient2.player.getName().getString());
                    } else {
                        output.add("No player currently available");
                    }
                    break;

                case "options":
                    MinecraftClient mcClient3 = MinecraftClient.getInstance();
                    if (mcClient3 != null && mcClient3.options != null) {
                        context.setCurrentInstance(mcClient3.options);
                        context.setCurrentClass(mcClient3.options.getClass());
                        output.add("Switched to game options: " + mcClient3.options.getClass().getSimpleName());
                    } else {
                        output.add("Could not access game options");
                    }
                    break;

                case "screen":
                    MinecraftClient mcClient4 = MinecraftClient.getInstance();
                    if (mcClient4 != null && mcClient4.currentScreen != null) {
                        context.setCurrentInstance(mcClient4.currentScreen);
                        context.setCurrentClass(mcClient4.currentScreen.getClass());
                        output.add("Switched to current screen: " + mcClient4.currentScreen.getClass().getSimpleName());
                    } else {
                        output.add("No screen currently open");
                    }
                    break;

                case "renderer":
                    MinecraftClient mcClient5 = MinecraftClient.getInstance();
                    if (mcClient5 != null && mcClient5.gameRenderer != null) {
                        context.setCurrentInstance(mcClient5.gameRenderer);
                        context.setCurrentClass(mcClient5.gameRenderer.getClass());
                        output.add("Switched to game renderer: " + mcClient5.gameRenderer.getClass().getSimpleName());
                    } else {
                        output.add("Game renderer not available");
                    }
                    break;

                case "window":
                    MinecraftClient mcClient6 = MinecraftClient.getInstance();
                    if (mcClient6 != null && mcClient6.getWindow() != null) {
                        context.setCurrentInstance(mcClient6.getWindow());
                        context.setCurrentClass(mcClient6.getWindow().getClass());
                        output.add("Switched to game window: " + mcClient6.getWindow().getClass().getSimpleName());
                    } else {
                        output.add("Game window not available");
                    }
                    break;

                default:
                    output.add("Unknown target: " + target);
                    output.add("Available: client, world, player, options, screen, renderer, window");
            }

        } catch (Exception e) {
            output.add("Error accessing " + target + ": " + e.getMessage());
            Main.OVERLAY_MANAGER.getLoggerOverlay().error(e.getMessage(), true);
        }

        return output;
    }

    @Override
    public List<String> getCompletions(TerminalContext context, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0];
            String[] targets = {"client", "world", "player", "options", "screen", "renderer", "window"};

            for (String target : targets) {
                if (target.startsWith(prefix)) {
                    completions.add(target);
                }
            }
        }

        return completions;
    }

    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
                "NAME", "    mc - quick access to Minecraft classes", "",
                "SYNOPSIS", "    mc [TARGET]", "",
                "DESCRIPTION", "    Quickly switch to common Minecraft classes and instances.",
                "",
                "TARGETS",
                "    client     MinecraftClient singleton instance",
                "    world      Current world instance",
                "    player     Current player instance",
                "    options    Game options/settings",
                "    screen     Current screen (GUI)",
                "    renderer   Game renderer",
                "    window     Game window"
        );
    }
}