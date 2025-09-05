package com.edgn.core.wynncraft.item.info.infos;

import com.edgn.core.wynncraft.item.enumerations.ArmourType;
import com.edgn.core.wynncraft.item.enumerations.DropRestriction;
import com.edgn.core.wynncraft.item.enumerations.ItemType;
import com.edgn.core.wynncraft.item.enumerations.Rarity;
import com.edgn.core.wynncraft.item.info.ItemInfo;
import com.edgn.core.wynncraft.item.settings.DropMeta;
import com.edgn.core.wynncraft.item.settings.ItemRequirements;
import com.edgn.core.wynncraft.item.settings.WynncraftStatRange;

import java.util.Map;
import java.util.Set;

public class ArmourInfo extends ItemInfo {
    private final ArmourType armourType;

    public ArmourInfo(String internalName, ArmourType armourType,
                      ItemRequirements requirements, Map<String, WynncraftStatRange> baseStats,
                      Rarity rarity, String lore, DropMeta dropMeta, int powderSlots,
                      Map<String, String> majorIds, DropRestriction dropRestriction,
                      Set<String> restrictions) {
        super(internalName, requirements, baseStats, rarity, lore, dropMeta,
              powderSlots, majorIds, dropRestriction, restrictions);

        this.armourType = armourType;
    }
    
    public ArmourType armourType() { return armourType; }

    @Override
    public ItemType getType() { return ItemType.ARMOUR; }
}