package com.edgn.wynncraft.raid;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface IRaid {
    Map<Integer, Map<String, String>> getRoomMap();
    int getTotalLevels();
    Set<String> getAvailableRooms();
    Optional<String> getRoomForObjective(int level, String objective);
    RaidEnum getRaidKind();
}
