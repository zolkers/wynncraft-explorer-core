package com.edgn.service.services;

import com.edgn.Main;
import com.edgn.event.EventManager;
import com.edgn.event.listeners.ChatMessageAddListener;
import com.edgn.event.listeners.ScoreboardListener;
import com.edgn.event.listeners.TitleListener;
import com.edgn.service.AbstractService;
import com.edgn.event.wynncraft.RaidListener;
import com.edgn.exceptions.RaidException;
import com.edgn.core.wynncraft.raid.RaidConstants;
import com.edgn.core.wynncraft.raid.RaidEnum;
import com.edgn.core.wynncraft.raid.IRaid;
import com.edgn.core.minecraft.text.TextStyle;

import java.util.*;
import java.util.regex.Matcher;

public class RaidEventService extends AbstractService
        implements TitleListener, ScoreboardListener, ChatMessageAddListener {
    private RaidEnum currentRaidType = null;
    private IRaid currentRaid = null;
    private boolean hasStarted = false;
    private int level = -1;
    private boolean isInIntermission = false;
    private boolean isInBuffRoom = false;

    @Override
    public void onTitle(TitleEvent event) {
        try {
            if (event.getTitle() == null || event.getTitle().getString() == null) return;

            String legacyTitle = event.getTitle().getString();

            if (legacyTitle.contains(RaidConstants.RAID_COMPLETED)) {
                onRaidCompleted();
            } else if (legacyTitle.contains(RaidConstants.RAID_FAILED)) {
                onRaidFailed();
            }

            if(hasStarted) return;

            this.detectRaidFromTitle(legacyTitle);
        } catch (RaidException e) {
            Main.OVERLAY_MANAGER.getLoggerOverlay().error(e.getMessage(), true);
        }
    }

    private void detectRaidFromTitle(String titleText) {
        try {
            Main.OVERLAY_MANAGER.getLoggerOverlay().info("Title: " + titleText, true);
            this.currentRaidType = RaidEnum.getRaidKind(titleText);
            if(currentRaidType == null) return;

            this.currentRaid = currentRaidType.createRaid();

            this.onStart();
        } catch (RaidException e) {/* blc si pas trouvé tamer heyoth */}
    }

    public void reset() {
        this.currentRaidType = null;
        this.currentRaid = null;
        this.hasStarted = false;
        this.level = -1;
        this.isInBuffRoom = false;
        this.isInIntermission = false;
    }

    @Override
    public void init() {
        Main.EVENT_MANAGER.add(TitleListener.class, this);
        Main.EVENT_MANAGER.add(ScoreboardListener.class, this);
        Main.EVENT_MANAGER.add(ChatMessageAddListener.class, this);
    }

    @Override
    public void onScoreboardUpdate(ScoreboardUpdateEvent event) {
        if(event.getScoreboard() == null) return;
        List<String> scoreboardLines = event.getScoreboardLines();

        if (!scoreboardLines.isEmpty()) {
            analyzeScoreboardPatterns(scoreboardLines);

            if (currentRaid != null) {
                analyzeScoreboardForRooms(scoreboardLines);
            }
        }
    }

    private void analyzeScoreboardPatterns(List<String> scoreboardLines) {
        for (String line : scoreboardLines) {
            String cleanLine = line.trim();

            if (cleanLine.contains(RaidConstants.SCOREBOARD_BUFF_TEXT)) {
                onBuffRoom();
                continue;
            }

            if (cleanLine.contains(RaidConstants.SCOREBOARD_CHALLENGE_COMPLETED_TEXT)) {
                onChallengeCompleted();
                continue;
            }

            if (cleanLine.contains(RaidConstants.SCOREBOARD_EXIT_TEXT)) {
                onIntermission();
                continue;
            }

            if (cleanLine.contains(RaidConstants.SCOREBOARD_PLAYERS_DIED_TEXT)) {
                onTooManyPlayersDied();
                continue;
            }

            if (cleanLine.contains(RaidConstants.SCOREBOARD_OUT_OF_TIME_TEXT)) {
                onOutOfTime();
            }
        }
    }

    private void analyzeScoreboardForRooms(List<String> scoreboardLines) {
        for (String line : scoreboardLines) {

            //Main.OVERLAY_MANAGER.getLoggerOverlay().info(line, true);

            for (int level = 1; level <= currentRaid.getTotalLevels(); level++) {
                Map<String, String> levelRooms = currentRaid.getRoomMap().get(level);
                if (levelRooms != null) {
                    for (Map.Entry<String, String> room : levelRooms.entrySet()) {
                        String objective = room.getKey();
                        String roomName = room.getValue();

                        if (line.contains(objective)) {
                            onRoomDetected(level, roomName);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void onRoomDetected(int level, String roomName) {
        isInIntermission = false;
        isInBuffRoom = false;

        if(level == 1) {
            EventManager.fire(new RaidListener.RaidFirstRoomEvent(currentRaidType, roomName));
            this.level = level;
        } else if (level == 2) {
            EventManager.fire(new RaidListener.RaidSecondRoomEvent(currentRaidType, roomName));
            this.level = level;
        } else if (level == 3) {
            EventManager.fire(new RaidListener.RaidThirdRoomEvent(currentRaidType, roomName));
            this.level = level;
        } else if (level == 4) {
            EventManager.fire(new RaidListener.BossRoomEvent(currentRaidType, roomName));
            this.level = level;
        } else if (level == 5) {
            EventManager.fire(new RaidListener.SecondBossRoomEvent(currentRaidType, roomName));
            this.level = level;
        }
    }

    private void onIntermission() {
        EventManager.fire(new RaidListener.IntermissionEvent(currentRaidType));
        isInIntermission = true;
    }

    private void onTooManyPlayersDied() {
        EventManager.fire(new RaidListener.TooManyPlayersDeadEvent(currentRaidType));
    }

    private void onOutOfTime() {
        EventManager.fire(new RaidListener.OutOfTimeEvent(currentRaidType));
        this.reset();
    }

    @Override
    public void onAddMessage(ChatMessageAddEvent event) {
        if(currentRaidType == null) return;

        String legacyText = TextStyle.of(event.getOriginalMessage()).toLegacy();

        if (RaidConstants.CHALLENGE_COMPLETED_PATTERN.matcher(legacyText).find()) {
            onChallengeCompleted();
        } else if (RaidConstants.RAID_CHOOSE_BUFF_PATTERN.matcher(legacyText).find()) {
            Matcher matcher = RaidConstants.RAID_CHOOSE_BUFF_PATTERN.matcher(legacyText);
            if (matcher.find()) {
                String playerName = matcher.group(4);
                String buffName = matcher.group(5);
                onPlayerChoseBuff(playerName, buffName);
            }
        }
    }

    private void onStart() {
        if(hasStarted) return;
        this.hasStarted = true;
        EventManager.fire(new RaidListener.RaidStartEvent(currentRaidType));
    }

    private void onBuffRoom(){
        isInBuffRoom = true;
        EventManager.fire(new RaidListener.RaidBuffRoomEvent(currentRaidType));
    }

    private void onChallengeCompleted() {
        EventManager.fire(new RaidListener.ChallengeCompletedEvent(currentRaidType));
    }

    private void onRaidCompleted() {
        if(!hasStarted) return;
        EventManager.fire(new RaidListener.SuccessEvent(currentRaidType));
        this.reset();
    }

    private void onRaidFailed() {
        if(!hasStarted) return;
        EventManager.fire(new RaidListener.FailedEvent(currentRaidType));
        this.reset();
    }

    private void onPlayerChoseBuff(String playerName, String buffName) {
        EventManager.fire(new RaidListener.BuffPickedEvent(playerName, buffName, currentRaidType));
    }

    public RaidEnum getCurrentRaidType() {
        return currentRaidType;
    }

    @Override
    public void onScoreboardScoreReset(ScoreboardScoreResetEvent event) {

    }

    @Override
    public void onScoreboardObjectiveAdd(ScoreboardObjectiveAddEvent event) {

    }

    @Override
    public void onScoreboardObjectiveRemove(ScoreboardObjectiveRemoveEvent event) {

    }

    @Override
    public void onScoreboardSlotChange(ScoreboardSlotChangeEvent event) {

    }

    @Override
    public void onTeamAdd(TeamAddEvent event) {

    }

    @Override
    public void onTeamRemove(TeamRemoveEvent event) {

    }

    @Override
    public void onPlayerTeamJoin(PlayerTeamJoinEvent event) {

    }

    @Override
    public void onPlayerTeamLeave(PlayerTeamLeaveEvent event) {

    }

    public int getLevel() {
        return level;
    }

    public boolean isInBuffRoom() {
        return isInBuffRoom;
    }

    public boolean isInIntermission() {
        return isInIntermission;
    }

    public boolean isInRaid() {
        return hasStarted;
    }
}