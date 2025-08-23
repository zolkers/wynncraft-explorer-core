package com.edgn.api.uifw.ui.core.item.items;

import com.edgn.api.uifw.ui.core.components.TextComponent;
import com.edgn.api.uifw.ui.core.item.BaseItem;
import com.edgn.api.uifw.ui.core.renderer.FontRenderer;
import com.edgn.api.uifw.ui.css.UIStyleSystem;
import com.edgn.api.uifw.ui.utils.DrawingUtils;
import net.minecraft.client.gui.DrawContext;

@SuppressWarnings({"unused", "UnusedReturnValue", "unchecked"})
public class LabelItem extends BaseItem {

    protected TextComponent textComponent;

    public LabelItem(UIStyleSystem styleSystem, int x, int y, int w, int h, String text) {
        super(styleSystem, x, y, w, h);
        this.textComponent = new TextComponent(text, this.fontRenderer)
                .verticalAlign(TextComponent.VerticalAlign.MIDDLE);
    }

    public LabelItem(UIStyleSystem styleSystem, int x, int y, String text) {
        super(styleSystem, x, y, 0, 0);
        this.textComponent = new TextComponent(text, this.fontRenderer)
                .verticalAlign(TextComponent.VerticalAlign.MIDDLE);
        this.width = textComponent.getTextWidth();
        this.height = textComponent.getTextHeight();
        markConstraintsDirty();
    }

    public LabelItem(UIStyleSystem styleSystem, int x, int y, int w, int h, TextComponent textComponent) {
        super(styleSystem, x, y, w, h);
        this.textComponent = (textComponent != null ? textComponent : new TextComponent("", this.fontRenderer));
        this.textComponent.setFontRenderer(this.fontRenderer);
        if (!this.textComponent.hasCustomStyling()) {
            this.textComponent.verticalAlign(TextComponent.VerticalAlign.MIDDLE);
        }
    }

    public LabelItem(UIStyleSystem styleSystem, int x, int y, TextComponent textComponent) {
        super(styleSystem, x, y, 0, 0);
        this.textComponent = (textComponent != null ? textComponent : new TextComponent("", this.fontRenderer));
        this.textComponent.setFontRenderer(this.fontRenderer);
        if (!this.textComponent.hasCustomStyling()) {
            this.textComponent.verticalAlign(TextComponent.VerticalAlign.MIDDLE);
        }
        this.width = this.textComponent.getTextWidth();
        this.height = this.textComponent.getTextHeight();
        markConstraintsDirty();
    }

    @Override
    public void render(DrawContext context) {
        if (!visible) return;
        updateConstraints();

        int cx = getCalculatedX();
        int cy = getCalculatedY();
        int cw = getCalculatedWidth();
        int ch = getCalculatedHeight();

        int bgColor = getBgColor();
        if (bgColor != 0) {
            DrawingUtils.drawRoundedRect(context, cx, cy, cw, ch, getBorderRadius(), bgColor);
        }

        textComponent.render(context,
                cx + getPaddingLeft(), cy + getPaddingTop(),
                cw - getPaddingLeft() - getPaddingRight(),
                ch - getPaddingTop() - getPaddingBottom());
    }

    public LabelItem setText(String text) {
        this.textComponent = this.textComponent.cloneWithNewText(text);
        return this;
    }

    public LabelItem setTextAndResize(String text) {
        setText(text);
        this.width  = textComponent.getTextWidth()  + getPaddingLeft() + getPaddingRight();
        this.height = textComponent.getTextHeight() + getPaddingTop()  + getPaddingBottom();
        markConstraintsDirty();
        return this;
    }

    public LabelItem color(int color) { textComponent.color(color); return this; }
    public LabelItem align(TextComponent.TextAlign align) { textComponent.align(align); return this; }
    public LabelItem verticalAlign(TextComponent.VerticalAlign align) { textComponent.verticalAlign(align); return this; }
    public LabelItem bold() { textComponent.bold(); return this; }
    public LabelItem italic() { textComponent.italic(); return this; }
    public LabelItem shadow() { textComponent.shadow(); return this; }
    public LabelItem shadow(int color, int offsetX, int offsetY) { textComponent.shadow(color, offsetX, offsetY); return this; }
    public LabelItem glow() { textComponent.glow(); return this; }
    public LabelItem glow(int color) { textComponent.glow(color); return this; }
    public LabelItem wave() { textComponent.wave(); return this; }
    public LabelItem pulse() { textComponent.pulse(); return this; }
    public LabelItem rainbow() { textComponent.rainbow(); return this; }
    public LabelItem typewriter() { textComponent.typewriter(); return this; }
    public LabelItem truncate() { textComponent.truncate(); return this; }
    public LabelItem wrap(int maxLines) { textComponent.wrap(maxLines); return this; }
    public LabelItem autoScale() { textComponent.autoScale(); return this; }

    public TextComponent getTextComponent() { return textComponent; }

    @Override
    public LabelItem setFontRenderer(FontRenderer fontRenderer) {
        super.setFontRenderer(fontRenderer);
        if (this.textComponent != null) {
            this.textComponent.setFontRenderer(fontRenderer);
        }
        return this;
    }

    @Override
    public String toString() {
        return String.format("LabelItem{text='%s', bounds=[%d,%d,%d,%d], visible=%b, styled=%b}",
                textComponent != null ? textComponent.getText() : "null",
                getCalculatedX(), getCalculatedY(), getCalculatedWidth(), getCalculatedHeight(),
                isVisible(), textComponent != null && textComponent.hasCustomStyling());
    }
}
