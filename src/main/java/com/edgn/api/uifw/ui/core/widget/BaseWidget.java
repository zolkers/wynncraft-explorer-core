package com.edgn.api.uifw.ui.core.widget;

import com.edgn.api.uifw.ui.core.container.BaseContainer;
import com.edgn.api.uifw.ui.css.UIStyleSystem;

public abstract class BaseWidget extends BaseContainer {
    protected BaseWidget(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
    }

    @Override
    protected void layoutChildren() {/* not a container, should stay empty*/}
}
