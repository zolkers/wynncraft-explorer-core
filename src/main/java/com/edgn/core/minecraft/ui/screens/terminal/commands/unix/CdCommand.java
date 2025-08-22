package com.edgn.core.minecraft.ui.screens.terminal.commands.unix;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;
import java.util.*;

public class CdCommand extends TerminalCommand {

    public CdCommand() {
        super("cd", "Change the shell working directory", "cd [DIRECTORY]");
    }

    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();

        String targetDir;
        if (args.length == 0) {
            targetDir = context.getEnvironment().get("HOME");
        } else {
            targetDir = args[0];
        }

        String currentDir = context.getCurrentDirectory();
        String resolvedPath = resolvePath(context, targetDir);

        if (directoryExists(context, resolvedPath)) {
            context.getEnvironment().put("OLDPWD", currentDir);
            context.setCurrentDirectory(resolvedPath);
        } else {
            output.add("cd: " + (args.length > 0 ? args[0] : "~") + ": No such file or directory");
        }

        return output;
    }

    @Override
    public List<String> getCompletions(TerminalContext context, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0];

            List<String> directories = getDirectoryEntries(context, ".", true);

            for (String dir : directories) {
                if (dir.startsWith(prefix)) {
                    completions.add(dir);
                }
            }

            if ("..".startsWith(prefix)) completions.add("..");
            if (".".startsWith(prefix)) completions.add(".");
            if ("~".startsWith(prefix)) completions.add("~");
            if ("-".startsWith(prefix)) completions.add("-");
        }

        Collections.sort(completions);
        return completions;
    }

    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
                "NAME", "    cd - change the shell working directory", "",
                "SYNOPSIS", "    cd [DIRECTORY]", "",
                "DESCRIPTION", "    Change the current directory to DIRECTORY.",
                "",
                "SPECIAL DIRECTORIES",
                "    ~      Home directory (root of Fabric directory)",
                "    -      Previous directory (OLDPWD)",
                "    ..     Parent directory",
                "    .      Current directory"
        );
    }
}