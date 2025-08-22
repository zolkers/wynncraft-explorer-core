package com.edgn.core.module.settings;

import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.minecraft.ui.screens.modules.settings.components.SettingComponent;
import com.edgn.core.minecraft.ui.screens.modules.settings.components.StringSettingComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class StringSetting extends Setting<String> {
    private final int maxLength;

    public StringSetting(String name, String description, String defaultValue) { this(name, description, defaultValue, 100); }
    public StringSetting(String name, String description, String defaultValue, int maxLength) {
        super(name, description, defaultValue);
        this.maxLength = maxLength;
    }

    public int getMaxLength() { return maxLength; }

    @Override
    public JsonElement toJsonElement() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void fromJsonElement(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            setValue(element.getAsString());
        }
    }

    @Override
    public SettingComponent createComponent(ISettingsScreen screen, int x, int y, int width, int height) {
        return new StringSettingComponent(this, screen, x, y, width, height);
    }
}