package com.edgn.core.module.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SettingsGroup {
    private final String name;
    private final String description;
    private final List<Setting<?>> settings;
    private boolean expanded = true;

    public SettingsGroup(String name, String description) {
        this.name = name;
        this.description = description;
        this.settings = new ArrayList<>();
    }

    public SettingsGroup(String name) {
        this(name, "");
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<Setting<?>> getSettings() { return settings; }
    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }

    private <S extends Setting<?>> S add(S setting) {
        settings.add(setting);
        return setting;
    }

    public BooleanSetting addBoolean(String name, String description, boolean defaultValue) {
        return add(new BooleanSetting(name, description, defaultValue));
    }

    public DoubleSetting addDoubleSlider(String name, String description, double defaultValue, double min, double max, double step) {
        return add(new DoubleSetting(name, description, defaultValue, min, max, step));
    }

    public StringSetting addString(String name, String description, String defaultValue) {
        return add(new StringSetting(name, description, defaultValue));
    }

    public <T> ListSetting<T> addList(String name, String description, List<T> defaultValue, Function<String, T> parser, Function<T, String> converter) {
        return add(new ListSetting<>(name, description, defaultValue, parser, converter));
    }

    public <T extends Enum<T>> EnumSetting<T> addEnum(String name, String description, T defaultValue) {
        return add(new EnumSetting<>(name, description, defaultValue));
    }

    public <T> ListSetting<T> addList(String name, String description, List<T> defaultValue, Function<String, T> parser) {
        return add(new ListSetting<>(name, description, defaultValue, parser));
    }

    public ColorSetting addColor(String name, String description, int defaultValue) {
        return add(new ColorSetting(name, description, defaultValue));
    }

    public SettingsGroup setOnValueChangedToAll(BiConsumer<Object, Object> callback) {
        for (Setting<?> setting : settings) {
            setting.setOnValueChanged(callback);
        }
        return this;
    }
    public Setting<?> getSetting(String name) {
        return settings.stream()
                .filter(setting -> setting.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <T> Setting<T> getSetting(String name, Class<T> type) {
        Setting<?> setting = getSetting(name);
        if (setting != null && setting.getClass().isAssignableFrom(type)) {
            return (Setting<T>) setting;
        }
        return null;
    }
}