package com.edgn.core.module.basic;

import com.edgn.Main;
import com.edgn.annotations.DefaultEnabled;
import com.edgn.annotations.DisabledOnLaunch;
import com.edgn.core.module.basic.data.SimpleModuleSaveManager;
import com.edgn.core.module.settings.Setting;
import com.edgn.core.module.settings.SettingsGroup;
import com.google.gson.JsonElement;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractModule implements ISettingsModule {
    private final String name;
    private final String id;
    private boolean enabled;
    protected List<SettingsGroup> settingsGroups;
    protected final MinecraftClient client = MinecraftClient.getInstance();

    private Map<String, JsonElement> loadedSettingsData = null;

    protected AbstractModule(String name) {
        this.name = name;
        this.id = this.getClass().getSimpleName();
        this.enabled = this.getClass().isAnnotationPresent(DefaultEnabled.class);
    }

    public final void enable() {
        if (!enabled) {
            enabled = true;
            onEnable();
            SimpleModuleSaveManager.saveModule(this);
            String message = "🟢 " + name + " enabled";
            Main.OVERLAY_MANAGER.getLoggerOverlay().success(message, false);
        }
    }

    public final void disable() {
        if (enabled) {
            enabled = false;
            onDisable();
            SimpleModuleSaveManager.saveModule(this);
            String message = "🔴 " + name + " disabled";
            Main.OVERLAY_MANAGER.getLoggerOverlay().info(message, false);
        }
    }

    public final void toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
    }

    public final void save() {
        SimpleModuleSaveManager.saveModule(this);
    }

    public static void saveAllModules() {
        SimpleModuleSaveManager.saveAllModules();
    }

    public static void loadAllModules() {
        SimpleModuleSaveManager.loadAllModules();
    }

    public static void initSaveManager() {
        SimpleModuleSaveManager.init();
    }

    public void setEnabledStateFromConfig(boolean enabled) {
        this.enabled = enabled;
    }

    public void performInitialActivation() {
        if(this.getClass().isAnnotationPresent(DisabledOnLaunch.class)) return;
        if (this.enabled) {
            this.onEnable();
        }
    }

    protected abstract void initializeSettings();
    protected abstract void onEnable();
    protected abstract void onDisable();

    public void setLoadedSettingsData(Map<String, JsonElement> data) {
        this.loadedSettingsData = data;
    }

    public void finishInitialization() {
        this.settingsGroups = new ArrayList<>();
        this.initializeSettings();

        if (this.loadedSettingsData != null) {
            applyLoadedData();
        }
        this.setupSettingsCallbacks();
    }

    private void applyLoadedData() {
        for (SettingsGroup group : this.settingsGroups) {
            for (Setting<?> setting : group.getSettings()) {
                if (loadedSettingsData.containsKey(setting.getName())) {
                    JsonElement savedElement = loadedSettingsData.get(setting.getName());
                    setting.fromJsonElement(savedElement);
                }
            }
        }
        this.loadedSettingsData = null;
    }

    protected void setupSettingsCallbacks() {
        for (SettingsGroup group : settingsGroups) {
            group.getSettings().forEach(setting -> {
                var existingCallback = setting.getOnValueChangedCallback();
                setting.setOnValueChanged((oldValue, newValue) -> {
                    if (existingCallback != null) {
                        existingCallback.accept(oldValue, newValue);
                    }
                    save();
                });
            });
        }
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<SettingsGroup> getSettingsGroups() {
        return settingsGroups;
    }

}