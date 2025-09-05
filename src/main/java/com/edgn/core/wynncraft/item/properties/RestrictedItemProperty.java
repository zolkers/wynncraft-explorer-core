package com.edgn.core.wynncraft.item.properties;

import com.edgn.core.wynncraft.item.enumerations.DropRestriction;

import java.util.Set;

public interface RestrictedItemProperty extends ItemProperty {
    DropRestriction getDropRestriction();
    Set<String> getRestrictions();
    boolean isRestricted();
}