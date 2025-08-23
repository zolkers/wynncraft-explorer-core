package com.edgn.core.minecraft.ui.screens;

import com.edgn.Main;
import com.edgn.api.ui.FeatureEntry;
import com.edgn.api.uifw.ui.core.components.TextComponent;
import com.edgn.api.uifw.ui.core.container.containers.ListContainer;
import com.edgn.api.uifw.ui.template.TemplateSettings;
import com.edgn.api.uifw.ui.utils.ColorUtils;
import com.edgn.api.uifw.ui.core.item.items.FeatureItem;
import com.edgn.core.minecraft.ui.screens.modules.ModulesScreen;
import com.edgn.api.uifw.ui.core.container.BaseContainer;
import com.edgn.api.uifw.ui.core.container.containers.FlexContainer;
import com.edgn.api.uifw.ui.css.StyleKey;
import com.edgn.api.uifw.ui.template.BaseTemplate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class MainScreen extends BaseTemplate {

    public MainScreen() {
        super(Text.of("Wynncraft Explorer"), null);
    }

    private ListContainer featureList;

    @Override
    protected TemplateSettings templateSettings() {
        return new TemplateSettings().setFooter(true).setHeader(true);
    }

    @Override
    protected BaseContainer createHeader() {
        return new FlexContainer(uiSystem, 0, 0, width, headerHeight)
                .addClass(StyleKey.FLEX_ROW, StyleKey.JUSTIFY_CENTER, StyleKey.ITEMS_CENTER, StyleKey.SHADOW_MD, StyleKey.P_3)
                .setRenderBackground(true)
                .setBackgroundColor(ColorUtils.setOpacity(0xFF0F1218, 0.85f));
    }

    @Override
    protected BaseContainer createContent() {
        FlexContainer content = new FlexContainer(uiSystem, 0, 0, width, contentHeight)
                .addClass(StyleKey.FLEX_COLUMN, StyleKey.JUSTIFY_CENTER, StyleKey.ITEMS_CENTER, StyleKey.P_4, StyleKey.GAP_4)
                .setRenderBackground(true)
                .setBackgroundColor(ColorUtils.setOpacity(0xFF0A0C11, 0.60f));

        featureList = new ListContainer(uiSystem, 0, 0, Math.min(680, width - 40), contentHeight - 40)
                .setOrientation(ListContainer.Orientation.VERTICAL)
                .setScrollable(true)
                .setScrollAxes(true, false)
                .setShowScrollbars(false)
                .setScrollStep(3)
                .addClass(StyleKey.ROUNDED_XL, StyleKey.SHADOW_LG, StyleKey.P_5, StyleKey.GAP_4)
                .setRenderBackground(true)
                .setBackgroundColor(0xFF141925);


        addFeatures(featureList);
        content.addChild(featureList);
        return content;
    }

    @Override
    protected BaseContainer createFooter() {
        return new FlexContainer(uiSystem, 0, 0, width, footerHeight)
                .addClass(StyleKey.FLEX_ROW, StyleKey.JUSTIFY_CENTER, StyleKey.ITEMS_CENTER, StyleKey.SHADOW_MD, StyleKey.P_3)
                .setRenderBackground(true)
                .setBackgroundColor(ColorUtils.setOpacity(0xFF0F1218, 0.85f));
    }

    private void addFeatures(ListContainer list) {
        list.addChild(new FeatureItem(uiSystem, 0, 0, 0, 76)
                .withTitle(new TextComponent("Modules manager").color(0xFFFFFFFF))
                .withDescription(new TextComponent("Modules list of the mod.").color(0xFFB9C0C8))
                .withVersion(new TextComponent("v1.0.0").color(0xFF9AA4AE))
                .setIcon(Identifier.of(Main.MOD_ID, "textures/ui/module.png"))
                .accent(0xFF5B8CFF)
                .glass(true)
                .iconBadge(true)
                .addClass(StyleKey.ROUNDED_LG, StyleKey.SHADOW_SM, StyleKey.HOVER_SCALE, StyleKey.HOVER_BRIGHTEN)
                .onClick(() -> navigateTo(new ModulesScreen(this))));

        for (FeatureEntry e : Main.getCoreApi().ui().all()) {
            int h = 76;
            FeatureItem item = new FeatureItem(uiSystem, 0, 0, 0, h)
                    .withTitle(new TextComponent(e.title != null ? e.title : e.id).color(0xFFFFFFFF))
                    .withDescription(new TextComponent(e.description != null ? e.description : "").color(0xFFB9C0C8))
                    .withVersion(new TextComponent(e.version != null ? e.version : "").color(0xFF9AA4AE))
                    .setIcon(e.icon != null ? e.icon : Identifier.of(Main.MOD_ID, "icon.png"))
                    .accent(0xFF7ED957)
                    .glass(true)
                    .iconBadge(true)
                    .addClass(StyleKey.ROUNDED_LG, StyleKey.SHADOW_SM, StyleKey.HOVER_SCALE, StyleKey.HOVER_BRIGHTEN);

            if (e.openScreen != null) item.onClick(() -> navigateTo(e.openScreen.get()));
            list.addChild(item);
        }
    }

    private void navigateTo(Screen screen) {
        try {
            MinecraftClient.getInstance().setScreen(screen);
        } catch (Exception e) {
            Main.LOGGER.error("Error while navigating: {} ", e.getMessage());
        }
    }

    @Override
    protected void resizeEvent() {
        featureList.setHeight(contentHeight - 40); // bugged gotta figure out why later I shouldn't have to do this usually
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}