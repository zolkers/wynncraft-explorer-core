package com.edgn.core.wynncraft.item.properties;

import com.edgn.core.wynncraft.item.info.ItemInfo;
import com.edgn.core.wynncraft.item.instances.ItemInstance;

import java.util.Map;
import java.util.Optional;

public interface IdentifiableItemProperty<I extends ItemInfo, T extends ItemInstance> extends ItemProperty {
    I getItemInfo();
    Optional<T> getItemInstance();
    boolean isIdentified();
    Map<String, Object> getIdentifications();
    boolean hasIdentification(String statName);
}