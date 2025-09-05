package com.edgn.core.wynncraft.item.properties;

import com.edgn.core.wynncraft.item.enumerations.ItemType;

public interface TypedItemProperty extends ItemProperty {
    ItemType getItemType();
}
