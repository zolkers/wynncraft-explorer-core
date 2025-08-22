package com.edgn.core.minecraft.ui.screens.terminal.commands.unix;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DateCommand extends TerminalCommand {
    
    public DateCommand() {
        super("date", 
              "Display or set the system date", 
              "date [OPTION]... [+FORMAT]");
    }
    
    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        if (args.length == 0) {
            String defaultFormat = now.format(DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy"));
            output.add(defaultFormat);
        } else {
            String arg = args[0];
            if (arg.startsWith("+")) {
                String format = arg.substring(1);
                try {
                    format = format.replace("%Y", "yyyy")
                                  .replace("%m", "MM")
                                  .replace("%d", "dd")
                                  .replace("%H", "HH")
                                  .replace("%M", "mm")
                                  .replace("%S", "ss")
                                  .replace("%A", "EEEE")
                                  .replace("%B", "MMMM");
                    output.add(now.format(DateTimeFormatter.ofPattern(format)));
                } catch (Exception e) {
                    output.add("date: invalid date format");
                }
            } else if (arg.equals("-u") || arg.equals("--utc")) {
                output.add(now.format(DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'UTC' yyyy")));
            } else if (arg.equals("-I") || arg.equals("--iso-8601")) {
                output.add(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } else {
                output.add("date: invalid option -- '" + arg + "'");
            }
        }
        
        return output;
    }
    
    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
            "NAME",
            "    date - display or set the system date",
            "",
            "SYNOPSIS",
            "    date [OPTION]... [+FORMAT]",
            "",
            "DESCRIPTION",
            "    Display the current time in the given FORMAT, or set the system date.",
            "",
            "OPTIONS",
            "    -u, --utc, --universal",
            "        print or set Coordinated Universal Time (UTC)",
            "",
            "    -I[TIMESPEC], --iso-8601[=TIMESPEC]",
            "        output date/time in ISO 8601 format",
            "",
            "FORMAT",
            "    %Y   year (e.g., 2023)",
            "    %m   month (01..12)",
            "    %d   day of month (e.g., 01)",
            "    %H   hour (00..23)",
            "    %M   minute (00..59)",
            "    %S   second (00..60)"
        );
    }
}