package com.edgn.core.minecraft.system.command.builder;

import com.edgn.core.minecraft.system.command.CommandContext;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.MinecraftClient;

import java.util.function.Function;
import java.util.stream.Stream;

public final class Arguments {

    private Arguments() {}

    public static EdgnLiteralArgumentBuilder literal(String name) {
        return new EdgnLiteralArgumentBuilder(name);
    }

    public static <S> EdgnRequiredArgumentBuilder<S> argument(String name, ArgumentType<S> type) {
        return new EdgnRequiredArgumentBuilder<>(name, type);
    }

    public static EdgnRequiredArgumentBuilder<String> string(String name) {
        return argument(name, StringArgumentType.string());
    }

    public static EdgnRequiredArgumentBuilder<String> greedyString(String name) {
        return argument(name, StringArgumentType.greedyString());
    }

    public static EdgnRequiredArgumentBuilder<Integer> integer(String name) {
        return argument(name, IntegerArgumentType.integer());
    }

    public static EdgnRequiredArgumentBuilder<Integer> integer(String name, int min) {
        return argument(name, IntegerArgumentType.integer(min));
    }

    public static EdgnRequiredArgumentBuilder<Integer> integer(String name, int min, int max) {
        return argument(name, IntegerArgumentType.integer(min, max));
    }

    public static EdgnRequiredArgumentBuilder<String> player(String name) {
        return (EdgnRequiredArgumentBuilder<String>) string(name).suggests(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getNetworkHandler() == null || client.getNetworkHandler().getPlayerList() == null) {
                return new String[0];
            }
            return client.getNetworkHandler().getPlayerList().stream()
                    .map(playerListEntry -> playerListEntry.getProfile().getName())
                    .toArray(String[]::new);
        });
    }

    public static Function<CommandContext, String[]> filteredSuggestions(String[] allSuggestions) {
        return context -> {
            String input = context.getPartialInput();

            if (input == null || input.trim().isEmpty()) {
                return allSuggestions;
            }

            String lowerInput = input.toLowerCase();
            String[] filtered = Stream.of(allSuggestions)
                    .filter(suggestion -> suggestion.toLowerCase().contains(lowerInput))
                    .toArray(String[]::new);

            return filtered.length > 0 ? filtered : allSuggestions;
        };
    }
}