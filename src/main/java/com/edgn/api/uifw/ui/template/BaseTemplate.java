package com.edgn.api.uifw.ui.template;

import com.edgn.Main;
import com.edgn.api.uifw.exceptions.ScreenCrashException;
import com.edgn.api.uifw.exceptions.safe.Crash;
import com.edgn.api.uifw.exceptions.safe.Safe;
import com.edgn.api.uifw.ui.core.container.BaseContainer;
import com.edgn.api.uifw.ui.css.UIStyleSystem;
import com.edgn.mixin.mixins.accessor.ScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Calendar;

/**
 * BaseTemplate is the main template which is recommended to use.
 * If you ever want to create your own template, You can, always make sure to
 * either extend {@link EventTemplate} or {@link BaseTemplate} as it is the core of the framework
 * @author EDGN
 */
public abstract class BaseTemplate extends EventTemplate {
    protected int headerHeight;
    protected int footerHeight;
    protected int contentHeight;
    protected Screen prevScreen;
    private final TemplateSettings settings;

    private BaseContainer headerContent;
    private BaseContainer mainContent;
    private BaseContainer footerContent;

    protected BaseTemplate(Text title, Screen prevScreen) {
        super(title);
        this.prevScreen = prevScreen;
        if(templateSettings() == null) this.settings = new TemplateSettings().setToDefault();
        else this.settings = templateSettings();
    }

    /**
     * The settings of your screen
     * @return the settings
     */
    protected abstract TemplateSettings templateSettings();

    /**
     * The header of your screen, a BaseContainer, FlexContainer is the expected container
     * to be used, but since we have to consider custom containers, a BaseContainer will do
     * @return a container
     */
    protected abstract BaseContainer createHeader();

    /**
     * The content of your screen, a BaseContainer, FlexContainer is the expected container
     * to be used, but since we have to consider custom containers, a BaseContainer will do
     * @return a container
     */
    protected abstract BaseContainer createContent();

    /**
     * The footer of your screen, a BaseContainer, FlexContainer is the expected container
     * to be used, but since we have to consider custom containers, a BaseContainer will do
     * @return a container
     */
    protected abstract BaseContainer createFooter();

    /**
     * The screen initialisation entry point for the user
     */
    protected void initialise() {}

    /**
     * The tick entry point for the user
     */
    protected void tickEvent() {}

    /**
     * The resize entry point for the user
     */
    protected void resizeEvent() {}

    @Override
    protected final void onInit() {
        String s = getClass().getSimpleName();
        try {
            Safe.run(s, ScreenCrashException.Phase.INIT, this::updateScreenValues);
            Safe.run(s, ScreenCrashException.Phase.INIT, this::buildUI);
            Safe.run(s, ScreenCrashException.Phase.INIT, this::initialise);
        } catch (ScreenCrashException e) {
            Crash.handle(this, e, this::close);
        }
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(prevScreen);
    }

    @Override
    public final void onTick() {
        String s = getClass().getSimpleName();
        try {
            Safe.run(s, ScreenCrashException.Phase.TICK, this::updateScreenValues);
            Safe.run(s, ScreenCrashException.Phase.TICK, this::tickEvent);
        } catch (ScreenCrashException e) {
            Crash.handle(this, e, this::close);
        }
    }

    @Override
    public final void onResize(MinecraftClient client, int width, int height) {
        String s = getClass().getSimpleName();
        try {
            Safe.run(s, ScreenCrashException.Phase.RESIZE, this::updateScreenValues);
            Safe.run(s, ScreenCrashException.Phase.RESIZE, this::reflowLayout);
            Safe.run(s, ScreenCrashException.Phase.RESIZE, this::resizeEvent);
            Safe.run(s, ScreenCrashException.Phase.RESIZE, () -> uiSystem.getEventManager().onResize(client, width, height));
        } catch (ScreenCrashException e) {
            Crash.handle(this, e, this::close);
        }
    }

    protected void reflowLayout() {
        if (headerContent != null) {
            headerContent.setX(0);
            headerContent.setY(0);
            headerContent.setWidth(width);
            headerContent.setHeight(headerHeight);
            headerContent.markConstraintsDirty();
        }

        if (mainContent != null) {
            mainContent.setX(0);
            mainContent.setY(headerHeight);
            mainContent.setWidth(width);
            mainContent.setHeight(contentHeight);
            mainContent.markConstraintsDirty();
        }

        if (footerContent != null) {
            footerContent.setX(0);
            footerContent.setY(height - footerHeight);
            footerContent.setWidth(width);
            footerContent.setHeight(footerHeight);
            footerContent.markConstraintsDirty();
        }
    }


    protected void buildUI() {
        if(settings.hasHeader()) {
            headerContent = createHeader();
            if (headerContent != null) {
                headerContent.setX(0);
                headerContent.setY(0);
                headerContent.setWidth(width);
                headerContent.setHeight(headerHeight);
            }
        }

        int contentY = headerHeight;
        mainContent = createContent();
        if (mainContent != null) {
            mainContent.setX(0);
            mainContent.setY(contentY);
            mainContent.setWidth(width);
            mainContent.setHeight(contentHeight);
        }

        if(settings.hasFooter()) {
            int footerY = height - footerHeight;
            footerContent = createFooter();
            if (footerContent != null) {
                footerContent.setX(0);
                footerContent.setY(footerY);
                footerContent.setWidth(width);
                footerContent.setHeight(footerHeight);
            }
        }
    }

    protected final void renderHeader(DrawContext context) {
        if(!settings.hasHeader()) return;
        if (headerContent != null) {
            headerContent.render(context);
        } else {
            renderDefaultHeader(context);
        }
    }

    protected final void renderContent(DrawContext context) {
        if (mainContent != null) {
            mainContent.render(context);
        }
    }

    protected final void renderFooter(DrawContext context) {
        if(!settings.hasFooter()) return;
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

    @Override
    public final void render(DrawContext context, int mouseX, int mouseY, float delta) {
        String s = getClass().getSimpleName();
        try {
            Safe.run(s, ScreenCrashException.Phase.RENDER, this::updateScreenValues);
            Safe.run(s, ScreenCrashException.Phase.RENDER, () -> renderBackground(context, mouseX, mouseY, delta));
            Safe.run(s, ScreenCrashException.Phase.RENDER, () -> renderHeader(context));
            Safe.run(s, ScreenCrashException.Phase.RENDER, () -> renderContent(context));
            Safe.run(s, ScreenCrashException.Phase.RENDER, () -> renderFooter(context));
            Safe.run(s, ScreenCrashException.Phase.RENDER, () -> {
                //in my original lib it's handled by its mixin, but since I was lazy to maven package it then it will use my accessor
                for (Drawable drawable : ((ScreenAccessor) this).getDrawables()) {
                    if(drawable != null) {
                        drawable.render(context, mouseX, mouseY, delta);
                    }
                }
            });
        } catch (ScreenCrashException e) {
            Crash.handle(this, e, this::close);
        }
    }

    @Override
    public final void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
    }

    protected void updateScreenValues() {
        if(settings.hasHeader()) {
            this.headerHeight = Math.max(30, this.height / 15);
        } else { this.headerHeight = 0; }

        if(settings.hasFooter()) {
            this.footerHeight = Math.max(20, this.height / 20);
        } else { footerHeight = 0; }

        this.contentHeight = height - headerHeight - footerHeight;
    }

    /**
     * Carrying everything
     */
    @Override
    protected void refreshWidgetPositions() {
        if (width <= 0 || height <= 0) return;

        updateScreenValues();

        reflowLayout();

        uiSystem.getEventManager().onResize(MinecraftClient.getInstance(), width, height);

        resizeEvent();
    }

    public UIStyleSystem getUISystem() { return uiSystem; }
    public int getHeaderHeight() { return headerHeight; }
    public int getFooterHeight() { return footerHeight; }
    public int getContentHeight() { return contentHeight; }

    protected void debug() {
        Main.LOGGER.info("Header {}  content {}  footer {}", headerHeight, contentHeight, footerHeight);
    }
}
