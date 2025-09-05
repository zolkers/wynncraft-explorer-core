package com.edgn.core.wynncraft.item.info;

import com.edgn.core.wynncraft.item.enumerations.DropRestriction;
import com.edgn.core.wynncraft.item.enumerations.ItemType;
import com.edgn.core.wynncraft.item.enumerations.Rarity;
import com.edgn.core.wynncraft.item.settings.DropMeta;
import com.edgn.core.wynncraft.item.settings.ItemRequirements;
import com.edgn.core.wynncraft.item.settings.WynncraftStatRange;

import java.util.*;

public abstract class ItemInfo {
    private final String internalName;
    private final ItemRequirements requirements;
    private final Map<String, WynncraftStatRange> baseStats;
    private final Rarity rarity;
    private final String lore;
    private final DropMeta dropMeta;
    private final int powderSlots;
    private final Map<String, String> majorIds;
    private final DropRestriction dropRestriction;
    private final Set<String> restrictions;
    
    protected ItemInfo(String internalName, ItemRequirements requirements,
                      Map<String, WynncraftStatRange> baseStats, Rarity rarity,
                      String lore, DropMeta dropMeta, int powderSlots,
                      Map<String, String> majorIds, DropRestriction dropRestriction,
                      Set<String> restrictions) {
        this.internalName = internalName;
        this.requirements = requirements;
        this.baseStats = new HashMap<>(baseStats);
        this.rarity = rarity;
        this.lore = lore;
        this.dropMeta = dropMeta;
        this.powderSlots = powderSlots;
        this.majorIds = new HashMap<>(majorIds);
        this.dropRestriction = dropRestriction;
        this.restrictions = new HashSet<>(restrictions);
    }
    
    public String name() { return internalName; }
    public ItemRequirements requirements() { return requirements; }
    public Map<String, WynncraftStatRange> baseStats() { return new HashMap<>(baseStats); }
    public Rarity rarity() { return rarity; }
    public Optional<String> lore() { return Optional.ofNullable(lore); }
    public Optional<DropMeta> dropMeta() { return Optional.ofNullable(dropMeta); }
    public int powderSlots() { return powderSlots; }
    public Map<String, String> majorIds() { return new HashMap<>(majorIds); }
    public DropRestriction dropRestriction() { return dropRestriction; }
    public Set<String> restrictions() { return new HashSet<>(restrictions); }
    
    public abstract ItemType getType();
}