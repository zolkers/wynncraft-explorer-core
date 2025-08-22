package com.edgn.core.module.basic.data;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

public class ModuleData {

    @Expose
    private String name;

    @Expose
    private String id;

    @Expose
    private boolean enabled;

    @Expose
    private Map<String, JsonElement> settings;

    public ModuleData() {
        this.settings = new HashMap<>();
    }

    public ModuleData(String name, String id, boolean enabled) {
        this.name = name;
        this.id = id;
        this.enabled = enabled;
        this.settings = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, JsonElement> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, JsonElement> settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "ModuleData{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", enabled=" + enabled +
                ", settings=" + settings +
                '}';
    }
}