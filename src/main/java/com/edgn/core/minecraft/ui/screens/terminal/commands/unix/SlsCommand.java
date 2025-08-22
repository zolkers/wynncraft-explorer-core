package com.edgn.core.minecraft.ui.screens.terminal.commands.unix;

import com.edgn.core.minecraft.ui.screens.terminal.TerminalCommand;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalContext;
import java.util.*;

public class SlsCommand extends TerminalCommand {

    public SlsCommand() {
        super("sls", "You really can't type 'ls' correctly, can you?", "sls");
    }

    @Override
    public List<String> execute(TerminalContext context, String[] args) {
        List<String> output = new ArrayList<>();

        output.add("");
        output.add("🤦‍♂️ SERIOUSLY?! You typed 'sls'?! 🤦‍♀️");
        output.add("");
        output.add("🚂🚂🚂 MULTIPLE TRAINS INCOMING! 🚂🚂🚂");
        output.add("");
        
        output.add("    🚂💨     🚂💨     🚂💨");
        output.add("   __|__    __|__    __|__");
        output.add("  |📦📦|  |📦📦|  |📦📦|");
        output.add("  |____|  |____|  |____|");
        output.add("   ◯  ◯    ◯  ◯    ◯  ◯");
        output.add("");
        output.add("💥 TRAIN COLLISION! CHOO CHOO CHAOS! 💥");
        output.add("");
        output.add("🆘 EMERGENCY SUGGESTION: Just type 'ls' next time! 🆘");
        output.add("");
        output.add("📚 Spelling lesson:");
        output.add("   L - Lima");
        output.add("   S - Sierra");
        output.add("   Together: 'ls' (list files)");
        output.add("");

        return output;
    }

    @Override
    public List<String> getDetailedHelp() {
        return Arrays.asList(
                "NAME", "    sls - Multiple steam locomotives (you really messed up)", "",
                "SYNOPSIS", "    sls", "",
                "DESCRIPTION", "    If 'sl' is for people who mistype 'ls', then 'sls' is for",
                "    people who REALLY can't type 'ls' correctly. Shows multiple trains.",
                "",
                "NOTE", "    Please, just type 'ls' next time. We believe in you!"
        );
    }
}