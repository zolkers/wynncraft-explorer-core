package com.edgn.wynncraft.raid;

import java.util.regex.Pattern;

public class RaidConstants {
    public static final Pattern CHALLENGE_COMPLETED_PATTERN = Pattern.compile("§a§lChallenge Completed");
    //public static final Pattern RAID_COMPLETED_PATTERN = Pattern.compile("§f§lR§#4d4d4dff§laid Completed!");
    //public static final Pattern RAID_FAILED_PATTERN = Pattern.compile("§4§kRa§c§lid Failed!");
    public static final Pattern RAID_CHOOSE_BUFF_PATTERN = Pattern.compile(
            "§#d6401eff(\\uE009\\uE002|\\uE001) §#fa7f63ff((§o)?(\\w+))§#d6401eff has chosen the §#fa7f63ff(\\w+ \\w+)§#d6401eff buff!");

    public static final String CHALLENGE_COMPLETED = "Challenge Completed";
    public static final String RAID_COMPLETED = "Raid Completed!";
    public static final String RAID_FAILED = "Raid Failed!";

    public static final String SCOREBOARD_BUFF_TEXT = "Choose a buff or go";
    public static final String SCOREBOARD_CHALLENGE_COMPLETED_TEXT = "Challenge Completed!";
    public static final String SCOREBOARD_EXIT_TEXT = "Go to the exit";
    public static final String SCOREBOARD_PLAYERS_DIED_TEXT = "Too many players have";
    public static final String SCOREBOARD_OUT_OF_TIME_TEXT = "You ran out of time!";


    public static final Pattern SCOREBOARD_TIMER_PATTERN = Pattern.compile(
            "^[-—] Time Left: (?<hours>\\d+:)?(?<minutes>\\d+):(?<seconds>\\d+)(?: \\[\\+\\d+[msMS]])?$");
}
