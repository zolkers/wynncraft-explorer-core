package com.edgn.core.minecraft.ui.screens.modules.settings.components.settings;

import com.edgn.api.uifw.ui.core.item.items.ButtonItem;
import com.edgn.api.uifw.ui.css.StyleKey;
import com.edgn.api.uifw.ui.css.UIStyleSystem;
import com.edgn.api.uifw.ui.utils.DrawingUtils;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.minecraft.ui.screens.modules.settings.components.complementary.ColorPickerScreen;
import com.edgn.core.module.settings.ColorSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ColorSettingItem extends ButtonItem {
    private final ColorSetting colorSetting;
    private final ISettingsScreen screen;

    public ColorSettingItem(
            UIStyleSystem ui, int x, int y, int width, int height,
            ColorSetting setting, ISettingsScreen screen
    ) {
        super(ui, x, y, width, height);
        this.colorSetting = setting;
        this.screen = screen;

        backgroundColor(screen.getBgSecondary());
        addClass(StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.HOVER_BRIGHTEN, StyleKey.P_2);

        onClick(() -> MinecraftClient.getInstance().setScreen(
                new ColorPickerScreen(
                        screen,
                        colorSetting.getValue(),
                        colorSetting::setValue
                )
        ));
    }

    @Override
    public void render(DrawContext context) {
        super.render(context);

        int cx = getCalculatedX();
        int cy = getCalculatedY();
        int cw = getCalculatedWidth();
        int ch = getCalculatedHeight();

        int previewSize = Math.max(12, ch - 8);
        int px = cx + cw - previewSize - 10;
        int py = cy + 4;

        drawCheckerboard(context, px, py, previewSize, previewSize);
        DrawingUtils.drawRoundedRect(context, px, py, previewSize, previewSize, 6, colorSetting.getValue());

        int borderColor = isHovered() ? screen.getAccentColor() : screen.getTextSecondary();
        int borderThickness = isHovered() ? 2 : 1;
        DrawingUtils.drawRoundedRectBorder(context, px, py, previewSize, previewSize, 6, borderColor, borderThickness);

        if (isHovered()) {
            context.drawText(MinecraftClient.getInstance().textRenderer, "🎨", px + 2, py + 2, 0xFFFFFFFF, false);
        }
    }

    private void drawCheckerboard(DrawContext context, int x, int y, int w, int h) {
        int tile = 4;
        int xTiles = Math.max(1, w / tile);
        int yTiles = Math.max(1, h / tile);
        for (int i = 0; i < xTiles; i++) {
            for (int j = 0; j < yTiles; j++) {
                boolean dark = ((i + j) & 1) == 0;
                int c = dark ? 0xFF808080 : 0xFFC0C0C0;
                int x0 = x + i * tile;
                int y0 = y + j * tile;
                context.fill(x0, y0, x0 + tile, y0 + tile, c);
            }
        }
    }
}
