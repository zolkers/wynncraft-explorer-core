package com.edgn.core.minecraft.ui.screens.terminal.commands.unix;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;

import java.util.*;

public class HelpCommand extends TerminalCommand {
    private final Map<String, TerminalCommand> commands;
    
    public HelpCommand(Map<String, TerminalCommand> commands) {
        super("help", 
              "Display help information", 
              "help [COMMAND]");
        this.commands = commands;
    }
    
    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();
        
        if (args.length == 0) {
            output.add("GNU bash");
            output.add("These shell commands are defined internally.");
            output.add("Type 'help name' to find out more about the function 'name'.");
            output.add("Type 'man name' for detailed documentation.");
            output.add("");
            output.add("AVAILABLE COMMANDS:");
            
            List<String> commandNames = new ArrayList<>(commands.keySet());
            Collections.sort(commandNames);
            
            for (String name : commandNames) {
                TerminalCommand cmd = commands.get(name);
                output.add(String.format("  %-12s - %s", name, cmd.getDescription()));
            }
            
            output.add("");
            output.add("Use 'help <command>' for specific command help.");
            output.add("Use 'man <command>' for detailed manual pages.");
        } else {
            String commandName = args[0].toLowerCase();
            TerminalCommand cmd = commands.get(commandName);
            
            if (cmd != null) {
                output.add(cmd.getName() + " - " + cmd.getDescription());
                output.add("Usage: " + cmd.getUsage());
                output.add("");
                output.add("For detailed help, use: man " + commandName);
            } else {
                output.add("help: no help topics match '" + commandName + "'");
                output.add("Try 'help' to see available commands.");
            }
        }
        
        return output;
    }
    
    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
            "NAME",
            "    help - display help information",
            "",
            "SYNOPSIS",
            "    help [COMMAND]",
            "",
            "DESCRIPTION",
            "    Display helpful information about builtin commands.",
            "    If COMMAND is specified, gives detailed help on that command."
        );
    }
}