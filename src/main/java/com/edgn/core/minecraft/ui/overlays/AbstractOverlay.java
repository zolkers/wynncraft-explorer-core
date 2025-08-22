package com.edgn.core.minecraft.ui.overlays;

public abstract class AbstractOverlay implements IOverlay{

    protected boolean visible = false;

    @Override
    public void hide() {
        this.visible = false;
    }

    @Override
    public void show() {
        this.visible = true;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void toggle() {
        if (!this.visible) show();
        else hide();
    }
}
