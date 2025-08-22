package com.edgn.core.minecraft.ui.screens.terminal;

import com.edgn.core.minecraft.ui.screens.terminal.commands.unix.*;
import com.edgn.core.minecraft.ui.screens.terminal.commands.java.*;

import java.util.*;

public class CommandManager {
    private final Map<String, TerminalCommand> commands;
    private final Map<String, String> aliases;
    private final TerminalContext context;
    
    public CommandManager() {
        this.commands = new HashMap<>();
        this.aliases = new HashMap<>();
        this.context = new TerminalContext();
        
        registerCommands();
    }

    private void registerCommands() {
        registerCommand(new LsCommand());
        registerCommand(new EchoCommand());
        registerCommand(new PwdCommand());
        registerCommand(new CdCommand());
        registerCommand(new ClearCommand());
        registerCommand(new DateCommand());
        registerCommand(new WhoamiCommand());
        registerCommand(new ExitCommand());
        registerCommand(new BackCommand());
        registerCommand(new CallCommand());
        registerCommand(new ClassCommand());
        registerCommand(new FindCommand());
        registerCommand(new ClassLsCommand());
        registerCommand(new GetCommand());
        registerCommand(new InstanceCommand());
        registerCommand(new InspectCommand());
        registerCommand(new McCommand());
        registerCommand(new ColorCommand());
        registerCommand(new SlCommand());
        registerCommand(new SlsCommand());
        registerCommand(new HelpCommand(commands));
        registerCommand(new ManCommand(commands));

        aliases.put("dir", "ls");
        aliases.put("ll", "ls -l");
        aliases.put("la", "ls -a");
        aliases.put("cls", "clear");
        aliases.put("logout", "exit");
    }
    
    private void registerCommand(TerminalCommand command) {
        commands.put(command.getName(), command);
        
        for (String alias : command.getAliases()) {
            aliases.put(alias, command.getName());
        }
    }
    
    public List<String> executeCommand(String commandLine) {
        if (commandLine.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String[] parts = commandLine.trim().split("\\s+");
        String commandName = parts[0].toLowerCase();
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        
        commandName = aliases.getOrDefault(commandName, commandName);
        
        TerminalCommand command = commands.get(commandName);
        if (command != null) {
            context.getCommandHistory().add(commandLine);
            return command.execute(context, args);
        } else {
            return Arrays.asList(
                "bash: " + parts[0] + ": command not found",
                "Type 'help' for available commands"
            );
        }
    }
    
    public TerminalContext getContext() {
        return context;
    }
    
    public Map<String, TerminalCommand> getCommands() {
        return commands;
    }
}