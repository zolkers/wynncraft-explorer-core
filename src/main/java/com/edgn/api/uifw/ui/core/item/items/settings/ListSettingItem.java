package com.edgn.api.uifw.ui.core.item.items.settings;

import com.edgn.api.uifw.ui.core.item.items.ButtonItem;
import com.edgn.api.uifw.ui.css.StyleKey;
import com.edgn.api.uifw.ui.css.UIStyleSystem;
import com.edgn.api.uifw.ui.utils.DrawingUtils;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.minecraft.ui.screens.modules.settings.components.ListEditScreen;
import com.edgn.core.module.settings.ListSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import java.util.List;

public class ListSettingItem<T> extends ButtonItem {
    private final ListSetting<T> listSetting;
    private final ISettingsScreen screen;

    private int controlWidth = 140;
    public ListSettingItem(UIStyleSystem ui,
                           int x, int y, int width, int height,
                           ListSetting<T> setting,
                           ISettingsScreen screen) {
        super(ui, x, y, width, height);
        this.listSetting = setting;
        this.screen = screen;

        backgroundColor(screen.getBgSecondary());
        addClass(StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.HOVER_BRIGHTEN, StyleKey.P_2);

        onClick(() -> {
            MinecraftClient.getInstance().getSoundManager()
                    .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            MinecraftClient.getInstance().setScreen(new ListEditScreen<>(screen, listSetting));
        });
    }

    public ListSettingItem<T> controlWidth(int w) {
        this.controlWidth = Math.max(80, w);
        return this;
    }

    @Override
    public void render(DrawContext context) {
        super.render(context);

        int cx = getCalculatedX();
        int cy = getCalculatedY();
        int cw = getCalculatedWidth();
        int ch = getCalculatedHeight();

        int sw = Math.min(controlWidth, Math.max(80, cw - 20));
        int sh = Math.max(16, ch - 8);
        int sx = cx + cw - sw - 10;
        int sy = cy + 4;

        int selectorBg = isHovered() ? screen.getAccentHoverColor() : screen.getAccentColor();
        DrawingUtils.drawRoundedRect(context, sx, sy, sw, sh, 6, selectorBg);

        List<T> list = listSetting.getValue();
        int size = (list != null) ? list.size() : 0;

        var fr = MinecraftClient.getInstance().textRenderer;
        int ty = sy + (sh - 8) / 2;

        context.drawTextWithShadow(fr, "📋", sx + 6, ty, screen.getTextPrimary());
        String text = "Edit (" + size + " items)";
        context.drawTextWithShadow(fr, text, sx + 20, ty, screen.getTextPrimary());

        if (isHovered()) {
            context.drawTextWithShadow(fr, "▶", sx + sw - 15, ty, 0xAAFFFFFF);
        }

        if (size > 0) {
            String badge = String.valueOf(size);
            int badgeW = fr.getWidth(badge) + 6;
            int badgeH = Math.max(12, sh - 4);
            int badgeX = sx + sw - badgeW - (isHovered() ? 25 : 10);
            int badgeY = sy + (sh - badgeH) / 2;

            int badgeColor = size > 5 ? 0xFF10B981 : 0x60FFFFFF;
            DrawingUtils.drawRoundedRect(context, badgeX, badgeY, badgeW, badgeH, badgeH / 2, badgeColor);
            context.drawText(fr, badge, badgeX + 3, badgeY + (badgeH - 8) / 2, screen.getTextPrimary(), false);
        }
    }
}
