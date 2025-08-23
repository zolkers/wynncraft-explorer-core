package com.edgn.api.uifw.ui.core.models.values;

public class DefaultFloatModel implements FloatModel {
    private float value;
    public DefaultFloatModel() { this(0f); }
    public DefaultFloatModel(float initial) { value = initial; }
    @Override public float get(){ return value; }
    @Override public void set(float v){ value = v; }
}