package com.edgn.core.minecraft.ui.screens.terminal.commands.unix;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;

import java.util.*;

public class PwdCommand extends TerminalCommand {
    
    public PwdCommand() {
        super("pwd", 
              "Print name of current/working directory", 
              "pwd [OPTION]...");
    }
    
    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();

        
        String currentDir = context.getCurrentDirectory();
        output.add(currentDir);
        
        return output;
    }
    
    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
            "NAME",
            "    pwd - print name of current/working directory",
            "",
            "SYNOPSIS",
            "    pwd [OPTION]...",
            "",
            "DESCRIPTION",
            "    Print the full filename of the current working directory.",
            ""
        );
    }
}