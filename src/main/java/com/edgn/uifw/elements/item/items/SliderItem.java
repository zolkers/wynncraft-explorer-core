package com.edgn.uifw.elements.item.items;

import com.edgn.uifw.elements.item.BaseItem;
import com.edgn.uifw.utils.Render2D;
import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.css.rules.Shadow;
import com.edgn.uifw.components.TextComponent;
import com.edgn.uifw.layout.LayoutConstraints;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Consumer;

@SuppressWarnings({"unused", "unchecked"})
public class SliderItem extends BaseItem {
    private double value;
    private double minValue;
    private double maxValue; 
    private double step;
    private boolean isDragging = false;
    
    private Consumer<Double> onValueChanged;
    
    private TextComponent labelComponent;
    private TextComponent valueComponent;
    private String label = "";
    private String suffix = "";
    private boolean showValue = true;
    private boolean showLabel = true;
    
    private int trackHeight = 6;
    private int thumbSize = 16;

    public SliderItem(UIStyleSystem styleSystem, int x, int y, int width, int height, 
                     double minValue, double maxValue, double initialValue) {
        super(styleSystem, x, y, width, height);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = Math.max(minValue, Math.min(maxValue, initialValue));
        this.step = 0.1;
        
        addClass(StyleKey.BG_SURFACE, StyleKey.ROUNDED_SM, StyleKey.FOCUS_RING);
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        if (textRenderer != null && !label.isEmpty()) {
            labelComponent = new TextComponent(label, textRenderer)
                .color(getTextColor())
                .align(TextComponent.TextAlign.LEFT);
        }
        
        updateValueComponent();
    }
    
    private void updateValueComponent() {
        if (textRenderer != null && showValue) {
            String valueText = formatValue();
            valueComponent = new TextComponent(valueText, textRenderer)
                .color(styleSystem.getColor(StyleKey.PRIMARY))
                .align(TextComponent.TextAlign.RIGHT)
                .bold();
        }
    }
    
    private String formatValue() {
        if (step >= 1) {
            return String.format("%.0f%s", value, suffix);
        } else if (step >= 0.1) {
            return String.format("%.1f%s", value, suffix);
        } else {
            return String.format("%.2f%s", value, suffix);
        }
    }
    
    public SliderItem setRange(double min, double max) {
        this.minValue = min;
        this.maxValue = max;
        this.value = Math.max(min, Math.min(max, this.value));
        updateValueComponent();
        return this;
    }
    
    public SliderItem setValue(double value) {
        double oldValue = this.value;
        this.value = Math.max(minValue, Math.min(maxValue, value));
        if (oldValue != this.value) {
            updateValueComponent();
            if (onValueChanged != null) {
                onValueChanged.accept(this.value);
            }
        }
        return this;
    }
    
    public SliderItem setStep(double step) {
        this.step = Math.max(0.001, step);
        return this;
    }
    
    public SliderItem setLabel(String label) {
        this.label = label;
        if (textRenderer != null && !label.isEmpty()) {
            labelComponent = new TextComponent(label, textRenderer)
                .color(getTextColor())
                .align(TextComponent.TextAlign.LEFT);
        }
        return this;
    }
    
    public SliderItem setSuffix(String suffix) {
        this.suffix = suffix;
        updateValueComponent();
        return this;
    }
    
    public SliderItem onValueChanged(Consumer<Double> callback) {
        this.onValueChanged = callback;
        return this;
    }
    
    public SliderItem showValue(boolean show) {
        this.showValue = show;
        updateValueComponent();
        return this;
    }
    
    public SliderItem showLabel(boolean show) {
        this.showLabel = show;
        return this;
    }
    
    @Override
    public void render(DrawContext context) {
        if (!visible) return;
        
        int bgColor = getStateColor();
        int borderRadius = getBorderRadius();
        Shadow shadow = getShadow();
        
        if (shadow != null) {
            Render2D.drawShadow(context, x, y, width, height, 2, 2, shadow.color);
        }
        if (bgColor != 0) {
            Render2D.drawRoundedRect(context, x, y, width, height, borderRadius, bgColor);
        }
        
        int textAreaHeight = (showLabel || showValue) ? 20 : 0;
        int sliderStartY = y + getPaddingTop() + textAreaHeight + (textAreaHeight > 0 ? 5 : 0);
        
        if (showLabel && labelComponent != null) {
            int labelX = x + getPaddingLeft();
            int labelY = y + getPaddingTop();
            labelComponent.render(context, labelX, labelY, width / 2, textAreaHeight);
        }
        
        if (showValue && valueComponent != null) {
            int valueX = x + width / 2;
            int valueY = y + getPaddingTop();
            valueComponent.render(context, valueX, valueY, width / 2 - getPaddingRight(), textAreaHeight);
        }
        
        int sliderHeight = height - getPaddingTop() - getPaddingBottom() - textAreaHeight - (textAreaHeight > 0 ? 5 : 0);
        int trackY = sliderStartY + (sliderHeight - trackHeight) / 2;
        int trackX = x + getPaddingLeft();
        int trackWidth = width - getPaddingLeft() - getPaddingRight();
        
        int trackColor = state == ItemState.DISABLED ?
            fadeColor(styleSystem.getColor(StyleKey.SECONDARY), 0.5f) :
            styleSystem.getColor(StyleKey.SECONDARY);
        
        Render2D.drawRoundedRect(context, trackX, trackY, trackWidth, trackHeight, trackHeight / 2, trackColor);
        
        double progress = (value - minValue) / (maxValue - minValue);
        int fillWidth = (int) (trackWidth * progress);
        if (fillWidth > 0) {
            int fillColor = state == ItemState.DISABLED ?
                fadeColor(styleSystem.getColor(StyleKey.PRIMARY), 0.5f) :
                styleSystem.getColor(StyleKey.PRIMARY);
            
            Render2D.drawRoundedRect(context, trackX, trackY, fillWidth, trackHeight, trackHeight / 2, fillColor);
        }
        
        int thumbX = trackX + fillWidth - thumbSize / 2;
        int thumbY = sliderStartY + (sliderHeight - thumbSize) / 2;
        
        int thumbColor = getThumbColor();
        
        if (state != ItemState.DISABLED) {
            Render2D.drawShadow(context, thumbX + 1, thumbY + 1, thumbSize, thumbSize, 0, 0, 0x40000000);
        }
        
        Render2D.drawRoundedRect(context, thumbX, thumbY, thumbSize, thumbSize, thumbSize / 2, thumbColor);
        
        if (focused && hasClass(StyleKey.FOCUS_RING)) {
            int focusColor = styleSystem.getColor(StyleKey.PRIMARY_LIGHT);
            Render2D.drawRoundedRectBorder(context, thumbX - 2, thumbY - 2, 
                thumbSize + 4, thumbSize + 4, (thumbSize + 4) / 2, focusColor, 2);
        }
    }
    
    private int getThumbColor() {
        return switch (state) {
            case HOVERED -> brightenColor(0xFFFFFFFF, 10);
            case PRESSED, FOCUSED -> styleSystem.getColor(StyleKey.PRIMARY_LIGHT);
            case DISABLED -> fadeColor(0xFFDDDDDD, 0.5f);
            default -> 0xFFFFFFFF;
        };
    }
    
    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled || !visible || button != 0) return false;
        
        if (contains(mouseX, mouseY)) {
            setState(ItemState.PRESSED);
            isDragging = true;
            updateValueFromMouse(mouseX);
            return super.onMouseClick(mouseX, mouseY, button);
        }
        return false;
    }
    
    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        if (button == 0 && isDragging) {
            isDragging = false;
            setState(contains(mouseX, mouseY) ? ItemState.HOVERED : ItemState.NORMAL);
            return true;
        }
        return super.onMouseRelease(mouseX, mouseY, button);
    }
    
    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        super.onMouseMove(mouseX, mouseY);
        if (isDragging) {
            updateValueFromMouse(mouseX);
        }
    }
    
    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        if (contains(mouseX, mouseY) && enabled) {
            double change = step * Math.signum(scrollDelta);
            setValue(value + change);
            return true;
        }
        return super.onMouseScroll(mouseX, mouseY, scrollDelta);
    }
    
    private void updateValueFromMouse(double mouseX) {
        int trackX = x + getPaddingLeft();
        int trackWidth = width - getPaddingLeft() - getPaddingRight();
        
        double relativeX = mouseX - trackX;
        double progress = Math.max(0, Math.min(1, relativeX / trackWidth));
        double newValue = minValue + progress * (maxValue - minValue);
        
        newValue = Math.round(newValue / step) * step;
        
        setValue(newValue);
    }
    
    @Override
    protected void onStateChanged(ItemState newState) {
        super.onStateChanged(newState);
        updateValueComponent();
    }
    
    public double getValue() { return value; }
    public double getMinValue() { return minValue; }
    public double getMaxValue() { return maxValue; }
    public double getStep() { return step; }
    public String getLabel() { return label; }
    public String getSuffix() { return suffix; }
    public boolean isDragging() { return isDragging; }
    
    @Override
    public SliderItem addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }
    
    @Override
    public SliderItem removeClass(StyleKey key) {
        super.removeClass(key);
        return this;
    }
    
    @Override
    public SliderItem onClick(Runnable handler) {
        super.onClick(handler);
        return this;
    }
    
    @Override
    public SliderItem onMouseEnter(Runnable handler) {
        super.onMouseEnter(handler);
        return this;
    }
    
    @Override
    public SliderItem onMouseLeave(Runnable handler) {
        super.onMouseLeave(handler);
        return this;
    }
    
    @Override
    public SliderItem onFocusGained(Runnable handler) {
        super.onFocusGained(handler);
        return this;
    }
    
    @Override
    public SliderItem onFocusLost(Runnable handler) {
        super.onFocusLost(handler);
        return this;
    }
    
    @Override
    public SliderItem setVisible(boolean visible) {
        super.setVisible(visible);
        return this;
    }
    
    @Override
    public SliderItem setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        return this;
    }
    
    @Override
    public SliderItem setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        return this;
    }
    
    @Override
    public SliderItem setConstraints(LayoutConstraints constraints) {
        super.setConstraints(constraints);
        return this;
    }
    
    @Override
    public SliderItem setTextRenderer(TextRenderer textRenderer) {
        super.setTextRenderer(textRenderer);
        initializeComponents();
        return this;
    }
}