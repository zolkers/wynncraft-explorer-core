package com.edgn.api.uifw.ui.core.item.items.settings;

import com.edgn.api.uifw.ui.core.item.items.ButtonItem;
import com.edgn.api.uifw.ui.css.StyleKey;
import com.edgn.api.uifw.ui.css.UIStyleSystem;
import com.edgn.api.uifw.ui.utils.DrawingUtils;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.module.settings.EnumSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class EnumSettingItem<T extends Enum<T>> extends ButtonItem {
    private final EnumSetting<T> enumSetting;
    private final ISettingsScreen screen;

    private int selectorWidth = 120;

    public EnumSettingItem(UIStyleSystem ui,
                           int x, int y, int width, int height,
                           EnumSetting<T> setting,
                           ISettingsScreen screen) {
        super(ui, x, y, width, height);
        this.enumSetting = setting;
        this.screen = screen;

        backgroundColor(screen.getBgSecondary());
        addClass(StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.HOVER_BRIGHTEN, StyleKey.P_2);
    }

    public EnumSettingItem<T> selectorWidth(int w) {
        this.selectorWidth = Math.max(60, w);
        return this;
    }

    @Override
    public void render(DrawContext context) {
        super.render(context);

        int cx = getCalculatedX();
        int cy = getCalculatedY();
        int cw = getCalculatedWidth();
        int ch = getCalculatedHeight();

        int sw = Math.min(selectorWidth, Math.max(60, cw - 20));
        int sh = Math.max(16, ch - 8);
        int sx = cx + cw - sw - 10;
        int sy = cy + 4;

        int selectorBg = isHovered() ? screen.getAccentHoverColor() : screen.getAccentColor();
        DrawingUtils.drawRoundedRect(context, sx, sy, sw, sh, 6, selectorBg);

        String label = formatEnum(enumSetting.getValue());
        var fr = MinecraftClient.getInstance().textRenderer;
        int ty = sy + (sh - 8) / 2; // 8 ~ hauteur font vanilla
        context.drawCenteredTextWithShadow(fr, label, sx + sw / 2, ty, screen.getTextPrimary());

        // Chevrons au hover
        if (isHovered()) {
            context.drawText(fr, "◀", sx + 4, ty, 0xAAFFFFFF, false);
            context.drawText(fr, "▶", sx + sw - 12, ty, 0xAAFFFFFF, false);
        }
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!isEnabled() || !canInteract(mouseX, mouseY)) return false;
        setState(ItemState.PRESSED);

        if (button == 0) {
            enumSetting.cycle();
            return true;
        } else if (button == 1) {
            enumSetting.cycleBackward();
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        if (!isEnabled()) return false;
        boolean inside = canInteract(mouseX, mouseY);
        setState(inside ? ItemState.HOVERED : ItemState.NORMAL);
        return inside;
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        if (!isEnabled() || !canInteract(mouseX, mouseY)) return false;
        if (scrollDelta > 0) enumSetting.cycleBackward();
        else if (scrollDelta < 0) enumSetting.cycle();
        return true;
    }

    private String formatEnum(Enum<?> e) {
        if (e == null) return "";
        String raw = e.name();
        String[] parts = raw.split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0)));
            if (p.length() > 1) sb.append(p.substring(1).toLowerCase());
            sb.append(' ');
        }
        return sb.toString().trim();
    }
}
