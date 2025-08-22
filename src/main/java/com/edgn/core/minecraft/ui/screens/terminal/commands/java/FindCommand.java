package com.edgn.core.minecraft.ui.screens.terminal.commands.java;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class FindCommand extends TerminalCommand {

    public FindCommand() {
        super("find", "Find classes, methods, or fields", "find <TYPE> <PATTERN>");
    }

    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();

        if (args.length < 2) {
            output.add("Usage: find <type> <pattern>");
            output.add("Types: class, method, field");
            output.add("Pattern: search pattern (case insensitive)");
            return output;
        }

        String type = args[0].toLowerCase();
        String pattern = args[1].toLowerCase();

        switch (type) {
            case "class":
                findClasses(context, pattern, output);
                break;
            case "method":
                findMethods(context, pattern, output);
                break;
            case "field":
                findFields(context, pattern, output);
                break;
            default:
                output.add("Unknown type: " + type);
                output.add("Available types: class, method, field");
        }

        return output;
    }

    private void findClasses(TerminalContext context, String pattern, List<String> output) {
        output.add("Searching for classes containing '" + pattern + "'...");
        
        List<String> allClasses = context.getAvailableClasses(null);
        List<String> matches = allClasses.stream()
            .filter(className -> className.toLowerCase().contains(pattern))
            .sorted()
            .toList();

        if (matches.isEmpty()) {
            output.add("No classes found matching '" + pattern + "'");
        } else {
            output.add("Found " + matches.size() + " classes:");
            for (String className : matches) {
                output.add("  " + className);
            }
        }
    }

    private void findMethods(TerminalContext context, String pattern, List<String> output) {
        if (context.getCurrentClass() == null) {
            output.add("No current class. Use 'class <ClassName>' first, or search in all loaded classes with 'find method-all <pattern>'");
            return;
        }

        output.add("Searching for methods containing '" + pattern + "' in " + context.getCurrentClass().getSimpleName() + "...");
        
        List<Method> matches = Arrays.stream(context.getCurrentClass().getDeclaredMethods())
            .filter(method -> method.getName().toLowerCase().contains(pattern))
            .sorted(Comparator.comparing(Method::getName))
            .toList();

        if (matches.isEmpty()) {
            output.add("No methods found matching '" + pattern + "'");
        } else {
            output.add("Found " + matches.size() + " methods:");
            for (Method method : matches) {
                String modifiers = Modifier.toString(method.getModifiers());
                String returnType = method.getReturnType().getSimpleName();
                String params = Arrays.stream(method.getParameterTypes())
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(", "));
                
                output.add("  " + modifiers + " " + returnType + " " + method.getName() + "(" + params + ")");
            }
        }
    }

    private void findFields(TerminalContext context, String pattern, List<String> output) {
        if (context.getCurrentClass() == null) {
            output.add("No current class. Use 'class <ClassName>' first.");
            return;
        }

        output.add("Searching for fields containing '" + pattern + "' in " + context.getCurrentClass().getSimpleName() + "...");
        
        List<Field> matches = Arrays.stream(context.getCurrentClass().getDeclaredFields())
            .filter(field -> field.getName().toLowerCase().contains(pattern))
            .sorted(Comparator.comparing(Field::getName))
            .toList();

        if (matches.isEmpty()) {
            output.add("No fields found matching '" + pattern + "'");
        } else {
            output.add("Found " + matches.size() + " fields:");
            for (Field field : matches) {
                String modifiers = Modifier.toString(field.getModifiers());
                String type = field.getType().getSimpleName();
                output.add("  " + modifiers + " " + type + " " + field.getName());
            }
        }
    }

    @Override
    public List<String> getCompletions(TerminalContext context, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0];
            String[] types = {"class", "method", "field"};
            
            for (String type : types) {
                if (type.startsWith(prefix)) {
                    completions.add(type);
                }
            }
        }
        
        return completions;
    }

    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
                "NAME", "    find - find classes, methods, or fields", "",
                "SYNOPSIS", "    find <TYPE> <PATTERN>", "",
                "DESCRIPTION", "    Search for classes, methods, or fields by name pattern.",
                "",
                "TYPES",
                "    class     Search in all available classes",
                "    method    Search methods in current class",
                "    field     Search fields in current class",
                "",
                "EXAMPLES",
                "    find class Client",
                "    find method get",
                "    find field name"
        );
    }
}