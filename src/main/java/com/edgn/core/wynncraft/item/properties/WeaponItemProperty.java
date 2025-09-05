package com.edgn.core.wynncraft.item.properties;

import com.edgn.core.wynncraft.item.enumerations.AttackSpeed;
import com.edgn.core.wynncraft.item.enumerations.ItemType;
import com.edgn.core.wynncraft.item.enumerations.WeaponType;

import java.util.Optional;

public interface WeaponItemProperty extends TypedItemProperty {
    WeaponType getWeaponType();
    AttackSpeed getAttackSpeed();
    Optional<Double> getAverageDps();
    
    @Override
    default ItemType getItemType() {
        return ItemType.WEAPON;
    }
}