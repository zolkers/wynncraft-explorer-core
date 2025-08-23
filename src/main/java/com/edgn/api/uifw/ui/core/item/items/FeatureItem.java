package com.edgn.api.uifw.ui.core.item.items;

import com.edgn.api.uifw.ui.core.components.TextComponent;
import com.edgn.api.uifw.ui.core.item.BaseItem;
import com.edgn.api.uifw.ui.core.renderer.FontRenderer;
import com.edgn.api.uifw.ui.css.StyleKey;
import com.edgn.api.uifw.ui.css.UIStyleSystem;
import com.edgn.api.uifw.ui.css.values.Shadow;
import com.edgn.api.uifw.ui.layout.LayoutConstraints;
import com.edgn.api.uifw.ui.layout.ZIndex;
import com.edgn.api.uifw.ui.utils.ColorUtils;
import com.edgn.api.uifw.ui.utils.DrawingUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.RenderLayer;

@SuppressWarnings({"unused", "unchecked", "UnusedReturnValue"})
public class FeatureItem extends BaseItem {

    
    private TextComponent titleComponent;
    private TextComponent descriptionComponent;
    private TextComponent versionComponent;
    private Identifier icon;
    private Runnable action;

    
    private int iconSize = 32;
    private int verticalSpacing = 4;
    private int versionSlotWidth = 80;
    private boolean showStatusIndicator = true;
    private boolean showVersion = true;
    private boolean showIcon = true;

    
    private Integer overrideBgColor = null;           
    private Integer overrideAccent = null;            
    private boolean glassBackground = true;           
    private boolean iconBadge = true;                 
    private boolean showAccentStripe = true;          
    private int accentStripeWidth = 4;

    
    private int statusActiveColor = 0xFF00FF00;
    private int statusInactiveColor = 0xFFFF0000;

    public FeatureItem(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
        addClass(StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.P_3, StyleKey.HOVER_BRIGHTEN);
    }

    
    public FeatureItem withTitle(String text) {
        if (text != null) {
            this.titleComponent = new TextComponent(text, fontRenderer)
                    .bold() 
                    .align(TextComponent.TextAlign.LEFT)
                    .verticalAlign(TextComponent.VerticalAlign.TOP)
                    .truncate();
        }
        return this;
    }
    public FeatureItem withTitle(TextComponent tc) {
        if (tc != null) {
            this.titleComponent = tc
                    .setOverflowMode(TextComponent.TextOverflowMode.TRUNCATE)
                    .align(TextComponent.TextAlign.LEFT)
                    .verticalAlign(TextComponent.VerticalAlign.TOP);
        }
        return this;
    }

    public FeatureItem withDescription(String text) {
        if (text != null) {
            this.descriptionComponent = new TextComponent(text, fontRenderer)
                    .align(TextComponent.TextAlign.LEFT)
                    .verticalAlign(TextComponent.VerticalAlign.TOP)
                    .truncate();
        }
        return this;
    }
    public FeatureItem withDescription(TextComponent tc) {
        if (tc != null) {
            this.descriptionComponent = tc
                    .setOverflowMode(TextComponent.TextOverflowMode.TRUNCATE)
                    .align(TextComponent.TextAlign.LEFT)
                    .verticalAlign(TextComponent.VerticalAlign.TOP);
        }
        return this;
    }

    public FeatureItem withVersion(String text) {
        if (text != null) {
            this.versionComponent = new TextComponent(text, fontRenderer)
                    .align(TextComponent.TextAlign.RIGHT)
                    .verticalAlign(TextComponent.VerticalAlign.TOP)
                    .truncate();
        }
        return this;
    }
    public FeatureItem withVersion(TextComponent tc) {
        if (tc != null) {
            this.versionComponent = tc
                    .setOverflowMode(TextComponent.TextOverflowMode.TRUNCATE)
                    .align(TextComponent.TextAlign.RIGHT)
                    .verticalAlign(TextComponent.VerticalAlign.TOP);
        }
        return this;
    }

    public FeatureItem setIcon(Identifier icon) { this.icon = icon; return this; }
    public FeatureItem setAction(Runnable action) { this.action = action; return this; }

    
    public FeatureItem background(int argb) { this.overrideBgColor = argb; return this; }
    public FeatureItem clearBackground() { this.overrideBgColor = null; return this; }

    public FeatureItem accent(int argb) { this.overrideAccent = argb; return this; }
    public FeatureItem clearAccent() { this.overrideAccent = null; return this; }

    public FeatureItem glass(boolean enabled) { this.glassBackground = enabled; return this; }
    public FeatureItem iconBadge(boolean enabled) { this.iconBadge = enabled; return this; }
    public FeatureItem accentStripe(boolean enabled) { this.showAccentStripe = enabled; return this; }
    public FeatureItem accentStripeWidth(int px) { this.accentStripeWidth = Math.max(2, px); return this; }

    
    public FeatureItem setIconSize(int size) { this.iconSize = Math.max(16, size); return this; }
    public FeatureItem setVerticalSpacing(int spacing) { this.verticalSpacing = Math.max(0, spacing); return this; }
    public FeatureItem setVersionSlotWidth(int width) { this.versionSlotWidth = Math.max(32, width); return this; }
    public FeatureItem setShowStatusIndicator(boolean show) { this.showStatusIndicator = show; return this; }
    public FeatureItem setShowVersion(boolean show) { this.showVersion = show; return this; }
    public FeatureItem setShowIcon(boolean show) { this.showIcon = show; return this; }
    public FeatureItem setStatusColors(int activeColor, int inactiveColor) { this.statusActiveColor = activeColor; this.statusInactiveColor = inactiveColor; return this; }

    
    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled || !canInteract(mouseX, mouseY)) return false;
        setState(ItemState.PRESSED);
        boolean handled = super.onMouseClick(mouseX, mouseY, button);
        if (!handled && button == 0 && action != null) {
            try { action.run(); handled = true; } catch (Exception ignored) {}
        }
        return handled;
    }
    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        if (!enabled) return false;
        boolean inside = canInteract(mouseX, mouseY);
        setState(inside ? ItemState.HOVERED : ItemState.NORMAL);
        return inside;
    }
    @Override
    public void onTick() { if (this.isHovered()) setState(ItemState.HOVERED); }

    
    @Override
    public void render(DrawContext context) {
        if (!visible) return;

        updateConstraints();
        int cx = getCalculatedX();
        int cy = getCalculatedY();
        int cw = getCalculatedWidth();
        int ch = getCalculatedHeight();

        
        int baseBg = (overrideBgColor != null) ? overrideBgColor : getBgColor();
        if (baseBg == 0) {
            
            baseBg = glassBackground ? ColorUtils.setOpacity(0xFF101318, 0.70f) : ColorUtils.NamedColor.AZURE.toInt();
        }

        int bg = backgroundForState(baseBg);
        int radius = getBorderRadius();
        Shadow shadow = getShadow();

        float scale = (hasClass(StyleKey.HOVER_SCALE) && isHovered()) ? getAnimatedScale() : 1.0f;

        if (scale != 1.0f) {
            int sw = Math.max(0, Math.round(cw * scale));
            int sh = Math.max(0, Math.round(ch * scale));
            int ox = (sw - cw) / 2;
            int oy = (sh - ch) / 2;
            if (shadow != null) DrawingUtils.drawShadow(context, cx - ox, cy - oy, sw, sh, 3, 3, shadow.color);
            DrawingUtils.drawRoundedRect(context, cx - ox, cy - oy, sw, sh, radius, bg);
        } else {
            if (shadow != null) DrawingUtils.drawShadow(context, cx, cy, cw, ch, 2, 2, shadow.color);
            DrawingUtils.drawRoundedRect(context, cx, cy, cw, ch, radius, bg);
        }

        
        if (showAccentStripe) {
            int acc = resolveAccentColor();
            int stripeX = cx;
            int stripeY = cy;
            int stripeW = Math.min(accentStripeWidth, Math.max(2, cw / 24));
            int stripeH = ch;
            DrawingUtils.drawRoundedRect(context, stripeX, stripeY, stripeW, stripeH, radius, acc);
        }

        renderContent(context, cx, cy, cw, ch);
    }

    private int resolveAccentColor() {
        if (overrideAccent != null) return overrideAccent;
        
        int txt = getComputedStyles().getTextColor();
        
        int a = (txt >>> 24) & 0xFF;
        int r = (txt >>> 16) & 0xFF;
        int g = (txt >>> 8) & 0xFF;
        int b = txt & 0xFF;
        r = Math.min(255, (int)(r * 1.1f) + 10);
        g = Math.min(255, (int)(g * 1.1f) + 10);
        b = Math.min(255, (int)(b * 1.1f) + 10);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int backgroundForState(int base) {
        if (getState() == ItemState.PRESSED) return darken(base);
        if (isHovered()) {
            float f = hasClass(StyleKey.HOVER_BRIGHTEN) ? 0.18f : 0.08f;
            return brighten(base, f);
        }
        return base;
    }

    private int brighten(int color, float ratio) {
        int a = (color >>> 24) & 0xFF;
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = color & 0xFF;
        r = Math.min(255, Math.round(r + (255 - r) * ratio));
        g = Math.min(255, Math.round(g + (255 - g) * ratio));
        b = Math.min(255, Math.round(b + (255 - b) * ratio));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    private int darken(int color) {
        int a = (color >>> 24) & 0xFF;
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = color & 0xFF;
        r = Math.max(0, Math.round(r * 0.84f));
        g = Math.max(0, Math.round(g * 0.84f));
        b = Math.max(0, Math.round(b * 0.84f));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private void renderContent(DrawContext context, int cx, int cy, int cw, int ch) {
        int contentX = cx + getPaddingLeft();
        int contentY = cy + getPaddingTop();
        int contentW = Math.max(0, cw - getPaddingLeft() - getPaddingRight());
        int contentH = Math.max(0, ch - getPaddingTop() - getPaddingBottom());

        int gap = getGap();
        if (gap == 0) gap = verticalSpacing;

        
        int leftBlockW = (showIcon && icon != null) ? (iconSize + gap) : 0;
        int rightBlockW = 0;
        if (showVersion && versionComponent != null) rightBlockW += versionSlotWidth;
        if (showStatusIndicator) rightBlockW += 14;

        int textAreaX = contentX + leftBlockW;
        int textAreaW = Math.max(0, contentW - leftBlockW - rightBlockW);

        
        if (showIcon && icon != null) {
            int iconX = contentX;
            int iconY = contentY + Math.max(0, (contentH - iconSize) / 2);

            if (iconBadge) {
                int pad = Math.max(4, iconSize / 6);
                int bgR = iconSize / 2 + pad;
                int bgX = iconX + iconSize / 2 - bgR;
                int bgY = iconY + iconSize / 2 - bgR;
                int badgeCol = ColorUtils.setOpacity(resolveAccentColor(), 0.20f);
                DrawingUtils.drawRoundedRect(context, bgX, bgY, bgR * 2, bgR * 2, bgR, badgeCol);
            }

            try {
                context.drawTexture(RenderLayer::getGuiTextured, icon, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
            } catch (Exception e) {
                DrawingUtils.drawRoundedRect(context, iconX, iconY, iconSize, iconSize, 6, 0xFF555555);
            }
        }

        
        int baseText = getComputedStyles().getTextColor();
        int titleColor = (titleComponent != null && titleComponent.hasCustomStyling()) ? 0 : 0xFFFFFFFF;
        int descColor  = (descriptionComponent != null && descriptionComponent.hasCustomStyling()) ? 0 : ColorUtils.setOpacity(0xFFFFFFFF, 0.72f);
        int verColor   = (versionComponent != null && versionComponent.hasCustomStyling()) ? 0 : ColorUtils.setOpacity(0xFFFFFFFF, 0.66f);


        int max = Math.max(1, Math.round(contentH * 0.48f));
        if (titleComponent != null) {
            if (!titleComponent.hasCustomStyling()) titleComponent.color(titleColor != 0 ? titleColor : baseText);


            titleComponent.render(context, textAreaX + 5, contentY, textAreaW, max);
        }

        
        if (descriptionComponent != null) {
            if (!descriptionComponent.hasCustomStyling()) descriptionComponent.color(descColor != 0 ? descColor : baseText);
            int descY = contentY + max + gap / 2;
            int descH = Math.max(0, contentH - max - gap / 2);
            descriptionComponent.render(context, textAreaX + 5, descY, textAreaW, descH);
        }

        
        if (showVersion && versionComponent != null) {
            if (!versionComponent.hasCustomStyling()) versionComponent.color(verColor != 0 ? verColor : baseText);
            int statusW = showStatusIndicator ? 14 : 0;
            int vx = contentX + contentW - statusW - versionSlotWidth;
            int vy = contentY;
            int vw = versionSlotWidth;
            int vh = Math.max(1, Math.round(contentH * 0.5f));
            versionComponent.render(context, vx, vy, vw, vh);
        }

        if (showStatusIndicator) {
            int ix = contentX + contentW - 10;
            int iy = contentY + (contentH / 2) - 5;
            int inner = 6;
            int outer = 10;
            int color = (action != null) ? statusActiveColor : statusInactiveColor;
            int halo  = ColorUtils.setOpacity(color, 0.30f);
            DrawingUtils.drawRoundedRect(context, ix, iy, outer, outer, outer / 2, halo);
            int vx = ix + (outer - inner) / 2;
            int vy = iy + (outer - inner) / 2;
            DrawingUtils.drawRoundedRect(context, vx, vy, inner, inner, inner / 2, color);
        }
    }

    
    @Override public FeatureItem addClass(StyleKey... keys) { super.addClass(keys); return this; }
    @Override public FeatureItem removeClass(StyleKey key) { super.removeClass(key); return this; }
    @Override public FeatureItem onClick(Runnable handler) { super.onClick(handler); return this; }
    @Override public FeatureItem onMouseEnter(Runnable handler) { super.onMouseEnter(handler); return this; }
    @Override public FeatureItem onMouseLeave(Runnable handler) { super.onMouseLeave(handler); return this; }
    @Override public FeatureItem onFocusGained(Runnable handler) { super.onFocusGained(handler); return this; }
    @Override public FeatureItem onFocusLost(Runnable handler) { super.onFocusLost(handler); return this; }
    @Override public FeatureItem setVisible(boolean visible) { super.setVisible(visible); return this; }
    @Override public FeatureItem setEnabled(boolean enabled) { super.setEnabled(enabled); return this; }
    @Override public FeatureItem setZIndex(int zIndex) { super.setZIndex(zIndex); return this; }
    @Override public FeatureItem setZIndex(ZIndex zIndex) { super.setZIndex(zIndex); return this; }
    @Override public FeatureItem setZIndex(ZIndex.Layer layer) { super.setZIndex(layer); return this; }
    @Override public FeatureItem setZIndex(ZIndex.Layer layer, int priority) { super.setZIndex(layer, priority); return this; }
    @Override public FeatureItem setConstraints(LayoutConstraints constraints) { super.setConstraints(constraints); return this; }

    @Override
    public FeatureItem setFontRenderer(FontRenderer fontRenderer) {
        super.setFontRenderer(fontRenderer);
        if (this.titleComponent != null) this.titleComponent.setFontRenderer(fontRenderer);
        if (this.descriptionComponent != null) this.descriptionComponent.setFontRenderer(fontRenderer);
        if (this.versionComponent != null) this.versionComponent.setFontRenderer(fontRenderer);
        return this;
    }

    public TextComponent getTitleComponent() { return titleComponent; }
    public TextComponent getDescriptionComponent() { return descriptionComponent; }
    public TextComponent getVersionComponent() { return versionComponent; }
    public Identifier getIcon() { return icon; }
    public Runnable getAction() { return action; }
    public boolean hasAction() { return action != null; }
    public int getIconSize() { return iconSize; }
    public int getVerticalSpacing() { return verticalSpacing; }
    public boolean isShowStatusIndicator() { return showStatusIndicator; }
    public boolean isShowVersion() { return showVersion; }
    public boolean isShowIcon() { return showIcon; }
    public int getStatusActiveColor() { return statusActiveColor; }
    public int getStatusInactiveColor() { return statusInactiveColor; }
    public int getVersionSlotWidth() { return versionSlotWidth; }

    @Override
    public String toString() {
        return String.format("FeatureItem{state=%s, enabled=%b, visible=%b, bounds=[%d,%d,%d,%d], hasIcon=%b, hasAction=%b}",
                getState(), isEnabled(), isVisible(),
                getCalculatedX(), getCalculatedY(), getCalculatedWidth(), getCalculatedHeight(),
                showIcon && icon != null, hasAction());
    }
}
