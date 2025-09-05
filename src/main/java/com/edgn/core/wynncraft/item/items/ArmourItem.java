package com.edgn.core.wynncraft.item.items;

import com.edgn.core.wynncraft.item.GameItem;
import com.edgn.core.wynncraft.item.enumerations.*;
import com.edgn.core.wynncraft.item.info.infos.ArmourInfo;
import com.edgn.core.wynncraft.item.instances.ArmourInstance;
import com.edgn.core.wynncraft.item.properties.*;
import com.edgn.core.wynncraft.item.settings.WynncraftStatRange;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ArmourItem extends GameItem
        implements ArmourItemProperty,
                   LeveledItemProperty,
                   ClassableItemProperty,
                   IdentifiableItemProperty<ArmourInfo, ArmourInstance>,
                   PowderedItemProperty,
                   MajorIdItemProperty,
                   RestrictedItemProperty,
                   RarityItemProperty,
                   BaseStatsItemProperty {
                   
    private final ArmourInfo armourInfo;
    private final ArmourInstance armourInstance;
    
    public ArmourItem(ArmourInfo armourInfo, ArmourInstance armourInstance) {
        this.armourInfo = armourInfo;
        this.armourInstance = armourInstance;
    }
    
    public ArmourItem(ArmourInfo armourInfo) {
        this(armourInfo, null);
    }
    
    @Override
    public ArmourInfo getItemInfo() { return armourInfo; }
    
    @Override
    public Optional<ArmourInstance> getItemInstance() { return Optional.ofNullable(armourInstance); }
    
    @Override
    public boolean isIdentified() { return armourInstance != null && armourInstance.identified(); }
    
    @Override
    public Map<String, Object> getIdentifications() {
        return armourInstance != null ? armourInstance.identifications() : Map.of();
    }
    
    @Override
    public boolean hasIdentification(String statName) {
        return armourInstance != null && armourInstance.identifications().containsKey(statName);
    }
    
    @Override
    public ArmourType getArmourType() { return armourInfo.armourType(); }
    
    @Override
    public int getLevel() { return armourInfo.requirements().level(); }
    
    @Override
    public Optional<ClassRequirement> getRequiredClass() { return armourInfo.requirements().classType(); }
    
    @Override
    public int getPowderSlots() { return armourInfo.powderSlots(); }
    
    @Override
    public boolean hasPowderSlots() { return armourInfo.powderSlots() > 0; }
    
    @Override
    public Map<String, String> getMajorIds() { return armourInfo.majorIds(); }
    
    @Override
    public boolean hasMajorId(String majorIdName) { return armourInfo.majorIds().containsKey(majorIdName); }
    
    @Override
    public Set<String> getMajorIdNames() { return armourInfo.majorIds().keySet(); }
    
    @Override
    public DropRestriction getDropRestriction() { return armourInfo.dropRestriction(); }
    
    @Override
    public Set<String> getRestrictions() { return armourInfo.restrictions(); }
    
    @Override
    public boolean isRestricted() { return armourInfo.dropRestriction() != DropRestriction.NORMAL; }
    
    @Override
    public Rarity getRarity() { return armourInfo.rarity(); }
    
    @Override
    public Map<String, WynncraftStatRange> getBaseStats() { return armourInfo.baseStats(); }
    
    @Override
    public Optional<WynncraftStatRange> getBaseStat(String statName) {
        return Optional.ofNullable(armourInfo.baseStats().get(statName));
    }
    
    @Override
    public boolean hasBaseStat(String statName) { return armourInfo.baseStats().containsKey(statName); }
    
    @Override
    public String getName() { return armourInfo.name(); }
    
    @Override
    public ItemType getItemType() { return ItemType.ARMOUR; }
    
    public boolean isUnidentified() { return armourInstance == null; }
}