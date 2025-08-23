package com.edgn.core.minecraft.ui.screens.modules.settings.components;

import com.edgn.api.uifw.ui.utils.DrawingUtils;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.module.settings.ListSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import java.util.List;

public class ListSettingComponent<T> extends SettingComponent {
    private final ListSetting<T> listSetting;

    public ListSettingComponent(ListSetting<T> setting, ISettingsScreen screen, int x, int y, int width, int height) {
        super(setting, screen, x, y, width, height);
        this.listSetting = setting;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean hovered = isHovered();

        int bgColor = hovered ? (screen.getAccentColor() & 0x40FFFFFF) : screen.getBgSecondary();
        DrawingUtils.drawRoundedRect(context, getX(), getY(), width, height, 8, bgColor);

        int controlWidth = 140;
        int controlHeight = height - 8;
        int controlX = getX() + width - controlWidth - 10;
        int controlY = getY() + 4;

        int controlBgColor = hovered ? screen.getAccentHoverColor() : screen.getAccentColor();
        DrawingUtils.drawRoundedRect(context, controlX, controlY, controlWidth, controlHeight, 6, controlBgColor);

        List<T> list = listSetting.getValue();
        int size = (list != null) ? list.size() : 0;
        String text = "Edit (" + size + " items)";

        String listIcon = "📋";
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, listIcon,
                controlX + 6, controlY + (controlHeight - 8) / 2, screen.getTextPrimary());

        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text,
                controlX + 20, controlY + (controlHeight - 8) / 2, screen.getTextPrimary());

        if (hovered) {
            String arrowIcon = "▶";
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, arrowIcon,
                    controlX + controlWidth - 15, controlY + (controlHeight - 8) / 2, 0xAAFFFFFF);
        }

        if (size > 0) {
            String countBadge = String.valueOf(size);
            int badgeWidth = MinecraftClient.getInstance().textRenderer.getWidth(countBadge) + 6;
            int badgeX = controlX + controlWidth - badgeWidth - (hovered ? 25 : 10);
            int badgeY = controlY + 2;
            int badgeHeight = controlHeight - 4;

            int badgeColor = size > 5 ? 0xFF10b981 : 0x60FFFFFF;
            DrawingUtils.drawRoundedRect(context, badgeX, badgeY, badgeWidth, badgeHeight, badgeHeight / 2, badgeColor);
            context.drawText(MinecraftClient.getInstance().textRenderer, countBadge,
                    badgeX + 3, badgeY + (badgeHeight - 8) / 2, screen.getTextPrimary(), false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int controlWidth = 140;
        int controlX = getX() + width - controlWidth - 10;

        if (DrawingUtils.isPointInRect(mouseX, mouseY, controlX, getY() + 4, controlWidth, height - 8) && button == 0) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            MinecraftClient.getInstance().setScreen(new ListEditScreen<>(screen, this.listSetting));
            return true;
        }
        return false;
    }
}
