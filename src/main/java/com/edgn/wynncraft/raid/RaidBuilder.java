package com.edgn.wynncraft.raid;

import java.util.*;
import java.util.stream.Collectors;

public class RaidBuilder {
    private final Map<Integer, Map<String, String>> levels = new TreeMap<>();
    private Integer currentLevel;

    public RaidBuilder addLevel(int level) {
        if (level <= 0) {
            throw new IllegalArgumentException("Level must be positive, got: " + level);
        }
        if (levels.containsKey(level)) {
            throw new IllegalArgumentException("Level " + level + " already exists");
        }
        
        this.currentLevel = level;
        levels.put(level, new HashMap<>());
        return this;
    }

    public RaidBuilder addRoom(String objective, String room) {
        if (currentLevel == null) {
            throw new IllegalStateException("Must call addLevel() before addRoom()");
        }
        if (objective == null || objective.trim().isEmpty()) {
            throw new IllegalArgumentException("Objective cannot be null or empty");
        }
        if (room == null || room.trim().isEmpty()) {
            throw new IllegalArgumentException("Room cannot be null or empty");
        }
        
        Map<String, String> currentLevelMap = levels.get(currentLevel);
        if (currentLevelMap.containsKey(objective)) {
            throw new IllegalArgumentException("Objective '" + objective + "' already exists for level " + currentLevel);
        }
        
        currentLevelMap.put(objective, room);
        return this;
    }

    public RaidBuilder addRooms(String... roomMappings) {
        if (roomMappings.length % 2 != 0) {
            throw new IllegalArgumentException("Room mappings must be provided in objective-room pairs");
        }
        
        for (int i = 0; i < roomMappings.length; i += 2) {
            addRoom(roomMappings[i], roomMappings[i + 1]);
        }
        return this;
    }

    public RaidBuilder addRoom(Map.Entry<String, String> entry) {
        return addRoom(entry.getKey(), entry.getValue());
    }

    public RaidBuilder addRooms(Map<String, String> roomMap) {
        roomMap.entrySet().forEach(this::addRoom);
        return this;
    }

    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public int getLevelCount() {
        return levels.size();
    }

    public boolean isEmpty() {
        return levels.isEmpty();
    }

    public int getRoomCount(int level) {
        return levels.getOrDefault(level, Collections.emptyMap()).size();
    }

    public Map<Integer, Map<String, String>> build() {
        if (levels.isEmpty()) {
            throw new IllegalStateException("Cannot build raid with no levels. Add at least one level.");
        }
        
        return levels.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> Map.copyOf(entry.getValue()),
                    (existing, replacement) -> existing,
                    TreeMap::new
                ));
    }

    public static RaidBuilder create() {
        return new RaidBuilder();
    }

    public RaidBuilder reset() {
        levels.clear();
        currentLevel = null;
        return this;
    }
    
    @Override
    public String toString() {
        return "RaidBuilder{" +
                "levels=" + levels.size() +
                ", currentLevel=" + currentLevel +
                '}';
    }
}