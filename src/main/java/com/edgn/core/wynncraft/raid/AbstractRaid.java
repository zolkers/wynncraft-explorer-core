package com.edgn.core.wynncraft.raid;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractRaid implements IRaid {

    protected final RaidEnum raidKind;
    protected final Map<Integer, Map<String, String>> roomMap;

    protected AbstractRaid(RaidEnum raidKind) {
        this.raidKind = raidKind;
        this.roomMap = buildRoomMap();
    }

    protected abstract Map<Integer, Map<String, String>> buildRoomMap();

    @Override
    public RaidEnum getRaidKind() {
        return raidKind;
    }

    @Override
    public Map<Integer, Map<String, String>> getRoomMap() {
        return roomMap;
    }

    @Override
    public int getTotalLevels() {
        return roomMap.size();
    }

    @Override
    public Set<String> getAvailableRooms() {
        return roomMap.values().stream()
                .flatMap(map -> map.values().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<String> getRoomForObjective(int level, String objective) {
        return Optional.ofNullable(roomMap.get(level))
                .map(levelMap -> levelMap.get(objective));
    }

}

