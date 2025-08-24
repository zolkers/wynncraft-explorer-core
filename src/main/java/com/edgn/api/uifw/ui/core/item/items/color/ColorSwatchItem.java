package com.edgn.api.uifw.ui.core.item.items.color;

import com.edgn.api.uifw.ui.core.item.BaseItem;
import com.edgn.api.uifw.ui.css.UIStyleSystem;
import com.edgn.api.uifw.ui.utils.DrawingUtils;
import net.minecraft.client.gui.DrawContext;

public class ColorSwatchItem extends BaseItem {
    private int color = 0xFFFFFFFF;

    public ColorSwatchItem(UIStyleSystem s, int x, int y, int w, int h) {
        super(s, x, y, w, h);
    }

    public ColorSwatchItem setColor(int argb) { this.color = argb; return this; }

    @Override
    public void render(DrawContext ctx) {
        int x = getCalculatedX();
        int y = getCalculatedY();
        int w = getCalculatedWidth();
        int h = getCalculatedHeight();

        DrawingUtils.drawRoundedRect(ctx, x, y, w, h, 8, getComputedStyles().getBackgroundColor());
        DrawingUtils.drawCheckerboard(ctx, x+6, y+6, w-12, h-12);
        ctx.fill(x+6, y+6, x+w-6, y+h-6, color);
        DrawingUtils.drawBorder(ctx, x, y, w, h, 0xFF000000, 1);
    }
}
