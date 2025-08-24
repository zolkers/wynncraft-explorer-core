package com.edgn.api.uifw.ui.core.item.items.color;

import com.edgn.api.uifw.ui.core.item.BaseItem;
import com.edgn.api.uifw.ui.css.UIStyleSystem;
import com.edgn.api.uifw.ui.utils.DrawingUtils;
import net.minecraft.client.gui.DrawContext;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;

public class GradientSliderItem extends BaseItem {
    private double value = 0.0; // 0..1
    private boolean dragging = false;
    private int thumbSize = 10;
    private boolean checkerboard = false;
    private DoubleFunction<Integer> gradient; // t -> ARGB
    private DoubleConsumer onChange;

    public GradientSliderItem(UIStyleSystem s, int x, int y, int w, int h) {
        super(s, x, y, w, h);
    }

    public GradientSliderItem withThumbSize(int px) { this.thumbSize = Math.max(6, px); return this; }
    public GradientSliderItem withGradient(DoubleFunction<Integer> g) { this.gradient = g; return this; }
    public GradientSliderItem withCompositeCheckerboard(boolean v) { this.checkerboard = v; return this; }
    public GradientSliderItem withValue(double v) { this.value = clamp01(v); return this; }
    public GradientSliderItem onChange(DoubleConsumer cb) { this.onChange = cb; return this; }
    public void setValue(double v) { this.value = clamp01(v); }

    @Override
    public void render(DrawContext ctx) {
        int x = getCalculatedX(), y = getCalculatedY();
        int w = getCalculatedWidth(), h = getCalculatedHeight();

        int bg = getComputedStyles().getBackgroundColor();
        DrawingUtils.drawRoundedRect(ctx, x, y, w, h, 6, bg == 0 ? 0xFF2A2A2E : bg);

        if (checkerboard) DrawingUtils.drawCheckerboard(ctx, x+1, y+1, w-2, h-2);

        for (int i = 0; i < w; i++) {
            double t = (w <= 1) ? 0 : (double) i / (double) (w - 1);
            int c = gradient != null ? gradient.apply(t) : 0xFFFFFFFF;
            ctx.fill(x + i, y + 1, x + i + 1, y + h - 1, c);
        }

        // thumb
        int tx = x + (int) Math.round(value * (w - 1));
        DrawingUtils.drawRoundedRect(ctx, tx - thumbSize/2, y - 2, thumbSize, h + 4, 4, 0xFFFFFFFF);
        DrawingUtils.drawBorder(ctx, tx - thumbSize/2, y - 2, thumbSize, h + 4, 0xFF000000, 1);
    }

    @Override
    public boolean onMouseClick(double mx, double my, int b) {
        if (!enabled || !canInteract(mx, my)) return false;
        dragging = true; updateFrom(mx); return true;
    }

    @Override
    public boolean onMouseDrag(double mx, double my, int b, double dx, double dy) {
        if (!enabled || !dragging) return false;
        updateFrom(mx); return true;
    }

    @Override
    public boolean onMouseRelease(double mx, double my, int b) {
        boolean was = dragging; dragging = false; return was;
    }

    private void updateFrom(double mx) {
        int x = getCalculatedX(), w = getCalculatedWidth();
        double t = (mx - x) / Math.max(1.0, (double) w - 1.0);
        value = clamp01(t);
        if (onChange != null) onChange.accept(value);
    }

    private static double clamp01(double v) { return Math.max(0.0, Math.min(1.0, v)); }
}
