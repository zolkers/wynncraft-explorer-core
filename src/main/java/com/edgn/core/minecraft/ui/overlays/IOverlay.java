package com.edgn.core.minecraft.ui.overlays;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public interface IOverlay {
    void render(DrawContext context, MinecraftClient client);
    void hide();
    void show();
    void toggle();
    boolean isVisible();
    void initialize();
}
