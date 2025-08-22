package com.edgn.uifw.elements.item.items;

import com.edgn.uifw.elements.item.BaseItem;
import com.edgn.uifw.utils.Render2D;
import com.edgn.uifw.components.TextComponent;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.css.rules.Shadow;
import com.edgn.uifw.layout.LayoutConstraints;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.StringHelper;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "unchecked", "UnusedReturnValue"})
public class TextFieldItem extends BaseItem {
    private String text = "";
    private boolean isPassword = false;
    private boolean isMultiline = false;
    private boolean isReadonly = false;
    private boolean isRequired = false;
    private boolean hasError = false;
    private String errorMessage = "";

    private int cursorPosition = 0;
    private int selectionStart = 0;
    private int selectionEnd = 0;
    private long lastCursorBlink = System.currentTimeMillis();
    private boolean showCursor = true;
    private static final long CURSOR_BLINK_INTERVAL = 500;

    private int scrollOffset = 0;
    private int maxLength = Integer.MAX_VALUE;
    private Predicate<String> validator;
    private Consumer<String> onTextChanged;
    private Runnable onEnterPressed;

    private int cursorColor = 0xFFFFFFFF;
    private int selectionColor = 0x803366CC;

    private TextComponent textStyleTemplate;
    private TextComponent currentTextComponent;
    private TextComponent placeholderComponent;
    private TextComponent hintComponent;
    private TextComponent errorComponent;

    public TextFieldItem(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
        addClass(StyleKey.BG_SURFACE, StyleKey.ROUNDED_SM, StyleKey.P_2, StyleKey.FOCUS_RING);

        this.textStyleTemplate = new TextComponent("", textRenderer).color(0xFFFFFFFF);
        this.placeholderComponent = new TextComponent("", textRenderer).color(0xFF888888);
        this.hintComponent = new TextComponent("", textRenderer).color(0xFF666666);
        this.errorComponent = new TextComponent("", textRenderer).color(0xFFDC3545);

        updateCurrentTextComponent();
    }

    public TextFieldItem setTextStyle(TextComponent styleTemplate) {
        this.textStyleTemplate = styleTemplate;
        updateCurrentTextComponent();
        return this;
    }

    public TextFieldItem setPlaceholder(TextComponent placeholder) {
        this.placeholderComponent = placeholder;
        return this;
    }

    public TextFieldItem setHint(TextComponent hint) {
        this.hintComponent = hint;
        return this;
    }

    private void updateCurrentTextComponent() {
        String textToRender = isPassword ? "*".repeat(this.text.length()) : this.text;
        this.currentTextComponent = textStyleTemplate.cloneWithNewText(textToRender);
        this.currentTextComponent.verticalAlign(TextComponent.VerticalAlign.MIDDLE);
    }

    private void onTextChange(String newText) {
        this.text = newText;
        updateCurrentTextComponent();

        if (onTextChanged != null) {
            onTextChanged.accept(this.text);
        }
        validate();
        updateScrollOffset();
    }

    private void insertText(String insertText) {
        if (isReadonly || insertText == null || insertText.isEmpty()) return;
        if (hasSelection()) deleteSelection();

        int safeCursorPosition = Math.max(0, Math.min(cursorPosition, text.length()));

        String newText = text.substring(0, safeCursorPosition) + insertText + text.substring(safeCursorPosition);
        if (newText.length() <= maxLength) {
            cursorPosition = safeCursorPosition + insertText.length();
            onTextChange(newText);
        }
    }

    private void deleteSelection() {
        if (!hasSelection()) return;
        int start = Math.min(selectionStart, selectionEnd);
        int end = Math.max(selectionStart, selectionEnd);
        String newText = text.substring(0, start) + text.substring(end);

        cursorPosition = start;
        clearSelection();
        onTextChange(newText);
    }

    private void copySelection() {
        if (!hasSelection()) return;

        int start = Math.min(selectionStart, selectionEnd);
        int end = Math.max(selectionStart, selectionEnd);
        String selectedText = text.substring(start, end);

        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
        GLFW.glfwSetClipboardString(windowHandle, selectedText);
    }

    private void pasteFromClipboard() {
        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
        String clipboardText = GLFW.glfwGetClipboardString(windowHandle);

        if (clipboardText != null) {
            insertText(clipboardText);
        }
    }

    private void cutSelection() {
        if (isReadonly) return;
        copySelection();
        deleteSelection();
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (!focused || !enabled) return false;
        boolean ctrl = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
        boolean shift = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;

        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT:
                if (ctrl) moveCursorToWordBoundary(-1, shift);
                else moveCursor(-1, shift);
                return true;
            case GLFW.GLFW_KEY_RIGHT:
                if (ctrl) moveCursorToWordBoundary(1, shift);
                else moveCursor(1, shift);
                return true;
            case GLFW.GLFW_KEY_HOME:
                moveCursorToStart(shift);
                return true;
            case GLFW.GLFW_KEY_END:
                moveCursorToEnd(shift);
                return true;
            case GLFW.GLFW_KEY_BACKSPACE:
                if (!isReadonly) {
                    if (hasSelection()) {
                        deleteSelection();
                    } else if (cursorPosition > 0) {
                        String newText = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                        cursorPosition--;
                        onTextChange(newText);
                    }
                }
                return true;
            case GLFW.GLFW_KEY_DELETE:
                if (!isReadonly) {
                    if (hasSelection()) {
                        deleteSelection();
                    } else if (cursorPosition < text.length()) {
                        String newText = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
                        onTextChange(newText);
                    }
                }
                return true;
            case GLFW.GLFW_KEY_ENTER:
                if (isMultiline && !isReadonly) insertText("\n");
                else if (onEnterPressed != null) onEnterPressed.run();
                return true;
            case GLFW.GLFW_KEY_A:
                if (ctrl) {
                    selectAll();
                    return true;
                }
                break;
            case GLFW.GLFW_KEY_C:
                if (ctrl && hasSelection()) {
                    copySelection();
                    return true;
                }
                break;
            case GLFW.GLFW_KEY_V:
                if (ctrl && !isReadonly) {
                    pasteFromClipboard();
                    return true;
                }
                break;
            case GLFW.GLFW_KEY_X:
                if (ctrl && hasSelection() && !isReadonly) {
                    cutSelection();
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public void render(DrawContext context) {
        if (!visible) return;

        updateConstraints();
        updateCursorBlink();
        renderBackground(context);
        renderText(context);
        renderCursor(context);
        renderHintAndError(context);

        if (focused && hasClass(StyleKey.FOCUS_RING)) {
            int focusColor = hasError ? 0xFFDC3545 : styleSystem.getColor(StyleKey.PRIMARY_LIGHT);
            int borderRadius = getBorderRadius();

            Render2D.drawRoundedRectBorder(context,
                    getCalculatedX() - 2, getCalculatedY() - 2,
                    getCalculatedWidth() + 4, getCalculatedHeight() + 4,
                    borderRadius + 2, focusColor, 2);
        }
    }

    private void renderBackground(DrawContext context) {
        int bgColor = getStateColor();
        if (hasError) {
            bgColor = UIStyleSystem.applyOpacity(0xFFDC3545, 0.1f);
        }
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
        if (hasError) {
            Render2D.drawRoundedRectBorder(context, renderX, renderY, renderWidth, renderHeight, borderRadius, 0xFFDC3545, 1);
        }
    }

    private void renderText(DrawContext context) {
        int contentX = getCalculatedX() + getPaddingLeft();
        int contentY = getCalculatedY() + getPaddingTop();
        int contentWidth = getCalculatedWidth() - getPaddingLeft() - getPaddingRight();
        int contentHeight = getCalculatedHeight() - getPaddingTop() - getPaddingBottom();

        Render2D.enableClipping(context, contentX, contentY, contentWidth, contentHeight);

        if (text.isEmpty() && !placeholderComponent.getText().isEmpty() && !focused) {
            placeholderComponent.verticalAlign(TextComponent.VerticalAlign.MIDDLE);
            placeholderComponent.render(context, contentX, contentY, contentWidth, contentHeight);
        } else if (currentTextComponent != null) {
            if (hasSelection()) {
                renderSelectionBackground(context, contentX, contentY, contentHeight);
            }
            currentTextComponent.render(context, contentX - scrollOffset, contentY, contentWidth, contentHeight);
        }

        Render2D.disableClipping(context);
    }

    private void renderSelectionBackground(DrawContext context, int contentX, int contentY, int contentHeight) {
        int selStart = Math.min(selectionStart, selectionEnd);
        int selEnd = Math.max(selectionStart, selectionEnd);
        String beforeSelection = text.substring(0, selStart);
        String selectedText = text.substring(selStart, selEnd);
        int startX = contentX + textRenderer.getWidth(beforeSelection) - scrollOffset;
        int selectionWidth = textRenderer.getWidth(selectedText);
        context.fill(startX, contentY, startX + selectionWidth, contentY + contentHeight, selectionColor);
    }

    private void renderCursor(DrawContext context) {
        if (!focused || !showCursor || isReadonly) return;

        int contentX = getCalculatedX() + getPaddingLeft();
        int contentY = getCalculatedY() + getPaddingTop();
        int contentWidth = getCalculatedWidth() - getPaddingLeft() - getPaddingRight();
        int contentHeight = getCalculatedHeight() - getPaddingTop() - getPaddingBottom();

        int safeCursorPosition = Math.max(0, Math.min(cursorPosition, text.length()));
        String beforeCursor = text.substring(0, safeCursorPosition);
        int cursorX = contentX + textRenderer.getWidth(beforeCursor) - scrollOffset;

        if (cursorX >= contentX && cursorX < contentX + contentWidth) {
            context.fill(cursorX, contentY + 2, cursorX + 1, contentY + contentHeight - 2, cursorColor);
        }
    }

    private void renderHintAndError(DrawContext context) {
        int messageY = y + height + 4;
        if (hasError && !errorComponent.getText().isEmpty()) {
            errorComponent.render(context, x, messageY, width, textRenderer.fontHeight);
        } else if (!hintComponent.getText().isEmpty()) {
            hintComponent.render(context, x, messageY, width, textRenderer.fontHeight);
        }
    }

    public TextFieldItem setPassword(boolean isPassword) {
        this.isPassword = isPassword;
        updateCurrentTextComponent();
        return this;
    }

    public TextFieldItem setMultiline(boolean isMultiline) {
        this.isMultiline = isMultiline;
        return this;
    }

    public TextFieldItem setReadonly(boolean isReadonly) {
        this.isReadonly = isReadonly;
        return this;
    }

    public TextFieldItem setRequired(boolean isRequired) {
        this.isRequired = isRequired;
        return this;
    }

    public TextFieldItem setMaxLength(int maxLength) {
        this.maxLength = Math.max(0, maxLength);
        return this;
    }

    public TextFieldItem setValidator(Predicate<String> validator) {
        this.validator = validator;
        return this;
    }

    public TextFieldItem onTextChanged(Consumer<String> handler) {
        this.onTextChanged = handler;
        return this;
    }

    public TextFieldItem onEnterPressed(Runnable handler) {
        this.onEnterPressed = handler;
        return this;
    }

    public TextFieldItem setError(boolean hasError, String errorMessage) {
        this.hasError = hasError;
        this.errorMessage = errorMessage != null ? errorMessage : "";
        this.errorComponent = new TextComponent(this.errorMessage, textRenderer).color(0xFFDC3545);
        return this;
    }

    public String getText() {
        return text;
    }

    public boolean hasError() {
        return hasError;
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled || !visible) return false;

        if (!canInteract(mouseX, mouseY)) return false;

        if (button == 0) {
            styleSystem.getEventManager().setFocus(this);
            setState(ItemState.FOCUSED);

            int relativeX = (int) (mouseX - getCalculatedX() - getPaddingLeft() + scrollOffset);
            cursorPosition = getCursorPositionFromX(relativeX);
            clearSelection();
            resetCursorBlink();

            if (onClickHandler != null) {
                onClickHandler.run();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onCharTyped(char chr, int modifiers) {
        if (!focused || !enabled || isReadonly) return false;
        if (StringHelper.isValidChar(chr)) {
            insertText(String.valueOf(chr));
            return true;
        }
        return false;
    }

    @Override
    public void onFocusGained() {
        super.onFocusGained();
        setState(ItemState.FOCUSED);
        resetCursorBlink();
    }

    @Override
    public void onFocusLost() {
        super.onFocusLost();
        setState(hovered ? ItemState.HOVERED : ItemState.NORMAL);
        clearSelection();
        validate();
    }

    private void moveCursor(int direction, boolean extend) {
        int newPos = Math.max(0, Math.min(text.length(), cursorPosition + direction));
        if (extend) {
            if (!hasSelection()) {
                selectionStart = cursorPosition;
            }
            selectionEnd = newPos;
        } else {
            clearSelection();
        }
        cursorPosition = newPos;
        resetCursorBlink();
        updateScrollOffset();
    }

    private void moveCursorToWordBoundary(int direction, boolean extend) {
        int pos = cursorPosition;
        if (direction < 0) {
            while (pos > 0 && Character.isWhitespace(text.charAt(pos - 1))) pos--;
            while (pos > 0 && !Character.isWhitespace(text.charAt(pos - 1))) pos--;
        } else {
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) pos++;
            while (pos < text.length() && !Character.isWhitespace(text.charAt(pos))) pos++;
        }
        if (extend) {
            if (!hasSelection()) {
                selectionStart = cursorPosition;
            }
            selectionEnd = pos;
        } else {
            clearSelection();
        }
        cursorPosition = pos;
        resetCursorBlink();
        updateScrollOffset();
    }

    private void moveCursorToStart(boolean extend) {
        cursorPosition = 0;
        if (extend) {
            if (!hasSelection()) selectionStart = cursorPosition;
            selectionEnd = 0;
        } else {
            clearSelection();
        }
        resetCursorBlink();
        updateScrollOffset();
    }

    private void moveCursorToEnd(boolean extend) {
        cursorPosition = text.length();
        if (extend) {
            if (!hasSelection()) selectionStart = cursorPosition;
            selectionEnd = text.length();
        } else {
            clearSelection();
        }
        resetCursorBlink();
        updateScrollOffset();
    }

    private void selectAll() {
        selectionStart = 0;
        selectionEnd = text.length();
        cursorPosition = text.length();
        resetCursorBlink();
    }

    private void clearSelection() {
        selectionStart = selectionEnd = cursorPosition;
    }

    private boolean hasSelection() {
        return selectionStart != selectionEnd;
    }

    private int getCursorPositionFromX(int x) {
        if (text.isEmpty()) return 0;
        for (int i = 0; i <= text.length(); i++) {
            String substr = text.substring(0, i);
            int textWidth = textRenderer.getWidth(substr);
            if (textWidth > x) {
                return Math.max(0, i - 1);
            }
        }
        return text.length();
    }

    private void updateScrollOffset() {
        int contentWidth = width - getPaddingLeft() - getPaddingRight();

        int safeCursorPosition = Math.max(0, Math.min(cursorPosition, text.length()));

        String beforeCursor = safeCursorPosition > 0 ? text.substring(0, safeCursorPosition) : "";
        int cursorX = textRenderer.getWidth(beforeCursor);

        if (cursorX - scrollOffset < 0) {
            scrollOffset = cursorX;
        } else if (cursorX - scrollOffset > contentWidth - 10) {
            scrollOffset = cursorX - contentWidth + 10;
        }
        scrollOffset = Math.max(0, scrollOffset);
    }
    private void updateCursorBlink() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCursorBlink > CURSOR_BLINK_INTERVAL) {
            showCursor = !showCursor;
            lastCursorBlink = currentTime;
        }
    }

    private void resetCursorBlink() {
        showCursor = true;
        lastCursorBlink = System.currentTimeMillis();
    }

    private void validate() {
        if (validator != null) {
            boolean isValid = validator.test(text);
            if (!isValid && !hasError) {
                setError(true, "Invalid input");
            } else if (isValid && hasError && errorMessage.equals("Invalid input")) {
                setError(false, "");
            }
        }
        if (isRequired && text.trim().isEmpty() && !hasError) {
            setError(true, "This field is required");
        }
    }

    @Override
    public TextFieldItem addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }

    @Override
    public TextFieldItem removeClass(StyleKey key) {
        super.removeClass(key);
        return this;
    }

    @Override
    public TextFieldItem onClick(Runnable handler) {
        super.onClick(handler);
        return this;
    }

    @Override
    public TextFieldItem onMouseEnter(Runnable handler) {
        super.onMouseEnter(handler);
        return this;
    }

    @Override
    public TextFieldItem onMouseLeave(Runnable handler) {
        super.onMouseLeave(handler);
        return this;
    }

    @Override
    public TextFieldItem onFocusGained(Runnable handler) {
        super.onFocusGained(handler);
        return this;
    }

    @Override
    public TextFieldItem onFocusLost(Runnable handler) {
        super.onFocusLost(handler);
        return this;
    }

    @Override
    public TextFieldItem setVisible(boolean visible) {
        super.setVisible(visible);
        return this;
    }

    @Override
    public TextFieldItem setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        return this;
    }

    @Override
    public TextFieldItem setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        return this;
    }

    @Override
    public TextFieldItem setConstraints(LayoutConstraints constraints) {
        super.setConstraints(constraints);
        return this;
    }

    @Override
    public TextFieldItem setTextRenderer(TextRenderer textRenderer) {
        super.setTextRenderer(textRenderer);
        return this;
    }

    public TextFieldItem setText(String text) {
        this.text = text != null ? text : "";
        this.cursorPosition = Math.max(0, Math.min(this.cursorPosition, this.text.length()));
        clearSelection();
        updateCurrentTextComponent();
        updateScrollOffset();
        return this;
    }
}