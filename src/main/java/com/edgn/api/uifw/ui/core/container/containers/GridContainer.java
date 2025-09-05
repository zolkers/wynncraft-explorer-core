package com.edgn.api.uifw.ui.core.container.containers;

import com.edgn.api.uifw.ui.core.UIElement;
import com.edgn.api.uifw.ui.core.item.items.ScrollbarItem;
import com.edgn.api.uifw.ui.css.UIStyleSystem;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class GridContainer extends ScrollContainer {

    private int columns = 3;

    public GridContainer(UIStyleSystem styleSystem, int x, int y, int w, int h) {
        super(styleSystem, x, y, w, h);
    }

    public GridContainer setColumns(int columns) {
        this.columns = Math.max(1, columns);
        return this;
    }

    @Override
    protected void layoutChildren() {
        List<UIElement> kids = getChildren();
        if (kids.isEmpty()) return;

        int contentX = getViewportX();
        int contentY = getViewportY();
        int vw = getViewportWidth();
        int gap = getGap();

        int colCount = Math.max(1, this.columns);
        int totalGap = gap * (colCount - 1);
        int cellW = Math.max(0, (vw - totalGap) / colCount);

        int xCursor = contentX;
        int yCursor = contentY;
        int rowMaxH = 0;
        int colIndex = 0;

        for (UIElement child : kids) {
            if (!child.isVisible()) continue;
            if (child instanceof ScrollbarItem) continue;

            int mt = child.getMarginTop();
            int mb = child.getMarginBottom();
            int ml = child.getMarginLeft();
            int mr = child.getMarginRight();

            int cx = xCursor + ml;
            int cy = yCursor + mt;
            int cw = Math.max(0, cellW - ml - mr);

            child.setX(cx);
            child.setY(cy);
            child.setWidth(cw);
            child.updateConstraints();
            child.getInteractionBounds();

            int occupiedH = mt + child.getCalculatedHeight() + mb;
            if (occupiedH > rowMaxH) rowMaxH = occupiedH;

            colIndex++;
            if (colIndex < colCount) {
                xCursor += cellW + gap;
            } else {
                xCursor = contentX;
                yCursor += rowMaxH + gap;
                rowMaxH = 0;
                colIndex = 0;
            }
        }
    }

    @Override
    public ScrollContainer clearContentChildren() {
        List<UIElement> toRemove = new ArrayList<>();

        for (UIElement c : getChildren()) {
            if (!(c instanceof ScrollbarItem)) {
                toRemove.add(c);
            }
        }

        for (UIElement element : toRemove) {
            styleSystem.getEventManager().unregisterElement(element);
        }

        for (UIElement element : toRemove) {
            removeChild(element);
        }

        captured = null;
        capturedButton = -1;

        markConstraintsDirty();
        computeContentSize();
        clampScroll();
        updateInteractionBounds();

        styleSystem.getEventManager().updateAllConstraints();

        return this;
    }

    @Override
    public String toString() {
        return String.format("GridContainer{columns=%d, children=%d, visibleChildren=%d, viewport=[%d,%d,%d,%d], gap=%d}",
                columns,
                getChildren().size(),
                getChildren().stream().filter(UIElement::isVisible).count(),
                getViewportX(),
                getViewportY(),
                getViewportWidth(),
                getViewportHeight(),
                getGap()
        );
    }
}
