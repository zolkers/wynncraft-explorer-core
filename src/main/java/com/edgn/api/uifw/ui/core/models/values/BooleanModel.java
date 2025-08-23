package com.edgn.api.uifw.ui.core.models.values;

public interface BooleanModel {
    boolean get();
    void set(boolean value);
    default void toggle() { set(!get()); }
}