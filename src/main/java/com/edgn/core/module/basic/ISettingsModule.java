package com.edgn.core.module.basic;

import com.edgn.core.module.settings.SettingsGroup;
import java.util.List;

public interface ISettingsModule {
    List<SettingsGroup> getSettingsGroups();
    void onSettingsChanged();
}