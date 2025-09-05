package com.edgn.core.wynncraft.item;

import com.edgn.core.wynncraft.item.enumerations.ItemType;
import com.edgn.core.wynncraft.item.properties.ItemProperty;

public abstract class GameItem implements ItemProperty {
    public abstract String getName();
    public abstract ItemType getItemType();
}