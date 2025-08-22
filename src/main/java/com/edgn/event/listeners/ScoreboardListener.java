package com.edgn.event.listeners;

import com.edgn.event.Event;
import com.edgn.event.Listener;
import net.minecraft.scoreboard.*;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface ScoreboardListener extends Listener {
    void onScoreboardUpdate(ScoreboardUpdateEvent event);
    void onScoreboardScoreReset(ScoreboardScoreResetEvent event);

    void onScoreboardObjectiveAdd(ScoreboardObjectiveAddEvent event);
    void onScoreboardObjectiveRemove(ScoreboardObjectiveRemoveEvent event);
    void onScoreboardSlotChange(ScoreboardSlotChangeEvent event);

    void onTeamAdd(TeamAddEvent event);
    void onTeamRemove(TeamRemoveEvent event);
    void onPlayerTeamJoin(PlayerTeamJoinEvent event);
    void onPlayerTeamLeave(PlayerTeamLeaveEvent event);

    class ScoreboardUpdateEvent extends Event<ScoreboardListener> {
        private final Scoreboard scoreboard;
        private final String playerName;
        private final String objectiveName;
        private final int score;
        private final Text displayText;
        private boolean cancelled = false;

        public ScoreboardUpdateEvent(Scoreboard scoreboard, String playerName, String objectiveName, int score, @Nullable Text displayText) {
            this.scoreboard = scoreboard;
            this.playerName = playerName;
            this.objectiveName = objectiveName;
            this.score = score;
            this.displayText = displayText;
        }

        public List<String> getScoreboardLines() {
            if(scoreboard == null) return List.of();
            ScoreboardObjective sidebarObjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);

            if (sidebarObjective == null) {
                return Collections.emptyList();
            }

            Collection<ScoreboardEntry> entries = scoreboard.getScoreboardEntries(sidebarObjective);
            List<ScoreboardEntry> sortedEntries = new ArrayList<>(entries);
            sortedEntries.sort((a, b) -> Integer.compare(b.value(), a.value()));

            List<String> lines = new ArrayList<>();

            lines.add(sidebarObjective.getDisplayName().getString());

            for (ScoreboardEntry entry : sortedEntries) {
                String text;
                if (entry.display() != null) {
                    text = entry.display().getString();
                } else {
                    text = entry.owner();
                }
                lines.add(text);
            }

            return lines;
        }

        @Override
        public void fire(ArrayList<ScoreboardListener> listeners) {
            for (ScoreboardListener listener : listeners) {
                listener.onScoreboardUpdate(this);
            }
        }

        @Override
        public Class<ScoreboardListener> getListenerType() {
            return ScoreboardListener.class;
        }

        // Getters existants...
        public Scoreboard getScoreboard() { return scoreboard; }
        public String getPlayerName() { return playerName; }
        public String getObjectiveName() { return objectiveName; }
        public int getScore() { return score; }
        @Nullable public Text getDisplayText() { return displayText; }
        public boolean isCancelled() { return cancelled; }
        public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    }

    // Score Reset Event
    class ScoreboardScoreResetEvent extends Event<ScoreboardListener> {
        private final String playerName;
        private final String objectiveName;
        private boolean cancelled = false;

        public ScoreboardScoreResetEvent(String playerName, String objectiveName) {
            this.playerName = playerName;
            this.objectiveName = objectiveName;
        }

        @Override
        public void fire(ArrayList<ScoreboardListener> listeners) {
            for (ScoreboardListener listener : listeners) {
                listener.onScoreboardScoreReset(this);
            }
        }

        @Override
        public Class<ScoreboardListener> getListenerType() {
            return ScoreboardListener.class;
        }

        public String getPlayerName() { return playerName; }
        public String getObjectiveName() { return objectiveName; }
        public boolean isCancelled() { return cancelled; }
        public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    }

    class ScoreboardObjectiveAddEvent extends Event<ScoreboardListener> {
        private final String name;
        private final ScoreboardCriterion criterion;
        private final Text displayName;
        private final ScoreboardCriterion.RenderType renderType;
        private boolean cancelled = false;

        public ScoreboardObjectiveAddEvent(String name, ScoreboardCriterion criterion, Text displayName, ScoreboardCriterion.RenderType renderType) {
            this.name = name;
            this.criterion = criterion;
            this.displayName = displayName;
            this.renderType = renderType;
        }

        @Override
        public void fire(ArrayList<ScoreboardListener> listeners) {
            for (ScoreboardListener listener : listeners) {
                listener.onScoreboardObjectiveAdd(this);
            }
        }

        @Override
        public Class<ScoreboardListener> getListenerType() {
            return ScoreboardListener.class;
        }

        public String getName() { return name; }
        public ScoreboardCriterion getCriterion() { return criterion; }
        public Text getDisplayName() { return displayName; }
        public ScoreboardCriterion.RenderType getRenderType() { return renderType; }
        public boolean isCancelled() { return cancelled; }
        public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    }

    class ScoreboardObjectiveRemoveEvent extends Event<ScoreboardListener> {
        private final ScoreboardObjective objective;
        private boolean cancelled = false;

        public ScoreboardObjectiveRemoveEvent(ScoreboardObjective objective) {
            this.objective = objective;
        }

        @Override
        public void fire(ArrayList<ScoreboardListener> listeners) {
            for (ScoreboardListener listener : listeners) {
                listener.onScoreboardObjectiveRemove(this);
            }
        }

        @Override
        public Class<ScoreboardListener> getListenerType() {
            return ScoreboardListener.class;
        }

        public ScoreboardObjective getObjective() { return objective; }
        public boolean isCancelled() { return cancelled; }
        public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    }

    class ScoreboardSlotChangeEvent extends Event<ScoreboardListener> {
        private final ScoreboardDisplaySlot slot;
        private final ScoreboardObjective objective;
        private boolean cancelled = false;

        public ScoreboardSlotChangeEvent(ScoreboardDisplaySlot slot, @Nullable ScoreboardObjective objective) {
            this.slot = slot;
            this.objective = objective;
        }

        @Override
        public void fire(ArrayList<ScoreboardListener> listeners) {
            for (ScoreboardListener listener : listeners) {
                listener.onScoreboardSlotChange(this);
            }
        }

        @Override
        public Class<ScoreboardListener> getListenerType() {
            return ScoreboardListener.class;
        }

        public ScoreboardDisplaySlot getSlot() { return slot; }
        @Nullable public ScoreboardObjective getObjective() { return objective; }
        public boolean isCancelled() { return cancelled; }
        public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    }

    class TeamAddEvent extends Event<ScoreboardListener> {
        private final String teamName;
        private boolean cancelled = false;

        public TeamAddEvent(String teamName) {
            this.teamName = teamName;
        }

        @Override
        public void fire(ArrayList<ScoreboardListener> listeners) {
            for (ScoreboardListener listener : listeners) {
                listener.onTeamAdd(this);
            }
        }

        @Override
        public Class<ScoreboardListener> getListenerType() {
            return ScoreboardListener.class;
        }

        public String getTeamName() { return teamName; }
        public boolean isCancelled() { return cancelled; }
        public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    }

    class TeamRemoveEvent extends Event<ScoreboardListener> {
        private final Team team;
        private boolean cancelled = false;

        public TeamRemoveEvent(Team team) {
            this.team = team;
        }

        @Override
        public void fire(ArrayList<ScoreboardListener> listeners) {
            for (ScoreboardListener listener : listeners) {
                listener.onTeamRemove(this);
            }
        }

        @Override
        public Class<ScoreboardListener> getListenerType() {
            return ScoreboardListener.class;
        }

        public Team getTeam() { return team; }
        public boolean isCancelled() { return cancelled; }
        public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    }

    class PlayerTeamJoinEvent extends Event<ScoreboardListener> {
        private final String playerName;
        private final Team team;
        private boolean cancelled = false;

        public PlayerTeamJoinEvent(String playerName, Team team) {
            this.playerName = playerName;
            this.team = team;
        }

        @Override
        public void fire(ArrayList<ScoreboardListener> listeners) {
            for (ScoreboardListener listener : listeners) {
                listener.onPlayerTeamJoin(this);
            }
        }

        @Override
        public Class<ScoreboardListener> getListenerType() {
            return ScoreboardListener.class;
        }

        public String getPlayerName() { return playerName; }
        public Team getTeam() { return team; }
        public boolean isCancelled() { return cancelled; }
        public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    }

    class PlayerTeamLeaveEvent extends Event<ScoreboardListener> {
        private final String playerName;
        private final Team team;
        private boolean cancelled = false;

        public PlayerTeamLeaveEvent(String playerName, Team team) {
            this.playerName = playerName;
            this.team = team;
        }

        @Override
        public void fire(ArrayList<ScoreboardListener> listeners) {
            for (ScoreboardListener listener : listeners) {
                listener.onPlayerTeamLeave(this);
            }
        }

        @Override
        public Class<ScoreboardListener> getListenerType() {
            return ScoreboardListener.class;
        }

        public String getPlayerName() { return playerName; }
        public Team getTeam() { return team; }
        public boolean isCancelled() { return cancelled; }
        public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    }
}