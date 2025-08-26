package com.edgn.core.minecraft.ui.screens.modules.settings.components.complementary;

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
import com.edgn.api.uifw.ui.layout.ZIndex;
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
        static final int FOREGROUND = 0xFFE5E7EB;
        static final int INPUT = 0xFF18181B;
        static final int CARD = 0xFF111113;
        static final int SECONDARY = 0xFF27272A;
        static final int SECONDARY_FG = 0xFFE5E7EB;
        static final int PRIMARY = 0xFFA78BFA;
        static final int PRIMARY_FG = 0xFF0B0B0F;
        static final int BORDER = 0xFF2A2A2E;
    }

    private static final int ROW_H       = 36;
    private static final int ACTIONS_H   = 36;

    private final int initialColor;
    private final Consumer<Integer> onPick;

    private float hue;
    private float sat;
    private float val;
    private float alpha;

    private FlexContainer root;
    private FlexContainer colorItemsContainer;
    private FlexContainer colorContainer;
    private FlexContainer visualisationContainer;

    private SVPadItem svPad;
    private GradientSliderItem hueSlider;
    private GradientSliderItem alphaSlider;

    private ColorSwatchItem swatchNew;
    private TextFieldItem hexField;
    private TextFieldItem rField;
    private TextFieldItem gField;
    private TextFieldItem bField;
    private TextFieldItem aField;

    private boolean selfUpdate = false;
    private static final Pattern HEX = Pattern.compile("^#?[0-9a-fA-F]{6}([0-9a-fA-F]{2})?$");

    public ColorPickerScreen(ISettingsScreen parent, int initialColor, Consumer<Integer> onColorSelect) {
        super(Text.literal("Color Picker"), (Screen) parent);
        this.initialColor = initialColor;
        this.onPick = onColorSelect;
        argbToHsva(initialColor);
    }

    @Override
    protected TemplateSettings templateSettings() {
        return new TemplateSettings().setHeader(true).setFooter(true);
    }

    @Override
    protected BaseContainer createHeader() {
        FlexContainer flex = new FlexContainer(uiSystem, 0, 0, this.width, this.headerHeight)
                .setRenderBackground(true)
                .setBackgroundColor(Theme.BG_MAIN)
                .addClass(StyleKey.GAP_2, StyleKey.PT_5)
                .setZIndex(ZIndex.Layer.OVERLAY, 0);

        LabelItem labelItem = new LabelItem(uiSystem, 0, 0, 0, 0, "Color Picker").align(TextComponent.TextAlign.CENTER)
                .addClass(StyleKey.FLEX_BASIS_100);

        return flex.addChild(labelItem);
    }

    @Override
    protected BaseContainer createContent() {
        ColorSwatchItem swatchOld;
        root = new FlexContainer(uiSystem, 0, 0, width, contentHeight)
                .setBackgroundColor(ColorUtils.setOpacity(Theme.BG_MAIN, 0.90f))
                .setRenderBackground(true).addClass(StyleKey.GAP_2, StyleKey.P_2);

        colorItemsContainer = new FlexContainer(uiSystem, 0, 0, width, contentHeight).addClass(StyleKey.FLEX_BASIS_100, StyleKey.GAP_2);
        svPad = new SVPadItem(uiSystem, 0, 0, 0, contentHeight)
                .setHue(hue).setSaturation(sat).setValue(val)
                .onChange((s, v) -> { sat = s; val = v; syncFromHsva(); })
                .addClass(StyleKey.FLEX_BASIS_40);

        colorContainer = new FlexContainer(uiSystem,0, 0, 0, contentHeight).addClass(StyleKey.FLEX_BASIS_40, StyleKey.GAP_2);
        visualisationContainer = new FlexContainer(uiSystem, 0, 0, 0, contentHeight).addClass(StyleKey.P_2, StyleKey.GAP_3);

        hueSlider = new GradientSliderItem(uiSystem, 0, 0, 280, 60)
                .withThumbSize(10)
                .withGradient(t -> Color.HSBtoRGB((float) t, 1f, 1f) | 0xFF000000)
                .withValue(hue / 360f)
                .onChange(t -> { hue = (float) (t * 360f); svPad.setHue(hue); syncFromHsva(); })
                .addClass(StyleKey.FLEX_BASIS_100);

        alphaSlider = new GradientSliderItem(uiSystem, 0, 0, 280, 60)
                .withThumbSize(10)
                .withCompositeCheckerboard(true)
                .withGradient(t -> {
                    int rgb = currentColor() & 0x00FFFFFF;
                    int a = (int) Math.round(t * 255.0);
                    return (a << 24) | rgb;
                })
                .withValue(alpha)
                .onChange(t -> { alpha = (float) t; syncFromHsva(); })
                .addClass(StyleKey.FLEX_BASIS_100);

        swatchOld = new ColorSwatchItem(uiSystem, 0, 0, 120, 120).setColor(initialColor);
        swatchNew = new ColorSwatchItem(uiSystem, 0, 0, 120, 120).setColor(currentColor());

        colorContainer.addChild(hueSlider).addChild(alphaSlider);
        visualisationContainer.addChild(swatchOld).addChild(swatchNew);
        colorContainer.addChild(visualisationContainer);
        colorItemsContainer.addChild(svPad).addChild(colorContainer);
        root.addChild(colorItemsContainer);

        FlexContainer hexContainer = new FlexContainer(uiSystem, 0, 0, width, 200).addClass(StyleKey.FLEX_BASIS_40, StyleKey.GAP_2);

        LabelItem hexLbl = new LabelItem(uiSystem, 0, 0,
                new TextComponent("HEX").rainbow(TextComponent.EffectMode.HORIZONTAL_LTR)).addClass(StyleKey.FLEX_BASIS_100);

        hexField = tf("#FFFFFFFF", this::onHexChanged).setBackgroundColor(Theme.INPUT).addClass(StyleKey.FLEX_BASIS_100);

        hexContainer.addChild(hexLbl).addChild(hexField);

        FlexContainer rgba = new FlexContainer(uiSystem, 0, 0, this.width, 200).addClass(StyleKey.FLEX_BASIS_40, StyleKey.GAP_2);

        LabelItem rgbLbl = new LabelItem(uiSystem, 0, 0, new TextComponent("RGBA")
                .rainbow(TextComponent.EffectMode.HORIZONTAL_LTR)).addClass(StyleKey.FLEX_BASIS_100);

        rField = tf("0", s -> onRgbChanged()).addClass(StyleKey.FLEX_BASIS_10).setBackgroundColor(Theme.INPUT);
        gField = tf("0", s -> onRgbChanged()).addClass(StyleKey.FLEX_BASIS_10).setBackgroundColor(Theme.INPUT);
        bField = tf("0", s -> onRgbChanged()).addClass(StyleKey.FLEX_BASIS_10).setBackgroundColor(Theme.INPUT);
        aField = tf("255", s -> onRgbChanged()).addClass(StyleKey.FLEX_BASIS_10).setBackgroundColor(Theme.INPUT);

        rgba.addChild(rgbLbl).addChild(rField).addChild(gField).addChild(bField).addChild(aField);

        root.addChild(hexContainer);
        root.addChild(rgba);

        syncFromHsva();
        this.layout();
        return root;
    }

    @Override
    protected BaseContainer createFooter() {
        FlexContainer flex = new FlexContainer(uiSystem, 0, this.contentHeight, this.width, this.footerHeight)
                .setRenderBackground(true)
                .setBackgroundColor(Theme.BG_MAIN)
                .addClass(StyleKey.GAP_2)
                .setZIndex(ZIndex.Layer.OVERLAY, 0);

        ButtonItem cancel = new ButtonItem(uiSystem, 0, 0, 120, ACTIONS_H,
                new TextComponent("Cancel").color(Theme.SECONDARY_FG))
                .backgroundColor(Theme.SECONDARY)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_40)
                .onClick(this::close);

        ButtonItem apply = new ButtonItem(uiSystem, 0, 0, 120, ACTIONS_H,
                new TextComponent("Apply").color(Theme.PRIMARY_FG))
                .backgroundColor(Theme.PRIMARY)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_40)
                .onClick(() -> {
                    if (onPick != null) onPick.accept(currentColor());
                    close();
                });

        return flex.addChild(cancel).addChild(apply);
    }

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

    @Override
    protected void resizeEvent() {
        this.layout();
    }

    private void layout() {
        if(root != null) {
            root.setHeight(this.contentHeight);
        }
        if(colorItemsContainer != null) {
            colorItemsContainer.setHeight(this.contentHeight);
        }
    }

    private static float clamp01(float v) { return Math.max(0f, Math.min(1f, v)); }
}
