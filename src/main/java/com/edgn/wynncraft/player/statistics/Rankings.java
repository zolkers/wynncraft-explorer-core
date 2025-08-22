package com.edgn.wynncraft.player.statistics;

import java.util.HashMap;
import java.util.Map;

public class Rankings {
    private final Map<String, Integer> rankingData = new HashMap<>();

    public void put(String rankingType, int value) {
        rankingData.put(rankingType, value);
    }

    public Integer get(String rankingType) {
        return rankingData.get(rankingType);
    }

    public Map<String, Integer> getAllRankings() {
        return rankingData;
    }
}