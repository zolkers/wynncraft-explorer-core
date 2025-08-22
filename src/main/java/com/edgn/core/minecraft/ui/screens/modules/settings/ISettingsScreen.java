package com.edgn.core.minecraft.ui.screens.modules.settings;

public interface ISettingsScreen {
    int getTextPrimary();
    int getTextSecondary();
    int getTextMuted();
    int getAccentColor();
    int getAccentHoverColor();
    int getBgPrimary();
    int getBgSecondary();
    boolean isDarkMode();
}