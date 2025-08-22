package com.edgn.core.minecraft.ui.screens.terminal.commands.java;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;
import java.util.*;

public class BackCommand extends TerminalCommand {

    public BackCommand() {
        super("back", "Return to previous class/instance or file mode", "back");
    }

    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();

        String prevClass = context.getEnvironment().get("PREV_CLASS");
        
        if (prevClass != null) {
            try {
                Class<?> clazz = Class.forName(prevClass);
                context.setCurrentClass(clazz);
                context.setCurrentInstance(null);
                
                context.getEnvironment().remove("PREV_CLASS");
                context.getEnvironment().remove("PREV_INSTANCE");
                
                output.add("Returned to class: " + clazz.getName());
                output.add("Instance cleared - use 'instance get' if needed");
                
            } catch (ClassNotFoundException e) {
                output.add("Could not return to previous class: " + e.getMessage());
            }
        } else if (context.getCurrentNamespace().equals("class")) {
            context.setCurrentNamespace("file");
            context.setCurrentClass(null);
            context.setCurrentInstance(null);
            
            output.add("Returned to file system mode");
            output.add("Current directory: " + context.getCurrentDirectory());
        } else {
            output.add("Nothing to go back to");
        }

        return output;
    }

    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
                "NAME", "    back - return to previous context", "",
                "SYNOPSIS", "    back", "",
                "DESCRIPTION", "    Return to the previous class/instance or switch back to file mode."
        );
    }
}