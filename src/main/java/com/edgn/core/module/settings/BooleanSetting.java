package com.edgn.core.module.settings;

import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.minecraft.ui.screens.modules.settings.components.BooleanSettingComponent;
import com.edgn.core.minecraft.ui.screens.modules.settings.components.SettingComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description, defaultValue);
    }

    @Override
    public JsonElement toJsonElement() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void fromJsonElement(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            setValue(element.getAsBoolean());
        }
    }

    public SettingComponent createComponent(ISettingsScreen screen, int x, int y, int width, int height) {
        return new BooleanSettingComponent(this, screen, x, y, width, height);
    }
}