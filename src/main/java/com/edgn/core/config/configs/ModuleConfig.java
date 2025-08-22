package com.edgn.core.config.configs;

import com.edgn.core.config.ConfigManager;
import com.edgn.core.module.basic.data.ModuleData;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class ModuleConfig {
    @ConfigManager.SaveField
    @Expose
    private List<ModuleData> modules;

    public ModuleConfig() {
        this.modules = new ArrayList<>();
    }

    public List<ModuleData> getModules() {
        return modules;
    }

    public void setModules(List<ModuleData> modules) {
        this.modules = modules;
    }

    public void addModule(ModuleData moduleData) {
        removeModuleById(moduleData.getId());
        modules.add(moduleData);
    }

    public void removeModuleById(String id) {
        modules.removeIf(data -> data.getId().equals(id));
    }

    public void clear() {
        modules.clear();
    }

    public static ModuleConfig load() {
        return ConfigManager.loadObject("modules", ModuleConfig.class, ModuleConfig::new);
    }

    public void save() {
        ConfigManager.saveObject("modules", this, ModuleConfig.class);
    }
}