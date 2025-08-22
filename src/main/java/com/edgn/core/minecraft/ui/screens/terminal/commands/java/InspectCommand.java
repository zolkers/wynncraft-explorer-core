package com.edgn.core.minecraft.ui.screens.terminal.commands.java;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;
import java.lang.reflect.*;
import java.util.*;

public class InspectCommand extends TerminalCommand {

    public InspectCommand() {
        super("inspect", "Inspect current class or object", "inspect [MEMBER_NAME]");
    }

    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();

        Class<?> currentClass = context.getCurrentClass();
        Object currentInstance = context.getCurrentInstance();

        if (currentClass == null) {
            output.add("No current class. Use 'class <ClassName>' first.");
            return output;
        }

        if (args.length == 0) {
            
            output.add("Class: " + currentClass.getName());
            output.add("Package: " + currentClass.getPackage().getName());
            output.add("Superclass: " + (currentClass.getSuperclass() != null ? currentClass.getSuperclass().getName() : "None"));
            output.add("Instance: " + (currentInstance != null ? "Available" : "None"));
            output.add("");

            
            Class<?>[] interfaces = currentClass.getInterfaces();
            if (interfaces.length > 0) {
                output.add("Interfaces:");
                for (Class<?> iface : interfaces) {
                    output.add("  " + iface.getName());
                }
                output.add("");
            }

            
            output.add("Fields:");
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                String modifiers = Modifier.toString(field.getModifiers());
                String type = field.getType().getSimpleName();
                output.add("  " + modifiers + " " + type + " " + field.getName());
            }
            output.add("");

            
            output.add("Methods:");
            Method[] methods = currentClass.getDeclaredMethods();
            for (Method method : methods) {
                String modifiers = Modifier.toString(method.getModifiers());
                String returnType = method.getReturnType().getSimpleName();
                String params = Arrays.toString(method.getParameterTypes()).replaceAll("class ", "");
                output.add("  " + modifiers + " " + returnType + " " + method.getName() + params);
            }

        } else {
            
            String memberName = args[0];
            
            
            try {
                Field field = currentClass.getDeclaredField(memberName);
                field.setAccessible(true);
                
                output.add("Field: " + field.getName());
                output.add("Type: " + field.getType().getName());
                output.add("Modifiers: " + Modifier.toString(field.getModifiers()));
                
                if (currentInstance != null || Modifier.isStatic(field.getModifiers())) {
                    try {
                        Object value = field.get(currentInstance);
                        output.add("Value: " + (value != null ? value.toString() : "null"));
                        output.add("Value type: " + (value != null ? value.getClass().getName() : "null"));
                    } catch (Exception e) {
                        output.add("Could not access value: " + e.getMessage());
                    }
                } else {
                    output.add("Value: (no instance available)");
                }
                
                return output;
            } catch (NoSuchFieldException ignored) {}
            
            
            Method[] methods = currentClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(memberName)) {
                    output.add("Method: " + method.getName());
                    output.add("Return type: " + method.getReturnType().getName());
                    output.add("Modifiers: " + Modifier.toString(method.getModifiers()));
                    output.add("Parameters: " + Arrays.toString(method.getParameterTypes()));
                    return output;
                }
            }
            
            output.add("Member '" + memberName + "' not found");
        }

        return output;
    }

    @Override
    public List<String> getCompletions(TerminalContext context, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length <= 1 && context.getCurrentClass() != null) {
            String prefix = args.length == 0 ? "" : args[0];
            Class<?> clazz = context.getCurrentClass();
            
            
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().startsWith(prefix)) {
                    completions.add(field.getName());
                }
            }
            
            
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().startsWith(prefix)) {
                    completions.add(method.getName());
                }
            }
        }
        
        return completions;
    }

    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
                "NAME", "    inspect - inspect current class or object", "",
                "SYNOPSIS", "    inspect [MEMBER_NAME]", "",
                "DESCRIPTION", "    Inspect the current class structure and members.",
                "    Without arguments, shows complete class information.",
                "    With MEMBER_NAME, shows detailed info about that field or method."
        );
    }
}