package com.edgn.uifw.elements.container.containers;

import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.elements.UIElement;
import com.edgn.uifw.elements.container.BaseContainer;
import com.edgn.uifw.layout.LayoutEngine;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "unchecked"})
public class GridContainer extends BaseContainer {
    private final List<GridItem> items = new ArrayList<>();
    private int columns = 1;
    private int rows = 1;

    public GridContainer(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
    }

    public GridContainer addItem(UIElement element, int column, int row) {
        return addItem(element, column, row, 1, 1);
    }

    public GridContainer addItem(UIElement element, int column, int row, int colSpan, int rowSpan) {
        items.add(new GridItem(element, column, row, colSpan, rowSpan));
        addChild(element);
        return this;
    }

    public GridContainer add(UIElement element) {
        int nextColumn = items.size() % columns;
        int nextRow = items.size() / columns;
        return addItem(element, nextColumn, nextRow);
    }

    public GridContainer setColumns(int columns) {
        this.columns = Math.max(1, columns);
        return this;
    }

    public GridContainer setRows(int rows) {
        this.rows = Math.max(1, rows);
        return this;
    }

    @Override
    protected void layoutChildren() {
        LayoutEngine.LayoutBox content = getContentArea();
        int gap = getGap();

        int cellWidth = (content.width() - (columns - 1) * gap) / columns;
        int cellHeight = (content.height() - (rows - 1) * gap) / rows;

        for (GridItem item : items) {
            int itemX = content.x() + item.column * (cellWidth + gap);
            int itemY = content.y() + item.row * (cellHeight + gap);
            int itemWidth = cellWidth * item.colSpan + (item.colSpan - 1) * gap;
            int itemHeight = cellHeight * item.rowSpan + (item.rowSpan - 1) * gap;

            item.element.setX(itemX);
            item.element.setY(itemY);
            item.element.setWidth(itemWidth);
            item.element.setHeight(itemHeight);
        }
    }

    @Override
    public GridContainer addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }

    @Override
    public GridContainer removeClass(StyleKey key) {
        super.removeClass(key);
        return this;
    }

    public record GridItem(UIElement element, int column, int row, int colSpan, int rowSpan) {}
}