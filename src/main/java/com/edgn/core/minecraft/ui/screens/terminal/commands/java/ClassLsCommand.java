package com.edgn.core.minecraft.ui.screens.terminal.commands.java;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;
import java.util.*;

public class ClassLsCommand extends TerminalCommand {

    public ClassLsCommand() {
        super("classls", "List available classes", "classls [PACKAGE_PREFIX]");
    }

    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();

        String packagePrefix = args.length > 0 ? args[0] : null;
        
        output.add("Scanning available classes" + (packagePrefix != null ? " in package: " + packagePrefix : "") + "...");
        
        List<String> classes = context.getAvailableClasses(packagePrefix);
        
        if (classes.isEmpty()) {
            output.add("No classes found");
        } else {
            output.add("Found " + classes.size() + " classes:");
            
            // Grouper par package
            Map<String, List<String>> packageGroups = new HashMap<>();
            
            for (String className : classes) {
                String packageName = className.contains(".") ? 
                    className.substring(0, className.lastIndexOf('.')) : "";
                    
                packageGroups.computeIfAbsent(packageName, k -> new ArrayList<>())
                    .add(className.substring(className.lastIndexOf('.') + 1));
            }
            
            // Afficher groupé par package
            for (Map.Entry<String, List<String>> entry : packageGroups.entrySet()) {
                output.add("");
                output.add("Package: " + (entry.getKey().isEmpty() ? "(default)" : entry.getKey()));
                
                Collections.sort(entry.getValue());
                for (String simpleClassName : entry.getValue()) {
                    output.add("  " + simpleClassName);
                }
            }
        }

        return output;
    }

    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
                "NAME", "    classls - list available classes", "",
                "SYNOPSIS", "    classls [PACKAGE_PREFIX]", "",
                "DESCRIPTION", "    List all available classes in the classpath.",
                "    Optionally filter by package prefix."
        );
    }
}