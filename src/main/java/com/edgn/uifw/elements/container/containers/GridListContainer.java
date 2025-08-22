package com.edgn.uifw.elements.container.containers;

import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.css.rules.JustifyContent;
import com.edgn.uifw.elements.UIElement;
import com.edgn.uifw.elements.container.ScrollableContainer;
import com.edgn.uifw.layout.LayoutEngine;

import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "unchecked", "UnusedReturnValue"})
public class GridListContainer extends ScrollableContainer {
    private int columns = 3;
    private int itemWidth = 100;
    private int itemHeight = 100;
    private int horizontalGap = 8;
    private int verticalGap = 8;
    private int horizontalPadding = 8;
    private int verticalPadding = 8;

    private JustifyContent justifyContent = JustifyContent.FLEX_START;
    private int totalRows = 0;

    private boolean autoSizeColumns = false;
    private int minItemWidth = 80;
    private int maxItemWidth = 200;

    public GridListContainer(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
        calculateLayout();
    }

    public GridListContainer setColumns(int columns) {
        this.columns = Math.max(1, columns);
        this.autoSizeColumns = false;
        calculateLayout();
        updateScrollbar();
        return this;
    }

    public GridListContainer setAutoSizeColumns(boolean autoSize) {
        this.autoSizeColumns = autoSize;
        if (autoSize) {
            calculateAutoColumns();
        }
        calculateLayout();
        updateScrollbar();
        return this;
    }

    public GridListContainer setMinItemWidth(int minWidth) {
        this.minItemWidth = Math.max(20, minWidth);
        if (autoSizeColumns) {
            calculateAutoColumns();
            calculateLayout();
            updateScrollbar();
        }
        return this;
    }

    public GridListContainer setMaxItemWidth(int maxWidth) {
        this.maxItemWidth = Math.max(minItemWidth, maxWidth);
        if (autoSizeColumns) {
            calculateAutoColumns();
            calculateLayout();
            updateScrollbar();
        }
        return this;
    }

    public GridListContainer setItemSize(int width, int height) {
        this.itemWidth = Math.max(10, width);
        this.itemHeight = Math.max(10, height);
        if (autoSizeColumns) {
            calculateAutoColumns();
        }
        calculateLayout();
        updateScrollbar();
        return this;
    }

    public GridListContainer setItemWidth(int width) {
        this.itemWidth = Math.max(10, width);
        if (autoSizeColumns) {
            calculateAutoColumns();
        }
        calculateLayout();
        updateScrollbar();
        return this;
    }

    public GridListContainer setItemHeight(int height) {
        this.itemHeight = Math.max(10, height);
        calculateLayout();
        updateScrollbar();
        return this;
    }

    public GridListContainer setGap(int gap) {
        return setGap(gap, gap);
    }

    public GridListContainer setGap(int horizontalGap, int verticalGap) {
        this.horizontalGap = Math.max(0, horizontalGap);
        this.verticalGap = Math.max(0, verticalGap);
        calculateLayout();
        updateScrollbar();
        return this;
    }

    public GridListContainer setPadding(int padding) {
        return setPadding(padding, padding);
    }

    public GridListContainer setPadding(int horizontal, int vertical) {
        this.horizontalPadding = Math.max(0, horizontal);
        this.verticalPadding = Math.max(0, vertical);
        calculateLayout();
        updateScrollbar();
        return this;
    }

    public GridListContainer setJustifyContent(JustifyContent justify) {
        this.justifyContent = justify;
        return this;
    }

    // Convenience methods for adding items
    public GridListContainer add(UIElement element) {
        return addChild(element);
    }

    public GridListContainer add(UIElement... elements) {
        for (UIElement element : elements) {
            addChild(element);
        }
        return this;
    }

    @Override
    public GridListContainer addChild(UIElement element) {
        super.addChild(element);
        calculateLayout();
        updateScrollbar();
        return this;
    }

    public GridListContainer insertItem(int index, UIElement element) {
        if (index < 0 || index > children.size()) {
            return addChild(element);
        }
        children.add(index, element);
        element.setParent(this);
        calculateLayout();
        updateScrollbar();
        return this;
    }

    public GridListContainer removeItem(int index) {
        if (index >= 0 && index < children.size()) {
            UIElement removed = children.remove(index);
            if (removed != null) removed.setParent(null);
            if (selectedIndex == index) selectedIndex = -1;
            else if (selectedIndex > index) selectedIndex--;
            calculateLayout();
            updateScrollbar();
        }
        return this;
    }

    @Override
    public GridListContainer removeChild(UIElement element) {
        int index = children.indexOf(element);
        if (index >= 0) {
            removeItem(index);
        }
        return this;
    }

    // Implementation of abstract methods from ScrollableContainer
    @Override
    protected int getItemCount() {
        return children.size();
    }

    @Override
    protected int getItemHeight() {
        return itemHeight + verticalGap;
    }

    @Override
    protected float getMaxPixelScrollOffset() {
        int totalHeight = totalRows * (itemHeight + verticalGap) - verticalGap;
        LayoutEngine.LayoutBox content = getContentArea();
        int availableHeight = content.height() - (verticalPadding * 2);
        return Math.max(0, totalHeight - availableHeight);
    }

    @Override
    protected int getMaxScrollOffset() {
        return Math.max(0, totalRows - maxVisibleItems);
    }

    @Override
    protected boolean isItemVisible(int index) {
        if (index < 0 || index >= children.size()) return false;

        int row = index / columns;

        if (pixelScrolling) {
            LayoutEngine.LayoutBox content = getContentArea();
            int availableHeight = content.height() - (verticalPadding * 2);
            float itemY = row * (itemHeight + verticalGap) - pixelScrollOffset;

            // Ajoutez une marge pour éviter les problèmes de clipping
            return itemY > -itemHeight - 10 && itemY < availableHeight + 10;
        } else {
            return row >= scrollOffset && row < scrollOffset + maxVisibleItems;
        }
    }

    @Override
    public void updateScrollbar() {
        boolean needsScrollbar = alwaysShowScrollbar || totalRows > maxVisibleItems;
        showScrollbar = needsScrollbar;
        scrollbar.setVisible(needsScrollbar);

        if (needsScrollbar && totalRows > maxVisibleItems) {
            float viewportRatio = (float) maxVisibleItems / totalRows;
            float scrollRatio;

            if (pixelScrolling) {
                float maxPixelOffset = getMaxPixelScrollOffset();
                scrollRatio = maxPixelOffset > 0 ? pixelScrollOffset / maxPixelOffset : 0.0f;
            } else {
                int maxOffset = getMaxScrollOffset();
                scrollRatio = maxOffset > 0 ? (float) scrollOffset / maxOffset : 0.0f;
            }

            // Assurez-vous que les ratios sont dans les bonnes limites
            viewportRatio = Math.max(0.0f, Math.min(1.0f, viewportRatio));
            scrollRatio = Math.max(0.0f, Math.min(1.0f, scrollRatio));

            scrollbar.updateScrollbar(viewportRatio, scrollRatio);
        }
    }

    @Override
    protected void updatePixelScroll() {
        if (Math.abs(scrollVelocity) > 0.5f) {
            pixelScrollOffset += scrollVelocity;
            scrollVelocity *= scrollDamping;

            float maxOffset = getMaxPixelScrollOffset();

            // Correction des limites avec plus de précision
            if (pixelScrollOffset < 0) {
                pixelScrollOffset = 0;
                scrollVelocity = 0;
            } else if (pixelScrollOffset > maxOffset) {
                pixelScrollOffset = maxOffset;
                scrollVelocity = 0;
            }

            // Recalcul du scrollOffset basé sur le pixelScrollOffset
            if (itemHeight + verticalGap > 0) {
                scrollOffset = (int) Math.floor(pixelScrollOffset / (itemHeight + verticalGap));
            } else {
                scrollOffset = 0;
            }

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

    private void calculateAutoColumns() {
        if (!autoSizeColumns) return;

        LayoutEngine.LayoutBox content = getContentArea();
        int availableWidth = content.width() - (horizontalPadding * 2) - (showScrollbar ? SCROLLBAR_WIDTH + 5 : 0);

        int possibleColumns = (availableWidth + horizontalGap) / (minItemWidth + horizontalGap);
        columns = Math.max(1, possibleColumns);

        int totalGapWidth = (columns - 1) * horizontalGap;
        int availableForItems = availableWidth - totalGapWidth;
        itemWidth = Math.min(maxItemWidth, availableForItems / columns);
    }

    private void calculateLayout() {
        if (autoSizeColumns) {
            calculateAutoColumns();
        }

        totalRows = (int) Math.ceil((double) children.size() / columns);

        LayoutEngine.LayoutBox content = getContentArea();
        int availableHeight = content.height() - (verticalPadding * 2);

        if (itemHeight + verticalGap > 0) {
            maxVisibleItems = Math.max(1, (availableHeight + verticalGap) / (itemHeight + verticalGap));
        } else {
            maxVisibleItems = 1;
        }
    }

    @Override
    protected void layoutChildren() {
        updateConstraints();
        calculateLayout();
        updateScrollbarPosition();

        if (pixelScrolling) {
            updatePixelScroll();
        } else {
            updateScrollbar();
        }

        LayoutEngine.LayoutBox content = getContentArea();
        int availableWidth = content.width() - (showScrollbar ? SCROLLBAR_WIDTH + 5 : 0);
        int contentWidth = availableWidth - (horizontalPadding * 2);

        int totalItemsWidth = columns * itemWidth + (columns - 1) * horizontalGap;
        int startX = content.x() + horizontalPadding;

        switch (justifyContent) {
            case CENTER -> startX = content.x() + (availableWidth - totalItemsWidth) / 2;
            case FLEX_END -> startX = content.x() + availableWidth - totalItemsWidth - horizontalPadding;
            case SPACE_BETWEEN -> {
                if (columns > 1) {
                    startX = content.x() + horizontalPadding;
                    horizontalGap = (contentWidth - columns * itemWidth) / (columns - 1);
                }
            }
            case SPACE_AROUND -> {
                int totalGap = contentWidth - columns * itemWidth;
                horizontalGap = totalGap / columns;
                startX = content.x() + horizontalGap / 2;
            }
            case SPACE_EVENLY -> {
                int totalGap = contentWidth - columns * itemWidth;
                horizontalGap = totalGap / (columns + 1);
                startX = content.x() + horizontalGap;
            }
        }

        for (int i = 0; i < children.size(); i++) {
            UIElement child = children.get(i);

            if (isItemVisible(i)) {
                int row = i / columns;
                int col = i % columns;

                int itemX = startX + col * (itemWidth + horizontalGap);
                int itemY;

                if (pixelScrolling) {
                    // Utilisation de float pour plus de précision
                    float exactY = content.y() + verticalPadding + (row * (itemHeight + verticalGap)) - pixelScrollOffset;
                    itemY = Math.round(exactY);
                } else {
                    int relativeRow = row - scrollOffset;
                    itemY = content.y() + verticalPadding + relativeRow * (itemHeight + verticalGap);
                }

                child.setX(itemX);
                child.setY(itemY);
                child.setWidth(itemWidth);
                child.setHeight(itemHeight);

                applyItemStyles(child, i);
            } else {
                // Placer les éléments invisibles très loin pour éviter les interactions
                child.setX(-2000);
                child.setY(-2000);
            }
        }
    }

    @Override
    protected boolean handleItemClick(double mouseX, double mouseY, int button) {
        LayoutEngine.LayoutBox content = getContentArea();

        if (mouseX >= content.x() && mouseY >= content.y() &&
                mouseX < content.x() + content.width() && mouseY < content.y() + content.height()) {

            layoutChildren();

            for (int i = 0; i < children.size(); i++) {
                if (isItemVisible(i)) {
                    UIElement child = children.get(i);

                    boolean inBounds = mouseX >= child.getX() && mouseY >= child.getY() &&
                            mouseX < child.getX() + child.getWidth() &&
                            mouseY < child.getY() + child.getHeight();

                    if (child.isEnabled() && child.isVisible() && inBounds) {
                        boolean handled = child.onMouseClick(mouseX, mouseY, button);

                        if (handled) {
                            setSelectedIndex(i);
                            if (onItemClickHandler != null) {
                                onItemClickHandler.accept(i);
                            }
                            return true;
                        }
                    }
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

    // Correction de la méthode onScroll
    protected void onScroll(float scrollRatioFromScrollbar) {
        // Utilisez directement le ratio de la scrollbar
        float correctedRatio = Math.max(0.0f, Math.min(1.0f, scrollRatioFromScrollbar));
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

    // Ajout d'une méthode pour forcer le scroll vers le haut
    public GridListContainer scrollToTopInstant() {
        if (pixelScrolling) {
            pixelScrollOffset = 0.0f;
            scrollVelocity = 0.0f;
            scrollOffset = 0;
        } else {
            scrollOffset = 0;
        }
        updateScrollbar();
        return this;
    }

    @Override
    public GridListContainer scrollToTop() {
        return scrollToTopInstant();
    }

    @Override
    public GridListContainer scrollToBottom() {
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

    @Override
    public GridListContainer scrollBy(int items) {
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

    @Override
    public GridListContainer scrollByPixels(float pixels) {
        if (pixelScrolling) {
            float maxVelocity = pixelsPerScrollStep * 2.0f;
            scrollVelocity = Math.max(-maxVelocity, Math.min(maxVelocity, scrollVelocity + pixels));
        } else {
            int items = Math.round(pixels / getItemHeight());
            scrollBy(items);
        }
        return this;
    }

    @Override
    public GridListContainer scrollToItem(int index) {
        if (index < 0 || index >= children.size()) return this;

        int row = index / columns;

        if (pixelScrolling) {
            float targetPixelOffset = row * (itemHeight + verticalGap);
            LayoutEngine.LayoutBox content = getContentArea();
            int availableHeight = content.height() - (verticalPadding * 2);

            float maxOffset = getMaxPixelScrollOffset();
            pixelScrollOffset = Math.max(0, Math.min(maxOffset, targetPixelOffset));
            scrollOffset = Math.round(pixelScrollOffset / getItemHeight());
            scrollVelocity = 0;
        } else {
            if (row < scrollOffset) {
                scrollOffset = row;
            } else if (row >= scrollOffset + maxVisibleItems) {
                scrollOffset = row - maxVisibleItems + 1;
            }
            scrollOffset = Math.max(0, Math.min(scrollOffset, getMaxScrollOffset()));
        }

        updateScrollbar();
        return this;
    }

    @Override
    public GridListContainer clear() {
        return clearItems();
    }

    @Override
    public GridListContainer clearItems() {
        for (UIElement child : children) {
            if (child != null) child.setParent(null);
        }
        children.clear();
        selectedIndex = -1;
        hoveredIndex = -1;

        scrollToTopInstant();

        return this;
    }

    public int getColumns() { return columns; }
    public int getItemWidth() { return itemWidth; }
    public int getHorizontalGap() { return horizontalGap; }
    public int getVerticalGap() { return verticalGap; }
    public int getHorizontalPadding() { return horizontalPadding; }
    public int getVerticalPadding() { return verticalPadding; }
    public int getTotalRows() { return totalRows; }
    public int getVisibleRows() { return Math.min(maxVisibleItems, totalRows); }
    public boolean isAutoSizeColumns() { return autoSizeColumns; }
    public int getMinItemWidth() { return minItemWidth; }
    public int getMaxItemWidth() { return maxItemWidth; }
    public JustifyContent getJustifyContent() { return justifyContent; }

    @Override
    public GridListContainer addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }

    @Override
    public GridListContainer removeClass(StyleKey key) {
        super.removeClass(key);
        return this;
    }

    @Override
    public GridListContainer setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        return this;
    }

    @Override
    public GridListContainer setInstantScrollThreshold(int threshold) {
        super.setInstantScrollThreshold(threshold);
        return this;
    }

    @Override
    public GridListContainer setGradualScrollThreshold(int threshold) {
        super.setGradualScrollThreshold(threshold);
        return this;
    }

    @Override
    public GridListContainer setSmartScrollThresholds(int instantThreshold, int gradualThreshold) {
        super.setSmartScrollThresholds(instantThreshold, gradualThreshold);
        return this;
    }

    @Override
    public GridListContainer smartScrollToTop() {
        super.smartScrollToTop();
        return this;
    }

    @Override
    public GridListContainer setAlwaysShowScrollbar(boolean alwaysShow) {
        super.setAlwaysShowScrollbar(alwaysShow);
        return this;
    }

    @Override
    public GridListContainer setSmoothScrolling(boolean smooth) {
        super.setSmoothScrolling(smooth);
        return this;
    }

    @Override
    public GridListContainer setPixelScrolling(boolean pixelScroll) {
        super.setPixelScrolling(pixelScroll);
        return this;
    }

    @Override
    public GridListContainer setPixelsPerScrollStep(int pixels) {
        super.setPixelsPerScrollStep(pixels);
        return this;
    }

    @Override
    public GridListContainer setScrollDamping(float damping) {
        super.setScrollDamping(damping);
        return this;
    }

    @Override
    public GridListContainer setScrollItemsPerStep(int itemsPerStep) {
        super.setScrollItemsPerStep(itemsPerStep);
        return this;
    }

    @Override
    public GridListContainer setDefaultItemStyles(StyleKey... styles) {
        super.setDefaultItemStyles(styles);
        return this;
    }

    @Override
    public GridListContainer setSelectedItemStyles(StyleKey... styles) {
        super.setSelectedItemStyles(styles);
        return this;
    }

    @Override
    public GridListContainer setHoveredItemStyles(StyleKey... styles) {
        super.setHoveredItemStyles(styles);
        return this;
    }

    @Override
    public GridListContainer onItemClick(Consumer<Integer> handler) {
        super.onItemClick(handler);
        return this;
    }

    @Override
    public GridListContainer setItemEnabledCheck(Predicate<Integer> check) {
        super.setItemEnabledCheck(check);
        return this;
    }

    @Override
    public GridListContainer setSelectedIndex(int index) {
        super.setSelectedIndex(index);
        return this;
    }
}