package com.edgn.core.module.settings;

import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.minecraft.ui.screens.modules.settings.components.SettingComponent;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import java.util.function.BiConsumer;

public abstract class Setting<T> {
    @Expose
    protected final String name;
    @Expose
    protected final String description;
    @Expose
    protected T value;
    @Expose
    protected final T defaultValue;

    protected boolean visible = true;

    @Deprecated
    protected Runnable onChangeCallback;

    protected BiConsumer<Object, Object> onValueChangedCallback;

    public Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public T getValue() { return value; }
    public T getDefaultValue() { return defaultValue; }
    public boolean isVisible() { return visible; }

    public void setValue(T newValue) {
        T oldValue = this.value;

        // Empêche la valeur de devenir null.
        this.value = (newValue != null) ? newValue : this.defaultValue;

        // Appeler l'ancien callback pour la compatibilité
        if (onChangeCallback != null) {
            onChangeCallback.run();
        }

        // Appeler le nouveau callback avec les valeurs
        if (onValueChangedCallback != null) {
            onValueChangedCallback.accept(oldValue, this.value);
        }
    }

    public Setting<T> setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    @Deprecated
    public Setting<T> onChanged(Runnable callback) {
        this.onChangeCallback = callback;
        return this;
    }

    public Setting<T> setOnValueChanged(BiConsumer<Object, Object> callback) {
        this.onValueChangedCallback = callback;
        return this;
    }

    public String getValueAsString() {
        return value == null ? "" : value.toString();
    }

    public BiConsumer<Object, Object> getOnValueChangedCallback() {
        return onValueChangedCallback;
    }

    public void reset() {
        setValue(defaultValue);
    }

    public abstract JsonElement toJsonElement();

    public abstract void fromJsonElement(JsonElement element);

    public abstract SettingComponent createComponent(ISettingsScreen screen, int x, int y, int width, int height);
}