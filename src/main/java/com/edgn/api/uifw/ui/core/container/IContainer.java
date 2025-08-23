package com.edgn.api.uifw.ui.core.container;

import com.edgn.api.uifw.ui.core.UIElement;

public interface IContainer {
    <T extends IContainer> T addChild(UIElement element);
    <T extends IContainer> T removeChild(UIElement element);
    <T extends IContainer> T clearChildren();
}
