package com.edgn.core.minecraft.ui.screens.modules.settings.components;

import com.edgn.api.uifw.ui.utils.DrawingUtils;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.module.settings.ColorSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ColorSettingComponent extends SettingComponent {
    private final ColorSetting colorSetting;

    public ColorSettingComponent(ColorSetting setting, ISettingsScreen screen, int x, int y, int width, int height) {
        super(setting, screen, x, y, width, height);
        this.colorSetting = setting;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean hovered = isHovered();

        int bgColor = hovered ? (screen.getAccentColor() & 0x40FFFFFF) : screen.getBgSecondary();
        DrawingUtils.drawRoundedRect(context, getX(), getY(), width, height, 8, bgColor);

        int colorPreviewSize = height - 8;
        int colorX = getX() + width - colorPreviewSize - 10;
        int colorY = getY() + 4;

        drawCheckerboard(context, colorX, colorY, colorPreviewSize, colorPreviewSize);

        DrawingUtils.drawRoundedRect(context, colorX, colorY, colorPreviewSize, colorPreviewSize, 6, colorSetting.getValue());

        int borderColor = hovered ? screen.getAccentColor() : screen.getTextSecondary();
        int borderThickness = hovered ? 2 : 1;
        DrawingUtils.drawRoundedRectBorder(context, colorX, colorY, colorPreviewSize, colorPreviewSize, 6, borderColor, borderThickness);

        if (hovered) {
            String paletteIcon = "🎨";
            context.drawText(MinecraftClient.getInstance().textRenderer, paletteIcon,
                    colorX + 2, colorY + 2, 0xFFFFFFFF, false);
        }
    }

    private void drawCheckerboard(DrawContext context, int x, int y, int width, int height) {
        for (int i = 0; i < width / 4; i++) {
            for (int j = 0; j < height / 4; j++) {
                boolean isDark = (i + j) % 2 == 0;
                int checkerColor = isDark ? 0xFF808080 : 0xFFC0C0C0;
                context.fill(x + i * 4, y + j * 4, x + i * 4 + 4, y + j * 4 + 4, checkerColor);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int colorPreviewSize = height - 8;
        int colorX = getX() + width - colorPreviewSize - 10;

        if (DrawingUtils.isPointInRect(mouseX, mouseY, colorX, getY() + 4, colorPreviewSize, colorPreviewSize) && button == 0) {
            MinecraftClient.getInstance().setScreen(
                    new ColorPickerScreen(
                            screen,
                            this.colorSetting.getValue(),
                            this.colorSetting::setValue
                    )
            );
            return true;
        }
        return false;
    }
}