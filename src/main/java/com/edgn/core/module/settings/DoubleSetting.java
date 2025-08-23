package com.edgn.core.module.settings;

import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class DoubleSetting extends Setting<Double> {
    private final double min, max, step;

    public DoubleSetting(String name, String description, double defaultValue, double min, double max, double step) {
        super(name, description, defaultValue);
        this.min = min; this.max = max; this.step = step;
    }

    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getStep() { return step; }

    @Override
    public JsonElement toJsonElement() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void fromJsonElement(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            setValue(element.getAsDouble());
        }
    }
}