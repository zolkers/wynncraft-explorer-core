package com.edgn.core.minecraft.ui.screens.modules.settings.components;

import com.edgn.api.uifw.ui.utils.DrawingUtils;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.module.settings.DoubleSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class DoubleSettingComponent extends SettingComponent {
    private final DoubleSetting doubleSetting;
    private boolean dragging = false;

    public DoubleSettingComponent(DoubleSetting setting, ISettingsScreen screen, int x, int y, int width, int height) {
        super(setting, screen, x, y, width, height);
        this.doubleSetting = setting;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean hovered = isHovered();

        
        int bgColor = hovered ? (screen.getAccentColor() & 0x40FFFFFF) : screen.getBgSecondary();
        DrawingUtils.drawRoundedRect(context, getX(), getY(), width, height, 8, bgColor);

        
        String valueText = String.format("%.2f", doubleSetting.getValue());
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer,
                valueText, getX() + 10, getY() + height - 16, screen.getTextSecondary());

        
        int sliderWidth = 120;
        int sliderHeight = 6;
        int sliderX = getX() + width - sliderWidth - 10;
        int sliderY = getY() + (height - sliderHeight) / 2;

        
        DrawingUtils.drawRoundedRect(context, sliderX, sliderY, sliderWidth, sliderHeight, 3, 0xFF525252);

        
        double progress = (doubleSetting.getValue() - doubleSetting.getMin()) /
                (doubleSetting.getMax() - doubleSetting.getMin());
        int progressWidth = (int) (sliderWidth * progress);

        if (progressWidth > 0) {
            DrawingUtils.drawRoundedRect(context, sliderX, sliderY, progressWidth, sliderHeight, 3, screen.getAccentColor());
        }

        
        int thumbX = sliderX + progressWidth - 4;
        int thumbY = sliderY - 2;
        DrawingUtils.drawRoundedRect(context, thumbX, thumbY, 8, 10, 4,
                dragging ? 0xFFffffff : (hovered ? 0xFFe5e7eb : 0xFFd1d5db));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isSliderArea(mouseX, mouseY)) {
            dragging = true;
            updateValueFromMouse(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && dragging) {
            dragging = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            updateValueFromMouse(mouseX);
            return true;
        }
        return false;
    }

    private boolean isSliderArea(double mouseX, double mouseY) {
        int sliderWidth = 120;
        int sliderX = getX() + width - sliderWidth - 10;
        int sliderY = getY() + (height - 6) / 2 - 5;
        return mouseX >= sliderX && mouseX <= sliderX + sliderWidth &&
                mouseY >= sliderY && mouseY <= sliderY + 16;
    }

    private void updateValueFromMouse(double mouseX) {
        int sliderWidth = 120;
        int sliderX = getX() + width - sliderWidth - 10;

        double progress = Math.max(0, Math.min(1, (mouseX - sliderX) / sliderWidth));
        double newValue = doubleSetting.getMin() + progress * (doubleSetting.getMax() - doubleSetting.getMin());

        
        double step = doubleSetting.getStep();
        newValue = Math.round(newValue / step) * step;

        doubleSetting.setValue(newValue);
    }
}
