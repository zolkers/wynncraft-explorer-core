package com.edgn.api.uifw.ui.core.item.items;

import com.edgn.api.uifw.ui.core.components.TextComponent;
import com.edgn.api.uifw.ui.core.container.BaseContainer;
import com.edgn.api.uifw.ui.core.item.BaseItem;
import com.edgn.api.uifw.ui.core.renderer.FontRenderer;
import com.edgn.api.uifw.ui.css.StyleKey;
import com.edgn.api.uifw.ui.css.UIStyleSystem;
import com.edgn.api.uifw.ui.css.values.Shadow;
import com.edgn.api.uifw.ui.layout.LayoutConstraints;
import com.edgn.api.uifw.ui.layout.ZIndex;
import com.edgn.api.uifw.ui.utils.ColorUtils;
import com.edgn.api.uifw.ui.utils.DrawingUtils;
import com.edgn.core.module.basic.AbstractModule;
import com.edgn.core.module.basic.ModuleInfo;
import net.minecraft.client.gui.DrawContext;

import java.awt.Rectangle;

@SuppressWarnings({"unused", "unchecked", "UnusedReturnValue"})
public class ModuleCardItem extends BaseItem {

    private final AbstractModule module;
    private final TextComponent title;
    private final TextComponent desc;
    private int bg = 0;
    private Runnable onOpenSettings;

    private Rectangle localToggleAreaBounds;
    private Rectangle localSettingsAreaBounds;

    private static final int CARD_FALLBACK_BG = ColorUtils.setOpacity(ColorUtils.NamedColor.SLATEGRAY.toInt(), 0.55f);

    public ModuleCardItem(UIStyleSystem styleSystem, int x, int y, int w, int h, AbstractModule module) {
        super(styleSystem, x, y, w, h);
        this.module = module;
        ModuleInfo info = module != null ? module.getClass().getAnnotation(ModuleInfo.class) : null;

        addClass(StyleKey.ROUNDED_LG, StyleKey.SHADOW_MD, StyleKey.P_3, StyleKey.GAP_2, StyleKey.HOVER_SCALE, StyleKey.HOVER_BRIGHTEN);

        String titleText  = module != null ? module.getName() : "Unknown";
        String descText   = (info != null && info.description() != null) ? info.description() : "No description";

        this.title = new TextComponent(titleText, fontRenderer).truncate();
        this.desc  = new TextComponent(descText, fontRenderer).truncate().wrap(3);
    }

    public ModuleCardItem onOpenSettings(Runnable cb) {
        this.onOpenSettings = cb;
        return this;
    }

    private int categoryStripeColor() {
        return ColorUtils.setOpacity(ColorUtils.NamedColor.WHITE.toInt(), 0.10f);
    }

    @Override
    public void updateConstraints() {
        if (!this.constraintsDirty) return;
        super.updateConstraints();

        int cw = getCalculatedWidth();
        int ch = getCalculatedHeight();

        int padR = getPaddingRight();
        int padB = getPaddingBottom();

        int settingsW = 48;
        int settingsH = 22;
        int localSettingsX = cw - padR - settingsW;

        int toggleW  = 56;
        int toggleH  = 22;
        int localToggleX  = localSettingsX - 8 - toggleW;

        int localBtnY = ch - padB - 26;

        this.localToggleAreaBounds   = new Rectangle(localToggleX,  localBtnY, toggleW,   toggleH);
        this.localSettingsAreaBounds = new Rectangle(localSettingsX, localBtnY, settingsW, settingsH);
    }

    private void toLocal(double mouseX, double mouseY, double[] out) {
        int px = 0, py = 0;
        if (parent instanceof BaseContainer bc) {
            px = bc.getChildInteractionOffsetX(this);
            py = bc.getChildInteractionOffsetY(this);
        }
        out[0] = mouseX - (getCalculatedX() + px);
        out[1] = mouseY - (getCalculatedY() + py);
    }

    @Override
    public void render(DrawContext context) {
        if (!visible) return;

        updateConstraints();

        int cx = getCalculatedX();
        int cy = getCalculatedY();
        int cw = getCalculatedWidth();
        int ch = getCalculatedHeight();

        int baseBg = bg == 0 ? CARD_FALLBACK_BG : bg;
        int cardBg = backgroundForState(baseBg);
        int radius = getBorderRadius();
        Shadow shadow = getShadow();

        float scale = (hasClass(StyleKey.HOVER_SCALE) && isHovered()) ? getAnimatedScale() : 1.0f;
        if (scale != 1.0f) {
            int sw = Math.max(0, Math.round(cw * scale));
            int sh = Math.max(0, Math.round(ch * scale));
            int ox = (sw - cw) / 2;
            int oy = (sh - ch) / 2;
            if (shadow != null) DrawingUtils.drawShadow(context, cx - ox, cy - oy, sw, sh, 3, 3, shadow.color);
            DrawingUtils.drawRoundedRect(context, cx - ox, cy - oy, sw, sh, radius, cardBg);
        } else {
            if (shadow != null) DrawingUtils.drawShadow(context, cx, cy, cw, ch, 2, 2, shadow.color);
            DrawingUtils.drawRoundedRect(context, cx, cy, cw, ch, radius, cardBg);
        }

        int stripeW = 6;
        DrawingUtils.drawRoundedRect(context, cx, cy, stripeW, ch, radius, categoryStripeColor());

        int padL = getPaddingLeft() + stripeW + 8;
        int padT = getPaddingTop();
        int padR = getPaddingRight();

        int contentX = cx + padL;
        int contentY = cy + padT;
        int contentW = Math.max(0, cw - padL - padR);
        int contentH = Math.max(0, ch - getPaddingTop() - getPaddingBottom());

        int line1H = Math.max(fontRenderer.lineHeight(), 14);
        title.render(context, contentX, contentY, contentW - 120, line1H);

        String st = module != null && module.isEnabled() ? "ON" : "OFF";
        int stateColor = module != null && module.isEnabled()
                ? ColorUtils.setOpacity(ColorUtils.NamedColor.LIGHTGREEN.toInt(), 0.85f)
                : ColorUtils.setOpacity(ColorUtils.NamedColor.TOMATO.toInt(), 0.85f);
        int badgeW = fontRenderer.width(st) + 16;
        int badgeH = 18;
        int badgeX = contentX + contentW - badgeW;
        int badgeY = contentY + (line1H - badgeH) / 2;
        int badgeBg = ColorUtils.setOpacity(stateColor, 0.22f);
        DrawingUtils.drawRoundedRect(context, badgeX, badgeY, badgeW, badgeH, 4, badgeBg);
        new TextComponent(st, fontRenderer).color(stateColor)
                .align(TextComponent.TextAlign.CENTER)
                .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                .render(context, badgeX, badgeY, badgeW, badgeH);

        int gap = Math.max(2, getGap());
        int descY = contentY + line1H + gap;
        int descH = contentH - line1H - gap - (fontRenderer.lineHeight() + 4);
        if (descH > 0) desc.render(context, contentX, descY, contentW, descH);

        if (localToggleAreaBounds != null) {
            int tBgBase = module != null && module.isEnabled()
                    ? ColorUtils.setOpacity(ColorUtils.NamedColor.LIGHTGREEN.toInt(), 0.80f)
                    : ColorUtils.setOpacity(ColorUtils.NamedColor.TOMATO.toInt(), 0.80f);
            DrawingUtils.drawRoundedRect(context,
                    cx + localToggleAreaBounds.x, cy + localToggleAreaBounds.y,
                    localToggleAreaBounds.width, localToggleAreaBounds.height,
                    4, tBgBase);
            String toggleText = module != null && module.isEnabled() ? "ON" : "OFF";
            new TextComponent(toggleText, fontRenderer)
                    .color(ColorUtils.NamedColor.WHITE.toInt())
                    .align(TextComponent.TextAlign.CENTER)
                    .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                    .render(context,
                            cx + localToggleAreaBounds.x, cy + localToggleAreaBounds.y,
                            localToggleAreaBounds.width, localToggleAreaBounds.height);
        }

        if (localSettingsAreaBounds != null) {
            int sBgBase = ColorUtils.setOpacity(ColorUtils.NamedColor.DIMGRAY.toInt(), 0.75f);
            DrawingUtils.drawRoundedRect(context,
                    cx + localSettingsAreaBounds.x, cy + localSettingsAreaBounds.y,
                    localSettingsAreaBounds.width, localSettingsAreaBounds.height,
                    4, sBgBase);
            new TextComponent("⚙️", fontRenderer)
                    .color(ColorUtils.NamedColor.WHITESMOKE.toInt())
                    .align(TextComponent.TextAlign.CENTER)
                    .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                    .render(context,
                            cx + localSettingsAreaBounds.x, cy + localSettingsAreaBounds.y,
                            localSettingsAreaBounds.width, localSettingsAreaBounds.height);
        }
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled || !canInteract(mouseX, mouseY)) return false;

        double[] local = new double[2];
        toLocal(mouseX, mouseY, local);
        double lx = local[0], ly = local[1];

        if (localToggleAreaBounds != null && localToggleAreaBounds.contains(lx, ly)) {
            if (module != null) module.toggle();
            return true;
        }
        if (localSettingsAreaBounds != null && localSettingsAreaBounds.contains(lx, ly)) {
            if (onOpenSettings != null) onOpenSettings.run();
            return true;
        }

        setState(ItemState.PRESSED);
        return super.onMouseClick(mouseX, mouseY, button);
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        super.onMouseMove(mouseX, mouseY);

        double[] local = new double[2];
        toLocal(mouseX, mouseY, local);
        double lx = local[0], ly = local[1];
    }

    private int backgroundForState(int base) {
        if (getState() == ItemState.PRESSED) return darken(base);
        if (isHovered()) return brighten(base, 0.10f);
        return base;
    }

    private int brighten(int color, float ratio) {
        int a = (color >>> 24) & 0xFF;
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = color & 0xFF;
        r = Math.min(255, Math.round(r + (255 - r) * ratio));
        g = Math.min(255, Math.round(g + (255 - g) * ratio));
        b = Math.min(255, Math.round(b + (255 - b) * ratio));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int darken(int color) {
        int a = (color >>> 24) & 0xFF;
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = color & 0xFF;
        r = Math.max(0, Math.round(r * (1.0f - 0.16f)));
        g = Math.max(0, Math.round(g * (1.0f - 0.16f)));
        b = Math.max(0, Math.round(b * (1.0f - 0.16f)));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public ModuleCardItem setBackgroundColor(int argb) {
        this.bg = argb;
        return this;
    }

    @Override public ModuleCardItem addClass(StyleKey... keys) { super.addClass(keys); return this; }
    @Override public ModuleCardItem removeClass(StyleKey key) { super.removeClass(key); return this; }
    @Override public ModuleCardItem onClick(Runnable handler) { super.onClick(handler); return this; }
    @Override public ModuleCardItem onMouseEnter(Runnable handler) { super.onMouseEnter(handler); return this; }
    @Override public ModuleCardItem onMouseLeave(Runnable handler) { super.onMouseLeave(handler); return this; }
    @Override public ModuleCardItem onFocusGained(Runnable handler) { super.onFocusGained(handler); return this; }
    @Override public ModuleCardItem onFocusLost(Runnable handler) { super.onFocusLost(handler); return this; }
    @Override public ModuleCardItem setVisible(boolean visible) { super.setVisible(visible); return this; }
    @Override public ModuleCardItem setEnabled(boolean enabled) { super.setEnabled(enabled); return this; }
    @Override public ModuleCardItem setZIndex(int zIndex) { super.setZIndex(zIndex); return this; }
    @Override public ModuleCardItem setZIndex(ZIndex zIndex) { super.setZIndex(zIndex); return this; }
    @Override public ModuleCardItem setZIndex(ZIndex.Layer layer) { super.setZIndex(layer); return this; }
    @Override public ModuleCardItem setZIndex(ZIndex.Layer layer, int p) { super.setZIndex(layer, p); return this; }
    @Override public ModuleCardItem setConstraints(LayoutConstraints c) { super.setConstraints(c); return this; }

    @Override
    public ModuleCardItem setFontRenderer(FontRenderer fontRenderer) {
        super.setFontRenderer(fontRenderer);
        if (title != null) title.setFontRenderer(fontRenderer);
        if (desc != null) desc.setFontRenderer(fontRenderer);
        return this;
    }
}
