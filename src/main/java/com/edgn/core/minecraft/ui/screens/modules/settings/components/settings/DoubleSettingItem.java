package com.edgn.core.minecraft.ui.screens.modules.settings.components.settings;

import com.edgn.api.uifw.ui.core.item.items.SliderItem;
import com.edgn.api.uifw.ui.core.models.setting.SettingDoubleModel;
import com.edgn.api.uifw.ui.css.UIStyleSystem;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.module.settings.DoubleSetting;

public class DoubleSettingItem extends SliderItem<Double> {
    private final DoubleSetting setting;
    private final ISettingsScreen screen;

    public DoubleSettingItem(UIStyleSystem ui,
                             int x, int y, int width, int height,
                             DoubleSetting setting, ISettingsScreen screen) {
        super(ui, x, y, width, height, new SettingDoubleModel(setting));
        this.setting = setting;
        this.screen = screen;

        trackHeight(6)
            .trackRadius(3)
            .thumbSize(10)
            .trackColor(0xFF525252)
            .fillColor(screen.getAccentColor())
            .thumbColor(0xFFE5E7EB);

        valuePosition(ValuePosition.TOP)
            .formatter(v -> String.format("%.2f", v))
            .textColor(screen.getTextSecondary());
    }

    public DoubleSetting getSetting() { return setting; }
    public ISettingsScreen getScreen() { return screen; }
}
