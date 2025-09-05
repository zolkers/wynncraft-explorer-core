package com.edgn.core.wynncraft.item.items;

import com.edgn.core.wynncraft.item.GameItem;
import com.edgn.core.wynncraft.item.enumerations.*;
import com.edgn.core.wynncraft.item.info.infos.WeaponInfo;
import com.edgn.core.wynncraft.item.instances.WeaponInstance;
import com.edgn.core.wynncraft.item.properties.*;
import com.edgn.core.wynncraft.item.settings.WynncraftStatRange;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class WeaponItem extends GameItem
        implements WeaponItemProperty,
        LeveledItemProperty,
        ClassableItemProperty,
        IdentifiableItemProperty<WeaponInfo, WeaponInstance>,
        PowderedItemProperty,
        MajorIdItemProperty,
        RestrictedItemProperty,
        RarityItemProperty,
        BaseStatsItemProperty {

    private final WeaponInfo weaponInfo;
    private final WeaponInstance weaponInstance;

    public WeaponItem(WeaponInfo weaponInfo, WeaponInstance weaponInstance) {
        this.weaponInfo = weaponInfo;
        this.weaponInstance = weaponInstance;
    }

    public WeaponItem(WeaponInfo weaponInfo) {
        this(weaponInfo, null);
    }

    @Override
    public WeaponInfo getItemInfo() { return weaponInfo; }

    @Override
    public Optional<WeaponInstance> getItemInstance() { return Optional.ofNullable(weaponInstance); }

    @Override
    public boolean isIdentified() { return weaponInstance != null && weaponInstance.identified(); }

    @Override
    public Map<String, Object> getIdentifications() {
        return weaponInstance != null ? weaponInstance.identifications() : Map.of();
    }

    @Override
    public boolean hasIdentification(String statName) {
        return weaponInstance != null && weaponInstance.identifications().containsKey(statName);
    }

    @Override
    public WeaponType getWeaponType() { return weaponInfo.weaponType(); }

    @Override
    public AttackSpeed getAttackSpeed() { return weaponInfo.attackSpeed(); }

    @Override
    public Optional<Double> getAverageDps() { return weaponInfo.averageDps(); }

    @Override
    public int getLevel() { return weaponInfo.requirements().level(); }

    @Override
    public Optional<ClassRequirement> getRequiredClass() { return weaponInfo.requirements().classType(); }

    @Override
    public int getPowderSlots() { return weaponInfo.powderSlots(); }

    @Override
    public boolean hasPowderSlots() { return weaponInfo.powderSlots() > 0; }

    @Override
    public Map<String, String> getMajorIds() { return weaponInfo.majorIds(); }

    @Override
    public boolean hasMajorId(String majorIdName) { return weaponInfo.majorIds().containsKey(majorIdName); }

    @Override
    public Set<String> getMajorIdNames() { return weaponInfo.majorIds().keySet(); }

    @Override
    public DropRestriction getDropRestriction() { return weaponInfo.dropRestriction(); }

    @Override
    public Set<String> getRestrictions() { return weaponInfo.restrictions(); }

    @Override
    public boolean isRestricted() { return weaponInfo.dropRestriction() != DropRestriction.NORMAL; }

    @Override
    public Rarity getRarity() { return weaponInfo.rarity(); }

    @Override
    public Map<String, WynncraftStatRange> getBaseStats() { return weaponInfo.baseStats(); }

    @Override
    public Optional<WynncraftStatRange> getBaseStat(String statName) {
        return Optional.ofNullable(weaponInfo.baseStats().get(statName));
    }

    @Override
    public boolean hasBaseStat(String statName) { return weaponInfo.baseStats().containsKey(statName); }

    @Override
    public String getName() { return weaponInfo.name(); }

    @Override
    public ItemType getItemType() { return ItemType.WEAPON; }

    public boolean isUnidentified() { return weaponInstance == null; }
}