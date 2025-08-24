package com.edgn.core.minecraft.ui.screens.modules.settings.components.complementary;

import com.edgn.api.uifw.ui.core.UIElement;
import com.edgn.api.uifw.ui.core.components.TextComponent;
import com.edgn.api.uifw.ui.core.container.BaseContainer;
import com.edgn.api.uifw.ui.core.container.containers.FlexContainer;
import com.edgn.api.uifw.ui.core.item.items.ButtonItem;
import com.edgn.api.uifw.ui.core.item.items.LabelItem;
import com.edgn.api.uifw.ui.core.item.items.TextFieldItem;
import com.edgn.api.uifw.ui.core.item.items.color.ColorSwatchItem;
import com.edgn.api.uifw.ui.core.item.items.color.GradientSliderItem;
import com.edgn.api.uifw.ui.core.item.items.color.SVPadItem;
import com.edgn.api.uifw.ui.css.StyleKey;
import com.edgn.api.uifw.ui.template.BaseTemplate;
import com.edgn.api.uifw.ui.template.TemplateSettings;
import com.edgn.api.uifw.ui.utils.ColorUtils;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class ColorPickerScreen extends BaseTemplate {

    private static final class Theme {
        static final int BG_MAIN = 0xFF0F1115;
        static final int SURFACE = 0xFF111113;
        static final int FOREGROUND = 0xFFE5E7EB;
        static final int INPUT = 0xFF18181B;
        static final int CARD = 0xFF111113;
        static final int SECONDARY = 0xFF27272A;
        static final int SECONDARY_FG = 0xFFE5E7EB;
        static final int PRIMARY = 0xFFA78BFA;
        static final int PRIMARY_FG = 0xFF0B0B0F;
        static final int BORDER = 0xFF2A2A2E;
    }

    private static final int PADDING = 16;
    private static final int INSET   = 12;
    private static final int GAP     = 12;
    private static final int PANEL_MIN_W = 620;
    private static final int PANEL_MAX_W = 960;
    private static final int ROW_H       = 36;
    private static final int ACTIONS_H   = 36;

    private final ISettingsScreen parent;
    private final int initialColor;
    private final Consumer<Integer> onPick;

    // HSVA
    private float hue, sat, val, alpha;

    // UI
    private FlexContainer root;
    private FlexContainer panel;
    private SVPadItem svPad;
    private GradientSliderItem hueSlider, alphaSlider;
    private ColorSwatchItem swatchOld, swatchNew;
    private TextFieldItem hexField, rField, gField, bField, aField;
    private FlexContainer actionsRow;

    private boolean selfUpdate = false;
    private static final Pattern HEX = Pattern.compile("^#?[0-9a-fA-F]{6}([0-9a-fA-F]{2})?$");

    public ColorPickerScreen(ISettingsScreen parent, int initialColor, Consumer<Integer> onColorSelect) {
        super(Text.literal("Color Picker"), (Screen) parent);
        this.parent = parent;
        this.initialColor = initialColor;
        this.onPick = onColorSelect;
        argbToHsva(initialColor);
    }

    @Override
    protected TemplateSettings templateSettings() {
        return new TemplateSettings().setHeader(false).setFooter(false);
    }

    @Override
    protected BaseContainer createHeader() {
        return null;
    }

    @Override
    protected BaseContainer createContent() {
        root = new FlexContainer(uiSystem, 0, 0, width, contentHeight)
                .setBackgroundColor(ColorUtils.setOpacity(Theme.BG_MAIN, 0.90f))
                .setRenderBackground(true);

        panel = new FlexContainer(uiSystem, 0, 0, 800, 480)
                .addClass(StyleKey.ROUNDED_LG, StyleKey.SHADOW_MD)
                .setBackgroundColor(Theme.CARD)
                .setRenderBackground(true);

        // titre soft
        LabelItem title = new LabelItem(uiSystem, 0, 0,
                new TextComponent("Color Picker").color(Theme.FOREGROUND));

        // gauche
        svPad = new SVPadItem(uiSystem, 0, 0, 280, 220)
                .setHue(hue).setSaturation(sat).setValue(val)
                .onChange((s, v) -> { sat = s; val = v; syncFromHsva(); });

        hueSlider = new GradientSliderItem(uiSystem, 0, 0, 280, 20)
                .withThumbSize(10)
                .withGradient(t -> Color.HSBtoRGB((float) t, 1f, 1f) | 0xFF000000)
                .withValue(hue / 360f)
                .onChange(t -> { hue = (float) (t * 360f); svPad.setHue(hue); syncFromHsva(); });

        alphaSlider = new GradientSliderItem(uiSystem, 0, 0, 280, 20)
                .withThumbSize(10)
                .withCompositeCheckerboard(true)
                .withGradient(t -> {
                    int rgb = currentColor() & 0x00FFFFFF;
                    int a = (int) Math.round(t * 255.0);
                    return (a << 24) | rgb;
                })
                .withValue(alpha)
                .onChange(t -> { alpha = (float) t; syncFromHsva(); });

        // droite
        swatchOld = new ColorSwatchItem(uiSystem, 0, 0, 120, 80).setColor(initialColor);
        swatchNew = new ColorSwatchItem(uiSystem, 0, 0, 120, 80).setColor(currentColor());

        hexField = tf("#FFFFFFFF", this::onHexChanged);
        rField = tf("0", s -> onRgbChanged());
        gField = tf("0", s -> onRgbChanged());
        bField = tf("0", s -> onRgbChanged());
        aField = tf("255", s -> onRgbChanged());

        // actions
        ButtonItem cancel = new ButtonItem(uiSystem, 0, 0, 120, ACTIONS_H,
                new TextComponent("Cancel").color(Theme.SECONDARY_FG))
                .backgroundColor(Theme.SECONDARY)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.HOVER_BRIGHTEN)
                .onClick(this::close);

        ButtonItem apply = new ButtonItem(uiSystem, 0, 0, 120, ACTIONS_H,
                new TextComponent("Apply").color(Theme.PRIMARY_FG))
                .backgroundColor(Theme.PRIMARY)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.HOVER_BRIGHTEN)
                .onClick(() -> {
                    if (onPick != null) onPick.accept(currentColor());
                    close();
                });

        actionsRow = new FlexContainer(uiSystem, 0, 0, 100, ACTIONS_H)
                .addClass(StyleKey.FLEX_ROW, StyleKey.JUSTIFY_END, StyleKey.GAP_2, StyleKey.ITEMS_CENTER);
        actionsRow.addChild(cancel).addChild(apply);

        root.addChild(panel);
        panel.addChild(title);      // 0
        panel.addChild(svPad);      // 1
        panel.addChild(hueSlider);  // 2
        panel.addChild(alphaSlider);// 3
        panel.addChild(swatchOld);  // 4
        panel.addChild(swatchNew);  // 5
        // labels + fields
        LabelItem hexLbl = new LabelItem(uiSystem, 0, 0,
                new TextComponent("HEX").color(Theme.FOREGROUND));
        LabelItem rgbLbl = new LabelItem(uiSystem, 0, 0,
                new TextComponent("RGBA").color(Theme.FOREGROUND));
        panel.addChild(hexLbl);     // 6
        panel.addChild(hexField);   // 7
        panel.addChild(rgbLbl);     // 8
        // R,G,B,A fields
        panel.addChild(rField);     // 9
        panel.addChild(gField);     // 10
        panel.addChild(bField);     // 11
        panel.addChild(aField);     // 12

        panel.addChild(actionsRow); // 13

        syncFromHsva();
        layoutPanel();
        return root;
    }

    @Override
    protected BaseContainer createFooter() {
        return null;
    }

    @Override
    protected void resizeEvent() {
        layoutPanel();
    }

    private void layoutPanel() {
        if (panel == null) return;

        int pw = Math.max(PANEL_MIN_W, Math.min(PANEL_MAX_W, width - 2 * PADDING));
        int ph = Math.max(320, contentHeight - 2 * PADDING);
        int px = (width - pw) / 2;
        int py = PADDING;

        panel.setX(px); panel.setY(py); panel.setWidth(pw); panel.setHeight(ph); panel.markConstraintsDirty();

        int innerX = px + INSET, innerY = py + INSET, innerW = pw - 2 * INSET;

        // titre
        UIElement title = panel.getChildren().get(0);
        title.setX(innerX); title.setY(innerY); title.setWidth(innerW); title.setHeight(20); title.markConstraintsDirty();

        int leftW = Math.min(320, (innerW * 5) / 10);
        int rightW = innerW - leftW - GAP;

        // gauche
        int gx = innerX, gy = innerY + 24, gW = leftW;
        svPad.setX(gx); svPad.setY(gy); svPad.setWidth(gW); svPad.setHeight(Math.min(260, (int)(gW * 0.8))); svPad.markConstraintsDirty();

        int sliderY = svPad.getCalculatedY() + svPad.getCalculatedHeight() + GAP;
        hueSlider.setX(gx); hueSlider.setY(sliderY); hueSlider.setWidth(gW); hueSlider.setHeight(20); hueSlider.markConstraintsDirty();

        int slider2Y = sliderY + 20 + GAP;
        alphaSlider.setX(gx); alphaSlider.setY(slider2Y); alphaSlider.setWidth(gW); alphaSlider.setHeight(20); alphaSlider.markConstraintsDirty();

        // droite
        int rx = gx + gW + GAP;
        int rTop = gy;
        swatchOld.setX(rx); swatchOld.setY(rTop); swatchOld.setWidth( (rightW - GAP) / 2 ); swatchOld.setHeight(70); swatchOld.markConstraintsDirty();
        swatchNew.setX(swatchOld.getCalculatedX() + swatchOld.getCalculatedWidth() + GAP);
        swatchNew.setY(rTop); swatchNew.setWidth( (rightW - GAP) / 2 ); swatchNew.setHeight(70); swatchNew.markConstraintsDirty();

        int fieldsY = rTop + 70 + GAP;

        // HEX
        UIElement hexLbl = panel.getChildren().get(6);
        hexLbl.setX(rx); hexLbl.setY(fieldsY); hexLbl.setWidth(60); hexLbl.setHeight(ROW_H); hexLbl.markConstraintsDirty();
        hexField.setX(rx + 60 + GAP); hexField.setY(fieldsY); hexField.setWidth(Math.max(120, rightW - 60 - GAP));
        hexField.setHeight(ROW_H); hexField.setBackgroundColor(Theme.INPUT); hexField.markConstraintsDirty();

        // RGBA
        UIElement rgbLbl = panel.getChildren().get(8);
        int rgbaY = fieldsY + ROW_H + GAP;
        rgbLbl.setX(rx); rgbLbl.setY(rgbaY); rgbLbl.setWidth(60); rgbLbl.setHeight(ROW_H); rgbLbl.markConstraintsDirty();

        int cellW = Math.max(60, (rightW - 60 - 3 * GAP) / 4);
        int x0 = rx + 60 + GAP;
        rField.setX(x0); rField.setY(rgbaY); rField.setWidth(cellW); rField.setHeight(ROW_H); rField.setBackgroundColor(Theme.INPUT); rField.markConstraintsDirty();
        gField.setX(x0 + cellW + GAP); gField.setY(rgbaY); gField.setWidth(cellW); gField.setHeight(ROW_H); gField.setBackgroundColor(Theme.INPUT); gField.markConstraintsDirty();
        bField.setX(x0 + 2 * (cellW + GAP)); bField.setY(rgbaY); bField.setWidth(cellW); bField.setHeight(ROW_H); bField.setBackgroundColor(Theme.INPUT); bField.markConstraintsDirty();
        aField.setX(x0 + 3 * (cellW + GAP)); aField.setY(rgbaY); aField.setWidth(cellW); aField.setHeight(ROW_H); aField.setBackgroundColor(Theme.INPUT); aField.markConstraintsDirty();

        // actions en bas
        int actionsY = py + ph - INSET - ACTIONS_H;
        actionsRow.setX(innerX); actionsRow.setY(actionsY); actionsRow.setWidth(innerW); actionsRow.setHeight(ACTIONS_H); actionsRow.markConstraintsDirty();
    }

    // ==== helpers ====

    private TextFieldItem tf(String placeholder, java.util.function.Consumer<String> onChange) {
        return new TextFieldItem(uiSystem, 0, 0, 120, ROW_H)
                .withPlaceholder(placeholder)
                .textColor(Theme.FOREGROUND)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.P_2)
                .onChange(onChange);
    }

    private void onHexChanged(String txt) {
        if (selfUpdate) return;
        if (!HEX.matcher(txt).matches()) return;
        try {
            String t = txt.startsWith("#") ? txt.substring(1) : txt;
            long v = Long.parseLong(t, 16);
            int argb = (t.length() == 8) ? (int) v : (int) (0xFF000000L | v);
            argbToHsva(argb);
            syncFromHsva();
        } catch (Exception ignored) {}
    }

    private void onRgbChanged() {
        if (selfUpdate) return;
        Integer r = p255(rField.getText()), g = p255(gField.getText()), b = p255(bField.getText()), a = p255(aField.getText());
        if (r == null || g == null || b == null || a == null) return;
        int argb = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
        argbToHsva(argb); syncFromHsva();
    }

    private Integer p255(String s) { try { int v = Integer.parseInt(s.trim()); return (v < 0 || v > 255) ? null : v; } catch (Exception e) { return null; } }

    private void syncFromHsva() {
        selfUpdate = true;
        int argb = currentColor();
        swatchNew.setColor(argb);
        hueSlider.setValue(hue / 360f);
        alphaSlider.setValue(alpha);

        int r = (argb >> 16) & 0xFF, g = (argb >> 8) & 0xFF, b = argb & 0xFF, a = (argb >>> 24) & 0xFF;
        hexField.setText(String.format("#%02X%02X%02X%02X", r, g, b, a));
        rField.setText(Integer.toString(r));
        gField.setText(Integer.toString(g));
        bField.setText(Integer.toString(b));
        aField.setText(Integer.toString(a));
        selfUpdate = false;
    }

    private int currentColor() {
        int rgb = Color.HSBtoRGB(hue / 360f, clamp01(sat), clamp01(val)) & 0x00FFFFFF;
        int a = (int) Math.round(clamp01(alpha) * 255.0);
        return (a << 24) | rgb;
    }

    private void argbToHsva(int argb) {
        float a = ((argb >>> 24) & 0xFF) / 255f;
        int r = (argb >> 16) & 0xFF, g = (argb >> 8) & 0xFF, b = argb & 0xFF;
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        hue = hsb[0] * 360f; sat = hsb[1]; val = hsb[2]; alpha = a;
        if (svPad != null) svPad.setHue(hue).setSaturation(sat).setValue(val);
    }

    private static float clamp01(float v) { return Math.max(0f, Math.min(1f, v)); }
}
