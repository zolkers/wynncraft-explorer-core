package com.edgn.uifw.elements.widget.widgets;

import com.edgn.uifw.components.TextComponent;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.elements.item.items.ButtonItem;
import com.edgn.uifw.elements.widget.BaseWidget;
import com.edgn.uifw.utils.Render2D;
import net.minecraft.client.gui.DrawContext;

@SuppressWarnings({"unused"})
public class ModuleCardWidget extends BaseWidget {

    private String title = "";
    private String description = "";
    private String version = "";

    private Runnable primaryAction;
    private String primaryButtonText = "";
    private StyleKey primaryButtonStyle = StyleKey.PRIMARY;

    private Runnable secondaryAction;
    private String secondaryButtonText = "";
    private StyleKey secondaryButtonStyle = StyleKey.SECONDARY;

    private static final int PADDING = 12;
    private static final int BUTTON_HEIGHT = 28;
    private static final int BUTTON_GAP = 8;
    private static final int TEXT_LINE_HEIGHT = 18;
    private static final int VERSION_HEIGHT = 16;

    private TextComponent titleComponent;
    private TextComponent descriptionComponent;
    private TextComponent versionComponent;

    public ModuleCardWidget(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
        addClass(StyleKey.BG_SURFACE, StyleKey.ROUNDED_LG, StyleKey.SHADOW_MD);
        updateTextComponents();
    }

    private void updateTextComponents() {
        int contentWidth = getCalculatedWidth() - (PADDING * 2);

        titleComponent = new TextComponent(title, textRenderer)
                .color(0xFFFFFFFF)
                .align(TextComponent.TextAlign.LEFT)
                .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                .truncate()
                .setSafetyMargin(4);

        versionComponent = new TextComponent(version, textRenderer)
                .color(styleSystem.getColor(StyleKey.MUTED))
                .align(TextComponent.TextAlign.LEFT)
                .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                .truncate()
                .setSafetyMargin(4);

        int maxDescriptionLines = calculateMaxDescriptionLines();
        descriptionComponent = new TextComponent(description, textRenderer)
                .color(styleSystem.getColor(StyleKey.MUTED))
                .align(TextComponent.TextAlign.LEFT)
                .verticalAlign(TextComponent.VerticalAlign.TOP)
                .wrap(maxDescriptionLines)
                .setSafetyMargin(4);
    }

    private int calculateMaxDescriptionLines() {
        int usedHeight = PADDING;

        if (!title.isEmpty()) {
            usedHeight += TEXT_LINE_HEIGHT + 4;
        }

        if (!version.isEmpty()) {
            usedHeight += VERSION_HEIGHT + 6;
        }

        usedHeight += PADDING;
        if (primaryAction != null || secondaryAction != null) {
            usedHeight += BUTTON_HEIGHT + 8;
        }

        int availableHeight = getCalculatedHeight() - usedHeight;
        int lineHeight = textRenderer.fontHeight + 2;

        return Math.max(1, availableHeight / lineHeight);
    }


    @Override
    protected void initializeWidget() {
        clearElements();
        createButtons();
    }

    @Override
    protected void layoutElements() {
        updateConstraints();
        updateTextComponents();
        clearElements();
        createButtons();
    }

    private void createButtons() {
        if (primaryAction == null && secondaryAction == null) return;

        int buttonY = getCalculatedY() + getCalculatedHeight() - PADDING - BUTTON_HEIGHT;
        int contentWidth = getCalculatedWidth() - (PADDING * 2);

        if (primaryAction != null && secondaryAction != null) {
            int buttonWidth = (contentWidth - BUTTON_GAP) / 2;

            ButtonItem primaryButton = new ButtonItem(styleSystem,
                    getCalculatedX() + PADDING, buttonY, buttonWidth, BUTTON_HEIGHT)
                    .withText(primaryButtonText)
                    .onClick(primaryAction);
            applyButtonStyle(primaryButton, primaryButtonStyle);
            addElement(primaryButton);

            ButtonItem secondaryButton = new ButtonItem(styleSystem,
                    getCalculatedX() + PADDING + buttonWidth + BUTTON_GAP, buttonY, buttonWidth, BUTTON_HEIGHT)
                    .withText(secondaryButtonText)
                    .onClick(secondaryAction);
            applyButtonStyle(secondaryButton, secondaryButtonStyle);
            addElement(secondaryButton);

        } else if (primaryAction != null) {
            int buttonWidth = Math.min(contentWidth, 150);
            int buttonX = getCalculatedX() + (getCalculatedWidth() - buttonWidth) / 2;

            ButtonItem primaryButton = new ButtonItem(styleSystem, buttonX, buttonY, buttonWidth, BUTTON_HEIGHT)
                    .withText(primaryButtonText)
                    .onClick(primaryAction);
            applyButtonStyle(primaryButton, primaryButtonStyle);
            addElement(primaryButton);
        }
    }

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
        renderText(context);
        renderElements(context);
    }


    private void renderText(DrawContext context) {
        int contentX = getCalculatedX() + PADDING;
        int contentY = getCalculatedY() + PADDING;
        int contentWidth = getCalculatedWidth() - (PADDING * 2);
        int currentY = contentY;

        if (!title.isEmpty() && titleComponent != null) {
            context.enableScissor(contentX, currentY, contentX + contentWidth, currentY + TEXT_LINE_HEIGHT);
            try {
                titleComponent.render(context, contentX, currentY, contentWidth, TEXT_LINE_HEIGHT);
            } finally {
                context.disableScissor();
            }
            currentY += TEXT_LINE_HEIGHT + 4;
        }

        if (!version.isEmpty() && versionComponent != null) {
            context.enableScissor(contentX, currentY, contentX + contentWidth, currentY + VERSION_HEIGHT);
            try {
                versionComponent.render(context, contentX, currentY, contentWidth, VERSION_HEIGHT);
            } finally {
                context.disableScissor();
            }
            currentY += VERSION_HEIGHT + 6;
        }

        if (!description.isEmpty() && descriptionComponent != null) {
            int descriptionEndY = getCalculatedY() + getCalculatedHeight() - PADDING;
            if (primaryAction != null || secondaryAction != null) {
                descriptionEndY -= BUTTON_HEIGHT + 8;
            }

            int availableHeight = descriptionEndY - currentY;

            if (availableHeight > 0) {
                context.enableScissor(contentX, currentY, contentX + contentWidth, descriptionEndY);
                try {
                    descriptionComponent.render(context, contentX, currentY, contentWidth, availableHeight);
                } finally {
                    context.disableScissor();
                }
            }
        }
    }


    protected void renderBackground(DrawContext context) {
        int bgColor = getBgColor();
        if (bgColor != 0) {
            int borderRadius = getBorderRadius();

            int renderX = getCalculatedX();
            int renderY = getCalculatedY();
            int renderWidth = getCalculatedWidth();
            int renderHeight = getCalculatedHeight();

            if (getShadow() != null) {
                Render2D.drawShadow(context, renderX, renderY, renderWidth, renderHeight, 4, 4, getShadow().color);
            }

            Render2D.drawRoundedRect(context, renderX, renderY, renderWidth, renderHeight, borderRadius, bgColor);
        }
    }

    @Override
    public void updateContent() {
        updateTextComponents();
        clearAndRebuild();
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        if (initialized) {
            updateTextComponents();
            layoutElements();
        }
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        if (initialized) {
            updateTextComponents();
            layoutElements();
        }
    }

    private void applyButtonStyle(ButtonItem button, StyleKey style) {
        switch (style) {
            case SUCCESS -> button.asSuccessButton();
            case DANGER -> button.asDangerButton();
            case WARNING -> button.asWarningButton();
            case INFO -> button.asInfoButton();
            case SECONDARY -> button.asSecondaryButton();
            default -> button.asPrimaryButton();
        }
    }

    public ModuleCardWidget withTitle(String title) {
        this.title = title != null ? title : "";
        updateTextComponents();
        return this;
    }

    public ModuleCardWidget withDescription(String description) {
        this.description = description != null ? description : "";
        updateTextComponents();
        return this;
    }

    public ModuleCardWidget withVersion(String version) {
        this.version = version != null ? version : "";
        updateTextComponents();
        return this;
    }

    public ModuleCardWidget withPrimaryAction(String buttonText, Runnable action) {
        this.primaryButtonText = buttonText != null ? buttonText : "";
        this.primaryAction = action;
        this.primaryButtonStyle = StyleKey.PRIMARY;
        if (initialized) clearAndRebuild();
        return this;
    }

    public ModuleCardWidget withPrimaryAction(String buttonText, Runnable action, StyleKey style) {
        this.primaryButtonText = buttonText != null ? buttonText : "";
        this.primaryAction = action;
        this.primaryButtonStyle = style != null ? style : StyleKey.PRIMARY;
        if (initialized) clearAndRebuild();
        return this;
    }

    public ModuleCardWidget withSecondaryAction(String buttonText, Runnable action) {
        this.secondaryButtonText = buttonText != null ? buttonText : "";
        this.secondaryAction = action;
        this.secondaryButtonStyle = StyleKey.SECONDARY;
        if (initialized) clearAndRebuild();
        return this;
    }

    public ModuleCardWidget withSecondaryAction(String buttonText, Runnable action, StyleKey style) {
        this.secondaryButtonText = buttonText != null ? buttonText : "";
        this.secondaryAction = action;
        this.secondaryButtonStyle = style != null ? style : StyleKey.SECONDARY;
        if (initialized) clearAndRebuild();
        return this;
    }

    public ModuleCardWidget setTitleColor(int color) {
        if (titleComponent != null) {
            titleComponent.color(color);
        }
        return this;
    }

    public ModuleCardWidget setDescriptionColor(int color) {
        if (descriptionComponent != null) {
            descriptionComponent.color(color);
        }
        return this;
    }

    public ModuleCardWidget setVersionColor(int color) {
        if (versionComponent != null) {
            versionComponent.color(color);
        }
        return this;
    }

    public ModuleCardWidget setTitleBold(boolean bold) {
        if (titleComponent != null && bold) {
            titleComponent.bold();
        }
        return this;
    }

    public ModuleCardWidget setTitleGlow(boolean glow) {
        if (titleComponent != null && glow) {
            titleComponent.glow();
        }
        return this;
    }

    public ModuleCardWidget setTitleAnimation(boolean animated) {
        if (titleComponent != null && animated) {
            titleComponent.pulse();
        }
        return this;
    }

    public ModuleCardWidget withStatus(String status) {
        return this;
    }

    public ModuleCardWidget withCategoryColor(int color) {
        return this;
    }

    public ModuleCardWidget withBorderColor(int color) {
        return this;
    }

    public ModuleCardWidget setDarkMode(boolean darkMode) {
        return this;
    }

    public ModuleCardWidget withPrimaryAction(Runnable action, String buttonText, StyleKey buttonStyle) {
        return withPrimaryAction(buttonText, action, buttonStyle);
    }

    public ModuleCardWidget withSecondaryAction(Runnable action, String buttonText) {
        return withSecondaryAction(buttonText, action);
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getVersion() { return version; }
    public TextComponent getTitleComponent() { return titleComponent; }
    public TextComponent getDescriptionComponent() { return descriptionComponent; }
    public TextComponent getVersionComponent() { return versionComponent; }

    @Override
    public ModuleCardWidget addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }

    @Override
    public ModuleCardWidget removeClass(StyleKey key) {
        super.removeClass(key);
        return this;
    }

    @Override
    public ModuleCardWidget setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        return this;
    }

    @Override
    public ModuleCardWidget onClick(Runnable handler) {
        super.onClick(handler);
        return this;
    }

    @Override
    public ModuleCardWidget onMouseEnter(Runnable handler) {
        super.onMouseEnter(handler);
        return this;
    }

    @Override
    public ModuleCardWidget onMouseLeave(Runnable handler) {
        super.onMouseLeave(handler);
        return this;
    }
}