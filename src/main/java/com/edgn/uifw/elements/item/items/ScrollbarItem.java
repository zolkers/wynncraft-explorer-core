package com.edgn.uifw.elements.item.items;

import com.edgn.uifw.elements.item.BaseItem;
import com.edgn.uifw.utils.Render2D;
import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.layout.LayoutConstraints;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Consumer;

@SuppressWarnings({"unused", "unchecked"})
public class ScrollbarItem extends BaseItem {
    public enum Orientation { VERTICAL, HORIZONTAL }

    private final Orientation orientation;
    private float thumbRatio = 0.5f;
    private float scrollRatio = 0.0f;

    private boolean dragging = false;
    private double dragStartPos = 0;
    private float dragStartScroll = 0;

    private Consumer<Float> onScrollHandler;

    private static final int MIN_THUMB_SIZE = 20;

    public ScrollbarItem(UIStyleSystem styleSystem, int x, int y, int width, int height, Orientation orientation) {
        super(styleSystem, x, y, width, height);
        this.orientation = orientation;
        addClass(StyleKey.BG_SURFACE, StyleKey.ROUNDED_SM);
    }

    public ScrollbarItem onScroll(Consumer<Float> handler) {
        this.onScrollHandler = handler;
        return this;
    }

    public void updateScrollbar(float viewportRatio, float scrollRatio) {
        this.thumbRatio = Math.max(0.1f, Math.min(1.0f, viewportRatio));
        this.scrollRatio = Math.max(0.0f, Math.min(1.0f, scrollRatio));
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled || !visible || !contains(mouseX, mouseY)) {
            return false;
        }

        ThumbBounds thumbBounds = getThumbBounds();

        if (isPointInThumb(mouseX, mouseY, thumbBounds)) {
            setState(ItemState.PRESSED);
            dragging = true;

            if (orientation == Orientation.VERTICAL) {
                dragStartPos = mouseY;
            } else {
                dragStartPos = mouseX;
            }
            dragStartScroll = scrollRatio;

        } else {
            float trackLength = orientation == Orientation.VERTICAL ? height : width;
            float thumbSize = Math.max(MIN_THUMB_SIZE, trackLength * thumbRatio);
            float availableTrack = trackLength - thumbSize;

            if (availableTrack > 0) {
                float relativePos;
                if (orientation == Orientation.VERTICAL) {
                    relativePos = (float) (mouseY - y);
                } else {
                    relativePos = (float) (mouseX - x);
                }

                float newScrollRatio = (relativePos - thumbSize / 2) / availableTrack;
                setScrollRatio(Math.max(0.0f, Math.min(1.0f - thumbRatio, newScrollRatio)));
            }
        }

        return true;
    }

    public boolean handleMouseRelease(double mouseX, double mouseY, int button) {
        if (dragging) {
            dragging = false;
            setState(contains(mouseX, mouseY) ? ItemState.HOVERED : ItemState.NORMAL);
            return true;
        }
        return false;
    }

    public boolean handleMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        if (!enabled || !visible || !contains(mouseX, mouseY)) {
            return false;
        }

        float scrollStep = thumbRatio > 0.5f ? 0.05f : 0.1f;
        float direction = scrollDelta > 0 ? -scrollStep : scrollStep;

        float maxRatio = 1.0f - thumbRatio;
        float newScrollRatio = scrollRatio + direction;
        setScrollRatio(Math.max(0.0f, Math.min(maxRatio, newScrollRatio)));

        return true;
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        super.onMouseMove(mouseX, mouseY);

        if (dragging) {
            float trackLength = orientation == Orientation.VERTICAL ? height : width;
            float thumbSize = Math.max(MIN_THUMB_SIZE, trackLength * thumbRatio);
            float availableTrack = trackLength - thumbSize;

            if (availableTrack > 0.001f) {
                double currentPos = orientation == Orientation.VERTICAL ? mouseY : mouseX;
                double deltaPos = currentPos - dragStartPos;
                float deltaRatio = (float) deltaPos / availableTrack;

                float newScrollRatio = dragStartScroll + deltaRatio;
                float maxRatio = 1.0f - thumbRatio;
                setScrollRatio(Math.max(0.0f, Math.min(maxRatio, newScrollRatio)));
            }
        }
    }

    @Override
    public void onMouseLeave() {
        super.onMouseLeave();
    }

    public void forceStopDragging() {
        if (dragging) {
            dragging = false;
            setState(ItemState.NORMAL);
        }
    }

    private void setScrollRatio(float newRatio) {
        float maxRatio = Math.max(0.0f, 1.0f - thumbRatio);
        this.scrollRatio = Math.max(0.0f, Math.min(maxRatio, newRatio));

        if (onScrollHandler != null) {
            float fullScrollRatio;
            if (maxRatio > 0.001f) {
                fullScrollRatio = this.scrollRatio / maxRatio;
            } else {
                fullScrollRatio = 0.0f;
            }

            fullScrollRatio = Math.max(0.0f, Math.min(1.0f, fullScrollRatio));
            onScrollHandler.accept(fullScrollRatio);
        }
    }

    private boolean isPointInThumb(double mouseX, double mouseY, ThumbBounds bounds) {
        return mouseX >= bounds.x && mouseX <= bounds.x + bounds.width &&
                mouseY >= bounds.y && mouseY <= bounds.y + bounds.height;
    }

    private ThumbBounds getThumbBounds() {
        if (orientation == Orientation.VERTICAL) {
            float thumbHeight = Math.max(MIN_THUMB_SIZE, height * thumbRatio);
            float availableTrack = height - thumbHeight;
            float thumbY = y + (availableTrack * scrollRatio);

            return new ThumbBounds(x, thumbY, width, thumbHeight);
        } else {
            float thumbWidth = Math.max(MIN_THUMB_SIZE, width * thumbRatio);
            float availableTrack = width - thumbWidth;
            float thumbX = x + (availableTrack * scrollRatio);

            return new ThumbBounds(thumbX, y, thumbWidth, height);
        }
    }

    @Override
    public void render(DrawContext context) {
        if (!visible) return;

        int trackColor = getStateColor();
        int thumbColor = getThumbColor();
        int borderRadius = getBorderRadius();

        Render2D.drawRoundedRect(context, x, y, width, height, borderRadius, trackColor);

        ThumbBounds thumbBounds = getThumbBounds();
        Render2D.drawRoundedRect(context,
                (int) thumbBounds.x, (int) thumbBounds.y,
                (int) thumbBounds.width, (int) thumbBounds.height,
                borderRadius, thumbColor);

        if (focused && hasClass(StyleKey.FOCUS_RING)) {
            int focusColor = styleSystem.getColor(StyleKey.PRIMARY_LIGHT);
            Render2D.drawRoundedRectBorder(context, x - 1, y - 1, width + 2, height + 2, borderRadius + 1, focusColor, 1);
        }
    }

    private int getThumbColor() {
        int baseColor = styleSystem.getColor(StyleKey.PRIMARY);

        return switch (state) {
            case HOVERED -> brightenColor(baseColor, 15);
            case PRESSED -> darkenColor(baseColor, 10);
            case DISABLED -> fadeColor(baseColor, 0.3f);
            default -> baseColor;
        };
    }

    @Override
    protected int getStateColor() {
        int baseColor = getBgColor();
        if (baseColor == 0) baseColor = styleSystem.getColor(StyleKey.SURFACE);

        return switch (state) {
            case HOVERED -> brightenColor(baseColor, 10);
            case PRESSED -> baseColor;
            case DISABLED -> fadeColor(baseColor, 0.5f);
            default -> baseColor;
        };
    }

    private record ThumbBounds(float x, float y, float width, float height) { }

    @Override
    public ScrollbarItem addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }

    @Override
    public ScrollbarItem removeClass(StyleKey key) {
        super.removeClass(key);
        return this;
    }

    @Override
    public ScrollbarItem onClick(Runnable handler) {
        super.onClick(handler);
        return this;
    }

    @Override
    public ScrollbarItem onMouseEnter(Runnable handler) {
        super.onMouseEnter(handler);
        return this;
    }

    @Override
    public ScrollbarItem onMouseLeave(Runnable handler) {
        super.onMouseLeave(handler);
        return this;
    }

    @Override
    public ScrollbarItem onFocusGained(Runnable handler) {
        super.onFocusGained(handler);
        return this;
    }

    @Override
    public ScrollbarItem onFocusLost(Runnable handler) {
        super.onFocusLost(handler);
        return this;
    }

    @Override
    public ScrollbarItem setVisible(boolean visible) {
        super.setVisible(visible);
        return this;
    }

    @Override
    public ScrollbarItem setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        return this;
    }

    @Override
    public ScrollbarItem setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        return this;
    }

    @Override
    public ScrollbarItem setConstraints(LayoutConstraints constraints) {
        super.setConstraints(constraints);
        return this;
    }

    @Override
    public ScrollbarItem setTextRenderer(TextRenderer textRenderer) {
        super.setTextRenderer(textRenderer);
        return this;
    }

    public boolean isDragging() { return dragging; }
    public float getThumbRatio() { return thumbRatio; }
    public float getScrollRatio() { return scrollRatio; }
    public Orientation getOrientation() { return orientation; }

    public void setScrollRatioDirectly(float ratio) {
        this.scrollRatio = Math.max(0.0f, Math.min(1.0f - thumbRatio, ratio));
    }

    public void resetScrollbar() {
        this.scrollRatio = 0.0f;
        this.thumbRatio = 1.0f;
        this.dragging = false;
        setState(ItemState.NORMAL);
    }

    public boolean isAtTop() {
        return scrollRatio <= 0.001f;
    }

    public boolean isAtBottom() {
        float maxRatio = 1.0f - thumbRatio;
        return scrollRatio >= maxRatio - 0.001f;
    }

    public float getScrollProgress() {
        float maxRatio = 1.0f - thumbRatio;
        return maxRatio > 0.001f ? scrollRatio / maxRatio : 0.0f;
    }
}