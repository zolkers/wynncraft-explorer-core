package com.edgn.core.minecraft.ui.screens.terminal.commands.unix;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;
import java.util.*;

public class SlCommand extends TerminalCommand {

    public SlCommand() {
        super("sl", "Steam Locomotive (You meant 'ls', right?)", "sl");
    }

    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();

        output.add("");
        output.add("🚂 CHOO CHOO! You typed 'sl' instead of 'ls'! 🚂");
        output.add("");

        String[] trainFrames = getTrainFrames();
        
        for (int i = 0; i < trainFrames.length; i++) {
            output.add("Frame " + (i + 1) + ":");
            output.add(trainFrames[i]);
            output.add("");
            
            if (i < trainFrames.length - 1) {
                output.add("🚂💨💨💨 CHOO CHOO! 💨💨💨🚂");
                output.add("");
            }
        }

        output.add("🎉 The train has passed! 🎉");
        output.add("");
        output.add("💡 Pro tip: Next time, type 'ls' to list files!");
        output.add("🔄 Or type 'ls' now to see your directory contents.");
        output.add("");
        
        String[] facts = getTrainFacts();
        output.add("🚂 Random train fact:");
        output.add("   " + facts[new Random().nextInt(facts.length)]);
        output.add("");

        if (args.length > 0) {
            String arg = args[0].toLowerCase();
            switch (arg) {
                case "help":
                    output.add("🤔 Help for 'sl'? You probably meant 'ls --help'!");
                    break;
                case "fast":
                    output.add("🚄 SUPER FAST TRAIN MODE ACTIVATED!");
                    output.add("💨💨💨 WOOOOOOSH! 💨💨💨");
                    break;
                case "slow":
                    output.add("🐌 Slow train mode... chug... chug... chug...");
                    break;
                case "quiet":
                    output.add("🤫 (Silent train passes by...)");
                    return output;
                default:
                    output.add("🚂 Unknown train option: " + arg);
            }
            output.add("");
        }

        return output;
    }

    private String[] getTrainFrames() {
        return new String[] {
            "                                 🚂",
            "                               __|_|__",
            "                              |  📦  |",
            "                              |______|",
            "                               ◯    ◯",
            
            "                         🚂💨",
            "                       __|_|__",
            "                      |  📦  |  📦",
            "                      |______|____",
            "                       ◯    ◯   ◯",
            
            "                 🚂💨💨",
            "               __|_|__",
            "              |  📦  |  📦  |  📦",
            "              |______|____|____|",
            "               ◯    ◯    ◯   ◯",
            
            "         🚂💨💨💨",
            "       __|_|__",
            "      |  📦  |  📦  |  📦  |  📦",
            "      |______|____|____|____|",
            "       ◯    ◯    ◯   ◯   ◯",
            
            "   🚂💨💨💨💨",
            " __|_|__",
            "|  📦  |  📦  |  📦  |  📦  |  📦",
            "|______|____|____|____|____|",
            " ◯    ◯    ◯   ◯   ◯   ◯",
            
            "🚂💨💨💨💨💨",
            "The train disappears into the distance...",
            "💨💨💨💨💨💨💨💨💨💨💨💨"
        };
    }

    private String[] getTrainFacts() {
        return new String[] {
            "The first steam locomotive was built in 1804 by Richard Trevithick.",
            "Steam trains can reach speeds of over 200 km/h (125 mph).",
            "The famous 'sl' command was created to help people who mistype 'ls'.",
            "Steam locomotives were the primary form of railway transportation from 1804 to the 1950s.",
            "The largest steam locomotive ever built was the Union Pacific Big Boy.",
            "Steam trains played a crucial role in the Industrial Revolution.",
            "The sound 'choo choo' comes from the steam being released from the cylinders.",
            "Some heritage railways still operate steam trains for tourists today.",
            "The Orient Express was one of the most famous steam train services.",
            "Steam locomotives are about 6-10% efficient in converting fuel to useful work."
        };
    }

    @Override
    public List<String> getCompletions(TerminalContext context, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0];
            String[] options = {"help", "fast", "slow", "quiet"};
            
            for (String option : options) {
                if (option.startsWith(prefix)) {
                    completions.add(option);
                }
            }
        }
        
        Collections.sort(completions);
        return completions;
    }

    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
                "NAME", "    sl - Steam Locomotive (classic Unix easter egg)", "",
                "SYNOPSIS", "    sl [OPTION]", "",
                "DESCRIPTION", "    Display a steam locomotive. This command exists to gently remind",
                "    users who mistype 'ls' as 'sl'. A classic Unix tradition!",
                "",
                "OPTIONS",
                "    help     Show this help (but you probably meant 'ls --help')",
                "    fast     Super fast train mode",
                "    slow     Slow train mode", 
                "    quiet    Silent train mode",
                "",
                "HISTORY", "    The 'sl' command is a famous Unix easter egg created to help",
                "    users who accidentally type 'sl' instead of 'ls'. It displays",
                "    an ASCII art steam locomotive animation.",
                "",
                "SEE ALSO", "    ls(1) - the command you probably meant to type"
        );
    }
}