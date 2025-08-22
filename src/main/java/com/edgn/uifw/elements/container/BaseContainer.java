package com.edgn.uifw.elements.container;

import com.edgn.uifw.utils.Render2D;
import com.edgn.uifw.layout.LayoutConstraints;
import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.css.rules.Shadow;
import com.edgn.uifw.elements.UIElement;
import com.edgn.uifw.layout.LayoutEngine;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "unchecked"})
public abstract class BaseContainer extends UIElement {
    protected final List<UIElement> children = new ArrayList<>();

    public BaseContainer(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
    }

    public BaseContainer addChild(UIElement element) {
        if (element != null && !children.contains(element)) {
            element.setParent(this);
            children.add(element);
            markConstraintsDirty();
        }
        return this;
    }

    public BaseContainer removeChild(UIElement element) {
        if (children.remove(element)) {
            if (element != null) {
                element.setParent(null);
            }
            markConstraintsDirty();
        }
        return this;
    }

    public BaseContainer clearChildren() {
        for (UIElement child : children) {
            if (child != null) {
                child.setParent(null);
            }
        }
        children.clear();
        markConstraintsDirty();
        return this;
    }

    @Override
    public void markConstraintsDirty() {
        if (constraintsDirty) return;
        super.markConstraintsDirty();
        for (UIElement child : children) {
            child.markConstraintsDirty();
        }
    }

    @Override
    public void updateConstraints() {
        if (!constraintsDirty) return;

        calculateEffectiveBounds();
        updateInteractionBounds();
        constraintsDirty = false;

        for (UIElement child : children) {
            child.updateConstraints();
        }
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!canInteract(mouseX, mouseY)) return false;

        List<UIElement> sortedChildren = LayoutEngine.sortByInteractionPriority(children, mouseX, mouseY);

        for (UIElement child : sortedChildren) {
            if (child.onMouseClick(mouseX, mouseY, button)) {
                return true;
            }
        }

        return super.onMouseClick(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        List<UIElement> sortedChildren = LayoutEngine.sortByInteractionPriority(children, mouseX, mouseY);

        for (UIElement child : sortedChildren) {
            if (child.onMouseRelease(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.onMouseRelease(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        List<UIElement> sortedChildren = LayoutEngine.sortByInteractionPriority(children, mouseX, mouseY);

        for (UIElement child : sortedChildren) {
            if (child.onMouseScroll(mouseX, mouseY, scrollDelta)) {
                return true;
            }
        }
        return super.onMouseScroll(mouseX, mouseY, scrollDelta);
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Pour le drag, on garde l'ordre Z-Index classique
        List<UIElement> sortedChildren = LayoutEngine.sortByZIndex(children);

        for (int i = sortedChildren.size() - 1; i >= 0; i--) {
            UIElement child = sortedChildren.get(i);
            if (child.isVisible() && child.isEnabled() &&
                    child.onMouseDrag(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return super.onMouseDrag(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        super.onMouseMove(mouseX, mouseY);
        for (UIElement child : children) {
            if (child.isVisible()) {
                child.onMouseMove(mouseX, mouseY);
            }
        }
    }

    public List<UIElement> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public BaseContainer onClick(Runnable handler) {
        super.onClick(handler);
        return this;
    }

    @Override
    public BaseContainer onMouseEnter(Runnable handler) {
        super.onMouseEnter(handler);
        return this;
    }

    @Override
    public BaseContainer onMouseLeave(Runnable handler) {
        super.onMouseLeave(handler);
        return this;
    }

    @Override
    public BaseContainer onFocusGained(Runnable handler) {
        super.onFocusGained(handler);
        return this;
    }

    @Override
    public BaseContainer onFocusLost(Runnable handler) {
        super.onFocusLost(handler);
        return this;
    }

    @Override
    public BaseContainer setVisible(boolean visible) {
        super.setVisible(visible);
        return this;
    }

    @Override
    public BaseContainer setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        return this;
    }

    @Override
    public BaseContainer setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        return this;
    }

    @Override
    public BaseContainer setConstraints(LayoutConstraints constraints) {
        super.setConstraints(constraints);
        return this;
    }

    @Override
    public BaseContainer setTextRenderer(TextRenderer textRenderer) {
        super.setTextRenderer(textRenderer);
        return this;
    }

    @Override
    public void render(DrawContext context) {
        if (!visible) return;

        updateConstraints();
        renderBackground(context);

        LayoutEngine.LayoutBox content = getContentArea();

        InteractionBounds bounds = getInteractionBounds();
        if (bounds.isValid()) {
            context.enableScissor(bounds.minX, bounds.minY, bounds.maxX, bounds.maxY);
        }

        try {
            layoutChildren();

            List<UIElement> sortedChildren = LayoutEngine.sortByZIndex(children);

            for (UIElement child : sortedChildren) {
                if (child != null && child.isVisible()) {
                    LayoutEngine.applyElementStyles(child);
                    child.render(context);
                }
            }
        } finally {
            if (bounds.isValid()) {
                context.disableScissor();
            }
        }
    }

    protected void renderBackground(DrawContext context) {
        int bgColor = getBgColor();
        if (bgColor != 0) {
            int borderRadius = getBorderRadius();
            Shadow shadow = getShadow();

            if (shadow != null) {
                Render2D.drawShadow(context, getCalculatedX(), getCalculatedY(),
                        getCalculatedWidth(), getCalculatedHeight(), 2, 2, shadow.color);
            }

            Render2D.drawRoundedRect(context, getCalculatedX(), getCalculatedY(),
                    getCalculatedWidth(), getCalculatedHeight(), borderRadius, bgColor);
        }
    }

    protected LayoutEngine.LayoutBox getContentArea() {
        return LayoutEngine.calculateContentBox(this);
    }

    protected abstract void layoutChildren();
}
