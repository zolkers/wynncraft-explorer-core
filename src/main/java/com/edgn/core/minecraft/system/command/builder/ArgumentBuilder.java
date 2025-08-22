package com.edgn.core.minecraft.system.command.builder;

import com.edgn.core.minecraft.system.command.CommandContext;
import com.edgn.core.minecraft.system.command.CommandManager;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ArgumentBuilder<T extends com.mojang.brigadier.builder.ArgumentBuilder<FabricClientCommandSource, T>> {

    protected final String name;
    protected Consumer<CommandContext> executor;
    protected SuggestionProvider<FabricClientCommandSource> suggestionProvider;
    protected final List<ArgumentBuilder<?>> children = new ArrayList<>();

    protected ArgumentBuilder(String name) {
        this.name = name;
    }

    public ArgumentBuilder<T> executes(Consumer<CommandContext> executor) {
        this.executor = executor;
        return this;
    }

    public ArgumentBuilder<T> then(ArgumentBuilder<?> child) {
        this.children.add(child);
        return this;
    }

    public ArgumentBuilder<T> suggests(Function<CommandContext, String[]> suggestions) {
        this.suggestionProvider = (ctx, builder) -> {
            CommandContext commandContext = new CommandManager.BrigadierCommandContext(ctx);
            String[] suggestionsArray = suggestions.apply(commandContext);
            for (String s : suggestionsArray) {
                builder.suggest(s);
            }
            return builder.buildFuture();
        };
        return this;
    }

    public String getName() {
        return name;
    }

    public Consumer<CommandContext> getExecutor() {
        return executor;
    }

    public List<ArgumentBuilder<?>> getChildren() {
        return children;
    }

    public abstract T build();
}