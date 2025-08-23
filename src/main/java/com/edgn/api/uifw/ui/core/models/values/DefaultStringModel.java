package com.edgn.api.uifw.ui.core.models.values;

public class DefaultStringModel implements StringModel {
    private String value;

    public DefaultStringModel(String initial) {
        value = initial;
    }
    @Override
    public String get() {
        return value;
    }

    @Override
    public void set(String value) {
        this.value = value;
    }
}
