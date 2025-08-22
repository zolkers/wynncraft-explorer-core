package com.edgn.core.minecraft.ui.screens.modules.settings.components;

import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.module.settings.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public abstract class SettingComponent extends ClickableWidget implements Element, Selectable {
    protected final Setting<?> setting;
    protected final ISettingsScreen screen;

    protected SettingComponent(Setting<?> setting, ISettingsScreen screen, int x, int y, int width, int height) {
        super(x, y, width, height, Text.literal(""));
        this.setting = setting;
        this.screen = screen;
    }

    public Setting<?> getSetting() {
        return setting;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible && mouseX >= (double)this.getX() && mouseY >= (double)this.getY() && mouseX < (double)(this.getX() + this.width) && mouseY < (double)(this.getY() + this.height);
    }

    @Override
    protected abstract void renderWidget(DrawContext context, int mouseX, int mouseY, float delta);

    @Override
    public SelectionType getType() {
        return this.isHovered() ? SelectionType.HOVERED : SelectionType.NONE;
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}