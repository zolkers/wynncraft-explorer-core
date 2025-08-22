package com.edgn.core.minecraft.ui.screens.terminal.commands.unix;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;

import java.util.*;

public class ClearCommand extends TerminalCommand {
    
    public ClearCommand() {
        super("clear", 
              "Clear the terminal screen", 
              "clear");
    }
    
    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        return List.of("__CLEAR_SCREEN__");
    }
    
    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
            "NAME",
            "    clear - clear the terminal screen",
            "",
            "SYNOPSIS",
            "    clear",
            "",
            "DESCRIPTION",
            "    Clear clears your screen if this is possible, including its",
            "    scrollback buffer."
        );
    }
}