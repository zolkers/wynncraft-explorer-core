package com.edgn.core.minecraft.ui.screens.terminal.commands.java;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;
import java.util.*;

public class InstanceCommand extends TerminalCommand {

    public InstanceCommand() {
        super("instance", "Get or create an instance of current class", "instance [get|new]");
    }

    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();

        Class<?> currentClass = context.getCurrentClass();
        if (currentClass == null) {
            output.add("No current class. Use 'class <ClassName>' first.");
            return output;
        }

        String action = args.length > 0 ? args[0] : "get";

        switch (action.toLowerCase()) {
            case "get":
                Object instance = context.getInstanceOf(currentClass);
                if (instance != null) {
                    context.setCurrentInstance(instance);
                    output.add("Instance obtained: " + instance.getClass().getName() + "@" + Integer.toHexString(instance.hashCode()));
                    output.add("Instance type: " + instance.getClass().getName());
                } else {
                    output.add("Could not obtain instance of " + currentClass.getName());
                    output.add("Try 'instance new' to create a new instance");
                }
                break;

            case "new":
                try {
                    Object newInstance = currentClass.getDeclaredConstructor().newInstance();
                    context.setCurrentInstance(newInstance);
                    output.add("New instance created: " + newInstance.getClass().getName() + "@" + Integer.toHexString(newInstance.hashCode()));
                } catch (Exception e) {
                    output.add("Could not create new instance: " + e.getMessage());
                }
                break;

            case "clear":
                context.setCurrentInstance(null);
                output.add("Instance cleared");
                break;

            case "info":
                Object current = context.getCurrentInstance();
                if (current != null) {
                    output.add("Current instance: " + current.getClass().getName() + "@" + Integer.toHexString(current.hashCode()));
                    output.add("Class: " + current.getClass().getName());
                    output.add("toString(): " + current);
                } else {
                    output.add("No current instance");
                }
                break;

            default:
                output.add("Unknown action: " + action);
                output.add("Available actions: get, new, clear, info");
        }

        return output;
    }

    @Override
    public List<String> getCompletions(TerminalContext context, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0];
            
            String[] actions = {"get", "new", "clear", "info"};
            for (String action : actions) {
                if (action.startsWith(prefix)) {
                    completions.add(action);
                }
            }
        }
        
        return completions;
    }

    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
                "NAME", "    instance - manage instances of current class", "",
                "SYNOPSIS", "    instance [ACTION]", "",
                "DESCRIPTION", "    Manage instances of the current class.",
                "",
                "ACTIONS",
                "    get      Try to get singleton instance (default)",
                "    new      Create new instance with default constructor",
                "    clear    Clear current instance",
                "    info     Show current instance information"
        );
    }
}
