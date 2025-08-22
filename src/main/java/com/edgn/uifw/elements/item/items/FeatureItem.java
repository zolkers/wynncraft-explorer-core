package com.edgn.uifw.elements.item.items;

import com.edgn.uifw.elements.item.BaseItem;
import com.edgn.uifw.utils.Render2D;
import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.css.rules.Shadow;
import com.edgn.uifw.layout.LayoutConstraints;
import com.edgn.uifw.components.TextComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

@SuppressWarnings({"unused", "unchecked"})
public class FeatureItem extends BaseItem {
    private TextComponent titleComponent;
    private TextComponent descriptionComponent;
    private TextComponent versionComponent;
    private Identifier icon;
    private Runnable action;

    private int iconSize = 32;
    private int padding = 12;
    private int verticalSpacing = 4;
    private boolean showStatusIndicator = true;
    private boolean showVersion = true;
    private boolean showIcon = true;

    private int statusActiveColor = 0xFF00FF00;
    private int statusInactiveColor = 0xFFFF0000;

    public FeatureItem(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
        addClass(StyleKey.BG_SURFACE, StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.TEXT_WHITE);
    }

    public FeatureItem withTitle(TextComponent titleComponent) {
        this.titleComponent = titleComponent;
        return this;
    }

    public FeatureItem withDescription(TextComponent descriptionComponent) {
        this.descriptionComponent = descriptionComponent;
        return this;
    }

    public FeatureItem withVersion(TextComponent versionComponent) {
        this.versionComponent = versionComponent;
        return this;
    }

    public FeatureItem setIcon(Identifier icon) {
        this.icon = icon;
        return this;
    }

    public FeatureItem setAction(Runnable action) {
        this.action = action;
        return this;
    }

    public FeatureItem setIconSize(int size) {
        this.iconSize = Math.max(16, size);
        return this;
    }

    public FeatureItem setPadding(int padding) {
        this.padding = Math.max(0, padding);
        return this;
    }

    public FeatureItem setVerticalSpacing(int spacing) {
        this.verticalSpacing = Math.max(0, spacing);
        return this;
    }

    public FeatureItem setShowStatusIndicator(boolean show) {
        this.showStatusIndicator = show;
        return this;
    }

    public FeatureItem setShowVersion(boolean show) {
        this.showVersion = show;
        return this;
    }

    public FeatureItem setShowIcon(boolean show) {
        this.showIcon = show;
        return this;
    }

    public FeatureItem setStatusColors(int activeColor, int inactiveColor) {
        this.statusActiveColor = activeColor;
        this.statusInactiveColor = inactiveColor;
        return this;
    }

    @Override
    public boolean contains(double x, double y) {
        return x >= this.x && x < this.x + this.width &&
                y >= this.y && y < this.y + this.height;
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled || !visible || !contains(mouseX, mouseY)) {
            return false;
        }

        if (action != null && button == 0) {
            try {
                action.run();
                return true;
            } catch (Exception e) {
                System.err.println("Error executing feature action: " + e.getMessage());
            }
        }

        return super.onMouseClick(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        if (state == ItemState.PRESSED) {
            setState(contains(mouseX, mouseY) ? ItemState.HOVERED : ItemState.NORMAL);
            return true;
        }
        return false;
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        boolean wasHovered = hovered;
        boolean nowHovered = contains(mouseX, mouseY);

        if (nowHovered != wasHovered) {
            if (nowHovered) {
                onMouseEnter();
            } else {
                onMouseLeave();
            }
        }

        super.onMouseMove(mouseX, mouseY);
    }

    @Override
    public void onMouseEnter() {
        if (enabled && visible && state != ItemState.PRESSED) {
            setState(ItemState.HOVERED);
        }
        super.onMouseEnter();

        // Start animations on hover
        if (titleComponent != null && !titleComponent.getActiveAnimations().isEmpty()) {
            titleComponent.startAnimation();
        }
        if (descriptionComponent != null && !descriptionComponent.getActiveAnimations().isEmpty()) {
            descriptionComponent.startAnimation();
        }
        if (versionComponent != null && !versionComponent.getActiveAnimations().isEmpty()) {
            versionComponent.startAnimation();
        }
    }

    @Override
    public void onMouseLeave() {
        if (state == ItemState.HOVERED) {
            setState(ItemState.NORMAL);
        }
        super.onMouseLeave();
    }

    @Override
    public void render(DrawContext context) {
        if (!visible || textRenderer == null) {
            return;
        }

        renderBackground(context);

        LayoutInfo layout = calculateLayout();

        if (showIcon && icon != null) {
            renderIcon(context, layout);
        }

        renderText(context, layout);

        if (showStatusIndicator) {
            renderStatusIndicator(context, layout);
        }

        if (focused && hasClass(StyleKey.FOCUS_RING)) {
            int focusColor = styleSystem.getColor(StyleKey.PRIMARY_LIGHT);
            int borderRadius = getBorderRadius();
            Render2D.drawRoundedRectBorder(context, x - 2, y - 2, width + 4, height + 4,
                    borderRadius + 2, focusColor, 2);
        }
    }

    private void renderBackground(DrawContext context) {
        int bgColor = getStateColor();
        int borderRadius = getBorderRadius();
        Shadow shadow = getShadow();

        if (state == ItemState.HOVERED && hasClass(StyleKey.HOVER_SCALE)) {
            float animationProgress = getAnimationProgress();
            float scale = 1.0f + (0.02f * animationProgress);

            int scaledWidth = (int) (width * scale);
            int scaledHeight = (int) (height * scale);
            int offsetX = (scaledWidth - width) / 2;
            int offsetY = (scaledHeight - height) / 2;

            if (shadow != null) {
                Render2D.drawShadow(context, x - offsetX, y - offsetY, scaledWidth, scaledHeight, 3, 3, shadow.color);
            }
            Render2D.drawRoundedRect(context, x - offsetX, y - offsetY,
                    scaledWidth, scaledHeight, borderRadius, bgColor);
        } else {
            if (shadow != null) {
                Render2D.drawShadow(context, x, y, width, height, 2, 2, shadow.color);
            }
            Render2D.drawRoundedRect(context, x, y, width, height, borderRadius, bgColor);
        }
    }

    private void renderIcon(DrawContext context, LayoutInfo layout) {
        try {
            context.drawTexture(RenderLayer::getGuiTextured, icon, layout.iconX, layout.iconY, 0, 0,
                    iconSize, iconSize, iconSize, iconSize);
        } catch (Exception e) {
            Render2D.drawRoundedRect(context, layout.iconX, layout.iconY,
                    iconSize, iconSize, 4, 0xFF555555);

            int centerX = layout.iconX + iconSize / 2;
            int centerY = layout.iconY + iconSize / 2 - 4;
            context.drawCenteredTextWithShadow(textRenderer, "?", centerX, centerY, 0xFFFFFFFF);
        }
    }

    private void renderText(DrawContext context, LayoutInfo layout) {
        if (titleComponent != null) {
            titleComponent.render(context, layout.titleX, layout.titleY, layout.maxTextWidth, textRenderer.fontHeight);
        }

        if (descriptionComponent != null) {
            descriptionComponent.render(context, layout.descriptionX, layout.descriptionY, layout.maxTextWidth, textRenderer.fontHeight);
        }

        if (showVersion && versionComponent != null) {
            int versionWidth = versionComponent.getTextWidth();
            int versionX = x + width - padding - versionWidth - (showStatusIndicator ? 15 : 0);
            versionComponent.render(context, versionX, layout.titleY, versionWidth, textRenderer.fontHeight);
        }
    }

    private void renderStatusIndicator(DrawContext context, LayoutInfo layout) {
        int statusColor = action != null ? statusActiveColor : statusInactiveColor;
        int indicatorSize = 6;
        int indicatorX = x + width - padding - indicatorSize;
        int indicatorY = y + height / 2 - indicatorSize / 2;

        Render2D.drawRoundedRect(context, indicatorX, indicatorY,
                indicatorSize, indicatorSize, indicatorSize / 2, statusColor);
    }

    private LayoutInfo calculateLayout() {
        int contentX = x + padding;
        int contentY = y + padding;
        int contentWidth = width - (padding * 2);

        int iconY = contentY + (height - padding * 2 - iconSize) / 2;

        int textStartX = showIcon && icon != null ? contentX + iconSize + padding : contentX;
        int maxTextWidth = contentWidth - (showIcon && icon != null ? iconSize + padding : 0)
                - (showVersion ? 80 : 0) - (showStatusIndicator ? 15 : 0);

        int titleY = contentY + (height > 40 ? 2 : 0);
        int descriptionY = titleY + textRenderer.fontHeight + verticalSpacing;

        return new LayoutInfo(contentX, iconY, textStartX, titleY, textStartX, descriptionY, maxTextWidth);
    }

    private record LayoutInfo(int iconX, int iconY, int titleX, int titleY, int descriptionX, int descriptionY,
                              int maxTextWidth) {
    }

    public FeatureItem asNewFeature() {
        return addClass(StyleKey.SUCCESS, StyleKey.SHADOW_MD);
    }

    public FeatureItem asDeprecatedFeature() {
        return addClass(StyleKey.DANGER, StyleKey.SHADOW_SM);
    }

    public FeatureItem asBetaFeature() {
        return addClass(StyleKey.WARNING, StyleKey.SHADOW_SM);
    }

    public FeatureItem asHighlightedFeature() {
        return addClass(StyleKey.SHADOW_LG, StyleKey.HOVER_SCALE);
    }

    public FeatureItem asImportantFeature() {
        return addClass(StyleKey.PRIMARY, StyleKey.SHADOW_MD);
    }

    @Override
    public FeatureItem addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }

    @Override
    public FeatureItem removeClass(StyleKey key) {
        super.removeClass(key);
        return this;
    }

    @Override
    public FeatureItem onClick(Runnable handler) {
        this.action = handler;
        return this;
    }

    @Override
    public FeatureItem onMouseEnter(Runnable handler) {
        super.onMouseEnter(handler);
        return this;
    }

    @Override
    public FeatureItem onMouseLeave(Runnable handler) {
        super.onMouseLeave(handler);
        return this;
    }

    @Override
    public FeatureItem onFocusGained(Runnable handler) {
        super.onFocusGained(handler);
        return this;
    }

    @Override
    public FeatureItem onFocusLost(Runnable handler) {
        super.onFocusLost(handler);
        return this;
    }

    @Override
    public FeatureItem setVisible(boolean visible) {
        super.setVisible(visible);
        return this;
    }

    @Override
    public FeatureItem setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        return this;
    }

    @Override
    public FeatureItem setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        return this;
    }

    @Override
    public FeatureItem setConstraints(LayoutConstraints constraints) {
        super.setConstraints(constraints);
        return this;
    }

    @Override
    public FeatureItem setTextRenderer(TextRenderer textRenderer) {
        super.setTextRenderer(textRenderer);
        return this;
    }

    public TextComponent getTitleComponent() { return titleComponent; }
    public TextComponent getDescriptionComponent() { return descriptionComponent; }
    public TextComponent getVersionComponent() { return versionComponent; }
    public Identifier getIcon() { return icon; }
    public Runnable getAction() { return action; }
    public boolean hasAction() { return action != null; }
    public int getIconSize() { return iconSize; }
    public int getPadding() { return padding; }
    public int getVerticalSpacing() { return verticalSpacing; }
    public boolean isShowStatusIndicator() { return showStatusIndicator; }
    public boolean isShowVersion() { return showVersion; }
    public boolean isShowIcon() { return showIcon; }
    public int getStatusActiveColor() { return statusActiveColor; }
    public int getStatusInactiveColor() { return statusInactiveColor; }
}