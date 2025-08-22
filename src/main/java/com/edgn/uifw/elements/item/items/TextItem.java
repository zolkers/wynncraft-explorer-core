package com.edgn.uifw.elements.item.items;

import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.components.TextComponent;
import com.edgn.uifw.elements.item.BaseItem;
import com.edgn.uifw.layout.LayoutConstraints;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

@SuppressWarnings({"unused", "unchecked"})
public class TextItem extends BaseItem {
    private TextComponent textComponent;
    private String text;

    public TextItem(UIStyleSystem styleSystem, int x, int y, int width, int height, String text) {
        super(styleSystem, x, y, width, height);
        this.text = text != null ? text : "";
        this.textComponent = new TextComponent(this.text, textRenderer)
                .align(TextComponent.TextAlign.LEFT)
                .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                .truncate();
    }

    @Override
    public void render(DrawContext context) {
        if (!visible || textComponent == null) return;

        renderBackground(context);

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

    private void renderBackground(DrawContext context) {
        int bgColor = getStateColor();
        if (bgColor != 0) {
            context.fill(x, y, x + width, y + height, bgColor);
        }
    }

    public TextItem setOverflowMode(TextComponent.TextOverflowMode mode) {
        this.textComponent = this.textComponent.setOverflowMode(mode);
        return this;
    }

    public TextItem setSafetyMargin(int margin) {
        this.textComponent = this.textComponent.setSafetyMargin(margin);
        return this;
    }

    public TextItem setMaxLines(int maxLines) {
        this.textComponent = this.textComponent.setMaxLines(maxLines);
        return this;
    }

    public TextItem setEllipsis(String ellipsis) {
        this.textComponent = this.textComponent.setEllipsis(ellipsis);
        return this;
    }

    public TextItem setMinScale(float minScale) {
        this.textComponent = this.textComponent.setMinScale(minScale);
        return this;
    }

    public TextItem truncate() {
        this.textComponent = this.textComponent.truncate();
        return this;
    }

    public TextItem truncate(int maxWidth) {
        this.textComponent = this.textComponent.truncate(maxWidth);
        return this;
    }

    public TextItem wrap(int maxLines) {
        this.textComponent = this.textComponent.wrap(maxLines);
        return this;
    }

    public TextItem autoScale() {
        this.textComponent = this.textComponent.autoScale();
        return this;
    }

    public TextItem autoScale(float minScale) {
        this.textComponent = this.textComponent.autoScale(minScale);
        return this;
    }

    public TextItem setText(String text) {
        this.text = text != null ? text : "";
        this.textComponent = this.textComponent.cloneWithNewText(this.text);
        return this;
    }

    public TextItem setTextComponent(TextComponent textComponent) {
        this.textComponent = textComponent;
        this.text = textComponent.getText();
        return this;
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
    }

    @Override
    public void setX(int x) {
        super.setX(x);
    }

    public TextItem color(int color) {
        this.textComponent = this.textComponent.color(color);
        return this;
    }

    public TextItem gradient(int startColor, int endColor, TextComponent.EffectMode mode, float speed) {
        this.textComponent = this.textComponent.gradient(startColor, endColor, mode, speed);
        return this;
    }

    public TextItem gradient(int startColor, int endColor, TextComponent.EffectMode mode) {
        this.textComponent = this.textComponent.gradient(startColor, endColor, mode);
        return this;
    }

    public TextItem rainbow(TextComponent.EffectMode mode, float speed) {
        this.textComponent = this.textComponent.rainbow(mode, speed);
        return this;
    }

    public TextItem rainbow(TextComponent.EffectMode mode) {
        this.textComponent = this.textComponent.rainbow(mode);
        return this;
    }

    public TextItem rainbow() {
        this.textComponent = this.textComponent.rainbow();
        return this;
    }

    public TextItem align(TextComponent.TextAlign align) {
        this.textComponent = this.textComponent.align(align);
        return this;
    }

    public TextItem verticalAlign(TextComponent.VerticalAlign align) {
        this.textComponent = this.textComponent.verticalAlign(align);
        return this;
    }

    public TextItem shadow() {
        this.textComponent = this.textComponent.shadow();
        return this;
    }

    public TextItem shadow(int color, int offsetX, int offsetY) {
        this.textComponent = this.textComponent.shadow(color, offsetX, offsetY);
        return this;
    }

    public TextItem bold() {
        this.textComponent = this.textComponent.bold();
        return this;
    }

    public TextItem italic() {
        this.textComponent = this.textComponent.italic();
        return this;
    }

    public TextItem underlined() {
        this.textComponent = this.textComponent.underlined();
        return this;
    }

    public TextItem strikethrough() {
        this.textComponent = this.textComponent.strikethrough();
        return this;
    }

    public TextItem wave(float amplitude, float frequency, float speed) {
        this.textComponent = this.textComponent.wave(amplitude, frequency, speed);
        return this;
    }

    public TextItem wave() {
        this.textComponent = this.textComponent.wave();
        return this;
    }

    public TextItem pulse(float min, float max, float speed) {
        this.textComponent = this.textComponent.pulse(min, max, speed);
        return this;
    }

    public TextItem pulse() {
        this.textComponent = this.textComponent.pulse();
        return this;
    }

    public TextItem typewriter(int delayMs) {
        this.textComponent = this.textComponent.typewriter(delayMs);
        return this;
    }

    public TextItem typewriter() {
        this.textComponent = this.textComponent.typewriter();
        return this;
    }

    public TextItem glow(int color, float radius, float intensity) {
        this.textComponent = this.textComponent.glow(color, radius, intensity);
        return this;
    }

    public TextItem glow(int color) {
        this.textComponent = this.textComponent.glow(color);
        return this;
    }

    public TextItem glow() {
        this.textComponent = this.textComponent.glow();
        return this;
    }

    public TextItem shake(float intensity, float speed) {
        this.textComponent = this.textComponent.shake(intensity, speed);
        return this;
    }

    public TextItem shake() {
        this.textComponent = this.textComponent.shake();
        return this;
    }

    public TextItem addEffect(TextComponent.TextEffect effect) {
        this.textComponent = this.textComponent.addEffect(effect);
        return this;
    }

    public TextItem asTitle() {
        int titleColor = styleSystem.getColor(StyleKey.PRIMARY);
        this.textComponent = this.textComponent.color(titleColor);
        return this;
    }

    public TextItem asSubtitle() {
        int subtitleColor = styleSystem.getColor(StyleKey.MUTED);
        this.textComponent = this.textComponent
                .color(subtitleColor)
                .italic();
        return this;
    }

    public TextItem asError() {
        this.textComponent = this.textComponent.asError();
        return this;
    }

    public TextItem asSuccess() {
        this.textComponent = this.textComponent.asSuccess();
        return this;
    }

    public TextItem asWarning() {
        this.textComponent = this.textComponent.asWarning();
        return this;
    }

    public TextItem asHighlight() {
        this.textComponent = this.textComponent.asHighlight();
        return this;
    }

    public TextItem asFancy() {
        this.textComponent = this.textComponent.asFancy();
        return this;
    }

    public void startAnimation() {
        if (textComponent != null) {
            textComponent.startAnimation();
        }
    }

    public void stopAnimation() {
        if (textComponent != null) {
            textComponent.stopAnimation();
        }
    }

    public void resetAnimation() {
        if (textComponent != null) {
            textComponent.resetAnimation();
        }
    }

    public String getText() {
        return text;
    }

    public String getDisplayedText() {
        return text;
    }

    public TextComponent getTextComponent() {
        return textComponent;
    }

    public TextComponent.TextOverflowMode getOverflowMode() {
        return textComponent.getOverflowMode();
    }

    public int getSafetyMargin() {
        return textComponent.getSafetyMargin();
    }

    public int getMaxLines() {
        return textComponent.getMaxLines();
    }

    public String getEllipsis() {
        return textComponent.getEllipsis();
    }

    public float getMinScale() {
        return textComponent.getMinScale();
    }

    public int getEffectiveWidth() {
        int baseWidth = width - getPaddingLeft() - getPaddingRight();
        return Math.max(baseWidth - textComponent.getSafetyMargin(), 20);
    }

    @Override
    public TextItem addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }

    @Override
    public TextItem removeClass(StyleKey key) {
        super.removeClass(key);
        return this;
    }

    @Override
    public TextItem onClick(Runnable handler) {
        super.onClick(handler);
        return this;
    }

    @Override
    public TextItem onMouseEnter(Runnable handler) {
        super.onMouseEnter(handler);
        return this;
    }

    @Override
    public TextItem onMouseLeave(Runnable handler) {
        super.onMouseLeave(handler);
        return this;
    }

    @Override
    public TextItem onFocusGained(Runnable handler) {
        super.onFocusGained(handler);
        return this;
    }

    @Override
    public TextItem onFocusLost(Runnable handler) {
        super.onFocusLost(handler);
        return this;
    }

    @Override
    public TextItem setVisible(boolean visible) {
        super.setVisible(visible);
        return this;
    }

    @Override
    public TextItem setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        return this;
    }

    @Override
    public TextItem setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        return this;
    }

    @Override
    public TextItem setConstraints(LayoutConstraints constraints) {
        super.setConstraints(constraints);
        return this;
    }

    @Override
    public TextItem setTextRenderer(TextRenderer textRenderer) {
        super.setTextRenderer(textRenderer);
        if (this.textComponent != null) {
            this.textComponent.setTextRenderer(textRenderer);
        }
        return this;
    }
}