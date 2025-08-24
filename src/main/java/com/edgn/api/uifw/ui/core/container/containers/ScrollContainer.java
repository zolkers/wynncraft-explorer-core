package com.edgn.api.uifw.ui.core.container.containers;

import com.edgn.api.uifw.ui.core.UIElement;
import com.edgn.api.uifw.ui.core.container.BaseContainer;
import com.edgn.api.uifw.ui.core.container.IContainer;
import com.edgn.api.uifw.ui.core.item.items.ScrollbarItem;
import com.edgn.api.uifw.ui.core.models.scroll.ContainerScrollbarModel;
import com.edgn.api.uifw.ui.css.UIStyleSystem;
import com.edgn.api.uifw.ui.layout.LayoutEngine;
import com.edgn.api.uifw.ui.layout.ZIndex;
import net.minecraft.client.gui.DrawContext;

@SuppressWarnings("unused")
public class ScrollContainer extends BaseContainer {

    protected boolean scrollEnabled = true;
    protected boolean verticalScroll = true;
    protected boolean horizontalScroll = false;

    private UIElement captured = null;
    private int capturedButton = -1;

    protected int scrollX = 0;
    protected int scrollY = 0;
    protected int contentWidth = 0;
    protected int contentHeight = 0;

    protected int scrollStep = 40;

    private ScrollbarItem vbar;
    private ScrollbarItem hbar;
    private boolean showScrollbars = true;

    private int scrollbarThickness = 8;
    private int scrollbarPadding = 2;
    private int reserveRight = 0;
    private int reserveBottom = 0;

    public ScrollContainer(UIStyleSystem styleSystem, int x, int y, int w, int h) {
        super(styleSystem, x, y, w, h);
    }

    public ScrollContainer setScrollable(boolean enabled) { this.scrollEnabled = enabled; return this; }
    public ScrollContainer setScrollAxes(boolean vertical, boolean horizontal) { this.verticalScroll = vertical; this.horizontalScroll = horizontal; return this; }
    public ScrollContainer setScrollStep(int step) { this.scrollStep = Math.max(1, step); return this; }
    public ScrollContainer setShowScrollbars(boolean show) { this.showScrollbars = show; return this; }
    public ScrollContainer setScrollbarStyle(int thickness, int padding) { this.scrollbarThickness = Math.max(4, thickness); this.scrollbarPadding = Math.max(0, padding); return this; }

    private int baseViewportWidth() { return Math.max(0, calculatedWidth - getPaddingLeft() - getPaddingRight()); }
    private int baseViewportHeight() { return Math.max(0, calculatedHeight - getPaddingTop() - getPaddingBottom()); }

    public int getViewportX() { return calculatedX + getPaddingLeft(); }
    public int getViewportY() { return calculatedY + getPaddingTop(); }
    public int getViewportWidth() { return Math.max(0, baseViewportWidth() - reserveRight); }
    public int getViewportHeight() { return Math.max(0, baseViewportHeight() - reserveBottom); }

    public int getContentWidth() { return contentWidth; }
    public int getContentHeight() { return contentHeight; }

    public int getScrollX() { return scrollX; }
    public int getScrollY() { return scrollY; }
    public void setScrollX(int v) { scrollX = v; clampScroll(); }
    public void setScrollY(int v) { scrollY = v; clampScroll(); }

    public boolean isVerticalScrollEnabled() { return verticalScroll; }
    public boolean isHorizontalScrollEnabled() { return horizontalScroll; }

    @Override
    protected void updateInteractionBounds() {
        int ix = calculatedX + getPaddingLeft();
        int iy = calculatedY + getPaddingTop();
        int iw = Math.max(0, calculatedWidth  - getPaddingLeft() - getPaddingRight());
        int ih = Math.max(0, calculatedHeight - getPaddingTop()  - getPaddingBottom());

        int minX = ix, minY = iy, maxX = ix + iw, maxY = iy + ih;

        if (parent != null) {
            InteractionBounds pb = parent.getInteractionBounds();
            if (pb != null && pb.width > 0 && pb.height > 0) {
                minX = Math.max(minX, pb.minX);
                minY = Math.max(minY, pb.minY);
                maxX = Math.min(maxX, pb.maxX);
                maxY = Math.min(maxY, pb.maxY);
            }
        }

        this.interactionBounds = new InteractionBounds(
                minX, minY,
                Math.max(0, maxX - minX),
                Math.max(0, maxY - minY)
        );
    }

    @Override
    public int getChildInteractionOffsetX(UIElement child) {
        if (child instanceof ScrollbarItem) return 0;
        return -scrollX;
    }

    @Override
    public int getChildInteractionOffsetY(UIElement child) {
        if (child instanceof ScrollbarItem) return 0;
        return -scrollY;
    }

    protected void computeContentSize() {
        final int originX = getViewportX();
        final int originY = getViewportY();

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        boolean any = false;

        for (UIElement child : getChildren()) {
            if (child == null || !child.isVisible() || child instanceof ScrollbarItem) continue;

            child.updateConstraints();

            final int rx = child.getCalculatedX() - originX;
            final int ry = child.getCalculatedY() - originY;
            final int rw = child.getCalculatedWidth();
            final int rh = child.getCalculatedHeight();

            minX = Math.min(minX, rx);
            minY = Math.min(minY, ry);
            maxX = Math.max(maxX, rx + rw);
            maxY = Math.max(maxY, ry + rh);

            any = true;
        }

        if (!any) {
            contentWidth = 0;
            contentHeight = 0;
            return;
        }

        int width = maxX - Math.min(0, minX);
        int height = maxY - Math.min(0, minY);

        contentWidth = Math.clamp(width, 0, Integer.MAX_VALUE);
        contentHeight = Math.clamp(height, 0, Integer.MAX_VALUE);
    }

    protected void clampScroll() {
        int maxX = Math.max(0, contentWidth - getViewportWidth());
        int maxY = Math.max(0, contentHeight - getViewportHeight());
        if (!horizontalScroll) scrollX = 0;
        if (!verticalScroll) scrollY = 0;
        if (scrollX < 0) scrollX = 0;
        if (scrollY < 0) scrollY = 0;
        if (scrollX > maxX) scrollX = maxX;
        if (scrollY > maxY) scrollY = maxY;
    }

    private int gutterV() { return scrollbarThickness + 2 * scrollbarPadding; }
    private int gutterH() { return scrollbarThickness + 2 * scrollbarPadding; }

    private boolean updateReservesOnce() {
        int oldRight = reserveRight;
        int oldBottom = reserveBottom;

        int bw = baseViewportWidth();
        int bh = baseViewportHeight();

        boolean needV = verticalScroll && contentHeight > bh;
        boolean needH = horizontalScroll && contentWidth > bw;

        int newRight = needV ? gutterV() : 0;
        int newBottom = needH ? gutterH() : 0;

        boolean needV2 = verticalScroll && contentHeight > (bh - newBottom);
        boolean needH2 = horizontalScroll && contentWidth > (bw - newRight);

        newRight = needV2 ? gutterV() : 0;
        newBottom = needH2 ? gutterH() : 0;

        reserveRight = newRight;
        reserveBottom = newBottom;

        return oldRight != reserveRight || oldBottom != reserveBottom;
    }

    private void ensureScrollbars() {
        if (!showScrollbars) {
            if (vbar != null) { removeChild(vbar); vbar = null; }
            if (hbar != null) { removeChild(hbar); hbar = null; }
            return;
        }

        boolean needV = verticalScroll && contentHeight > getViewportHeight();
        boolean needH = horizontalScroll && contentWidth > getViewportWidth();

        int baseX = calculatedX + getPaddingLeft();
        int baseY = calculatedY + getPaddingTop();
        int baseW = baseViewportWidth();
        int baseH = baseViewportHeight();

        ContainerScrollbarModel model = new ContainerScrollbarModel(this);

        if (needV) {
            if (vbar == null) {
                vbar = new ScrollbarItem(styleSystem, 0, 0, 1, 1, model, ScrollbarItem.Orientation.VERTICAL)
                        .setThickness(scrollbarThickness).setPadding(scrollbarPadding)
                        .setZIndex(ZIndex.Layer.OVERLAY);
                addChild(vbar);
            }
            int gx = baseX + baseW - gutterV();
            int gw = gutterV();
            int gh = baseH - reserveBottom;
            vbar.setX(gx);
            vbar.setY(baseY);
            vbar.setWidth(gw);
            vbar.setHeight(Math.max(0, gh));
        } else if (vbar != null) {
            removeChild(vbar);
            vbar = null;
        }

        if (needH) {
            if (hbar == null) {
                hbar = new ScrollbarItem(styleSystem, 0, 0, 1, 1, model, ScrollbarItem.Orientation.HORIZONTAL)
                        .setThickness(scrollbarThickness).setPadding(scrollbarPadding)
                        .setZIndex(ZIndex.Layer.OVERLAY);
                addChild(hbar);
            }
            int gy = baseY + baseH - gutterH();
            int gw = baseW - reserveRight;
            int gh = gutterH();
            hbar.setX(baseX);
            hbar.setY(gy);
            hbar.setWidth(Math.max(0, gw));
            hbar.setHeight(gh);
        } else if (hbar != null) {
            removeChild(hbar);
            hbar = null;
        }
    }

    @Override
    public void render(DrawContext context) {
        if (!visible) {
            markNotRenderedDeep();
            return;
        }

        renderBackground(context);
        markAsRendered();
        prepareLayoutAndScroll();

        final int vpMinX = getViewportX();
        final int vpMinY = getViewportY();
        final int vpMaxX = vpMinX + getViewportWidth();
        final int vpMaxY = vpMinY + getViewportHeight();

        final int inMinX = calculatedX + getPaddingLeft();
        final int inMinY = calculatedY + getPaddingTop();
        final int inMaxX = inMinX + baseViewportWidth();
        final int inMaxY = inMinY + baseViewportHeight();

        withScissor(context, vpMinX, vpMinY, vpMaxX, vpMaxY, () -> {
            context.getMatrices().push();
            context.getMatrices().translate(-scrollX, -scrollY, 0.0f);
            renderChildren(context, false);
            context.getMatrices().pop();
        });

        withScissor(context, inMinX, inMinY, inMaxX, inMaxY,
                () -> renderChildren(context, true));
    }


    private void markNotRenderedDeep() {
        markAsNotRendered();
        for (UIElement c : getChildren()) {
            if (c != null) c.markAsNotRendered();
        }
    }

    private void prepareLayoutAndScroll() {
        updateConstraints();
        performLayoutCycle();

        if (updateReservesOnce()) {
            performLayoutCycle(); // recompute after reserves changed
        }

        ensureScrollbars();
        updateInteractionBounds();
    }

    private void performLayoutCycle() {
        layoutChildren();
        computeContentSize();
        clampScroll();
    }

    private boolean isScrollbar(UIElement el) { return (el instanceof ScrollbarItem); }


    private boolean dispatchToScrollbarsClick(double x, double y, int button) {
        for (UIElement child : getChildren()) {
            if (!child.isVisible() || !child.isEnabled() || !child.isRendered()) continue;
            if (!isScrollbar(child)) continue;
            if (!child.canInteract(x, y)) continue;
            if (child.onMouseClick(x, y, button)) return true;
        }
        return false;
    }

    private boolean dispatchToScrollbarsRelease(double x, double y, int button) {
        for (UIElement child : getChildren()) {
            if (!child.isVisible() || !child.isEnabled() || !child.isRendered()) continue;
            if (!isScrollbar(child)) continue;
            if (child.onMouseRelease(x, y, button)) return true;
        }
        return false;
    }

    private boolean dispatchToScrollbarsDrag(double x, double y, int button, double dx, double dy) {
        for (UIElement child : getChildren()) {
            if (!child.isVisible() || !child.isEnabled() || !child.isRendered()) continue;
            if (!isScrollbar(child)) continue;
            if (child.onMouseDrag(x, y, button, dx, dy)) return true;
        }
        return false;
    }

    private boolean dispatchToScrollbarsScroll(double x, double y, double delta) {
        for (UIElement child : getChildren()) {
            if (!child.isVisible() || !child.isEnabled() || !child.isRendered()) continue;
            if (!isScrollbar(child)) continue;
            if (child.onMouseScroll(x, y, delta)) return true;
        }
        return false;
    }


    private void renderChildren(DrawContext context, boolean includeScrollbars) {
        for (UIElement child : getChildren()) {
            if (!isRenderable(child)) continue;
            boolean isScrollbar = child instanceof ScrollbarItem;
            if (includeScrollbars != isScrollbar) continue;
            child.renderElement(context);
        }
    }

    private boolean isRenderable(UIElement child) {
        return child != null && child.isVisible();
    }

    private void withScissor(DrawContext ctx, int x1, int y1, int x2, int y2, Runnable draw) {
        ctx.enableScissor(x1, y1, x2, y2);
        try {
            draw.run();
        } finally {
            ctx.disableScissor();
        }
    }

    @Override
    protected void layoutChildren() {
        /* not necessary */
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!isInInteractionZone(mouseX, mouseY)) return false;

        // z-order top → bottom
        java.util.List<UIElement> sorted = LayoutEngine.sortByRenderOrder(getChildren());
        for (int i = sorted.size() - 1; i >= 0; i--) {
            UIElement child = sorted.get(i);
            if (child == null || !child.isVisible() || !child.isEnabled() || !child.isRendered()) continue;

            // Test & dispatch en COORDONNÉES MONDE
            if (!child.canInteract(mouseX, mouseY)) continue;
            if (child.onMouseClick(mouseX, mouseY, button)) {
                captured = child;
                capturedButton = button;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        try {
            if (captured != null && button == capturedButton) {
                return captured.onMouseRelease(mouseX, mouseY, button);
            }
        } finally {
            if (button == capturedButton) { captured = null; capturedButton = -1; }
        }

        // sinon, on offre quand même la release aux scrollbars si besoin
        java.util.List<UIElement> sorted = LayoutEngine.sortByRenderOrder(getChildren());
        for (int i = sorted.size() - 1; i >= 0; i--) {
            UIElement child = sorted.get(i);
            if (!isScrollbar(child)) continue;
            if (child.onMouseRelease(mouseX, mouseY, button)) return true;
        }
        return false;
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double dx, double dy) {
        if (captured != null && button == capturedButton) {
            return captured.onMouseDrag(mouseX, mouseY, button, dx, dy);
        }
        return false;
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        for (UIElement child : getChildren()) {
            if (!child.isVisible() || !child.isRendered()) continue;
            boolean inside = child.isInInteractionZone(mouseX, mouseY);
            if (inside && !child.isHovered()) child.onMouseEnter();
            else if (!inside && child.isHovered()) child.onMouseLeave();
            if (inside) child.onMouseMove(mouseX, mouseY);
        }
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        if (!scrollEnabled) return false;
        if (!isInInteractionZone(mouseX, mouseY)) return false;
        boolean used = false;
        if (verticalScroll)   { scrollY -= (int) Math.round(scrollDelta * scrollStep); used = true; }
        if (horizontalScroll) { scrollX -= (int) Math.round(scrollDelta * scrollStep); used = true; }
        clampScroll();
        return used;
    }

    @Override
    public ScrollContainer removeChild(UIElement element) {
        super.removeChild(element);
        if (element == captured) {
            captured = null;
            capturedButton = -1;
        }
        return this;
    }

    @Override
    public ScrollContainer clearChildren() {
        super.clearChildren();
        captured = null;
        capturedButton = -1;
        return this;
    }

    public ScrollContainer clearContentChildren() {
        for (UIElement c : getChildren()) {
            if (c instanceof ScrollbarItem) continue;
            removeChild(c);
        }
        captured = null;
        capturedButton = -1;
        markConstraintsDirty();
        return this;
    }

    @Override
    public String toString() {
        return String.format("ScrollContainer{scroll=[%d,%d], content=[%d,%d], viewport=[%d,%d,%d,%d], scrollbars=[v=%b, h=%b], children=%d, visibleChildren=%d}",
                scrollX,
                scrollY,
                contentWidth,
                contentHeight,
                getViewportX(),
                getViewportY(),
                getViewportWidth(),
                getViewportHeight(),
                verticalScroll,
                horizontalScroll,
                getChildren().size(),
                getChildren().stream().filter(UIElement::isVisible).count()
        );
    }
}
