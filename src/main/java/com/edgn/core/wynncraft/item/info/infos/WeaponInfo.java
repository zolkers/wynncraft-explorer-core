package com.edgn.core.wynncraft.item.info.infos;

import com.edgn.core.wynncraft.item.enumerations.*;
import com.edgn.core.wynncraft.item.info.ItemInfo;
import com.edgn.core.wynncraft.item.settings.DropMeta;
import com.edgn.core.wynncraft.item.settings.ItemRequirements;
import com.edgn.core.wynncraft.item.settings.WynncraftStatRange;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class WeaponInfo extends ItemInfo {
    private final WeaponType weaponType;
    private final AttackSpeed attackSpeed;
    private final Double averageDps;
    
    public WeaponInfo(String internalName, WeaponType weaponType, AttackSpeed attackSpeed,
                      Double averageDps, ItemRequirements requirements,
                      Map<String, WynncraftStatRange> baseStats, Rarity rarity,
                      String lore, DropMeta dropMeta, int powderSlots,
                      Map<String, String> majorIds, DropRestriction dropRestriction,
                      Set<String> restrictions) {
        super(internalName, requirements, baseStats, rarity, lore, dropMeta,
              powderSlots, majorIds, dropRestriction, restrictions);
        this.weaponType = weaponType;
        this.attackSpeed = attackSpeed;
        this.averageDps = averageDps;
    }
    
    public WeaponType weaponType() { return weaponType; }
    public AttackSpeed attackSpeed() { return attackSpeed; }
    public Optional<Double> averageDps() { return Optional.ofNullable(averageDps); }
    
    @Override
    public ItemType getType() { return ItemType.WEAPON; }
}