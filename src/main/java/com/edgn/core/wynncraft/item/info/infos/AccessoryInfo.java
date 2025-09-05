package com.edgn.core.wynncraft.item.info.infos;

import com.edgn.core.wynncraft.item.enumerations.AccessoryType;
import com.edgn.core.wynncraft.item.enumerations.DropRestriction;
import com.edgn.core.wynncraft.item.enumerations.ItemType;
import com.edgn.core.wynncraft.item.enumerations.Rarity;
import com.edgn.core.wynncraft.item.info.ItemInfo;
import com.edgn.core.wynncraft.item.settings.DropMeta;
import com.edgn.core.wynncraft.item.settings.ItemRequirements;
import com.edgn.core.wynncraft.item.settings.WynncraftStatRange;

import java.util.Map;
import java.util.Set;

public class AccessoryInfo extends ItemInfo {
    private final AccessoryType accessoryType;
    
    public AccessoryInfo(String internalName, AccessoryType accessoryType,
                         ItemRequirements requirements, Map<String, WynncraftStatRange> baseStats,
                         Rarity rarity, String lore, DropMeta dropMeta, int powderSlots,
                         Map<String, String> majorIds, DropRestriction dropRestriction,
                         Set<String> restrictions) {
        super(internalName, requirements, baseStats, rarity, lore, dropMeta,
              powderSlots, majorIds, dropRestriction, restrictions);
        this.accessoryType = accessoryType;
    }
    
    public AccessoryType accessoryType() { return accessoryType; }
    
    @Override
    public ItemType getType() { return ItemType.ACCESSORY; }
}