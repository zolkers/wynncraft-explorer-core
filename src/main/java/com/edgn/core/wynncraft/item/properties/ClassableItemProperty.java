package com.edgn.core.wynncraft.item.properties;

import com.edgn.core.wynncraft.item.enumerations.ClassRequirement;

import java.util.Optional;

public interface ClassableItemProperty extends ItemProperty {
    Optional<ClassRequirement> getRequiredClass();
}