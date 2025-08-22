package com.edgn.uifw.elements.item.items;

import com.edgn.uifw.elements.item.BaseItem;
import com.edgn.uifw.utils.Render2D;
import com.edgn.uifw.layout.LayoutConstraints;
import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.css.rules.Shadow;
import com.edgn.uifw.components.TextComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

@SuppressWarnings({"unused", "unchecked", "UnusedReturnValue"})
public class ButtonItem extends BaseItem {
    private TextComponent textComponent;
    private int textSafetyMargin = 8;

    public ButtonItem(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
        addClass(StyleKey.PRIMARY, StyleKey.ROUNDED_MD, StyleKey.P_2, StyleKey.TEXT_WHITE);
    }

    public ButtonItem withText(String text) {
        if (text != null && !text.isEmpty()) {
            this.textComponent = new TextComponent(text, textRenderer)
                    .align(TextComponent.TextAlign.CENTER)
                    .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                    .truncate()
                    .setSafetyMargin(textSafetyMargin);
        }
        return this;
    }

    public ButtonItem withText(TextComponent textComponent) {
        if (textComponent != null) {
            this.textComponent = textComponent
                    .setOverflowMode(TextComponent.TextOverflowMode.TRUNCATE)
                    .setSafetyMargin(textSafetyMargin)
                    .align(TextComponent.TextAlign.CENTER)
                    .verticalAlign(TextComponent.VerticalAlign.MIDDLE);
        }
        return this;
    }

    public ButtonItem setText(String text) {
        return withText(text);
    }

    public ButtonItem setTextSafetyMargin(int margin) {
        this.textSafetyMargin = Math.max(0, margin);
        if (textComponent != null) {
            textComponent.setSafetyMargin(this.textSafetyMargin);
        }
        return this;
    }

    public ButtonItem setEllipsis(String ellipsis) {
        if (textComponent != null) {
            textComponent.setEllipsis(ellipsis);
        }
        return this;
    }

    public ButtonItem textColor(int color) {
        if (textComponent != null) {
            textComponent.color(color);
        }
        return this;
    }

    public ButtonItem textBold() {
        if (textComponent != null) {
            textComponent.bold();
        }
        return this;
    }

    public ButtonItem textItalic() {
        if (textComponent != null) {
            textComponent.italic();
        }
        return this;
    }

    public ButtonItem textShadow() {
        if (textComponent != null) {
            textComponent.shadow();
        }
        return this;
    }

    public ButtonItem textGlow() {
        if (textComponent != null) {
            textComponent.glow();
        }
        return this;
    }

    public ButtonItem textGlow(int color) {
        if (textComponent != null) {
            textComponent.glow(color);
        }
        return this;
    }

    public ButtonItem textPulse() {
        if (textComponent != null) {
            textComponent.pulse();
        }
        return this;
    }

    public ButtonItem textWave() {
        if (textComponent != null) {
            textComponent.wave();
        }
        return this;
    }

    public ButtonItem textTypewriter() {
        if (textComponent != null) {
            textComponent.typewriter();
        }
        return this;
    }

    public ButtonItem textRainbow() {
        if (textComponent != null) {
            textComponent.rainbow();
        }
        return this;
    }

    public TextComponent getTextComponent() {
        return textComponent;
    }

    public String getText() {
        return textComponent != null ? textComponent.getText() : "";
    }

    public boolean hasText() {
        return textComponent != null && !textComponent.getText().isEmpty();
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (enabled && contains(mouseX, mouseY)) {
            setState(ItemState.PRESSED);
            return super.onMouseClick(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public void onMouseEnter() {
        super.onMouseEnter();
        if (textComponent != null && !textComponent.getActiveAnimations().isEmpty()) {
            textComponent.startAnimation();
        }
    }

    @Override
    public void render(DrawContext context) {
        if (!visible) return;

        int bgColor = getStateColor();
        int borderRadius = getBorderRadius();
        Shadow shadow = getShadow();

        float animationProgress = getAnimationProgress();

        if (state == ItemState.HOVERED && hasClass(StyleKey.HOVER_SCALE)) {
            float scale = 1.0f + (0.05f * animationProgress);
            int scaledWidth = (int) (width * scale);
            int scaledHeight = (int) (height * scale);
            int offsetX = (scaledWidth - width) / 2;
            int offsetY = (scaledHeight - height) / 2;

            if (shadow != null) {
                Render2D.drawShadow(context, x - offsetX, y - offsetY, scaledWidth, scaledHeight, 3, 3, shadow.color);
            }
            Render2D.drawRoundedRect(context, x - offsetX, y - offsetY, scaledWidth, scaledHeight, borderRadius, bgColor);
        } else {
            if (shadow != null) {
                Render2D.drawShadow(context, x, y, width, height, 2, 2, shadow.color);
            }
            Render2D.drawRoundedRect(context, x, y, width, height, borderRadius, bgColor);
        }

        if (focused && hasClass(StyleKey.FOCUS_RING)) {
            int focusColor = styleSystem.getColor(StyleKey.PRIMARY_LIGHT);
            Render2D.drawRoundedRectBorder(context, x - 2, y - 2, width + 4, height + 4, borderRadius + 2, focusColor, 2);
        }

        renderText(context);
    }

    private void renderText(DrawContext context) {
        if (textComponent == null) return;

        int contentX = x + getPaddingLeft();
        int contentY = y + getPaddingTop();
        int contentWidth = width - getPaddingLeft() - getPaddingRight();
        int contentHeight = height - getPaddingTop() - getPaddingBottom();

        context.enableScissor(contentX, contentY, contentX + contentWidth, contentY + contentHeight);

        try {
            textComponent.render(context, contentX, contentY, contentWidth, contentHeight);
        } finally {
            context.disableScissor();
        }
    }

    public void startTextAnimation() {
        if (textComponent != null) {
            textComponent.startAnimation();
        }
    }

    public void stopTextAnimation() {
        if (textComponent != null) {
            textComponent.stopAnimation();
        }
    }

    public ButtonItem asPrimaryButton() {
        return addClass(StyleKey.PRIMARY, StyleKey.HOVER_SCALE, StyleKey.SHADOW_SM);
    }

    public ButtonItem asSecondaryButton() {
        return removeClass(StyleKey.PRIMARY)
                .addClass(StyleKey.SECONDARY, StyleKey.HOVER_BRIGHTEN);
    }

    public ButtonItem asDangerButton() {
        return removeClass(StyleKey.PRIMARY)
                .addClass(StyleKey.DANGER, StyleKey.HOVER_SCALE);
    }

    public ButtonItem asSuccessButton() {
        return removeClass(StyleKey.PRIMARY)
                .addClass(StyleKey.SUCCESS, StyleKey.HOVER_SCALE);
    }

    public ButtonItem asWarningButton() {
        return removeClass(StyleKey.PRIMARY)
                .addClass(StyleKey.WARNING, StyleKey.HOVER_SCALE);
    }

    public ButtonItem asInfoButton() {
        return removeClass(StyleKey.PRIMARY)
                .addClass(StyleKey.INFO, StyleKey.HOVER_BRIGHTEN);
    }

    public ButtonItem asGhostButton() {
        return removeClass(StyleKey.PRIMARY)
                .addClass(StyleKey.BG_OPACITY_0, StyleKey.HOVER_BRIGHTEN);
    }

    public ButtonItem asFancyButton() {
        return addClass(StyleKey.HOVER_SCALE, StyleKey.SHADOW_LG);
    }

    public ButtonItem withFancyText() {
        if (textComponent != null) {
            textComponent.rainbow().glow().pulse();
        }
        return this;
    }

    public ButtonItem withGlowingText() {
        if (textComponent != null) {
            textComponent.glow().shadow();
        }
        return this;
    }

    public ButtonItem withAnimatedText() {
        if (textComponent != null) {
            textComponent.wave().pulse();
        }
        return this;
    }

    @Override
    public ButtonItem addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }

    @Override
    public ButtonItem removeClass(StyleKey key) {
        super.removeClass(key);
        return this;
    }

    @Override
    public ButtonItem onClick(Runnable handler) {
        super.onClick(handler);
        return this;
    }

    @Override
    public ButtonItem onMouseEnter(Runnable handler) {
        super.onMouseEnter(handler);
        return this;
    }

    @Override
    public ButtonItem onMouseLeave(Runnable handler) {
        super.onMouseLeave(handler);
        return this;
    }

    @Override
    public ButtonItem onFocusGained(Runnable handler) {
        super.onFocusGained(handler);
        return this;
    }

    @Override
    public ButtonItem onFocusLost(Runnable handler) {
        super.onFocusLost(handler);
        return this;
    }

    @Override
    public ButtonItem setVisible(boolean visible) {
        super.setVisible(visible);
        return this;
    }

    @Override
    public ButtonItem setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        return this;
    }

    @Override
    public ButtonItem setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        return this;
    }

    @Override
    public ButtonItem setConstraints(LayoutConstraints constraints) {
        super.setConstraints(constraints);
        return this;
    }

    @Override
    public ButtonItem setTextRenderer(TextRenderer textRenderer) {
        super.setTextRenderer(textRenderer);
        if (this.textComponent != null) {
            this.textComponent.setTextRenderer(textRenderer);
        }
        return this;
    }
}