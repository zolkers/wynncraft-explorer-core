package com.edgn.api.uifw.ui.core;

import com.edgn.api.uifw.ui.core.renderer.FontRenderer;
import com.edgn.api.uifw.ui.css.StyleKey;
import com.edgn.api.uifw.ui.layout.LayoutConstraints;
import com.edgn.api.uifw.ui.layout.ZIndex;

public interface IElement {
    <T extends IElement> T addClass(StyleKey... keys);
    <T extends IElement> T removeClass(StyleKey key);
    <T extends IElement> T setZIndex(ZIndex zIndex);
    <T extends IElement> T setZIndex(ZIndex.Layer layer);
    <T extends IElement> T setZIndex(ZIndex.Layer layer, int priority);
    <T extends IElement> T setZIndex(int intZIndex);
    <T extends IElement> T onClick(Runnable handler);
    <T extends IElement> T onMouseEnter(Runnable handler);
    <T extends IElement> T onMouseLeave(Runnable handler);
    <T extends IElement> T onFocusGained(Runnable handler);
    <T extends IElement> T onFocusLost(Runnable handler);
    <T extends IElement> T setConstraints(LayoutConstraints constraints);
    <T extends IElement> T setVisible(boolean visible);
    <T extends IElement> T setEnabled(boolean enabled);
    <T extends IElement> T setFontRenderer(FontRenderer fontRenderer);

    default int getChildInteractionOffsetX(UIElement child) {
        return 0;
    }
    default int getChildInteractionOffsetY(UIElement child) {
        return 0;
    }
}
