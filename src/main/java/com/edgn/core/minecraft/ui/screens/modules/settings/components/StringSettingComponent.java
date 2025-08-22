package com.edgn.core.minecraft.ui.screens.modules.settings.components;

import com.edgn.uifw.utils.Render2D;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.module.settings.StringSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class StringSettingComponent extends SettingComponent {
    private final StringSetting stringSetting;
    private String currentText;

    public StringSettingComponent(StringSetting setting, ISettingsScreen screen, int x, int y, int width, int height) {
        super(setting, screen, x, y, width, height);
        this.stringSetting = setting;
        this.currentText = setting.getValue() == null ? "" : setting.getValue();
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            stringSetting.setValue(this.currentText);
        }
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean focused = this.isFocused();
        boolean hovered = this.isHovered();

        int bgColor = focused ? (screen.getAccentColor() & 0x60FFFFFF) :
                (hovered ? (screen.getAccentColor() & 0x40FFFFFF) : screen.getBgSecondary());
        Render2D.drawRoundedRect(context, getX(), getY(), width, height, 8, bgColor);

        int inputWidth = 200;
        int inputHeight = height - 8;
        int inputX = getX() + width - inputWidth - 10;
        int inputY = getY() + 4;

        int inputBgColor = focused ? screen.getAccentColor() : (hovered ? 0x55555555 : 0x33555555);
        int borderColor = focused ? screen.getAccentHoverColor() : (hovered ? screen.getAccentColor() : 0xFF888888);
        int borderThickness = focused ? 2 : 1;

        if (focused) {
            Render2D.drawShadow(context, inputX, inputY, inputWidth, inputHeight, 1, 1, 0x60000000);
        }

        Render2D.drawPanel(context, inputX, inputY, inputWidth, inputHeight, 6, inputBgColor, borderColor, borderThickness);

        String textIcon = "✏️";
        context.drawText(MinecraftClient.getInstance().textRenderer, textIcon,
                inputX + 4, inputY + (inputHeight - 8) / 2, screen.getTextPrimary(), false);

        String textToRender = this.currentText;
        boolean showCursor = focused && (System.currentTimeMillis() / 500) % 2 == 0;
        if (showCursor) {
            textToRender += "|";
        }

        int padding = 20;
        int innerX = inputX + padding;
        int innerWidth = inputWidth - padding - 4;

        Render2D.enableClipping(context, innerX, inputY, innerWidth, inputHeight);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int textWidth = textRenderer.getWidth(textToRender);
        int textX = innerX;

        if (textWidth > innerWidth) {
            int scrollOffset = textWidth - innerWidth;
            textX -= scrollOffset;
        }

        int textY = inputY + (inputHeight - 8) / 2;

        if (currentText.isEmpty() && !focused) {
            context.drawText(textRenderer, "Enter text...", innerX, textY, screen.getTextMuted(), false);
        } else {
            context.drawTextWithShadow(textRenderer, textToRender, textX, textY, screen.getTextPrimary());
        }

        Render2D.disableClipping(context);

        if (focused && !currentText.isEmpty()) {
            String lengthText = currentText.length() + "/" + stringSetting.getMaxLength();
            int lengthWidth = textRenderer.getWidth(lengthText);
            int lengthX = inputX + inputWidth - lengthWidth - 6;
            int lengthY = inputY - 12;

            Render2D.drawRoundedRect(context, lengthX - 2, lengthY - 1, lengthWidth + 4, 10, 3, 0xCC000000);
            context.drawText(textRenderer, lengthText, lengthX, lengthY, screen.getTextMuted(), false);
        }

        if (focused && !currentText.isEmpty() && isValidText(currentText)) {
            String checkmark = "✓";
            context.drawText(textRenderer, checkmark,
                    inputX + inputWidth - 16, inputY + (inputHeight - 8) / 2, 0xFF55FF55, false);
        }
    }

    private boolean isValidText(String text) {
        return text != null && !text.trim().isEmpty() && text.length() <= stringSetting.getMaxLength();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int inputWidth = 200;
        int inputX = getX() + width - inputWidth - 10;

        if (button == 0 && Render2D.isPointInRect(mouseX, mouseY, inputX, getY() + 4, inputWidth, height - 8)) {
            this.setFocused(true);
            return true;
        }

        this.setFocused(false);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.isFocused()) {
            return false;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.setFocused(false);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.currentText = this.stringSetting.getValue();
            this.setFocused(false);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (this.currentText != null && !this.currentText.isEmpty()) {
                this.currentText = this.currentText.substring(0, this.currentText.length() - 1);
            }
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_A && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            return true;
        }
        return keyCode == GLFW.GLFW_KEY_V && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!this.isFocused()) {
            return false;
        }

        if (chr >= ' ' && currentText.length() < stringSetting.getMaxLength()) {
            this.currentText += chr;
            return true;
        }
        return false;
    }
}