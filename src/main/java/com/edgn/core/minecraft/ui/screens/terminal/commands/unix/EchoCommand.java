package com.edgn.core.minecraft.ui.screens.terminal.commands.unix;


import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;
import net.minecraft.text.Text;

import java.util.*;

public class EchoCommand extends TerminalCommand {
    
    public EchoCommand() {
        super("echo", 
              "Display a line of text", 
              "echo [SHORT-OPTION]... [STRING]...");
    }
    
    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();
        
        boolean noNewline = hasFlag(args, "-n");
        boolean enableEscapes = hasFlag(args, "-e");
        
        String[] textArgs = removeFlags(args);
        String text = String.join(" ", textArgs);
        
        if (enableEscapes) {
            text = processEscapes(text);
        }
        
        if (noNewline) {
            output.add(text + " (no newline)");
        } else {
            output.add(text);
        }

        client.inGameHud.getChatHud().addMessage(Text.of(text));

        return output;
    }
    
    private String processEscapes(String text) {
        return text.replace("\\n", "\n")
                  .replace("\\t", "\t")
                  .replace("\\r", "\r")
                  .replace("\\\\", "\\")
                  .replace("\\\"", "\"");
    }
    
    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
            "NAME",
            "    echo - display a line of text, it will also echo the message in game",
            "",
            "SYNOPSIS",
            "    echo [SHORT-OPTION]... [STRING]...",
            "",
            "DESCRIPTION",
            "    Echo the STRINGs to standard output.",
            "",
            "OPTIONS",
            "    -n",
            "        do not output the trailing newline",
            "",
            "    -e",
            "        enable interpretation of backslash escapes",
            "",
            "ESCAPE SEQUENCES",
            "    \\n     new line",
            "    \\t     horizontal tab",
            "    \\r     carriage return",
            "    \\\\     backslash"
        );
    }
}