package com.edgn.api.uifw.ui.core.item.items.color;

import com.edgn.api.uifw.ui.core.item.BaseItem;
import com.edgn.api.uifw.ui.css.UIStyleSystem;
import com.edgn.api.uifw.ui.utils.DrawingUtils;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;
import java.util.function.BiConsumer;

public class SVPadItem extends BaseItem {
    private float hue = 0f;
    private float s = 1f;
    private float v = 1f; // 0..1
    private boolean dragging = false;
    private BiConsumer<Float, Float> onChange;

    public SVPadItem(UIStyleSystem styleSystem, int x, int y, int w, int h) {
        super(styleSystem, x, y, w, h);
    }

    public SVPadItem setHue(float degrees) { this.hue = degrees; return this; }
    public SVPadItem setSaturation(float s) { this.s = clamp01(s); return this; }
    public SVPadItem setValue(float v) { this.v = clamp01(v); return this; }
    public SVPadItem onChange(BiConsumer<Float, Float> cb) { this.onChange = cb; return this; }

    @Override
    public void render(DrawContext ctx) {
        final int cx = getCalculatedX();
        final int cy = getCalculatedY();
        final int cw = Math.max(1, getCalculatedWidth());
        final int ch = Math.max(1, getCalculatedHeight());

        final int radius = Math.max(4, getComputedStyles().getBorderRadius());
        int bg = getComputedStyles().getBackgroundColor();
        if (bg == 0) bg = 0xFF111113;

        DrawingUtils.drawRoundedRect(ctx, cx, cy, cw, ch, radius, bg);
        final int fullHue = (Color.HSBtoRGB(hue / 360f, 1f, 1f) | 0xFF000000);
        ctx.fillGradient(cx, cy, cx + cw, cy + ch, 0xFFFFFFFF, fullHue);

        ctx.fillGradient(cx, cy, cx + cw, cy + ch, 0x00FFFFFF, 0xFF000000);

        if (isFocused()) {
            DrawingUtils.drawBorder(ctx, cx, cy, cw, ch, 0x60A78BFA, 2);
        } else if (isHovered()) {
            DrawingUtils.drawBorder(ctx, cx, cy, cw, ch, 0x40FFFFFF, 1);
        }

        final int ix = clampInt(cx, cx + cw - 1, cx + Math.round(s * (cw - 1)));
        final int iy = clampInt(cy, cy + ch - 1, cy + Math.round((1f - v) * (ch - 1)));
        DrawingUtils.drawRoundedRect(ctx, ix - 4, iy - 4, 8, 8, 4, 0xFFFFFFFF);
        DrawingUtils.drawRoundedRect(ctx, ix - 2, iy - 2, 4, 4, 2, 0xFF000000);
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled || !canInteract(mouseX, mouseY)) return false;
        dragging = true;
        updateFrom(mouseX, mouseY);
        return true;
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double dx, double dy) {
        if (!enabled || !dragging) return false;
        updateFrom(mouseX, mouseY);
        return true;
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        boolean was = dragging; dragging = false; return was;
    }

    private void updateFrom(double mx, double my) {
        final int cx = getCalculatedX(), cy = getCalculatedY();
        final int cw = Math.max(1, getCalculatedWidth());
        final int ch = Math.max(1, getCalculatedHeight());

        final double tx = clamp01d((mx - cx) / Math.max(1.0, cw - 1.0));
        final double ty = clamp01d((my - cy) / Math.max(1.0, ch - 1.0));

        s = (float) tx;
        v = 1f - (float) ty;

        if (onChange != null) onChange.accept(s, v);
    }

    private static float clamp01(float v) { return Math.max(0f, Math.min(1f, v)); }
    private static double clamp01d(double v) { return Math.max(0.0, Math.min(1.0, v)); }
    private static int clampInt(int lo, int hi, int v) { return (v < lo) ? lo : (v > hi) ? hi : v; }
}
