package com.edgn.core.minecraft.ui.screens.terminal;

import com.edgn.uifw.elements.item.BaseItem;
import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.components.TextComponent;
import net.minecraft.client.gui.DrawContext;

public class TerminalLineItem extends BaseItem {
    private final TextComponent textComponent;
    private final String line;
    
    private static final int DEFAULT_COLOR = 0xFF00FF00;
    private static final int ERROR_COLOR = 0xFFFF4444;
    private static final int WARNING_COLOR = 0xFFFFAA00;
    private static final int SUCCESS_COLOR = 0xFF44FF44;
    private static final int COMMAND_COLOR = 0xFF88AAFF;
    private static final int SYSTEM_COLOR = 0xFF888888;

    public TerminalLineItem(UIStyleSystem styleSystem, int x, int y, int width, int height, String line) {
        super(styleSystem, x, y, width, height);
        this.line = line;
        this.textComponent = new TextComponent(line, textRenderer)
                .color(getLineColor(line))
                .align(TextComponent.TextAlign.LEFT);
    }

    @Override
    public void render(DrawContext context) {
        if (!visible || line == null || line.isEmpty()) return;

        int contentX = x + getPaddingLeft();
        int contentY = y + getPaddingTop();
        int contentWidth = width - getPaddingLeft() - getPaddingRight();
        int contentHeight = height - getPaddingTop() - getPaddingBottom();

        textComponent.render(context, contentX, contentY, contentWidth, contentHeight);
    }

    private int getLineColor(String line) {
        if (line == null || line.isEmpty()) {
            return DEFAULT_COLOR;
        }

        if (line.toLowerCase().contains("error") || line.startsWith("ERROR:")) {
            return ERROR_COLOR;
        }
        
        if (line.toLowerCase().contains("warning") || line.startsWith("WARNING:")) {
            return WARNING_COLOR;
        }
        
        if (line.toLowerCase().contains("success") || line.startsWith("SUCCESS:")) {
            return SUCCESS_COLOR;
        }
        
        if (line.contains("@minecraft:") && line.contains("$")) {
            return COMMAND_COLOR;
        }
        
        if (line.startsWith("┌") || line.startsWith("└") || line.startsWith("├") || line.startsWith("│")) {
            return SYSTEM_COLOR;
        }
        
        if (line.startsWith("INFO:") || line.startsWith("DEBUG:")) {
            return SYSTEM_COLOR;
        }

        return DEFAULT_COLOR;
    }

    public String getLine() {
        return line;
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        return false;
    }
}