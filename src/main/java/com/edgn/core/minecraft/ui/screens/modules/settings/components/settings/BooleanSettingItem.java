package com.edgn.core.minecraft.ui.screens.modules.settings.components.settings;

import com.edgn.api.uifw.ui.core.item.items.SwitchItem;
import com.edgn.api.uifw.ui.core.models.setting.SettingBooleanModel;
import com.edgn.api.uifw.ui.css.StyleKey;
import com.edgn.api.uifw.ui.css.UIStyleSystem;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.module.settings.BooleanSetting;

public class BooleanSettingItem extends SwitchItem {
    private final BooleanSetting setting;
    private final ISettingsScreen screen;

    public BooleanSettingItem(UIStyleSystem ui, int x, int y, int w, int h,
                              BooleanSetting setting, ISettingsScreen screen) {
        super(ui, x, y, w, h, new SettingBooleanModel(setting));
        this.setting = setting;
        this.screen = screen;

        int accent = screen != null ? screen.getAccentColor() : 0xFF4F8EF7;
        int offTrack = 0xFF444444;

        trackSize(40, 20)
            .thumbSize(16)
            .thumbPadding(2)
            .onTrack(accent)
            .offTrack(offTrack)
            .thumbColor(0xFFFFFFFF)
            .hoverOutline(UIStyleSystem.applyOpacity(accent, 0.40f));

        addClass(StyleKey.SHADOW_SM, StyleKey.FOCUS_RING, StyleKey.HOVER_BRIGHTEN, StyleKey.ROUNDED_FULL);
    }

    public BooleanSetting getSetting() { return setting; }
    public ISettingsScreen getScreen() { return screen; }
}