package com.edgn.api.uifw.ui.core.models.setting;

import com.edgn.api.uifw.ui.core.models.slider.SliderModel;
import com.edgn.core.module.settings.DoubleSetting;

import java.util.Objects;

public final class SettingDoubleModel implements SliderModel<Double> {
    private final DoubleSetting setting;

    public SettingDoubleModel(DoubleSetting setting) {
        this.setting = Objects.requireNonNull(setting, "setting");
    }

    @Override public Double get() { return setting.getValue(); }
    @Override public void set(Double v) { setting.setValue(clampSnap(v)); }

    @Override public Double min() { return setting.getMin(); }
    @Override public Double max() { return setting.getMax(); }

    @Override public void setRange(Double min, Double max) {

    }

    @Override public Double step() { return setting.getStep(); }
    @Override public void setStep(Double step) {
        set(get());
    }

    @Override
    public Double clampSnap(Double v) {
        double mi = setting.getMin();
        double ma = setting.getMax();
        double st = Math.max(1e-12d, setting.getStep());

        double x = Math.max(mi, Math.min(ma, v));
        double n = Math.round((x - mi) / st);
        x = mi + n * st;
        return Math.max(mi, Math.min(ma, x));
    }

    @Override
    public Double ratioToValue(double t) {
        t = Math.max(0d, Math.min(1d, t));
        double mi = setting.getMin();
        double ma = setting.getMax();
        return clampSnap(mi + t * (ma - mi));
    }

    @Override
    public double valueToRatio(Double v) {
        double mi = setting.getMin();
        double ma = setting.getMax();
        double span = Math.max(1e-12d, ma - mi);
        double x = Math.max(mi, Math.min(ma, v));
        return (x - mi) / span;
    }
}
