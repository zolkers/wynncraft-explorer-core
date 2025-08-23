package com.edgn.core.minecraft.ui.screens.modules.settings.components;


import com.edgn.api.uifw.ui.utils.DrawingUtils;
import com.edgn.core.minecraft.ui.screens.BaseScreen;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class ColorPickerScreen extends BaseScreen {
    private final ISettingsScreen parentScreen;
    private final Consumer<Integer> onColorSelect;
    private final int initialColor;

    private int pickerX, pickerY, pickerWidth, pickerHeight;
    private int colorAreaX, colorAreaY, colorAreaSize;
    private int hueSliderX, hueSliderY, sliderWidth, sliderHeight;
    private int alphaSliderX, alphaSliderY;

    private TextFieldWidget hexField, rField, gField, bField;

    private float hue, saturation, brightness, alpha;
    private boolean isDraggingColor = false;
    private boolean isDraggingHue = false;
    private boolean isDraggingAlpha = false;
    private boolean selfUpdate = false;

    private static final Pattern HEX_PATTERN = Pattern.compile("^#?([0-9a-fA-F]{6}|[0-9a-fA-F]{8})$");
    private static final Pattern INT_PATTERN = Pattern.compile("^[0-9]{1,3}$");

    public ColorPickerScreen(ISettingsScreen parent, int initialColor, Consumer<Integer> onColorSelect) {
        super(Text.literal("Color Picker"));
        this.parentScreen = parent;
        this.onColorSelect = onColorSelect;
        this.initialColor = initialColor;
        argbToHsba(initialColor);
    }

    @Override
    protected void init() {
        super.init();

        pickerWidth = 300;
        pickerHeight = 250;
        pickerX = (this.width - pickerWidth) / 2;
        pickerY = (this.height - pickerHeight) / 2;

        colorAreaSize = 150;
        sliderWidth = 20;
        sliderHeight = colorAreaSize;

        colorAreaX = pickerX + 15;
        colorAreaY = pickerY + 30;
        hueSliderX = colorAreaX + colorAreaSize + 10;
        hueSliderY = colorAreaY;
        alphaSliderX = hueSliderX + sliderWidth + 10;
        alphaSliderY = colorAreaY;

        int fieldY = pickerY + pickerHeight - 55;
        int fieldWidth = 40;
        int fieldSpacing = 10;
        int currentX = pickerX + 15;

        rField = createIntField(currentX, fieldY, fieldWidth, String.valueOf((initialColor >> 16) & 0xFF));
        currentX += fieldWidth + fieldSpacing;
        gField = createIntField(currentX, fieldY, fieldWidth, String.valueOf((initialColor >> 8) & 0xFF));
        currentX += fieldWidth + fieldSpacing;
        bField = createIntField(currentX, fieldY, fieldWidth, String.valueOf(initialColor & 0xFF));

        hexField = new TextFieldWidget(textRenderer, pickerX + 175, fieldY, 110, 20, Text.literal("Hex"));
        hexField.setMaxLength(9);
        hexField.setText(String.format("#%08X", initialColor));
        hexField.setChangedListener(this::onHexChanged);

        this.addDrawableChild(rField);
        this.addDrawableChild(gField);
        this.addDrawableChild(bField);
        this.addDrawableChild(hexField);

        int buttonY = pickerY + pickerHeight - 30;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Confirm"), button -> {
            onColorSelect.accept(getCurrentColor());
            this.close();
        }).dimensions(pickerX + pickerWidth - 170, buttonY, 80, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> this.close())
                .dimensions(pickerX + pickerWidth - 85, buttonY, 80, 20).build());
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x90000000);
    }

    @Override
    protected void renderHeader(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    protected void renderContent(DrawContext context, int mouseX, int mouseY, float delta) {
        int bgColor = parentScreen.getBgPrimary();
        int accentColor = parentScreen.getAccentColor();

        DrawingUtils.drawPanelWithShadow(context, pickerX, pickerY, pickerWidth, pickerHeight, 12,
                bgColor, accentColor, 2, 0x60000000);

        context.drawText(textRenderer, this.title, pickerX + 10, pickerY + 10, parentScreen.getTextPrimary(), false);

        drawColorArea(context);
        drawHueSlider(context);
        drawAlphaSlider(context);
        drawPreviews(context);
        drawTextFieldLabels(context);
    }

    @Override
    protected void renderFooter(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    protected void renderOverridElements(DrawContext context, int mouseX, int mouseY, float delta) {}

    private TextFieldWidget createIntField(int x, int y, int width, String initialValue) {
        TextFieldWidget field = new TextFieldWidget(textRenderer, x, y, width, 20, Text.empty());
        field.setMaxLength(3);
        field.setText(initialValue);
        field.setChangedListener(str -> onRgbChanged());
        return field;
    }

    private void drawColorArea(DrawContext context) {
        DrawingUtils.drawRoundedRect(context, colorAreaX - 2, colorAreaY - 2, colorAreaSize + 4, colorAreaSize + 4, 8, 0xFF333333);

        int fullHueColor = Color.HSBtoRGB(hue, 1f, 1f);
        context.fillGradient(colorAreaX, colorAreaY, colorAreaX + colorAreaSize, colorAreaY + colorAreaSize, 0xFFFFFFFF, fullHueColor);
        context.fillGradient(colorAreaX, colorAreaY, colorAreaX + colorAreaSize, colorAreaY + colorAreaSize, 0x00000000, 0xFF000000);

        int indicatorX = colorAreaX + (int)(saturation * colorAreaSize);
        int indicatorY = colorAreaY + (int)((1 - brightness) * colorAreaSize);
        DrawingUtils.drawRoundedRect(context, indicatorX - 4, indicatorY - 4, 8, 8, 4, 0xFFFFFFFF);
        DrawingUtils.drawRoundedRect(context, indicatorX - 2, indicatorY - 2, 4, 4, 2, 0xFF000000);
    }

    private void drawHueSlider(DrawContext context) {
        DrawingUtils.drawRoundedRect(context, hueSliderX - 2, hueSliderY - 2, sliderWidth + 4, sliderHeight + 4, 6, 0xFF333333);

        for (int i = 0; i < sliderHeight; i++) {
            float h = (float) i / sliderHeight;
            context.fill(hueSliderX, hueSliderY + i, hueSliderX + sliderWidth, hueSliderY + i + 1, Color.HSBtoRGB(h, 1f, 1f));
        }

        int indicatorY = hueSliderY + (int)(hue * sliderHeight);
        DrawingUtils.drawRoundedRect(context, hueSliderX - 3, indicatorY - 2, sliderWidth + 6, 4, 2, 0xFFFFFFFF);
        DrawingUtils.drawBorder(context, hueSliderX - 3, indicatorY - 2, sliderWidth + 6, 4, 0xFF000000, 1);
    }

    private void drawAlphaSlider(DrawContext context) {
        DrawingUtils.drawRoundedRect(context, alphaSliderX - 2, alphaSliderY - 2, sliderWidth + 4, sliderHeight + 4, 6, 0xFF333333);

        drawCheckerboard(context, alphaSliderX, alphaSliderY, sliderWidth, sliderHeight);
        int colorNoAlpha = getCurrentColor() & 0x00FFFFFF;
        context.fillGradient(alphaSliderX, alphaSliderY, alphaSliderX + sliderWidth, alphaSliderY + sliderHeight, colorNoAlpha, 0xFF000000 | colorNoAlpha);

        int indicatorY = alphaSliderY + (int)(alpha * sliderHeight);
        DrawingUtils.drawRoundedRect(context, alphaSliderX - 3, indicatorY - 2, sliderWidth + 6, 4, 2, 0xFFFFFFFF);
        DrawingUtils.drawBorder(context, alphaSliderX - 3, indicatorY - 2, sliderWidth + 6, 4, 0xFF000000, 1);
    }

    private void drawPreviews(DrawContext context) {
        int previewX = alphaSliderX + sliderWidth + 15;

        context.drawText(textRenderer, "New", previewX, colorAreaY, parentScreen.getTextMuted(), false);

        DrawingUtils.drawRoundedRect(context, previewX - 2, colorAreaY + 8, 42, 42, 8, 0xFF333333);
        drawCheckerboard(context, previewX, colorAreaY + 10, 40, 40);
        DrawingUtils.drawRoundedRect(context, previewX, colorAreaY + 10, 40, 40, 6, getCurrentColor());

        context.drawText(textRenderer, "Current", previewX, colorAreaY + 59, parentScreen.getTextMuted(), false);

        DrawingUtils.drawRoundedRect(context, previewX - 2, colorAreaY + 68, 42, 42, 8, 0xFF333333);
        drawCheckerboard(context, previewX, colorAreaY + 70, 40, 40);
        DrawingUtils.drawRoundedRect(context, previewX, colorAreaY + 70, 40, 40, 6, initialColor);
    }

    private void drawTextFieldLabels(DrawContext context) {
        int labelY = rField.getY() - 12;
        context.drawText(textRenderer, "R", rField.getX() + rField.getWidth() / 2 - 4, labelY, parentScreen.getTextMuted(), false);
        context.drawText(textRenderer, "G", gField.getX() + gField.getWidth() / 2 - 4, labelY, parentScreen.getTextMuted(), false);
        context.drawText(textRenderer, "B", bField.getX() + bField.getWidth() / 2 - 4, labelY, parentScreen.getTextMuted(), false);
        context.drawText(textRenderer, "Hex", hexField.getX(), labelY, parentScreen.getTextMuted(), false);
    }

    private void drawCheckerboard(DrawContext context, int x, int y, int width, int height) {
        for (int i = 0; i < width / 4; i++) {
            for (int j = 0; j < height / 4; j++) {
                boolean isDark = (i + j) % 2 == 0;
                context.fill(x + i * 4, y + j * 4, x + i * 4 + 4, y + j * 4 + 4, isDark ? 0xFF808080 : 0xFFC0C0C0);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (DrawingUtils.isPointInRect(mouseX, mouseY, colorAreaX, colorAreaY, colorAreaSize, colorAreaSize)) isDraggingColor = true;
            else if (DrawingUtils.isPointInRect(mouseX, mouseY, hueSliderX, hueSliderY, sliderWidth, sliderHeight)) isDraggingHue = true;
            else if (DrawingUtils.isPointInRect(mouseX, mouseY, alphaSliderX, alphaSliderY, sliderWidth, sliderHeight)) isDraggingAlpha = true;
        }
        updateAllDrags(mouseX, mouseY);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0) updateAllDrags(mouseX, mouseY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDraggingColor = isDraggingHue = isDraggingAlpha = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateAllDrags(double mouseX, double mouseY) {
        if (isDraggingColor) {
            this.saturation = (float) Math.min(1, Math.max(0, (mouseX - colorAreaX) / colorAreaSize));
            this.brightness = (float) Math.min(1, Math.max(0, 1 - (mouseY - colorAreaY) / colorAreaSize));
            updateTextFields();
        }
        if (isDraggingHue) {
            this.hue = (float) Math.min(1, Math.max(0, (mouseY - hueSliderY) / sliderHeight));
            updateTextFields();
        }
        if (isDraggingAlpha) {
            this.alpha = (float) Math.min(1, Math.max(0, (mouseY - alphaSliderY) / sliderHeight));
            updateTextFields();
        }
    }

    private void argbToHsba(int argb) {
        Color color = new Color(argb, true);
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        this.hue = hsb[0]; this.saturation = hsb[1]; this.brightness = hsb[2];
        this.alpha = color.getAlpha() / 255.0f;
    }

    private int hsbaToArgb() {
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        return ((int)(alpha * 255.0f) << 24) | (rgb & 0x00FFFFFF);
    }

    private int getCurrentColor() { return hsbaToArgb(); }

    private void updateTextFields() {
        selfUpdate = true;
        int color = getCurrentColor();
        rField.setText(String.valueOf((color >> 16) & 0xFF));
        gField.setText(String.valueOf((color >> 8) & 0xFF));
        bField.setText(String.valueOf(color & 0xFF));
        hexField.setText(String.format("#%08X", color));
        selfUpdate = false;
    }

    private void onHexChanged(String text) {
        if (selfUpdate) return;
        if (HEX_PATTERN.matcher(text).matches()) {
            try {
                long val = Long.parseLong(text.replace("#", ""), 16);
                int color = text.length() > 7 ? (int)val : (int)(0xFF000000L | val);
                argbToHsba(color);
                updateTextFieldsFromColor();
            } catch (NumberFormatException ignored) {}
        }
    }

    private void onRgbChanged() {
        if (selfUpdate) return;
        try {
            int r = parseRgb(rField.getText());
            int g = parseRgb(gField.getText());
            int b = parseRgb(bField.getText());
            if (r != -1 && g != -1 && b != -1) {
                int colorInt = ((int)(alpha * 255) << 24) | (r << 16) | (g << 8) | b;
                argbToHsba(colorInt);
                selfUpdate = true;
                hexField.setText(String.format("#%08X", colorInt));
                selfUpdate = false;
            }
        } catch (NumberFormatException ignored) {}
    }

    private void updateTextFieldsFromColor() {
        selfUpdate = true;
        rField.setText(String.valueOf((getCurrentColor() >> 16) & 0xFF));
        gField.setText(String.valueOf((getCurrentColor() >> 8) & 0xFF));
        bField.setText(String.valueOf(getCurrentColor() & 0xFF));
        selfUpdate = false;
    }

    private int parseRgb(String s) {
        if (!INT_PATTERN.matcher(s).matches()) return -1;
        int val = Integer.parseInt(s);
        return (val >= 0 && val <= 255) ? val : -1;
    }

    @Override
    public void close() {
        assert this.client != null;
        // Retourner à l'écran parent (qui peut être un AIModuleSettingsScreen ou ModuleSettingsScreen)
        if (parentScreen instanceof net.minecraft.client.gui.screen.Screen) {
            this.client.setScreen((net.minecraft.client.gui.screen.Screen) parentScreen);
        } else {
            super.close();
        }
    }
}
