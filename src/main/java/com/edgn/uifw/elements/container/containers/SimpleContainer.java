package com.edgn.uifw.elements.container.containers;

import com.edgn.uifw.elements.container.BaseContainer;
import com.edgn.uifw.utils.Render2D;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.elements.UIElement;
import net.minecraft.client.gui.DrawContext;

@SuppressWarnings({"unused"})
public class SimpleContainer extends BaseContainer {

    private int categoryColor = 0;
    private int overrideBorderColor = -1;

    public SimpleContainer(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
        addClass(StyleKey.BG_SURFACE);
    }

    public SimpleContainer setCategoryColor(int color) {
        this.categoryColor = color;
        return this;
    }

    public SimpleContainer setOverrideBorderColor(int color) {
        this.overrideBorderColor = color;
        return this;
    }

    public SimpleContainer add(UIElement element) {
        return (SimpleContainer) addChild(element);
    }

    @Override
    protected void layoutChildren() {

    }

    @Override
    protected void renderBackground(DrawContext context) {
        int bgColor = getBgColor();
        if (bgColor != 0) {
            int borderRadius = getBorderRadius();

            if (getShadow() != null) {
                Render2D.drawShadow(context, x, y, width, height, 2, 2, getShadow().color);
            }

            int borderColor = overrideBorderColor != -1 ? overrideBorderColor :
                    (focused ? styleSystem.getColor(StyleKey.PRIMARY_LIGHT) : 0);

            if (borderColor != 0) {
                Render2D.drawPanel(context, x, y, width, height, borderRadius, bgColor, borderColor, 2);
            } else {
                Render2D.drawRoundedRect(context, x, y, width, height, borderRadius, bgColor);
            }

            if (categoryColor != 0) {
                context.fill(x, y, x + 6, y + height, categoryColor);
            }
        }
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled || !visible) return false;

        for (UIElement child : children) {
            if (child.isVisible() && child.isEnabled() && child.onMouseClick(mouseX, mouseY, button)) {
                return true;
            }
        }

        return super.onMouseClick(mouseX, mouseY, button);
    }

    @Override
    public SimpleContainer addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }

    @Override
    public SimpleContainer removeClass(StyleKey key) {
        super.removeClass(key);
        return this;
    }

    @Override
    public SimpleContainer setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        return this;
    }

}