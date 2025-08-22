package com.edgn.uifw.elements.container.containers;

import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.css.rules.AlignItems;
import com.edgn.uifw.elements.UIElement;
import com.edgn.uifw.elements.container.ScrollableContainer;
import com.edgn.uifw.layout.LayoutEngine;

import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "unchecked", "UnusedReturnValue"})
public class ListContainer extends ScrollableContainer {
    private int itemHeight = 30;
    private int itemSpacing = 2;
    private AlignItems itemAlignment = AlignItems.STRETCH;
    private int horizontalPadding = 5;

    public ListContainer(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
        calculateMaxVisibleItems();
    }

    public ListContainer setItemHeight(int height) {
        this.itemHeight = Math.max(10, height);
        calculateMaxVisibleItems();
        updateScrollbar();
        return this;
    }

    public ListContainer setItemSpacing(int spacing) {
        this.itemSpacing = Math.max(0, spacing);
        calculateMaxVisibleItems();
        updateScrollbar();
        return this;
    }

    public ListContainer setItemAlignment(AlignItems alignment) {
        this.itemAlignment = alignment;
        return this;
    }

    public ListContainer setHorizontalPadding(int padding) {
        this.horizontalPadding = Math.max(0, padding);
        return this;
    }

    public ListContainer add(UIElement element) {
        return addChild(element);
    }

    public ListContainer add(UIElement... elements) {
        for (UIElement element : elements) {
            addChild(element);
        }
        return this;
    }

    @Override
    public ListContainer addChild(UIElement element) {
        super.addChild(element);
        updateScrollbar();
        return this;
    }

    public ListContainer insertItem(int index, UIElement element) {
        if (index < 0 || index > children.size()) {
            return addChild(element);
        }
        children.add(index, element);
        element.setParent(this);
        updateScrollbar();
        return this;
    }

    public ListContainer removeItem(int index) {
        if (index >= 0 && index < children.size()) {
            UIElement removed = children.remove(index);
            if (removed != null) removed.setParent(null);
            if (selectedIndex == index) selectedIndex = -1;
            else if (selectedIndex > index) selectedIndex--;
            updateScrollbar();
        }
        return this;
    }

    @Override
    public ListContainer removeChild(UIElement element) {
        int index = children.indexOf(element);
        if (index >= 0) {
            removeItem(index);
        }
        return this;
    }

    @Override
    protected int getItemCount() {
        return children.size();
    }

    @Override
    protected int getItemHeight() {
        return itemHeight + itemSpacing;
    }

    @Override
    protected float getMaxPixelScrollOffset() {
        int totalHeight = children.size() * (itemHeight + itemSpacing);
        LayoutEngine.LayoutBox content = getContentArea();
        return Math.max(0, totalHeight - content.height());
    }

    @Override
    protected int getMaxScrollOffset() {
        return Math.max(0, children.size() - maxVisibleItems);
    }

    @Override
    protected boolean isItemVisible(int index) {
        if (pixelScrolling) {
            LayoutEngine.LayoutBox content = getContentArea();
            int itemY = (int)(index * (itemHeight + itemSpacing) - pixelScrollOffset);
            return itemY > -itemHeight && itemY < content.height();
        } else {
            return index >= scrollOffset && index < scrollOffset + maxVisibleItems;
        }
    }

    @Override
    protected void updateScrollbar() {
        boolean needsScrollbar = alwaysShowScrollbar || children.size() > maxVisibleItems;
        showScrollbar = needsScrollbar;
        scrollbar.setVisible(needsScrollbar);

        if (needsScrollbar && children.size() > maxVisibleItems) {
            float viewportRatio = (float) maxVisibleItems / children.size();
            float scrollRatio;

            if (pixelScrolling) {
                float maxPixelOffset = getMaxPixelScrollOffset();
                scrollRatio = maxPixelOffset > 0 ? pixelScrollOffset / maxPixelOffset : 0.0f;
            } else {
                int maxOffset = getMaxScrollOffset();
                scrollRatio = maxOffset > 0 ? (float) scrollOffset / maxOffset : 0.0f;
            }

            scrollbar.updateScrollbar(viewportRatio, scrollRatio);
        }
    }

    @Override
    protected void updatePixelScroll() {
        if (Math.abs(scrollVelocity) > 0.5f) {
            pixelScrollOffset += scrollVelocity;
            scrollVelocity *= scrollDamping;

            float maxOffset = getMaxPixelScrollOffset();
            if (pixelScrollOffset < 0) {
                pixelScrollOffset = 0;
                scrollVelocity = 0;
            } else if (pixelScrollOffset > maxOffset) {
                pixelScrollOffset = maxOffset;
                scrollVelocity = 0;
            }

            scrollOffset = Math.round(pixelScrollOffset / (itemHeight + itemSpacing));
            scrollOffset = Math.max(0, Math.min(scrollOffset, getMaxScrollOffset()));

            updateScrollbar();
        } else {
            scrollVelocity = 0;
        }
    }

    @Override
    protected void applyItemStyles(UIElement item, int index) {
        for (StyleKey style : defaultItemStyles) item.removeClass(style);
        for (StyleKey style : selectedItemStyles) item.removeClass(style);
        for (StyleKey style : hoveredItemStyles) item.removeClass(style);

        if (index == selectedIndex) {
            item.addClass(selectedItemStyles);
        } else if (index == hoveredIndex) {
            item.addClass(hoveredItemStyles);
        } else {
            item.addClass(defaultItemStyles);
        }

        if (itemEnabledCheck != null) {
            item.setEnabled(itemEnabledCheck.test(index));
        }
    }

    @Override
    protected void layoutChildren() {
        updateConstraints();
        calculateMaxVisibleItems();
        updateScrollbarPosition();

        if (pixelScrolling) {
            updatePixelScroll();
        } else {
            updateScrollbar();
        }

        LayoutEngine.LayoutBox content = getContentArea();
        int availableWidth = content.width() - (showScrollbar ? SCROLLBAR_WIDTH + 5 : 0);

        for (int i = 0; i < children.size(); i++) {
            UIElement child = children.get(i);

            if (isItemVisible(i)) {
                int itemY;

                if (pixelScrolling) {
                    itemY = content.y() + (int)(i * (itemHeight + itemSpacing) - pixelScrollOffset);
                } else {
                    int relativeIndex = i - scrollOffset;
                    itemY = content.y() + relativeIndex * (itemHeight + itemSpacing);
                }

                switch (itemAlignment) {
                    case STRETCH -> {
                        child.setX(content.x() + horizontalPadding);
                        child.setY(itemY);
                        child.setWidth(availableWidth - (horizontalPadding * 2));
                        child.setHeight(itemHeight);
                    }
                    case CENTER -> {
                        int childWidth = Math.min(child.getWidth(), availableWidth - (horizontalPadding * 2));
                        child.setX(content.x() + (availableWidth - childWidth) / 2);
                        child.setY(itemY);
                        child.setWidth(childWidth);
                        child.setHeight(itemHeight);
                    }
                    case FLEX_START -> {
                        child.setX(content.x() + horizontalPadding);
                        child.setY(itemY);
                        child.setHeight(itemHeight);
                    }
                    case FLEX_END -> {
                        child.setX(content.x() + availableWidth - child.getWidth() - horizontalPadding);
                        child.setY(itemY);
                        child.setHeight(itemHeight);
                    }
                }

                applyItemStyles(child, i);
            } else {
                child.setX(-1000);
                child.setY(-1000);
            }
        }
    }

    @Override
    protected boolean handleItemClick(double mouseX, double mouseY, int button) {
        LayoutEngine.LayoutBox content = getContentArea();

        if (mouseX >= content.x() && mouseY >= content.y() &&
                mouseX < content.x() + content.width() && mouseY < content.y() + content.height()) {

            int itemIndex;
            int relativeY = (int) (mouseY - content.y());

            if (pixelScrolling) {
                float itemPosition = relativeY + pixelScrollOffset;
                itemIndex = (int) (itemPosition / (itemHeight + itemSpacing));
            } else {
                itemIndex = relativeY / (itemHeight + itemSpacing) + scrollOffset;
            }

            if (itemIndex >= 0 && itemIndex < children.size() && isItemVisible(itemIndex)) {
                UIElement item = children.get(itemIndex);

                boolean inItemBounds = mouseX >= item.getX() && mouseY >= item.getY() &&
                        mouseX < item.getX() + item.getWidth() &&
                        mouseY < item.getY() + item.getHeight();

                if (item.isEnabled() && inItemBounds && item.onMouseClick(mouseX, mouseY, button)) {
                    setSelectedIndex(itemIndex);

                    if (onItemClickHandler != null) {
                        onItemClickHandler.accept(itemIndex);
                    }

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected boolean handleItemRelease(double mouseX, double mouseY, int button) {
        for (int i = 0; i < children.size(); i++) {
            if (isItemVisible(i)) {
                UIElement child = children.get(i);
                if (child.isVisible() && child.isEnabled() && child.onMouseRelease(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean handleItemDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (int i = 0; i < children.size(); i++) {
            if (isItemVisible(i)) {
                UIElement child = children.get(i);
                if (child.isVisible() && child.isEnabled() && child.onMouseDrag(mouseX, mouseY, button, deltaX, deltaY)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void calculateMaxVisibleItems() {
        LayoutEngine.LayoutBox content = getContentArea();
        if (itemHeight + itemSpacing > 0) {
            maxVisibleItems = Math.max(1, content.height() / (itemHeight + itemSpacing));
        } else {
            maxVisibleItems = 1;
        }
    }

    public int getItemSpacing() { return itemSpacing; }
    public AlignItems getItemAlignment() { return itemAlignment; }
    public int getHorizontalPadding() { return horizontalPadding; }
    public int getVisibleItemCount() { return Math.min(maxVisibleItems, children.size()); }

    @Override
    public ListContainer addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }

    @Override
    public ListContainer removeClass(StyleKey key) {
        super.removeClass(key);
        return this;
    }

    @Override
    public ListContainer setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        return this;
    }

    @Override
    public ListContainer setInstantScrollThreshold(int threshold) {
        super.setInstantScrollThreshold(threshold);
        return this;
    }

    @Override
    public ListContainer setGradualScrollThreshold(int threshold) {
        super.setGradualScrollThreshold(threshold);
        return this;
    }

    @Override
    public ListContainer setSmartScrollThresholds(int instantThreshold, int gradualThreshold) {
        super.setSmartScrollThresholds(instantThreshold, gradualThreshold);
        return this;
    }

    @Override
    public ListContainer smartScrollToTop() {
        super.smartScrollToTop();
        return this;
    }

    @Override
    public ListContainer scrollToTop() {
        super.scrollToTop();
        return this;
    }

    @Override
    public ListContainer scrollToBottom() {
        super.scrollToBottom();
        return this;
    }

    @Override
    public ListContainer scrollBy(int items) {
        super.scrollBy(items);
        return this;
    }

    @Override
    public ListContainer scrollByPixels(float pixels) {
        super.scrollByPixels(pixels);
        return this;
    }

    @Override
    public ListContainer scrollToItem(int index) {
        super.scrollToItem(index);
        return this;
    }

    @Override
    public ListContainer setAlwaysShowScrollbar(boolean alwaysShow) {
        super.setAlwaysShowScrollbar(alwaysShow);
        return this;
    }

    @Override
    public ListContainer setSmoothScrolling(boolean smooth) {
        super.setSmoothScrolling(smooth);
        return this;
    }

    @Override
    public ListContainer setPixelScrolling(boolean pixelScroll) {
        super.setPixelScrolling(pixelScroll);
        return this;
    }

    @Override
    public ListContainer setPixelsPerScrollStep(int pixels) {
        super.setPixelsPerScrollStep(pixels);
        return this;
    }

    @Override
    public ListContainer setScrollDamping(float damping) {
        super.setScrollDamping(damping);
        return this;
    }

    @Override
    public ListContainer setScrollItemsPerStep(int itemsPerStep) {
        super.setScrollItemsPerStep(itemsPerStep);
        return this;
    }

    @Override
    public ListContainer setDefaultItemStyles(StyleKey... styles) {
        super.setDefaultItemStyles(styles);
        return this;
    }

    @Override
    public ListContainer setSelectedItemStyles(StyleKey... styles) {
        super.setSelectedItemStyles(styles);
        return this;
    }

    @Override
    public ListContainer setHoveredItemStyles(StyleKey... styles) {
        super.setHoveredItemStyles(styles);
        return this;
    }

    @Override
    public ListContainer onItemClick(Consumer<Integer> handler) {
        super.onItemClick(handler);
        return this;
    }

    @Override
    public ListContainer setItemEnabledCheck(Predicate<Integer> check) {
        super.setItemEnabledCheck(check);
        return this;
    }

    @Override
    public ListContainer setSelectedIndex(int index) {
        super.setSelectedIndex(index);
        return this;
    }

    @Override
    public ListContainer clearItems() {
        super.clearItems();
        return this;
    }
}