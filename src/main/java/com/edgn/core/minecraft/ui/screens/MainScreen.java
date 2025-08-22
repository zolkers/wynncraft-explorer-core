package com.edgn.core.minecraft.ui.screens;

import com.edgn.Main;
import com.edgn.api.ui.FeatureEntry;
import com.edgn.uifw.templates.HtmlTemplate;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.css.rules.AlignItems;
import com.edgn.uifw.elements.container.BaseContainer;
import com.edgn.uifw.elements.container.containers.FlexContainer;
import com.edgn.uifw.elements.container.containers.ListContainer;
import com.edgn.uifw.elements.item.items.FeatureItem;
import com.edgn.uifw.components.TextComponent;
import com.edgn.core.minecraft.ui.screens.modules.ModulesScreen;
import com.edgn.core.minecraft.ui.screens.terminal.TerminalScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class MainScreen extends HtmlTemplate {

    public MainScreen() {
        super(Text.of("Wynncraft Explorer"), null);
    }

    @Override
    protected BaseContainer createHeader() {
        return new FlexContainer(uiSystem, 0, 0, width, headerHeight)
                .addClass(StyleKey.FLEX_ROW, StyleKey.BG_SURFACE, StyleKey.JUSTIFY_CENTER,
                        StyleKey.ITEMS_CENTER, StyleKey.SHADOW_MD, StyleKey.BG_OPACITY_13);
    }

    @Override
    protected BaseContainer createContent() {
        FlexContainer content = new FlexContainer(uiSystem, 0, 0, width, contentHeight)
                .addClass(StyleKey.FLEX_COLUMN, StyleKey.JUSTIFY_CENTER, StyleKey.ITEMS_CENTER,
                        StyleKey.BG_BACKGROUND, StyleKey.P_4, StyleKey.BG_OPACITY_13);

        ListContainer featureList = new ListContainer(uiSystem, 0, 0, Math.min(600, width - 40), contentHeight - 40)
                .addClass(StyleKey.BG_SURFACE, StyleKey.ROUNDED_LG, StyleKey.SHADOW_LG, StyleKey.P_3, StyleKey.BG_OPACITY_13)
                .setItemHeight(70)
                .setItemSpacing(8)
                .setItemAlignment(AlignItems.STRETCH)
                .setAlwaysShowScrollbar(false)
                .setPixelScrolling(true)
                .setPixelsPerScrollStep(3)
                .setScrollDamping(0.75f);

        addFeatures(featureList);

        content.add(featureList);
        return content;
    }

    @Override
    protected BaseContainer createFooter() {
        return new FlexContainer(uiSystem, 0, 0, width, footerHeight)
                .addClass(StyleKey.FLEX_ROW, StyleKey.BG_SURFACE, StyleKey.JUSTIFY_CENTER,
                        StyleKey.ITEMS_CENTER, StyleKey.SHADOW_MD, StyleKey.BG_OPACITY_13);
    }

    private void addFeatures(ListContainer list) {

        list.add(new FeatureItem(uiSystem, 0, 0, 0, 70)
                .withTitle(new TextComponent("Modules manager", textRenderer).color(0xFFFFFFFF))
                .withDescription(new TextComponent("Heynul", textRenderer).color(0xFFAAAAAA))
                .withVersion(new TextComponent("V1.0.0", textRenderer).color(0xFF888888))
                .setIcon(Identifier.of(Main.MOD_ID, "textures/ui/module.png"))
                .addClass(StyleKey.SUCCESS, StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.HOVER_SCALE, StyleKey.BG_OPACITY_13)
                .onClick(() -> navigateTo(new ModulesScreen(this))));

        list.add(new FeatureItem(uiSystem, 0, 0, 0, 70)
                .withTitle(new TextComponent("Terminal", textRenderer).color(0xFFFFFFFF))
                .withDescription(new TextComponent("Terminal, might gonna use wsl lib soon idk", textRenderer).color(0xFFAAAAAA))
                .withVersion(new TextComponent("V2.0.0", textRenderer).color(0xFF888888))
                .setIcon(Identifier.of(Main.MOD_ID, "textures/ui/terminal.png"))
                .addClass(StyleKey.DARK, StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.HOVER_SCALE, StyleKey.BG_OPACITY_13)
                .onClick(() -> navigateTo(new TerminalScreen())));

        if (isDevelopmentMode()) {
            list.add(new FeatureItem(uiSystem, 0, 0, 0, 70)
                    .withTitle(new TextComponent("Test Screen", textRenderer).rainbow(TextComponent.EffectMode.HORIZONTAL_LTR).bold().glow())
                    .withDescription(new TextComponent("Edgn's tests uuuuuh dont click might crash", textRenderer).wave().color(0xFFFF6666))
                    .setIcon(Identifier.of(Main.MOD_ID, "icon.png"))
                    .addClass(StyleKey.ACCENT, StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.HOVER_SCALE, StyleKey.BG_OPACITY_13)
                    .setShowVersion(false)
                    .onClick(() -> navigateTo(new TestScreen(this))));
        }

        for (FeatureEntry e : Main.getCoreApi().ui().all()) {
            FeatureItem item = new FeatureItem(uiSystem, 0, 0, 0, 70)
                    .withTitle(new TextComponent(e.title != null ? e.title : e.id, textRenderer).color(0xFFFFFFFF))
                    .withDescription(new TextComponent(e.description != null ? e.description : "", textRenderer).color(0xFFAAAAAA))
                    .withVersion(new TextComponent(e.version != null ? e.version : "", textRenderer).color(0xFF888888))
                    .setIcon(e.icon != null ? e.icon : Identifier.of(Main.MOD_ID, "icon.png"))
                    .addClass(StyleKey.SECONDARY, StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.HOVER_SCALE, StyleKey.BG_OPACITY_13);

            if (e.openScreen != null) {
                item.onClick(() -> navigateTo(e.openScreen.get()));
            }
            list.add(item);
        }
    }

    private void navigateTo(Screen screen) {
        try {
            MinecraftClient.getInstance().setScreen(screen);
        } catch (Exception e) {
            System.err.println("Error while navigating: " + e.getMessage());
        }
    }

    private boolean isDevelopmentMode() {
        return Main.class.getPackage().getImplementationVersion() == null ||
                Main.class.getPackage().getImplementationVersion().contains("dev");
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}