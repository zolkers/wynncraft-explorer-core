package com.edgn.core.wynncraft.item.settings;

public class DropMeta {
    private final String name;
    private final String type;
    private final int[] coordinates;
    
    public DropMeta(String name, String type, int[] coordinates) {
        this.name = name;
        this.type = type;
        this.coordinates = coordinates != null ? coordinates.clone() : null;
    }
    
    public String name() { return name; }
    public String type() { return type; }
    public int[] coordinates() { return coordinates != null ? coordinates.clone() : null; }
}