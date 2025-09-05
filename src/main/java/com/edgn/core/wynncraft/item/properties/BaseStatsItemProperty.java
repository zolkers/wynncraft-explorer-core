package com.edgn.core.wynncraft.item.properties;

import com.edgn.core.wynncraft.item.settings.WynncraftStatRange;

import java.util.Map;
import java.util.Optional;

public interface BaseStatsItemProperty extends ItemProperty {
    Map<String, WynncraftStatRange> getBaseStats();
    Optional<WynncraftStatRange> getBaseStat(String statName);
    boolean hasBaseStat(String statName);
}