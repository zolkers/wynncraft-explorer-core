package com.edgn.event.wynncraft;

import com.edgn.event.Event;
import com.edgn.event.Listener;
import com.edgn.wynncraft.raid.RaidEnum;

import java.util.ArrayList;

public interface RaidListener extends Listener {
    void onRaidStart(RaidStartEvent event);
    void onFirstRoom(RaidFirstRoomEvent event);
    void onBuffRoom(RaidBuffRoomEvent event);
    void onSecondRoom(RaidSecondRoomEvent event);
    void onThirdRoom(RaidThirdRoomEvent event);
    void onBossRoom(BossRoomEvent event);
    void onSecondBossRoom(SecondBossRoomEvent event);
    void onRaidFailed(FailedEvent event);
    void onRaidSuccess(SuccessEvent event);
    void onChallengeCompleted(ChallengeCompletedEvent event);
    void onBuffPicked(BuffPickedEvent event);
    void onIntermission(IntermissionEvent event);
    void onTooManyPlayersDead(TooManyPlayersDeadEvent event);
    void onOutOfTime(OutOfTimeEvent event);

    class RaidStartEvent extends Event<RaidListener> {
        private final RaidEnum currentRaid;

        public RaidStartEvent(RaidEnum currentRaid) {
            this.currentRaid = currentRaid;
        }

        public RaidEnum getCurrentRaid() {
            return currentRaid;
        }

        @Override
        public void fire(ArrayList<RaidListener> listeners) {
            for (RaidListener listener : listeners) {
                listener.onRaidStart(this);
            }
        }

        @Override
        public Class<RaidListener> getListenerType() {
            return RaidListener.class;
        }
    }

    class RaidFirstRoomEvent extends Event<RaidListener> {
        private final RaidEnum currentRaid;
        private final String roomName;

        public RaidFirstRoomEvent(RaidEnum currentRaid, String roomName) {
            this.currentRaid = currentRaid;
            this.roomName = roomName;
        }

        public RaidEnum getCurrentRaid() {
            return currentRaid;
        }

        public String getRoomName() {
            return roomName;
        }

        @Override
        public void fire(ArrayList<RaidListener> listeners) {
            for (RaidListener listener : listeners) {
                listener.onFirstRoom(this);
            }
        }

        @Override
        public Class<RaidListener> getListenerType() {
            return RaidListener.class;
        }
    }

    class RaidBuffRoomEvent extends Event<RaidListener> {
        private final RaidEnum currentRaid;

        public RaidBuffRoomEvent(RaidEnum currentRaid) {
            this.currentRaid = currentRaid;
        }

        public RaidEnum getCurrentRaid() {
            return currentRaid;
        }

        @Override
        public void fire(ArrayList<RaidListener> listeners) {
            for (RaidListener listener : listeners) {
                listener.onBuffRoom(this);
            }
        }

        @Override
        public Class<RaidListener> getListenerType() {
            return RaidListener.class;
        }
    }

    class RaidSecondRoomEvent extends Event<RaidListener> {
        private final RaidEnum currentRaid;
        private final String roomName;

        public RaidSecondRoomEvent(RaidEnum currentRaid, String roomName) {
            this.currentRaid = currentRaid;
            this.roomName = roomName;
        }

        public RaidEnum getCurrentRaid() {
            return currentRaid;
        }

        public String getRoomName() {
            return roomName;
        }

        @Override
        public void fire(ArrayList<RaidListener> listeners) {
            for (RaidListener listener : listeners) {
                listener.onSecondRoom(this);
            }
        }

        @Override
        public Class<RaidListener> getListenerType() {
            return RaidListener.class;
        }
    }

    class RaidThirdRoomEvent extends Event<RaidListener> {
        private final RaidEnum currentRaid;
        private final String roomName;

        public RaidThirdRoomEvent(RaidEnum currentRaid, String roomName) {
            this.currentRaid = currentRaid;
            this.roomName = roomName;
        }

        public RaidEnum getCurrentRaid() {
            return currentRaid;
        }

        public String getRoomName() {
            return roomName;
        }

        @Override
        public void fire(ArrayList<RaidListener> listeners) {
            for (RaidListener listener : listeners) {
                listener.onThirdRoom(this);
            }
        }

        @Override
        public Class<RaidListener> getListenerType() {
            return RaidListener.class;
        }
    }

    class BossRoomEvent extends Event<RaidListener> {
        private final RaidEnum currentRaid;
        private final String roomName;

        public BossRoomEvent(RaidEnum currentRaid, String roomName) {
            this.currentRaid = currentRaid;
            this.roomName = roomName;
        }

        public RaidEnum getCurrentRaid() {
            return currentRaid;
        }

        public String getRoomName() {
            return roomName;
        }

        @Override
        public void fire(ArrayList<RaidListener> listeners) {
            for (RaidListener listener : listeners) {
                listener.onBossRoom(this);
            }
        }

        @Override
        public Class<RaidListener> getListenerType() {
            return RaidListener.class;
        }
    }

    class SecondBossRoomEvent extends Event<RaidListener> {
        private final RaidEnum currentRaid;
        private final String roomName;

        public SecondBossRoomEvent(RaidEnum currentRaid, String roomName) {
            this.currentRaid = currentRaid;
            this.roomName = roomName;
        }

        public RaidEnum getCurrentRaid() {
            return currentRaid;
        }

        public String getRoomName() {
            return roomName;
        }

        @Override
        public void fire(ArrayList<RaidListener> listeners) {
            for (RaidListener listener : listeners) {
                listener.onSecondBossRoom(this);
            }
        }

        @Override
        public Class<RaidListener> getListenerType() {
            return RaidListener.class;
        }
    }

    class FailedEvent extends Event<RaidListener> {
        private final RaidEnum currentRaid;

        public FailedEvent(RaidEnum currentRaid) {
            this.currentRaid = currentRaid;
        }

        public RaidEnum getCurrentRaid() {
            return currentRaid;
        }

        @Override
        public void fire(ArrayList<RaidListener> listeners) {
            for (RaidListener listener : listeners) {
                listener.onRaidFailed(this);
            }
        }

        @Override
        public Class<RaidListener> getListenerType() {
            return RaidListener.class;
        }
    }

    class SuccessEvent extends Event<RaidListener> {
        private final RaidEnum currentRaid;

        public SuccessEvent(RaidEnum currentRaid) {
            this.currentRaid = currentRaid;
        }

        public RaidEnum getCurrentRaid() {
            return currentRaid;
        }

        @Override
        public void fire(ArrayList<RaidListener> listeners) {
            for (RaidListener listener : listeners) {
                listener.onRaidSuccess(this);
            }
        }

        @Override
        public Class<RaidListener> getListenerType() {
            return RaidListener.class;
        }
    }

    class ChallengeCompletedEvent extends Event<RaidListener> {
        private final RaidEnum currentRaid;

        public ChallengeCompletedEvent(RaidEnum currentRaid) {
            this.currentRaid = currentRaid;
        }

        public RaidEnum getCurrentRaid() {
            return currentRaid;
        }

        @Override
        public void fire(ArrayList<RaidListener> listeners) {
            for (RaidListener listener : listeners) {
                listener.onChallengeCompleted(this);
            }
        }

        @Override
        public Class<RaidListener> getListenerType() {
            return RaidListener.class;
        }
    }

    class BuffPickedEvent extends Event<RaidListener> {
        private final String playerName;
        private final String buffName;
        private final RaidEnum currentRaid;

        public BuffPickedEvent(String playerName, String buffName, RaidEnum currentRaid) {
            this.playerName = playerName;
            this.buffName = buffName;
            this.currentRaid = currentRaid;
        }

        public RaidEnum getCurrentRaid() {
            return currentRaid;
        }

        @Override
        public void fire(ArrayList<RaidListener> listeners) {
            for (RaidListener listener : listeners) {
                listener.onBuffPicked(this);
            }
        }

        @Override
        public Class<RaidListener> getListenerType() {
            return RaidListener.class;
        }

        public String getPlayerName() {
            return playerName;
        }

        public String getBuffName() {
            return buffName;
        }
    }

     class IntermissionEvent extends Event<RaidListener> {
        private final RaidEnum raidType;

        public IntermissionEvent(RaidEnum raidType) {
            this.raidType = raidType;
        }

        @Override
        public void fire(ArrayList<RaidListener> listeners) {
            for (RaidListener listener : listeners) {
                listener.onIntermission(this);
            }
        }

        @Override
        public Class<RaidListener> getListenerType() {
            return RaidListener.class;
        }

        public RaidEnum getRaidType() { return raidType; }
    }

     class TooManyPlayersDeadEvent extends Event<RaidListener> {
        private final RaidEnum raidType;

        public TooManyPlayersDeadEvent(RaidEnum raidType) {
            this.raidType = raidType;
        }

        @Override
        public void fire(ArrayList<RaidListener> listeners) {
            for (RaidListener listener : listeners) {
                listener.onTooManyPlayersDead(this);
            }
        }

        @Override
        public Class<RaidListener> getListenerType() {
            return RaidListener.class;
        }

        public RaidEnum getRaidType() { return raidType; }
    }

    class OutOfTimeEvent extends Event<RaidListener> {
        private final RaidEnum raidType;

        public OutOfTimeEvent(RaidEnum raidType) {
            this.raidType = raidType;
        }

        @Override
        public void fire(ArrayList<RaidListener> listeners) {
            for (RaidListener listener : listeners) {
                listener.onOutOfTime(this);
            }
        }

        @Override
        public Class<RaidListener> getListenerType() {
            return RaidListener.class;
        }

        public RaidEnum getRaidType() { return raidType; }
    }
}