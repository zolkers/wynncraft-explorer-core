package com.edgn.core.module.settings;

import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.minecraft.ui.screens.modules.settings.components.EnumSettingComponent;
import com.edgn.core.minecraft.ui.screens.modules.settings.components.SettingComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class EnumSetting<T extends Enum<T>> extends Setting<T> {

    private final T[] values;

    public EnumSetting(String name, String description, T defaultValue) {
        super(name, description, defaultValue);
        this.values = defaultValue.getDeclaringClass().getEnumConstants();
    }

    public T[] getValues() {
        return values;
    }

    public void cycle() {
        setValue(getNextValue());
    }

    public void cycleBackward() {
        setValue(getPreviousValue());
    }

    public T getNextValue() {
        int currentIndex = getCurrentIndex();

        if (currentIndex != -1) {
            int nextIndex = (currentIndex + 1) % values.length;
            return values[nextIndex];
        }

        return values.length > 0 ? values[0] : getValue();
    }

    public T getPreviousValue() {
        int currentIndex = getCurrentIndex();

        if (currentIndex != -1) {
            int previousIndex = (currentIndex - 1 + values.length) % values.length;
            return values[previousIndex];
        }

        return values.length > 0 ? values[values.length - 1] : getValue();
    }

    public int getCurrentIndex() {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == getValue()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public JsonElement toJsonElement() {
        return new JsonPrimitive(getValue().name());
    }

    @Override
    public void fromJsonElement(JsonElement element) {
        if (element == null || !element.isJsonPrimitive()) return;

        try {
            String enumName = element.getAsString();
            T enumValue = Enum.valueOf(getDefaultValue().getDeclaringClass(), enumName);
            setValue(enumValue);
        } catch (IllegalArgumentException e) {

        }
    }

    @Override
    public SettingComponent createComponent(ISettingsScreen screen, int x, int y, int width, int height) {
        return new EnumSettingComponent<>(this, screen, x, y, width, height);
    }
}