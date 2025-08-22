package com.edgn.core.minecraft.system.command.builder;

import com.edgn.core.minecraft.system.command.CommandManager;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class EdgnRequiredArgumentBuilder<S> extends ArgumentBuilder<RequiredArgumentBuilder<FabricClientCommandSource, S>> {

    private final ArgumentType<S> type;

    public EdgnRequiredArgumentBuilder(String name, ArgumentType<S> type) {
        super(name);
        this.type = type;
    }

    @Override
    public RequiredArgumentBuilder<FabricClientCommandSource, S> build() {
        RequiredArgumentBuilder<FabricClientCommandSource, S> builder = RequiredArgumentBuilder.argument(this.name, this.type);

        if (executor != null) {
            builder.executes(ctx -> {
                executor.accept(new CommandManager.BrigadierCommandContext(ctx));
                return 1;
            });
        }

        if (suggestionProvider != null) {
            builder.suggests(suggestionProvider);
        }

        for (ArgumentBuilder<?> child : children) {
            builder.then(child.build());
        }

        return builder;
    }
}