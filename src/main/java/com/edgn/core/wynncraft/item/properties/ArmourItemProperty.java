package com.edgn.core.wynncraft.item.properties;

import com.edgn.core.wynncraft.item.enumerations.ArmourType;
import com.edgn.core.wynncraft.item.enumerations.ItemType;

public interface ArmourItemProperty extends TypedItemProperty {
    ArmourType getArmourType();

    @Override
    default ItemType getItemType() {
        return ItemType.ARMOUR;
    }
}
