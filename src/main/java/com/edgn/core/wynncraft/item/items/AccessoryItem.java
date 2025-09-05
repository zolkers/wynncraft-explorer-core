package com.edgn.core.wynncraft.item.items;

import com.edgn.core.wynncraft.item.GameItem;
import com.edgn.core.wynncraft.item.enumerations.*;
import com.edgn.core.wynncraft.item.info.infos.AccessoryInfo;
import com.edgn.core.wynncraft.item.instances.AccessoryInstance;
import com.edgn.core.wynncraft.item.properties.*;
import com.edgn.core.wynncraft.item.settings.WynncraftStatRange;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AccessoryItem extends GameItem
        implements AccessoryItemProperty,
                   LeveledItemProperty,
                   ClassableItemProperty,
                   IdentifiableItemProperty<AccessoryInfo, AccessoryInstance>,
                   PowderedItemProperty,
                   MajorIdItemProperty,
                   RestrictedItemProperty,
                   RarityItemProperty,
                   BaseStatsItemProperty {
                   
    private final AccessoryInfo accessoryInfo;
    private final AccessoryInstance accessoryInstance;
    
    public AccessoryItem(AccessoryInfo accessoryInfo, AccessoryInstance accessoryInstance) {
        this.accessoryInfo = accessoryInfo;
        this.accessoryInstance = accessoryInstance;
    }
    
    public AccessoryItem(AccessoryInfo accessoryInfo) {
        this(accessoryInfo, null);
    }
    
    @Override
    public AccessoryInfo getItemInfo() { return accessoryInfo; }
    
    @Override
    public Optional<AccessoryInstance> getItemInstance() { return Optional.ofNullable(accessoryInstance); }
    
    @Override
    public boolean isIdentified() { return accessoryInstance != null && accessoryInstance.identified(); }
    
    @Override
    public Map<String, Object> getIdentifications() {
        return accessoryInstance != null ? accessoryInstance.identifications() : Map.of();
    }
    
    @Override
    public boolean hasIdentification(String statName) {
        return accessoryInstance != null && accessoryInstance.identifications().containsKey(statName);
    }
    
    @Override
    public AccessoryType getAccessoryType() { return accessoryInfo.accessoryType(); }
    
    @Override
    public int getLevel() { return accessoryInfo.requirements().level(); }
    
    @Override
    public Optional<ClassRequirement> getRequiredClass() { return accessoryInfo.requirements().classType(); }
    
    @Override
    public int getPowderSlots() { return accessoryInfo.powderSlots(); }
    
    @Override
    public boolean hasPowderSlots() { return accessoryInfo.powderSlots() > 0; }
    
    @Override
    public Map<String, String> getMajorIds() { return accessoryInfo.majorIds(); }
    
    @Override
    public boolean hasMajorId(String majorIdName) { return accessoryInfo.majorIds().containsKey(majorIdName); }
    
    @Override
    public Set<String> getMajorIdNames() { return accessoryInfo.majorIds().keySet(); }
    
    @Override
    public DropRestriction getDropRestriction() { return accessoryInfo.dropRestriction(); }
    
    @Override
    public Set<String> getRestrictions() { return accessoryInfo.restrictions(); }
    
    @Override
    public boolean isRestricted() { return accessoryInfo.dropRestriction() != DropRestriction.NORMAL; }
    
    @Override
    public Rarity getRarity() { return accessoryInfo.rarity(); }
    
    @Override
    public Map<String, WynncraftStatRange> getBaseStats() { return accessoryInfo.baseStats(); }
    
    @Override
    public Optional<WynncraftStatRange> getBaseStat(String statName) {
        return Optional.ofNullable(accessoryInfo.baseStats().get(statName));
    }
    
    @Override
    public boolean hasBaseStat(String statName) { return accessoryInfo.baseStats().containsKey(statName); }
    
    @Override
    public String getName() { return accessoryInfo.name(); }
    
    @Override
    public ItemType getItemType() { return ItemType.ACCESSORY; }
    
    public boolean isUnidentified() { return accessoryInstance == null; }
}