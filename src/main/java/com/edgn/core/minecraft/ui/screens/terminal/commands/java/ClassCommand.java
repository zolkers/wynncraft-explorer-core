package com.edgn.core.minecraft.ui.screens.terminal.commands.java;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;
import java.util.*;

public class ClassCommand extends TerminalCommand {

    public ClassCommand() {
        super("class", "Switch to class exploration mode", "class [CLASS_NAME]");
    }

    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();

        if (args.length == 0) {
            output.add("Loaded classes:");
            for (Map.Entry<String, Class<?>> entry : context.getLoadedClasses().entrySet()) {
                output.add("  " + entry.getKey() + " -> " + entry.getValue().getName());
            }
            output.add("");
            output.add("Use 'class <ClassName>' to inspect a class");
            output.add("Use 'classls [package]' to list available classes");
        } else {
            String className = args[0];
            
            Class<?> clazz = context.getLoadedClasses().get(className);
            if (clazz == null) {
                clazz = context.loadClass(className);
            }
            
            if (clazz != null) {
                context.setCurrentClass(clazz);
                output.add("Switched to class: " + clazz.getName());
                output.add("Use 'inspect' to see class members");
                output.add("Use 'instance' to get/create an instance");
            } else {
                output.add("Class not found: " + className);
                output.add("Use 'classls' to see available classes");
            }
        }

        return output;
    }

    @Override
    public List<String> getCompletions(TerminalContext context, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0];
            
            for (String className : context.getLoadedClasses().keySet()) {
                if (className.toLowerCase().startsWith(prefix.toLowerCase())) {
                    completions.add(className);
                }
            }
        }
        
        return completions;
    }

    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
                "NAME", "    class - switch to class exploration mode", "",
                "SYNOPSIS", "    class [CLASS_NAME]", "",
                "DESCRIPTION", "    Switch to class exploration mode to inspect Java classes.",
                "    Without arguments, shows loaded classes.",
                "    With CLASS_NAME, switches to that class for inspection."
        );
    }
}
