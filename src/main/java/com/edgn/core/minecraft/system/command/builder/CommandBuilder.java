package com.edgn.core.minecraft.system.command.builder;

import com.edgn.core.minecraft.system.command.CommandContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CommandBuilder {
    private final String name;
    private String description = "No description provided.";
    private final List<String> aliases = new ArrayList<>();
    private final List<ArgumentBuilder<?>> arguments = new ArrayList<>();
    private Consumer<CommandContext> executor;

    private CommandBuilder(String name) {
        this.name = name;
    }

    public static CommandBuilder create(String name) {
        return new CommandBuilder(name);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<ArgumentBuilder<?>> getArguments() {
        return arguments;
    }

    public Consumer<CommandContext> getExecutor() {
        return executor;
    }

    public CommandBuilder description(String description) {
        this.description = description;
        return this;
    }

    public CommandBuilder aliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }

    public CommandBuilder executes(Consumer<CommandContext> executor) {
        this.executor = executor;
        return this;
    }

    public CommandBuilder argument(ArgumentBuilder<?> argument) {
        this.arguments.add(argument);
        return this;
    }
}