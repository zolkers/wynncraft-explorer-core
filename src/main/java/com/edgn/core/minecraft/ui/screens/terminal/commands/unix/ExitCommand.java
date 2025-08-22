package com.edgn.core.minecraft.ui.screens.terminal.commands.unix;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;

import java.util.*;

public class ExitCommand extends TerminalCommand {
    
    public ExitCommand() {
        super("exit", 
              "Exit the shell", 
              "exit [n]",
              "logout");
    }
    
    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();
        
        int exitCode = 0;
        if (args.length > 0) {
            try {
                exitCode = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                output.add("exit: " + args[0] + ": numeric argument required");
                return output;
            }
        }
        
        output.add("logout");
        output.add(exitCode + "");
        output.add("__EXIT_TERMINAL__");
        
        return output;
    }
    
    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
            "NAME",
            "    exit - exit the shell",
            "",
            "SYNOPSIS",
            "    exit [n]",
            "",
            "DESCRIPTION",
            "    Exit the shell with a status of n. If n is omitted, the exit status",
            "    is that of the last command executed."
        );
    }
}