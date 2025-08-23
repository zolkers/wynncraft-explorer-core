package com.edgn.core.minecraft.ui.screens.modules.settings.components;

import com.edgn.api.uifw.ui.utils.DrawingUtils;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.module.settings.BooleanSetting;
import net.minecraft.client.gui.DrawContext;

public class BooleanSettingComponent extends SettingComponent {
    private final BooleanSetting booleanSetting;

    public BooleanSettingComponent(BooleanSetting setting, ISettingsScreen screen, int x, int y, int width, int height) {
        super(setting, screen, x, y, width, height);
        this.booleanSetting = setting;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean value = booleanSetting.getValue();
        boolean hovered = isHovered();

        int bgColor = hovered ? (screen.getAccentColor() & 0x40FFFFFF) : (screen.getBgSecondary());
        DrawingUtils.drawRoundedRect(context, getX(), getY(), width, height, 8, bgColor);

        int switchWidth = 40;
        int switchHeight = 20;
        int switchX = getX() + width - switchWidth - 10;
        int switchY = getY() + (height - switchHeight) / 2;

        int trackColor = value ? screen.getAccentColor() : (hovered ? 0xFF555555 : 0xFF444444);
        DrawingUtils.drawRoundedRect(context, switchX, switchY, switchWidth, switchHeight, switchHeight / 2, trackColor);

        int knobSize = switchHeight - 4;
        int knobX = value ? (switchX + switchWidth - knobSize - 2) : (switchX + 2);
        int knobY = switchY + 2;
        int knobColor = hovered ? 0xFFFFFFFF : 0xFFDDDDDD;

        DrawingUtils.drawShadow(context, knobX, knobY, knobSize, knobSize, 1, 1, 0x40000000);
        DrawingUtils.drawRoundedRect(context, knobX, knobY, knobSize, knobSize, knobSize / 2, knobColor);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && DrawingUtils.isPointInRect(mouseX, mouseY, getX(), getY(), width, height)) {
            booleanSetting.setValue(!booleanSetting.getValue());
            return true;
        }
        return false;
    }
}