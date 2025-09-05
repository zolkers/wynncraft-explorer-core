package com.edgn.core.wynncraft.item.instances;

import com.edgn.core.wynncraft.item.settings.WynncraftStatRange;

import java.util.HashMap;
import java.util.Map;

public abstract class ItemInstance {
    private final Map<String, Object> identifications;
    private final boolean identified;
    
    protected ItemInstance(Map<String, Object> identifications, boolean identified) {
        this.identifications = new HashMap<>(identifications);
        this.identified = identified;
    }
    
    public Map<String, Object> identifications() { return new HashMap<>(identifications); }
    public boolean identified() { return identified; }
    
    public Object getIdentification(String statName) {
        return identifications.get(statName);
    }
    
    public WynncraftStatRange getStatRange(String statName) {
        Object value = identifications.get(statName);
        if (value instanceof WynncraftStatRange) {
            return (WynncraftStatRange) value;
        } else if (value instanceof Integer) {
            return new WynncraftStatRange((Integer) value);
        }
        return null;
    }
}