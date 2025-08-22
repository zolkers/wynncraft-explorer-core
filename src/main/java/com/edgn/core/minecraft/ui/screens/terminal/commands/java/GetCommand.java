package com.edgn.core.minecraft.ui.screens.terminal.commands.java;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;
import java.lang.reflect.*;
import java.util.*;

public class GetCommand extends TerminalCommand {

    public GetCommand() {
        super("get", "Get value of a field", "get <FIELD_NAME>");
    }

    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();

        if (args.length == 0) {
            output.add("Usage: get <field_name>");
            return output;
        }

        Class<?> currentClass = context.getCurrentClass();
        Object currentInstance = context.getCurrentInstance();

        if (currentClass == null) {
            output.add("No current class. Use 'class <ClassName>' first.");
            return output;
        }

        String fieldName = args[0];

        try {
            Field field = findField(currentClass, fieldName);
            if (field == null) {
                output.add("Field '" + fieldName + "' not found");
                return output;
            }

            field.setAccessible(true);

            boolean isStatic = Modifier.isStatic(field.getModifiers());
            if (!isStatic && currentInstance == null) {
                output.add("Field is not static and no instance available. Use 'instance get' first.");
                return output;
            }

            Object value = field.get(isStatic ? null : currentInstance);

            output.add("Field: " + field.getName());
            output.add("Type: " + field.getType().getName());
            output.add("Modifiers: " + Modifier.toString(field.getModifiers()));
            output.add("Value: " + (value != null ? value.toString() : "null"));
            
            if (value != null) {
                output.add("Value type: " + value.getClass().getName());
                
                if (!isPrimitiveOrWrapper(value.getClass()) && !value.getClass().equals(String.class)) {
                    output.add("");
                    output.add("This is a complex object. Use 'explore " + fieldName + "' to inspect it further.");
                }
            }

        } catch (Exception e) {
            output.add("Error accessing field: " + e.getMessage());
        }

        return output;
    }

    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || 
               clazz.equals(Boolean.class) || clazz.equals(Integer.class) || 
               clazz.equals(Character.class) || clazz.equals(Byte.class) ||
               clazz.equals(Short.class) || clazz.equals(Double.class) || 
               clazz.equals(Long.class) || clazz.equals(Float.class);
    }

    @Override
    public List<String> getCompletions(TerminalContext context, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length <= 1 && context.getCurrentClass() != null) {
            String prefix = args.length == 0 ? "" : args[0];
            
            Class<?> current = context.getCurrentClass();
            while (current != null) {
                for (Field field : current.getDeclaredFields()) {
                    if (field.getName().startsWith(prefix)) {
                        completions.add(field.getName());
                    }
                }
                current = current.getSuperclass();
            }
        }
        
        return completions;
    }

    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
                "NAME", "    get - get value of a field", "",
                "SYNOPSIS", "    get <FIELD_NAME>", "",
                "DESCRIPTION", "    Get the value of a field from the current instance or static field.",
                "    Works with fields from current class and all superclasses."
        );
    }
}