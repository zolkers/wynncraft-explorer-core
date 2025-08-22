package com.edgn.uifw.elements.widget;

import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.css.rules.Shadow;
import com.edgn.uifw.elements.UIElement;
import com.edgn.uifw.layout.LayoutEngine;
import com.edgn.uifw.utils.Render2D;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused"})
public abstract class BaseWidget extends UIElement {

    protected final List<UIElement> internalElements = new ArrayList<>();
    protected boolean initialized = false;

    public BaseWidget(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
    }

    
    @Override
    public void markConstraintsDirty() {
        super.markConstraintsDirty();
        for (UIElement element : internalElements) {
            element.markConstraintsDirty();
        }
    }


    @Override
    public void updateConstraints() {
        if (!constraintsDirty) return;

        calculateEffectiveBounds();
        updateInteractionBounds();
        constraintsDirty = false;

        for (UIElement element : internalElements) {
            element.updateConstraints();
        }
    }

    protected abstract void initializeWidget();

    public abstract void updateContent();

    public void applyTheme(boolean isDarkMode) {}

    protected void addElement(UIElement element) {
        if (element != null) {
            element.setParent(this);
            internalElements.add(element);
            markConstraintsDirty(); 
        }
    }

    protected void removeElement(UIElement element) {
        if (internalElements.remove(element)) {
            if (element != null) {
                element.setParent(null);
            }
            markConstraintsDirty(); 
        }
    }

    protected void clearElements() {
        for (UIElement element : internalElements) {
            if (element != null) {
                element.setParent(null);
            }
        }
        internalElements.clear();
        markConstraintsDirty(); 
    }

    protected void layoutElements() {}

    @Override
    public void render(DrawContext context) {
        if (!visible) return;

        updateConstraints(); 

        if (!initialized) {
            initializeWidget();
            initialized = true;
        }

        layoutElements();
        renderBackground(context);
        renderElements(context);
        renderDecorations(context);
    }

    protected void renderBackground(DrawContext context) {
        int bgColor = getBgColor();
        if (bgColor != 0) {
            int borderRadius = getBorderRadius();
            Shadow shadow = getShadow();

            
            int renderX = getCalculatedX();
            int renderY = getCalculatedY();
            int renderWidth = getCalculatedWidth();
            int renderHeight = getCalculatedHeight();

            if (shadow != null) {
                Render2D.drawShadow(context, renderX, renderY, renderWidth, renderHeight, 2, 2, shadow.color);
            }

            Render2D.drawRoundedRect(context, renderX, renderY, renderWidth, renderHeight, borderRadius, bgColor);
        }
    }

    protected void renderElements(DrawContext context) {
        
        InteractionBounds bounds = getInteractionBounds();
        if (bounds.isValid()) {
            context.enableScissor(bounds.minX, bounds.minY, bounds.maxX, bounds.maxY);
        }

        try {
            
            List<UIElement> sortedElements = LayoutEngine.sortByZIndex(internalElements);

            for (UIElement element : sortedElements) {
                if (element != null && element.isVisible()) {
                    element.updateConstraints(); 
                    element.render(context);
                }
            }
        } finally {
            if (bounds.isValid()) {
                context.disableScissor();
            }
        }
    }

    protected void renderDecorations(DrawContext context) {
        if (focused && hasFocusRing()) {
            int focusColor = styleSystem.getColor(StyleKey.PRIMARY_LIGHT);
            int borderRadius = getBorderRadius();

            
            int renderX = getCalculatedX();
            int renderY = getCalculatedY();
            int renderWidth = getCalculatedWidth();
            int renderHeight = getCalculatedHeight();

            Render2D.drawRoundedRectBorder(context, renderX - 2, renderY - 2,
                    renderWidth + 4, renderHeight + 4, borderRadius + 2, focusColor, 2);
        }
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!canInteract(mouseX, mouseY)) return false; 

        
        List<UIElement> sortedElements = LayoutEngine.sortByInteractionPriority(internalElements, mouseX, mouseY);

        for (UIElement element : sortedElements) {
            if (element.onMouseClick(mouseX, mouseY, button)) {
                return true;
            }
        }

        return super.onMouseClick(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        List<UIElement> sortedElements = LayoutEngine.sortByInteractionPriority(internalElements, mouseX, mouseY);

        for (UIElement element : sortedElements) {
            if (element.onMouseRelease(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.onMouseRelease(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        
        List<UIElement> sortedElements = LayoutEngine.sortByZIndex(internalElements);

        for (int i = sortedElements.size() - 1; i >= 0; i--) {
            UIElement element = sortedElements.get(i);
            if (element.isVisible() && element.isEnabled() &&
                    element.onMouseDrag(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return super.onMouseDrag(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        super.onMouseMove(mouseX, mouseY);
        for (UIElement element : internalElements) {
            if (element.isVisible()) {
                element.onMouseMove(mouseX, mouseY);
            }
        }
    }

    protected void clearAndRebuild() {
        clearElements();
        initialized = false;
        markConstraintsDirty(); 
    }

    
    @Override
    public void setX(int x) {
        super.setX(x);
        layoutElements(); 
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        layoutElements();
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        layoutElements();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        layoutElements();
    }

    @Override
    public BaseWidget addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }

    @Override
    public BaseWidget removeClass(StyleKey key) {
        super.removeClass(key);
        return this;
    }

    @Override
    public BaseWidget setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        return this;
    }

    @Override
    public BaseWidget onClick(Runnable handler) {
        super.onClick(handler);
        return this;
    }

    @Override
    public BaseWidget onMouseEnter(Runnable handler) {
        super.onMouseEnter(handler);
        return this;
    }

    @Override
    public BaseWidget onMouseLeave(Runnable handler) {
        super.onMouseLeave(handler);
        return this;
    }

    public List<UIElement> getInternalElements() {
        return new ArrayList<>(internalElements);
    }
}