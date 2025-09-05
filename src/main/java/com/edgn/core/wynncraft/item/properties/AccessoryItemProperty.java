package com.edgn.core.wynncraft.item.properties;

import com.edgn.core.wynncraft.item.enumerations.AccessoryType;
import com.edgn.core.wynncraft.item.enumerations.ItemType;

public interface AccessoryItemProperty extends TypedItemProperty {
    AccessoryType getAccessoryType();
    
    @Override
    default ItemType getItemType() {
        return ItemType.ACCESSORY;
    }
}