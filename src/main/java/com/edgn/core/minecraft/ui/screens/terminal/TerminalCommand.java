package com.edgn.core.minecraft.ui.screens.terminal;

import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Files;

public abstract class TerminalCommand {
    protected final String name;
    protected final String description;
    protected final String usage;
    protected final String[] aliases;
    protected final MinecraftClient client = MinecraftClient.getInstance();

    public TerminalCommand(String name, String description, String usage, String... aliases) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
    }

    public abstract List<String> execute(TerminalContext context, String[] args);
    public abstract List<String> getDetailedHelp();

    public List<String> getCompletions(TerminalContext context, String[] args) {
        return new ArrayList<>();
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getUsage() { return usage; }
    public String[] getAliases() { return aliases; }

    protected boolean hasFlag(String[] args, String flag) {
        for (String arg : args) {
            if (arg.equals(flag)) return true;
        }
        return false;
    }

    protected String[] removeFlags(String[] args) {
        return java.util.Arrays.stream(args)
                .filter(arg -> !arg.startsWith("-"))
                .toArray(String[]::new);
    }

    protected List<String> getDirectoryEntries(TerminalContext context, String path, boolean directoriesOnly) {
        String resolvedPath = resolvePath(context, path);
        return context.getRealDirectoryEntries(resolvedPath, directoriesOnly);
    }

    protected boolean directoryExists(TerminalContext context, String path) {
        String resolvedPath = resolvePath(context, path);
        return context.realDirectoryExists(resolvedPath);
    }

    protected boolean isDirectory(TerminalContext context, String parentPath, String entryName) {
        try {
            String fullPath = resolvePath(context, parentPath);
            if (!fullPath.equals("/")) {
                fullPath += "/";
            }
            fullPath += entryName;

            Path realPath = context.getRealPath(fullPath);
            return Files.exists(realPath) && Files.isDirectory(realPath);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Résout un chemin (relatif vers absolu, gère ~, ., ..)
     */
    protected String resolvePath(TerminalContext context, String path) {
        if (path.equals("~")) {
            return context.getEnvironment().get("HOME");
        } else if (path.equals(".")) {
            return context.getCurrentDirectory();
        } else if (path.equals("..")) {
            String current = context.getCurrentDirectory();
            if (!current.equals("/")) {
                int lastSlash = current.lastIndexOf('/');
                return lastSlash <= 0 ? "/" : current.substring(0, lastSlash);
            } else {
                return "/";
            }
        } else if (path.equals("-")) {
            return context.getEnvironment().getOrDefault("OLDPWD", context.getCurrentDirectory());
        } else if (!path.startsWith("/")) {
            // Chemin relatif
            String current = context.getCurrentDirectory();
            if (current.equals("/")) {
                return "/" + path;
            } else {
                return current + "/" + path;
            }
        }

        return normalizePath(path);
    }

    /**
     * Normalise un chemin
     */
    protected String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }

        path = path.replaceAll("/+", "/");

        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }
}