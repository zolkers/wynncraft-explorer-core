package com.edgn.core.wynncraft.item.properties;

import java.util.Map;
import java.util.Set;

public interface MajorIdItemProperty extends ItemProperty {
    Map<String, String> getMajorIds();
    boolean hasMajorId(String majorIdName);
    Set<String> getMajorIdNames();
}