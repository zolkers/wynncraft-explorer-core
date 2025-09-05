package com.edgn.core.wynncraft.item.enumerations;

public enum StatUnit {
    RAW(""),
    PERCENT("%"),
    PER_3_S("/3s"),
    PER_5_S("/5s"),
    TIER(" tier");

    private final String displayName;

    StatUnit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}