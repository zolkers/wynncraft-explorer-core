package com.edgn.uifw.elements.container;

import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.elements.UIElement;
import com.edgn.uifw.elements.item.items.ScrollbarItem;
import com.edgn.uifw.layout.LayoutEngine;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class ScrollableContainer extends BaseContainer {

    protected ScrollbarItem scrollbar;
    protected boolean showScrollbar = false;
    protected boolean alwaysShowScrollbar = false;
    protected static final int SCROLLBAR_WIDTH = 12;

    protected int scrollOffset = 0;
    protected int maxVisibleItems = 0;

    protected boolean smoothScrolling = true;
    protected boolean pixelScrolling = false;
    protected float pixelScrollOffset = 0.0f;
    protected float scrollVelocity = 0.0f;
    protected float scrollDamping = 0.85f;
    protected int pixelsPerScrollStep = 20;
    protected int scrollItemsPerStep = 1;

    protected int instantScrollThreshold = 5;
    protected int gradualScrollThreshold = 20;

    protected Consumer<Integer> onItemClickHandler;
    protected Predicate<Integer> itemEnabledCheck;
    protected StyleKey[] defaultItemStyles = {StyleKey.BG_SURFACE, StyleKey.ROUNDED_SM};
    protected StyleKey[] selectedItemStyles = {StyleKey.PRIMARY, StyleKey.ROUNDED_SM};
    protected StyleKey[] hoveredItemStyles = {StyleKey.PRIMARY_LIGHT, StyleKey.ROUNDED_SM};
    protected int selectedIndex = -1;
    protected int hoveredIndex = -1;

    public ScrollableContainer(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
        initializeScrollbar();
    }

    protected void initializeScrollbar() {
        scrollbar = new ScrollbarItem(styleSystem,
                x + width - SCROLLBAR_WIDTH, y,
                SCROLLBAR_WIDTH, height,
                ScrollbarItem.Orientation.VERTICAL)
                .addClass(StyleKey.BG_SURFACE, StyleKey.ROUNDED_SM)
                .onScroll(this::onScroll);
    }

    public ScrollableContainer setInstantScrollThreshold(int threshold) {
        this.instantScrollThreshold = Math.max(0, threshold);
        return this;
    }

    public ScrollableContainer setGradualScrollThreshold(int threshold) {
        this.gradualScrollThreshold = Math.max(instantScrollThreshold, threshold);
        return this;
    }

    public ScrollableContainer setSmartScrollThresholds(int instantThreshold, int gradualThreshold) {
        setInstantScrollThreshold(instantThreshold);
        setGradualScrollThreshold(gradualThreshold);
        return this;
    }

    public ScrollableContainer smartScrollToTop() {
        int itemCount = getItemCount();

        if (itemCount <= instantScrollThreshold) {
            scrollToTop();
        } else if (itemCount <= gradualScrollThreshold) {
            gradualScrollToTop();
        }

        return this;
    }

    protected void gradualScrollToTop() {
        if (pixelScrolling) {
            float targetOffset = 0.0f;
            float currentOffset = pixelScrollOffset;
            float distance = currentOffset - targetOffset;

            if (distance > 0) {
                scrollVelocity = -distance * 0.08f;

                if (Math.abs(scrollVelocity) < 30.0f) {
                    scrollVelocity = -30.0f;
                }

                if (scrollVelocity < -100.0f) {
                    scrollVelocity = -100.0f;
                }
            }
        } else {
            if (scrollOffset > 0) {
                int stepsToTop = Math.min(scrollOffset, Math.max(1, scrollOffset / 5));
                scrollBy(-stepsToTop);
            }
        }
    }

    public ScrollableContainer setAlwaysShowScrollbar(boolean alwaysShow) {
        this.alwaysShowScrollbar = alwaysShow;
        updateScrollbar();
        return this;
    }

    public ScrollableContainer setSmoothScrolling(boolean smooth) {
        this.smoothScrolling = smooth;
        return this;
    }

    public ScrollableContainer setPixelScrolling(boolean pixelScroll) {
        this.pixelScrolling = pixelScroll;
        if (pixelScroll) {
            this.pixelScrollOffset = scrollOffset * getItemHeight();
        }
        return this;
    }

    public ScrollableContainer setPixelsPerScrollStep(int pixels) {
        this.pixelsPerScrollStep = Math.max(1, pixels);
        return this;
    }

    public ScrollableContainer setScrollDamping(float damping) {
        this.scrollDamping = Math.max(0.0f, Math.min(1.0f, damping));
        return this;
    }

    public ScrollableContainer setScrollItemsPerStep(int itemsPerStep) {
        this.scrollItemsPerStep = Math.max(1, itemsPerStep);
        return this;
    }

    public ScrollableContainer setDefaultItemStyles(StyleKey... styles) {
        this.defaultItemStyles = styles != null ? styles : new StyleKey[0];
        return this;
    }

    public ScrollableContainer setSelectedItemStyles(StyleKey... styles) {
        this.selectedItemStyles = styles != null ? styles : new StyleKey[0];
        return this;
    }

    public ScrollableContainer setHoveredItemStyles(StyleKey... styles) {
        this.hoveredItemStyles = styles != null ? styles : new StyleKey[0];
        return this;
    }

    public ScrollableContainer onItemClick(Consumer<Integer> handler) {
        this.onItemClickHandler = handler;
        return this;
    }

    public ScrollableContainer setItemEnabledCheck(Predicate<Integer> check) {
        this.itemEnabledCheck = check;
        return this;
    }

    public ScrollableContainer setSelectedIndex(int index) {
        this.selectedIndex = (index >= 0 && index < children.size()) ? index : -1;
        return this;
    }

    public ScrollableContainer scrollToTop() {
        if (pixelScrolling) {
            pixelScrollOffset = 0.0f;
            scrollVelocity = 0.0f;
            updatePixelScroll();
        } else {
            scrollOffset = 0;
            updateScrollbar();
        }
        return this;
    }

    public ScrollableContainer scrollToBottom() {
        if (pixelScrolling) {
            pixelScrollOffset = getMaxPixelScrollOffset();
            scrollVelocity = 0.0f;
            updatePixelScroll();
        } else {
            scrollOffset = getMaxScrollOffset();
            updateScrollbar();
        }
        return this;
    }

    public ScrollableContainer scrollBy(int items) {
        if (pixelScrolling) {
            float pixelDelta = items * getItemHeight();
            scrollByPixels(pixelDelta);
        } else {
            int newOffset = scrollOffset + items;
            scrollOffset = Math.max(0, Math.min(newOffset, getMaxScrollOffset()));
            updateScrollbar();
        }
        return this;
    }

    public ScrollableContainer scrollByPixels(float pixels) {
        if (pixelScrolling) {
            float maxVelocity = pixelsPerScrollStep * 2.0f;
            scrollVelocity = Math.max(-maxVelocity, Math.min(maxVelocity, scrollVelocity + pixels));
        } else {
            int items = Math.round(pixels / getItemHeight());
            scrollBy(items);
        }
        return this;
    }

    public ScrollableContainer scrollToItem(int index) {
        if (index < 0 || index >= children.size()) return this;

        if (index < scrollOffset) {
            scrollOffset = index;
        } else if (index >= scrollOffset + maxVisibleItems) {
            scrollOffset = index - maxVisibleItems + 1;
        }

        scrollOffset = Math.max(0, Math.min(scrollOffset, getMaxScrollOffset()));
        updateScrollbar();
        return this;
    }

    public ScrollableContainer clear() {
        return clearItems();
    }

    public ScrollableContainer clearItems() {
        for (UIElement child : children) {
            if (child != null) child.setParent(null);
        }
        children.clear();
        selectedIndex = -1;
        hoveredIndex = -1;
        updateScrollbar();
        return this;
    }

    protected abstract int getItemCount();
    protected abstract int getItemHeight();
    protected abstract float getMaxPixelScrollOffset();
    protected abstract int getMaxScrollOffset();
    protected abstract boolean isItemVisible(int index);
    protected abstract void updateScrollbar();
    protected abstract void updatePixelScroll();
    protected abstract void applyItemStyles(UIElement item, int index);

    protected void updateScrollbarPosition() {
        LayoutEngine.LayoutBox content = getContentArea();
        scrollbar.setX(content.x() + content.width() - SCROLLBAR_WIDTH);
        scrollbar.setY(content.y());
        scrollbar.setHeight(content.height());
    }

    protected void onScroll(float scrollRatioFromScrollbar) {
        float correctedRatio = scrollRatioFromScrollbar;
        int itemCount = getItemCount();

        if (itemCount > maxVisibleItems) {
            float viewportRatio = (float) maxVisibleItems / itemCount;
            if (viewportRatio > 0 && viewportRatio < 1) {
                correctedRatio = scrollRatioFromScrollbar / (1.0f - viewportRatio);
            }
        }

        correctedRatio = Math.max(0.0f, Math.min(1.0f, correctedRatio));
        int maxOffset = getMaxScrollOffset();

        if (pixelScrolling) {
            float maxPixelOffset = getMaxPixelScrollOffset();
            pixelScrollOffset = correctedRatio * maxPixelOffset;
            scrollOffset = Math.round(pixelScrollOffset / getItemHeight());
            scrollVelocity = 0;
        } else {
            scrollOffset = Math.round(correctedRatio * maxOffset);
        }

        scrollOffset = Math.max(0, Math.min(scrollOffset, maxOffset));
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (showScrollbar && scrollbar.contains(mouseX, mouseY)) {
            return scrollbar.onMouseClick(mouseX, mouseY, button);
        }

        return handleItemClick(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        if (showScrollbar && scrollbar.handleMouseRelease(mouseX, mouseY, button)) {
            return true;
        }
        return handleItemRelease(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (showScrollbar && scrollbar.isDragging()) {
            scrollbar.onMouseMove(mouseX, mouseY);
            return true;
        }
        return handleItemDrag(mouseX, mouseY, button, deltaX, deltaY);
    }

    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        LayoutEngine.LayoutBox content = getContentArea();

        if (showScrollbar && scrollbar.contains(mouseX, mouseY)) {
            return scrollbar.handleMouseScroll(mouseX, mouseY, scrollDelta);
        }

        if (mouseX >= content.x() && mouseY >= content.y() &&
                mouseX < content.x() + content.width() && mouseY < content.y() + content.height()) {

            if (pixelScrolling) {
                float scrollDirection = scrollDelta > 0 ? -1.0f : 1.0f;
                float pixelDelta = scrollDirection * pixelsPerScrollStep;
                scrollByPixels(pixelDelta);
            } else if (smoothScrolling) {
                int scrollDirection = scrollDelta > 0 ? -1 : 1;
                int itemsToScroll = scrollItemsPerStep * scrollDirection;
                scrollBy(itemsToScroll);
            } else {
                int pixelScroll = (int) (-scrollDelta * getItemHeight());
                int itemsToScroll = pixelScroll / getItemHeight();
                scrollBy(itemsToScroll);
            }

            return true;
        }
        return false;
    }

    protected abstract boolean handleItemClick(double mouseX, double mouseY, int button);
    protected abstract boolean handleItemRelease(double mouseX, double mouseY, int button);
    protected abstract boolean handleItemDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY);

    public int getSelectedIndex() { return selectedIndex; }
    public UIElement getSelectedItem() {
        return (selectedIndex >= 0 && selectedIndex < children.size()) ?
                children.get(selectedIndex) : null;
    }
    public int getScrollOffset() { return scrollOffset; }
    public boolean isScrollbarVisible() { return showScrollbar; }
    public boolean isSmoothScrolling() { return smoothScrolling; }
    public boolean isPixelScrolling() { return pixelScrolling; }
    public float getPixelScrollOffset() { return pixelScrollOffset; }
    public int getPixelsPerScrollStep() { return pixelsPerScrollStep; }
    public float getScrollDamping() { return scrollDamping; }
    public int getScrollItemsPerStep() { return scrollItemsPerStep; }
    public int getInstantScrollThreshold() { return instantScrollThreshold; }
    public int getGradualScrollThreshold() { return gradualScrollThreshold; }

    @Override
    public void render(DrawContext context) {
        if (!visible) return;

        if (pixelScrolling && Math.abs(scrollVelocity) > 0.5f) {
            updatePixelScroll();
        }

        renderBackground(context);

        LayoutEngine.LayoutBox content = getContentArea();
        int clipWidth = content.width() - (showScrollbar ? SCROLLBAR_WIDTH + 5 : 0);

        context.enableScissor(content.x(), content.y(),
                content.x() + clipWidth, content.y() + content.height());

        layoutChildren();

        for (int i = 0; i < children.size(); i++) {
            if (isItemVisible(i)) {
                UIElement child = children.get(i);
                if (child.isVisible()) {
                    child.render(context);
                }
            }
        }

        context.disableScissor();

        if (showScrollbar) {
            scrollbar.render(context);
        }
    }
}