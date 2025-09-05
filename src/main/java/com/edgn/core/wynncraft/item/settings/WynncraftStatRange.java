package com.edgn.core.wynncraft.item.settings;

public class WynncraftStatRange {
    private final Integer min;
    private final Integer raw;
    private final Integer max;
    
    public WynncraftStatRange(Integer min, Integer raw, Integer max) {
        this.min = min;
        this.raw = raw;
        this.max = max;
    }
    
    public WynncraftStatRange(Integer value) {
        this.min = value;
        this.raw = value;
        this.max = value;
    }
    
    public Integer getMin() { return min; }
    public Integer getRaw() { return raw; }
    public Integer getMax() { return max; }
    
    @Override
    public String toString() {
        if (min != null && max != null && !min.equals(max)) {
            return String.format("%d-%d (%d)", min, max, raw);
        }
        return String.valueOf(raw);
    }
}
