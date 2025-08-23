package com.edgn.core.minecraft.ui.screens.modules.settings.components;

import com.edgn.api.uifw.ui.utils.DrawingUtils;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.module.settings.EnumSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class EnumSettingComponent<T extends Enum<T>> extends SettingComponent {
    private final EnumSetting<T> enumSetting;

    public EnumSettingComponent(EnumSetting<T> setting, ISettingsScreen screen, int x, int y, int width, int height) {
        super(setting, screen, x, y, width, height);
        this.enumSetting = setting;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean hovered = isHovered();

        int bgColor = hovered ? (screen.getAccentColor() & 0x40FFFFFF) : screen.getBgSecondary();
        DrawingUtils.drawRoundedRect(context, getX(), getY(), width, height, 8, bgColor);

        int selectorWidth = 120;
        int selectorHeight = height - 8;
        int selectorX = getX() + width - selectorWidth - 10;
        int selectorY = getY() + 4;

        int selectorBgColor = hovered ? screen.getAccentHoverColor() : screen.getAccentColor();
        DrawingUtils.drawRoundedRect(context, selectorX, selectorY, selectorWidth, selectorHeight, 6, selectorBgColor);

        String text = enumSetting.getValue().name();
        String formattedText = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase().replace("_", " ");

        context.drawCenteredTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                formattedText,
                selectorX + selectorWidth / 2,
                selectorY + (selectorHeight - 8) / 2,
                screen.getTextPrimary()
        );

        if (hovered) {
            context.drawText(MinecraftClient.getInstance().textRenderer, "◀",
                    selectorX + 4, selectorY + (selectorHeight - 8) / 2, 0xAAFFFFFF, false);

            context.drawText(MinecraftClient.getInstance().textRenderer, "▶",
                    selectorX + selectorWidth - 12, selectorY + (selectorHeight - 8) / 2, 0xAAFFFFFF, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int selectorWidth = 120;
        int selectorX = getX() + width - selectorWidth - 10;

        if (DrawingUtils.isPointInRect(mouseX, mouseY, selectorX, getY(), selectorWidth, height)) {
            if (button == 0) {
                this.enumSetting.cycle();
                return true;
            } else if (button == 1) {
                this.enumSetting.cycleBackward();
                return true;
            }
        }
        return false;
    }
}