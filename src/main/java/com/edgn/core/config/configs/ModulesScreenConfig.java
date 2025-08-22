package com.edgn.core.config.configs;

import com.edgn.core.config.ConfigManager;
import com.google.gson.annotations.Expose;

public class ModulesScreenConfig {

    @ConfigManager.SaveField
    @Expose
    private boolean isDarkMode = true;

    @ConfigManager.SaveField
    @Expose
    private boolean animationsEnabled = true;

    @ConfigManager.SaveField
    @Expose
    private String lastSelectedCategory = null;

    @ConfigManager.SaveField
    @Expose
    private String lastSearchQuery = "";

    public ModulesScreenConfig() {}

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public boolean isAnimationsEnabled() {
        return animationsEnabled;
    }

    public String getLastSelectedCategory() {
        return lastSelectedCategory;
    }

    public String getLastSearchQuery() {
        return lastSearchQuery;
    }

    public void setDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
    }

    public void setAnimationsEnabled(boolean animationsEnabled) {
        this.animationsEnabled = animationsEnabled;
    }

    public void setLastSelectedCategory(String lastSelectedCategory) {
        this.lastSelectedCategory = lastSelectedCategory;
    }

    public void setLastSearchQuery(String lastSearchQuery) {
        this.lastSearchQuery = lastSearchQuery;
    }

    public void save() {
        try {
            boolean success = ConfigManager.saveObject("modules_screen_config", this, ModulesScreenConfig.class);
            if (!success) {
                System.err.println("Failed to save ModulesScreenConfig - ConfigManager returned false");
            }
        } catch (Exception e) {
            System.err.println("Exception while saving ModulesScreenConfig: " + e.getMessage());
        }
    }

    public static ModulesScreenConfig load() {
        return ConfigManager.loadObject("modules_screen_config", ModulesScreenConfig.class, ModulesScreenConfig::new);
    }
}