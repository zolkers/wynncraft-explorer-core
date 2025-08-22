package com.edgn.core.module.settings;

import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.minecraft.ui.screens.modules.settings.components.ColorSettingComponent;
import com.edgn.core.minecraft.ui.screens.modules.settings.components.SettingComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ColorSetting extends Setting<Integer> {
    private final boolean hasAlpha;

    public ColorSetting(String name, String description, int defaultValue) { this(name, description, defaultValue, false); }
    public ColorSetting(String name, String description, int defaultValue, boolean hasAlpha) {
        super(name, description, defaultValue);
        this.hasAlpha = hasAlpha;
    }

    public boolean hasAlpha() { return hasAlpha; }

    @Override
    public JsonElement toJsonElement() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void fromJsonElement(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            setValue(element.getAsInt());
        }
    }

    @Override
    public SettingComponent createComponent(ISettingsScreen screen, int x, int y, int width, int height) {
        return new ColorSettingComponent(this, screen, x, y, width, height);
    }
}