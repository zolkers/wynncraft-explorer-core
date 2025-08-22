package com.edgn.core.minecraft.ui.screens.terminal.commands.unix;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;

import java.util.*;

public class ManCommand extends TerminalCommand {
    private final Map<String, TerminalCommand> commands;
    
    public ManCommand(Map<String, TerminalCommand> commands) {
        super("man", 
              "Display manual pages", 
              "man [OPTION]... [SECTION] PAGE...");
        this.commands = commands;
    }
    
    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();
        
        if (args.length == 0) {
            output.add("What manual page do you want?");
            output.add("For example, try 'man man'.");
            return output;
        }
        
        String commandName = args[0].toLowerCase();
        TerminalCommand command = commands.get(commandName);
        
        if (command != null) {
            output.add("MANUAL PAGE FOR " + commandName.toUpperCase() + "(1)");
            output.add("");
            output.addAll(command.getDetailedHelp());
            output.add("");
            output.add("SEE ALSO");
            output.add("    help(1), bash(1)");
        } else {
            output.add("No manual entry for " + commandName);
        }
        
        return output;
    }
    
    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
            "NAME",
            "    man - an interface to the on-line reference manuals",
            "",
            "SYNOPSIS",
            "    man [OPTION]... [SECTION] PAGE...",
            "",
            "DESCRIPTION",
            "    man is the system's manual pager. Each PAGE argument given to man",
            "    is normally the name of a program, utility or function."
        );
    }
}