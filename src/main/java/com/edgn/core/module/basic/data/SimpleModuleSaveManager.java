package com.edgn.core.module.basic.data;

import com.edgn.Main;
import com.edgn.core.config.configs.ModuleConfig;
import com.edgn.core.module.basic.AbstractModule;
import com.edgn.core.module.basic.ISettingsModule;
import com.edgn.core.module.basic.ModuleManager;
import com.edgn.core.module.settings.Setting;
import com.edgn.core.module.settings.SettingsGroup;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;


public class SimpleModuleSaveManager {
    private static ModuleConfig moduleConfig;

    public static void init() {
        moduleConfig = ModuleConfig.load();
    }

    public static void saveAllModules() {
        if (moduleConfig == null) init();
        try {
            moduleConfig.clear();
            for (AbstractModule module : ModuleManager.getInstance().getModules()) {
                moduleConfig.addModule(createModuleData(module));
            }
            moduleConfig.save();
            Main.OVERLAY_MANAGER.getLoggerOverlay().success("🍉 " + moduleConfig.getModules().size() + " modules saved successfully", false);
        } catch (Exception e) {
            Main.OVERLAY_MANAGER.getLoggerOverlay().error("❌ Failed to save modules: " + e.getMessage(), true);
        }
    }

    public static void loadAllModules() {
        if (moduleConfig == null) init();
        try {
            for (ModuleData moduleData : moduleConfig.getModules()) {
                AbstractModule module = findModuleById(moduleData.getId());
                if (module != null) {
                    module.setEnabledStateFromConfig(moduleData.isEnabled());

                    module.setLoadedSettingsData(moduleData.getSettings());
                }
            }
            Main.OVERLAY_MANAGER.getLoggerOverlay().success("🍉 " + moduleConfig.getModules().size() + " modules loaded successfully", false);
        } catch (Exception e) {
            Main.OVERLAY_MANAGER.getLoggerOverlay().error("❌ Failed to load modules: " + e.getMessage(), true);
        }
    }

    public static void saveModule(AbstractModule module) {
        if (moduleConfig == null) init();
        try {
            moduleConfig.addModule(createModuleData(module));
            moduleConfig.save();
        } catch (Exception e) {
            Main.OVERLAY_MANAGER.getLoggerOverlay().error("❌ Failed to save module " + module.getName() + ": " + e.getMessage(), true);
        }
    }

    private static ModuleData createModuleData(AbstractModule module) {
        ModuleData moduleData = new ModuleData(module.getName(), module.getClass().getSimpleName(), module.isEnabled());
        Map<String, JsonElement> settingsMap = new HashMap<>();

        if (module instanceof ISettingsModule) {
            for (SettingsGroup group : ((ISettingsModule) module).getSettingsGroups()) {
                for (Setting<?> setting : group.getSettings()) {
                    settingsMap.put(setting.getName(), setting.toJsonElement());
                }
            }
        }
        moduleData.setSettings(settingsMap);
        return moduleData;
    }

    private static void applySettingsDataToModule(ISettingsModule module, Map<String, JsonElement> savedSettings) {
        for (SettingsGroup group : module.getSettingsGroups()) {
            for (Setting<?> setting : group.getSettings()) {
                if (savedSettings.containsKey(setting.getName())) {
                    JsonElement savedElement = savedSettings.get(setting.getName());
                    try {
                        setting.fromJsonElement(savedElement);
                    } catch (Exception e) {
                        Main.OVERLAY_MANAGER.getLoggerOverlay().warn(
                                "⚠️ Failed to load setting '" + setting.getName() + "'. Using default value. Error: " + e.getMessage(),
                                false
                        );
                    }
                }
            }
        }
    }

    private static AbstractModule findModuleById(String id) {
        return ModuleManager.getInstance().getModules().stream()
                .filter(module -> module.getClass().getSimpleName().equals(id))
                .findFirst()
                .orElse(null);
    }
}