package com.edgn.uifw.elements.item;

import com.edgn.uifw.layout.LayoutConstraints;
import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.css.CSSStyleApplier;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.elements.UIElement;
import net.minecraft.client.font.TextRenderer;

@SuppressWarnings("unused")
public abstract class BaseItem extends UIElement {
    protected ItemState state = ItemState.NORMAL;
    protected long lastStateChange = 0;
    protected static final long ANIMATION_DURATION = 150;

    public enum ItemState {
        NORMAL, HOVERED, PRESSED, FOCUSED, DISABLED, ACTIVE
    }

    public BaseItem(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UIElement> T addClass(StyleKey... keys) {
        super.addClass(keys);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UIElement> T removeClass(StyleKey key) {
        super.removeClass(key);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UIElement> T onClick(Runnable handler) {
        super.onClick(handler);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UIElement> T onMouseEnter(Runnable handler) {
        super.onMouseEnter(handler);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UIElement> T onMouseLeave(Runnable handler) {
        super.onMouseLeave(handler);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UIElement> T onFocusGained(Runnable handler) {
        super.onFocusGained(handler);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UIElement> T onFocusLost(Runnable handler) {
        super.onFocusLost(handler);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UIElement> T setVisible(boolean visible) {
        super.setVisible(visible);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UIElement> T setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UIElement> T setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UIElement> T setConstraints(LayoutConstraints constraints) {
        super.setConstraints(constraints);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UIElement> T setTextRenderer(TextRenderer textRenderer) {
        super.setTextRenderer(textRenderer);
        return (T) this;
    }

    public ItemState getState() {
        return state;
    }

    protected void setState(ItemState newState) {
        if (state != newState) {
            state = newState;
            lastStateChange = System.currentTimeMillis();
            onStateChanged(state);
        }
    }

    protected void onStateChanged(ItemState newState) {}

    @Override
    public void onMouseEnter() {
        super.onMouseEnter();
        if (enabled && state != ItemState.PRESSED && state != ItemState.DISABLED) {
            setState(ItemState.HOVERED);
        }
    }

    @Override
    public void onMouseLeave() {
        super.onMouseLeave();
        if (enabled && state != ItemState.PRESSED && state != ItemState.DISABLED) {
            setState(ItemState.NORMAL);
        }
    }

    @Override
    public void onFocusGained() {
        super.onFocusGained();
        if (enabled && state != ItemState.DISABLED) {
            setState(ItemState.FOCUSED);
        }
    }

    @Override
    public void onFocusLost() {
        super.onFocusLost();
        if (enabled && state != ItemState.DISABLED) {
            setState(hovered ? ItemState.HOVERED : ItemState.NORMAL);
        }
    }

    protected float getAnimationProgress() {
        return Math.min(1.0f, (System.currentTimeMillis() - lastStateChange) / (float) ANIMATION_DURATION);
    }

    protected int getStateColor() {
        CSSStyleApplier.ComputedStyles styles = getComputedStyles();
        int baseColor = styles.backgroundColor;
        if (baseColor == 0) baseColor = styleSystem.getColor(StyleKey.PRIMARY);

        return switch (state) {
            case HOVERED -> styles.hasHoverEffect ? brightenColor(baseColor, 20) : baseColor;
            case PRESSED -> darkenColor(baseColor, 20);
            case FOCUSED -> styles.hasFocusRing ? styleSystem.getColor(StyleKey.PRIMARY_LIGHT) : baseColor;
            case DISABLED -> fadeColor(baseColor, 0.5f);
            case ACTIVE -> brightenColor(baseColor, 30);
            default -> baseColor;
        };
    }

    protected int getAnimatedColor(int baseColor) {
        float progress = getAnimationProgress();

        if (hasHoverEffect() && state == ItemState.HOVERED) {
            int brightenAmount = (int) (20 * progress);
            return brightenColor(baseColor, brightenAmount);
        }

        return getStateColor();
    }

    protected float getAnimatedScale() {
        if (hasClass(StyleKey.HOVER_SCALE) && state == ItemState.HOVERED) {
            float progress = getAnimationProgress();
            float scaleValue = styleSystem.getValue(StyleKey.HOVER_SCALE) / 100.0f;
            return 1.0f + (scaleValue - 1.0f) * progress;
        }
        return 1.0f;
    }

    protected int brightenColor(int color, int amount) {
        int r = Math.min(255, ((color >> 16) & 0xFF) + amount);
        int g = Math.min(255, ((color >> 8) & 0xFF) + amount);
        int b = Math.min(255, (color & 0xFF) + amount);
        int a = (color >> 24) & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    protected int darkenColor(int color, int amount) {
        int r = Math.max(0, ((color >> 16) & 0xFF) - amount);
        int g = Math.max(0, ((color >> 8) & 0xFF) - amount);
        int b = Math.max(0, (color & 0xFF) - amount);
        int a = (color >> 24) & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    protected int fadeColor(int color, float alpha) {
        int a = (int) (((color >> 24) & 0xFF) * alpha);
        return (a << 24) | (color & 0x00FFFFFF);
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        if (state == ItemState.PRESSED) {
            setState(canInteract(mouseX, mouseY) ? ItemState.HOVERED : ItemState.NORMAL);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!canInteract(mouseX, mouseY)) return false;

        if (enabled) {
            setState(ItemState.PRESSED);
        }

        return super.onMouseClick(mouseX, mouseY, button);
    }
}
