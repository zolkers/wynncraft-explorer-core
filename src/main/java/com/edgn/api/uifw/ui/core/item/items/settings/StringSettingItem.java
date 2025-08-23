package com.edgn.api.uifw.ui.core.item.items.settings;

import com.edgn.api.uifw.ui.core.item.items.TextFieldItem;
import com.edgn.api.uifw.ui.css.StyleKey;
import com.edgn.api.uifw.ui.css.UIStyleSystem;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.module.settings.StringSetting;

public class StringSettingItem extends TextFieldItem {
    private final StringSetting setting;
    private final ISettingsScreen screen;

    public StringSettingItem(UIStyleSystem ui,
                             int x, int y, int width, int height,
                             StringSetting setting,
                             ISettingsScreen screen) {
        super(ui, x, y, width, height);
        this.setting = setting;
        this.screen = screen;

        String initial = setting.getValue() == null ? "" : setting.getValue();
        setText(initial);
        setMaxLength(safeMaxLen(setting));

        setBackgroundColor(screen.getBgSecondary());
        addClass(StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.P_2, StyleKey.HOVER_BRIGHTEN);

        onChange(txt -> setting.setValue(txt != null ? txt : ""));
        onEnter(() -> setting.setValue(getText()));
        onFocusLost(() -> setting.setValue(getText()));
    }

    private int safeMaxLen(StringSetting s) {
        try {
            int m = s.getMaxLength();
            return m > 0 ? m : Integer.MAX_VALUE;
        } catch (Throwable t) {
            return Integer.MAX_VALUE;
        }
    }

    public StringSetting getSetting() { return setting; }
    public ISettingsScreen getScreen() { return screen; }
}
