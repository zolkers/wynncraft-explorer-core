package com.edgn.core.minecraft.ui.screens.terminal.commands.unix;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;

import java.util.*;

public class WhoamiCommand extends TerminalCommand {
    
    public WhoamiCommand() {
        super("whoami", 
              "Print effective userid", 
              "whoami [OPTION]...");
    }
    
    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();
        String user = context.getEnvironment().get("USER");
        output.add(user != null ? user : "minecraft");
        return output;
    }
    
    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
            "NAME",
            "    whoami - print effective userid",
            "",
            "SYNOPSIS",
            "    whoami [OPTION]...",
            "",
            "DESCRIPTION",
            "    Print the user name associated with the current effective user ID.",
            "    Same as id -un."
        );
    }
}