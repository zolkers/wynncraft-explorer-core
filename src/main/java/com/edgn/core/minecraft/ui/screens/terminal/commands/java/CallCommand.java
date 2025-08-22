package com.edgn.core.minecraft.ui.screens.terminal.commands.java;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;
import java.lang.reflect.*;
import java.util.*;

public class CallCommand extends TerminalCommand {

    public CallCommand() {
        super("call", "Call a method on current instance or class", "call <METHOD_NAME> [ARGS...]");
    }

    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();

        if (args.length == 0) {
            output.add("Usage: call <method_name> [args...]");
            return output;
        }

        Class<?> currentClass = context.getCurrentClass();
        Object currentInstance = context.getCurrentInstance();

        if (currentClass == null) {
            output.add("No current class. Use 'class <ClassName>' first.");
            return output;
        }

        String methodName = args[0];
        String[] methodArgs = Arrays.copyOfRange(args, 1, args.length);

        Method[] methods = currentClass.getDeclaredMethods();
        List<Method> candidates = new ArrayList<>();

        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                candidates.add(method);
            }
        }

        if (candidates.isEmpty()) {
            output.add("Method '" + methodName + "' not found");
            return output;
        }

        Method targetMethod = null;
        for (Method method : candidates) {
            if (method.getParameterCount() == methodArgs.length) {
                targetMethod = method;
                break;
            }
        }

        if (targetMethod == null) {
            output.add("No method '" + methodName + "' found with " + methodArgs.length + " parameters");
            output.add("Available overloads:");
            for (Method method : candidates) {
                output.add("  " + method.getName() + "(" + method.getParameterCount() + " params): " + 
                    Arrays.toString(method.getParameterTypes()));
            }
            return output;
        }

        boolean isStatic = Modifier.isStatic(targetMethod.getModifiers());
        if (!isStatic && currentInstance == null) {
            output.add("Method is not static and no instance available. Use 'instance get' first.");
            return output;
        }

        try {
            targetMethod.setAccessible(true);

            Object[] convertedArgs = convertArguments(methodArgs, targetMethod.getParameterTypes());

            Object result = targetMethod.invoke(isStatic ? null : currentInstance, convertedArgs);

            output.add("Method called successfully");
            output.add("Return type: " + targetMethod.getReturnType().getName());
            if (targetMethod.getReturnType() != void.class) {
                output.add("Result: " + (result != null ? result.toString() : "null"));
                if (result != null) {
                    output.add("Result type: " + result.getClass().getName());
                }
            }

        } catch (Exception e) {
            output.add("Error calling method: " + e.getMessage());
            if (e.getCause() != null) {
                output.add("Cause: " + e.getCause().getMessage());
            }
        }

        return output;
    }

    private Object[] convertArguments(String[] args, Class<?>[] paramTypes)  {
        Object[] converted = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            Class<?> paramType = paramTypes[i];

            if (paramType == String.class) {
                converted[i] = arg;
            } else if (paramType == int.class || paramType == Integer.class) {
                converted[i] = Integer.parseInt(arg);
            } else if (paramType == long.class || paramType == Long.class) {
                converted[i] = Long.parseLong(arg);
            } else if (paramType == float.class || paramType == Float.class) {
                converted[i] = Float.parseFloat(arg);
            } else if (paramType == double.class || paramType == Double.class) {
                converted[i] = Double.parseDouble(arg);
            } else if (paramType == boolean.class || paramType == Boolean.class) {
                converted[i] = Boolean.parseBoolean(arg);
            } else if (paramType == char.class || paramType == Character.class) {
                converted[i] = arg.charAt(0);
            } else {
                if (arg.equals("null")) {
                    converted[i] = null;
                } else {
                    throw new IllegalArgumentException("Cannot convert '" + arg + "' to " + paramType.getName());
                }
            }
        }

        return converted;
    }

    @Override
    public List<String> getCompletions(TerminalContext context, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length <= 1 && context.getCurrentClass() != null) {
            String prefix = args.length == 0 ? "" : args[0];
            
            for (Method method : context.getCurrentClass().getDeclaredMethods()) {
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
                "NAME", "    call - call a method on current instance or class", "",
                "SYNOPSIS", "    call <METHOD_NAME> [ARGS...]", "",
                "DESCRIPTION", "    Call a method on the current instance or static method on current class.",
                "    Arguments are automatically converted to appropriate types.",
                "",
                "SUPPORTED ARGUMENT TYPES",
                "    String, int, long, float, double, boolean, char",
                "    Use 'null' for null values"
        );
    }
}
