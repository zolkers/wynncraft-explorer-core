package com.edgn.core.minecraft.ui.screens;

import com.edgn.Main;
import com.edgn.mixin.mixins.accessor.ScreenAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Calendar;

@Deprecated(since = "0.3.5.3", forRemoval = true)
public abstract class BaseScreen extends Screen {

    protected int headerHeight;
    protected int footerHeight;

    protected BaseScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        this.updateScreenValues();
    }

    protected abstract void renderHeader(DrawContext context, int mouseX, int mouseY, float delta);
    protected abstract void renderContent(DrawContext context, int mouseX, int mouseY, float delta);
    protected abstract void renderFooter(DrawContext context, int mouseX, int mouseY, float delta);
    protected abstract void renderOverridElements(DrawContext context, int mouseX, int mouseY, float delta);

    protected void renderDefaultHeader(DrawContext context) {
        context.drawCenteredTextWithShadow(textRenderer, this.title, width / 2, headerHeight / 2 - 3, 0xFFFFFF);
        context.fill(0, headerHeight - 1, width, headerHeight, 0xFFFFFFFF);
    }

    protected void renderDefaultFooter(DrawContext context) {
        String footerText = "© " + Calendar.getInstance().get(Calendar.YEAR) + " " + Main.MOD_ID;
        context.drawCenteredTextWithShadow(textRenderer, footerText, width / 2, this.height - (this.footerHeight / 2) - 3, 0xAAAAAA);
        context.fill(0, height - footerHeight, width, height - footerHeight + 1, 0xFFFFFFFF);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.updateScreenValues();

        renderBackground(context, mouseX, mouseY, delta);

        renderHeader(context, mouseX, mouseY, delta);
        renderContent(context, mouseX, mouseY, delta);
        renderFooter(context, mouseX, mouseY, delta);

        for (Drawable drawable : ((ScreenAccessor) this).getDrawables()) {
            if(drawable != null) { //how tf can drawable be null ?!
                drawable.render(context, mouseX, mouseY, delta);
            }
        }

        renderOverridElements(context, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        super.tick();
        this.updateScreenValues();
    }

    private void updateScreenValues() {
        this.headerHeight = this.height / 15;
        this.footerHeight = this.height / 20;
    }
}