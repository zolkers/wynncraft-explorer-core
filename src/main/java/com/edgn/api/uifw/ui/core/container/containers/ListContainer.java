package com.edgn.api.uifw.ui.core.container.containers;

import com.edgn.api.uifw.ui.core.UIElement;
import com.edgn.api.uifw.ui.core.item.items.ScrollbarItem;
import com.edgn.api.uifw.ui.css.StyleKey;
import com.edgn.api.uifw.ui.css.UIStyleSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ListContainer<T> extends ScrollContainer {

    public enum Orientation { VERTICAL, HORIZONTAL }

    public static class ListItem<T> {
        private final T data;
        private final int index;
        private final UIElement element;
        private boolean selected = false;

        public ListItem(T data, int index, UIElement element) {
            this.data = data;
            this.index = index;
            this.element = element;
        }

        public T getData() { return data; }
        public int getIndex() { return index; }
        public UIElement getElement() { return element; }
        public boolean isSelected() { return selected; }

        void setSelected(boolean selected) { this.selected = selected; }
    }

    private final List<T> items = new ArrayList<>();
    private final List<ListItem<T>> listItems = new ArrayList<>();
    private Orientation orientation = Orientation.VERTICAL;

    // Item factory and handlers
    private Function<T, UIElement> itemFactory;
    private BiConsumer<ListItem<T>, UIElement> itemUpdater;
    private Consumer<ListItem<T>> onItemClick;
    private Consumer<ListItem<T>> onItemDoubleClick;
    private Consumer<ListItem<T>> onItemSelect;
    private BiConsumer<ListItem<T>, Boolean> onItemHover;

    // Selection management
    private boolean selectionEnabled = true;
    private boolean multiSelection = false;
    private final List<Integer> selectedIndices = new ArrayList<>();

    // Item sizing
    private Integer fixedItemHeight;
    private Integer fixedItemWidth;
    private int defaultItemHeight = 40;
    private int defaultItemWidth = 200;

    public ListContainer(UIStyleSystem styleSystem, int x, int y, int w, int h) {
        super(styleSystem, x, y, w, h);
    }

    // Item management methods
    public ListContainer<T> setItems(List<T> items) {
        clearItems();
        if (items != null) {
            this.items.addAll(items);
        }
        rebuildList();
        return this;
    }

    public ListContainer<T> addItem(T item) {
        if (item != null) {
            items.add(item);
            forceCompleteRebuild();
        }
        return this;
    }

    public ListContainer<T> addItem(int index, T item) {
        if (item != null && index >= 0 && index <= items.size()) {
            items.add(index, item);
            forceCompleteRebuild();
        }
        return this;
    }

    public ListContainer<T> removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            selectedIndices.removeIf(i -> i == index);

            for (int i = 0; i < selectedIndices.size(); i++) {
                if (selectedIndices.get(i) > index) {
                    selectedIndices.set(i, selectedIndices.get(i) - 1);
                }
            }

            forceCompleteRebuild();
        }
        return this;
    }

    public ListContainer<T> removeItem(T item) {
        int index = items.indexOf(item);
        if (index >= 0) {
            removeItem(index);
        }
        return this;
    }

    public ListContainer<T> updateItem(int index, T newItem) {
        if (index >= 0 && index < items.size() && newItem != null) {
            items.set(index, newItem);
            forceCompleteRebuild();
        }
        return this;
    }

    public ListContainer<T> clearItems() {
        items.clear();
        selectedIndices.clear();
        forceCompleteRebuild();
        return this;
    }

    private void forceCompleteRebuild() {
        UIElement focusedElement = styleSystem.getEventManager().getFocusedElement();

        for (UIElement child : getChildren()) {
            if (child != null) {
                styleSystem.getEventManager().unregisterElement(child);
            }
        }

        listItems.clear();

        List<UIElement> toRemove = new ArrayList<>();
        for (UIElement child : getChildren()) {
            if (!(child instanceof ScrollbarItem)) {
                toRemove.add(child);
            }
        }

        for (UIElement element : toRemove) {
            removeChild(element);
        }

        if (itemFactory != null && !items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                T item = items.get(i);
                UIElement element = itemFactory.apply(item);

                if (element != null) {
                    ListItem<T> listItem = new ListItem<>(item, i, element);
                    listItem.setSelected(selectedIndices.contains(i));
                    listItems.add(listItem);

                    setupElementInteractions(listItem, element);
                    addChild(element);
                    styleSystem.getEventManager().registerElement(element);
                }
            }
        }

        markConstraintsDirty();
        updateConstraints();

        if (focusedElement != null) {
            styleSystem.getEventManager().setFocus(focusedElement);
        }
    }


    public ListContainer<T> setItemFactory(Function<T, UIElement> factory) {
        this.itemFactory = factory;
        rebuildList();
        return this;
    }

    public ListContainer<T> setItemUpdater(BiConsumer<ListItem<T>, UIElement> updater) {
        this.itemUpdater = updater;
        return this;
    }

    public ListContainer<T> setOrientation(Orientation orientation) {
        this.orientation = orientation != null ? orientation : Orientation.VERTICAL;
        rebuildList();
        return this;
    }

    public ListContainer<T> setFixedItemHeight(Integer height) {
        this.fixedItemHeight = height;
        rebuildList();
        return this;
    }

    public ListContainer<T> setFixedItemWidth(Integer width) {
        this.fixedItemWidth = width;
        rebuildList();
        return this;
    }

    public ListContainer<T> setDefaultItemSize(int width, int height) {
        this.defaultItemWidth = width;
        this.defaultItemHeight = height;
        return this;
    }

    public ListContainer<T> onItemClick(Consumer<ListItem<T>> handler) {
        this.onItemClick = handler;
        return this;
    }

    public ListContainer<T> onItemDoubleClick(Consumer<ListItem<T>> handler) {
        this.onItemDoubleClick = handler;
        return this;
    }

    public ListContainer<T> onItemSelect(Consumer<ListItem<T>> handler) {
        this.onItemSelect = handler;
        return this;
    }

    public ListContainer<T> onItemHover(BiConsumer<ListItem<T>, Boolean> handler) {
        this.onItemHover = handler;
        return this;
    }

    // Selection methods
    public ListContainer<T> setSelectionEnabled(boolean enabled) {
        this.selectionEnabled = enabled;
        if (!enabled) {
            clearSelection();
        }
        return this;
    }

    public ListContainer<T> setMultiSelection(boolean enabled) {
        this.multiSelection = enabled;
        if (!enabled && selectedIndices.size() > 1) {
            int first = selectedIndices.get(0);
            clearSelection();
            selectItem(first);
        }
        return this;
    }

    public ListContainer<T> selectItem(int index) {
        if (!selectionEnabled || index < 0 || index >= items.size()) {
            return this;
        }

        if (!multiSelection) {
            clearSelection();
        }

        if (!selectedIndices.contains(index)) {
            selectedIndices.add(index);
            if (index < listItems.size()) {
                listItems.get(index).setSelected(true);
                if (onItemSelect != null) {
                    onItemSelect.accept(listItems.get(index));
                }
            }
        }
        return this;
    }

    public ListContainer<T> deselectItem(int index) {
        if (selectedIndices.contains(index)) {
            selectedIndices.remove(Integer.valueOf(index));
            if (index < listItems.size()) {
                listItems.get(index).setSelected(false);
            }
        }
        return this;
    }

    public ListContainer<T> clearSelection() {
        for (int index : selectedIndices) {
            if (index < listItems.size()) {
                listItems.get(index).setSelected(false);
            }
        }
        selectedIndices.clear();
        return this;
    }

    public ListContainer<T> toggleSelection(int index) {
        if (selectedIndices.contains(index)) {
            deselectItem(index);
        } else {
            selectItem(index);
        }
        return this;
    }

    // Getters
    public List<T> getItems() {
        return new ArrayList<>(items);
    }

    public T getItem(int index) {
        return (index >= 0 && index < items.size()) ? items.get(index) : null;
    }

    public ListItem<T> getListItem(int index) {
        return (index >= 0 && index < listItems.size()) ? listItems.get(index) : null;
    }

    public List<ListItem<T>> getSelectedItems() {
        return selectedIndices.stream()
                .filter(i -> i < listItems.size())
                .map(listItems::get)
                .toList();
    }

    public List<Integer> getSelectedIndices() {
        return new ArrayList<>(selectedIndices);
    }

    public int getItemCount() {
        return items.size();
    }

    public int getSelectedCount() {
        return selectedIndices.size();
    }

    public boolean isItemSelected(int index) {
        return selectedIndices.contains(index);
    }

    private void refreshItem(int index) {
        if (index >= 0 && index < listItems.size() && itemUpdater != null) {
            ListItem<T> listItem = listItems.get(index);
            itemUpdater.accept(listItem, listItem.getElement());
        }
    }

    private void rebuildList() {
        forceCompleteRebuild();
    }

    private void setupElementInteractions(ListItem<T> listItem, UIElement element) {
        final int currentIndex = listItem.getIndex();

        element.onClick(() -> {
            if (selectionEnabled) {
                if (multiSelection) {
                    toggleSelection(currentIndex);
                } else {
                    selectItem(currentIndex);
                }
            }
            if (onItemClick != null) {
                onItemClick.accept(listItem);
            }
        });

        element.onMouseEnter(() -> {
            if (onItemHover != null) {
                onItemHover.accept(listItem, true);
            }
        });

        element.onMouseLeave(() -> {
            if (onItemHover != null) {
                onItemHover.accept(listItem, false);
            }
        });
    }

    @Override
    protected void layoutChildren() {
        var kids = getChildren();
        if (kids.isEmpty()) return;

        for (UIElement child : kids) {
            if (child.isVisible()) {
                child.markConstraintsDirty();
            }
        }

        int contentX = getViewportX();
        int contentY = getViewportY();
        int vw = getViewportWidth();
        int vh = getViewportHeight();
        int gap = getGap();

        if (orientation == Orientation.VERTICAL) {
            layoutVertical(kids, contentX, contentY, vw, gap);
        } else {
            layoutHorizontal(kids, contentX, contentY, vh, gap);
        }

        for (UIElement child : kids) {
            if (child.isVisible()) {
                child.updateConstraints();
            }
        }
    }

    private void layoutVertical(List<UIElement> kids, int contentX, int contentY, int vw, int gap) {
        int yCursor = contentY;
        int prevMB = 0;

        for (UIElement child : kids) {
            if (isNotLayoutCandidate(child)) continue;

            int mt = child.getMarginTop();
            int mb = child.getMarginBottom();
            int ml = child.getMarginLeft();
            int mr = child.getMarginRight();

            yCursor += (yCursor == contentY ? 0 : gap) + prevMB + mt;

            int cx = contentX + ml;
            int cw = Math.max(0, vw - ml - mr);
            int ch = fixedItemHeight != null ? fixedItemHeight : child.getHeight();

            child.setX(cx);
            child.setY(yCursor);
            child.setWidth(cw);
            if (fixedItemHeight != null) {
                child.setHeight(ch);
            }
            child.updateConstraints();
            child.getInteractionBounds();

            yCursor += child.getCalculatedHeight();
            prevMB = mb;
        }
    }

    private void layoutHorizontal(List<UIElement> kids, int contentX, int contentY, int vh, int gap) {
        int xCursor = contentX;
        int prevMR = 0;

        for (UIElement child : kids) {
            if (isNotLayoutCandidate(child)) continue;

            int mt = child.getMarginTop();
            int mb = child.getMarginBottom();
            int ml = child.getMarginLeft();
            int mr = child.getMarginRight();

            xCursor += (xCursor == contentX ? 0 : gap) + prevMR + ml;

            int cy = contentY + mt;
            int ch = Math.max(0, vh - mt - mb);
            int cw = fixedItemWidth != null ? fixedItemWidth : child.getWidth();

            child.setX(xCursor);
            child.setY(cy);
            if (fixedItemWidth != null) {
                child.setWidth(cw);
            }
            child.setHeight(ch);
            child.updateConstraints();
            child.getInteractionBounds();

            xCursor += child.getCalculatedWidth();
            prevMR = mr;
        }
    }

    private boolean isNotLayoutCandidate(UIElement c) {
        return c == null || !c.isVisible() || c instanceof ScrollbarItem;
    }

    // Utility methods
    public ListContainer<T> scrollToItem(int index) {
        if (index >= 0 && index < listItems.size()) {
            ListItem<T> item = listItems.get(index);
            UIElement element = item.getElement();

            if (orientation == Orientation.VERTICAL) {
                int targetY = element.getCalculatedY() - getViewportY();
                setScrollY(targetY - getViewportHeight() / 2);
            } else {
                int targetX = element.getCalculatedX() - getViewportX();
                setScrollX(targetX - getViewportWidth() / 2);
            }
        }
        return this;
    }

    public ListContainer<T> refresh() {
        rebuildList();
        return this;
    }

    // Fluent API chaining overrides
    @Override
    public ListContainer<T> setBackgroundColor(int argb) {
        super.setBackgroundColor(argb);
        return this;
    }

    @Override
    public ListContainer<T> setRenderBackground(boolean enabled) {
        super.setRenderBackground(enabled);
        return this;
    }

    @Override
    public ListContainer<T> addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }

    @Override
    public ListContainer<T> removeClass(StyleKey key) {
        super.removeClass(key);
        return this;
    }

    @Override
    public ListContainer<T> setScrollable(boolean enabled) {
        super.setScrollable(enabled);
        return this;
    }

    @Override
    public ListContainer<T> setScrollAxes(boolean vertical, boolean horizontal) {
        super.setScrollAxes(vertical, horizontal);
        return this;
    }

    @Override
    public ListContainer<T> setShowScrollbars(boolean show) {
        super.setShowScrollbars(show);
        return this;
    }

    @Override
    public ListContainer<T> setScrollStep(int step) {
        super.setScrollStep(step);
        return this;
    }

    @Override
    public String toString() {
        return String.format("ListContainer{orientation=%s, items=%d, selected=%d, viewport=[%d,%d,%d,%d], gap=%d}",
                orientation,
                getItemCount(),
                getSelectedCount(),
                getViewportX(),
                getViewportY(),
                getViewportWidth(),
                getViewportHeight(),
                getGap()
        );
    }
}