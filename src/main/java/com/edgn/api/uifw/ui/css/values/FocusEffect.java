package com.edgn.api.uifw.ui.css.values;

public enum FocusEffect {
    RING(true),
    OUTLINE(true);

    public final boolean value;

    FocusEffect(boolean value) {
        this.value = value;
    }
}