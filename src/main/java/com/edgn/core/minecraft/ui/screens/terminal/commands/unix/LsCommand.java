package com.edgn.core.minecraft.ui.screens.terminal.commands.unix;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class LsCommand extends TerminalCommand {

    public LsCommand() {
        super("ls", "List directory contents", "ls [OPTION]... [FILE]...", "dir");
    }

    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();

        boolean longFormat = hasFlag(args, "-l");
        boolean showHidden = hasFlag(args, "-a");
        boolean humanReadable = hasFlag(args, "-h");

        String[] paths = removeFlags(args);
        String targetPath = paths.length > 0 ? paths[0] : ".";

        List<String> entries = getDirectoryEntries(context, targetPath, false);
        if (entries.isEmpty() && !targetPath.equals(".")) {
            output.add("ls: cannot access '" + targetPath + "': No such file or directory");
            return output;
        }

        if (!showHidden) {
            entries = entries.stream()
                    .filter(name -> !name.startsWith("."))
                    .collect(Collectors.toList());
        }

        Collections.sort(entries);

        if (longFormat) {
            output.add("total " + entries.size());
            for (String entry : entries) {
                String line = formatLongEntry(context, targetPath, entry, humanReadable);
                output.add(line);
            }
        } else {
            StringBuilder line = new StringBuilder();
            for (String entry : entries) {
                boolean isDir = isDirectory(context, targetPath, entry);
                String displayName = entry + (isDir ? "/" : "");

                if (line.length() + displayName.length() > 80) {
                    output.add(line.toString());
                    line = new StringBuilder();
                }
                line.append(String.format("%-20s", displayName));
            }
            if (!line.isEmpty()) {
                output.add(line.toString());
            }
        }

        return output;
    }

    private String formatLongEntry(TerminalContext context, String parentPath, String entryName, boolean humanReadable) {
        try {
            String fullPath = resolvePath(context, parentPath);
            if (!fullPath.equals("/")) {
                fullPath += "/";
            }
            fullPath += entryName;

            Path realPath = context.getRealPath(fullPath);

            if (Files.exists(realPath)) {
                BasicFileAttributes attrs = Files.readAttributes(realPath, BasicFileAttributes.class);
                boolean isDir = Files.isDirectory(realPath);

                String permissions = isDir ? "drwxr-xr-x" : "-rw-r--r--";

                long size = attrs.size();
                String sizeStr;
                if (humanReadable) {
                    sizeStr = formatHumanReadableSize(size);
                } else {
                    sizeStr = String.valueOf(size);
                }

                String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd HH:mm"));

                return String.format("%s 1 %s %s %8s %s %s%s",
                        permissions,
                        context.getEnvironment().get("USER"),
                        context.getEnvironment().get("USER"),
                        sizeStr,
                        date,
                        entryName,
                        isDir ? "/" : "");
            }
        } catch (Exception ignored) {
        }

        boolean isDir = isDirectory(context, parentPath, entryName);
        String permissions = isDir ? "drwxr-xr-x" : "-rw-r--r--";
        String size = humanReadable ? "4.0K" : "4096";
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd HH:mm"));

        return String.format("%s 1 %s %s %8s %s %s%s",
                permissions,
                context.getEnvironment().get("USER"),
                context.getEnvironment().get("USER"),
                size,
                date,
                entryName,
                isDir ? "/" : "");
    }

    private String formatHumanReadableSize(long bytes) {
        if (bytes < 1024) return bytes + "B";
        if (bytes < 1024 * 1024) return String.format("%.1fK", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1fM", bytes / (1024.0 * 1024));
        return String.format("%.1fG", bytes / (1024.0 * 1024 * 1024));
    }

    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
                "NAME", "    ls - list directory contents", "",
                "SYNOPSIS", "    ls [OPTION]... [FILE]...", "",
                "DESCRIPTION", "    List information about files and directories.",
                "",
                "OPTIONS",
                "    -a     do not ignore entries starting with .",
                "    -l     use a long listing format",
                "    -h     with -l, print sizes in human readable format"
        );
    }
}