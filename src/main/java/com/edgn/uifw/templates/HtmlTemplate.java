package com.edgn.uifw.templates;

import com.edgn.Main;
import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.elements.container.BaseContainer;
import com.edgn.mixin.mixins.accessor.ScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Calendar;

public abstract class HtmlTemplate extends EventScreen {
    protected int headerHeight;
    protected int footerHeight;
    protected int contentHeight;
    protected Screen prevScreen;

    private BaseContainer headerContent;
    private BaseContainer mainContent;
    private BaseContainer footerContent;

    protected HtmlTemplate(Text title, Screen prevScreen) {
        super(title);
        this.prevScreen = prevScreen;
    }

    @Override
    protected void onInit() {
        this.updateScreenValues();
        this.buildUI();
    }

    protected void buildUI() {
        headerContent = createHeader();
        if (headerContent != null) {
            headerContent.setX(0);
            headerContent.setY(0);
            headerContent.setWidth(width);
            headerContent.setHeight(headerHeight);
        }

        int contentY = headerHeight;
        mainContent = createContent();
        if (mainContent != null) {
            mainContent.setX(0);
            mainContent.setY(contentY);
            mainContent.setWidth(width);
            mainContent.setHeight(contentHeight);
        }

        int footerY = height - footerHeight;
        footerContent = createFooter();
        if (footerContent != null) {
            footerContent.setX(0);
            footerContent.setY(footerY);
            footerContent.setWidth(width);
            footerContent.setHeight(footerHeight);
        }
    }

    protected abstract BaseContainer createHeader();
    protected abstract BaseContainer createContent();
    protected abstract BaseContainer createFooter();

    protected final void renderHeader(DrawContext context, int mouseX, int mouseY, float delta) {
        if (headerContent != null) {
            headerContent.render(context);
        } else {
            renderDefaultHeader(context);
        }
    }

    protected final void renderContent(DrawContext context, int mouseX, int mouseY, float delta) {
        if (mainContent != null) {
            mainContent.render(context);
        }
    }

    protected final void renderFooter(DrawContext context, int mouseX, int mouseY, float delta) {
        if (footerContent != null) {
            footerContent.render(context);
        } else {
            renderDefaultFooter(context);
        }
    }

    protected void renderDefaultHeader(DrawContext context) {
        context.drawCenteredTextWithShadow(textRenderer, this.title, width / 2, headerHeight / 2 - 3, 0xFFFFFF);
        context.fill(0, headerHeight - 1, width, headerHeight, 0x40FFFFFF);
    }

    protected void renderDefaultFooter(DrawContext context) {
        String footerText = Calendar.getInstance().get(Calendar.YEAR) + " " + Main.MOD_ID;
        context.drawCenteredTextWithShadow(textRenderer, footerText, width / 2,
                this.height - (this.footerHeight / 2) - 3, 0xFFAAAAAA);
        context.fill(0, height - footerHeight, width, height - footerHeight + 1, 0x40FFFFFF);
    }

    protected void updateLayout() {
        buildUI();
    }

    @Override
    public final void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.updateScreenValues();

        renderBackground(context, mouseX, mouseY, delta);

        renderHeader(context, mouseX, mouseY, delta);
        renderContent(context, mouseX, mouseY, delta);
        renderFooter(context, mouseX, mouseY, delta);

        for (Drawable drawable : ((ScreenAccessor) this).getDrawables()) {
            if(drawable != null) {
                drawable.render(context, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(prevScreen);
    }

    /**
     * tick() can be useful to be used by the user, but I heavily recommend to call super.tick() at the head or tail of the method
     */
    @Override
    public void tick() {
        super.tick();
        this.updateScreenValues();
    }


    /**
     * resize(MinecraftClient, int, int) can be useful to be used by the user,
     * but I heavily recommend to call super.resize(MinecraftClient, int, int) at the head or tail of the method
     */
    @Override
    public void onResize(MinecraftClient client, int width, int height) {
        this.updateScreenValues();
        this.updateLayout();
    }

    private void updateScreenValues() {
        this.headerHeight = Math.max(30, this.height / 15);
        this.footerHeight = Math.max(20, this.height / 20);
        this.contentHeight = height - headerHeight - footerHeight;
    }

    public UIStyleSystem getUISystem() { return uiSystem; }
    public int getHeaderHeight() { return headerHeight; }
    public int getFooterHeight() { return footerHeight; }
    public int getContentHeight() { return contentHeight; }
}