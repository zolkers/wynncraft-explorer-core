package com.edgn.api.uifw.ui.core.models.setting;

import com.edgn.api.uifw.ui.core.models.values.BooleanModel;
import com.edgn.core.module.settings.BooleanSetting;

import java.util.Objects;

public final class SettingBooleanModel implements BooleanModel {
    private final BooleanSetting setting;

    public SettingBooleanModel(BooleanSetting setting) {
        this.setting = Objects.requireNonNull(setting, "setting");
    }

    @Override public boolean get() { return setting.getValue(); }
    @Override public void set(boolean value) { setting.setValue(value); }
}