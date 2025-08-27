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

import java.util.ArrayList;
import java.util.List;

public final class MainScreen extends BaseTemplate {

    public static class Feature {
        public final String id;
        public final String title;
        public final String description;
        public final String version;
        public final Identifier icon;
        public final int accent;
        public final Runnable action;

        public Feature(String id, String title, String description, String version,
                       Identifier icon, int accent, Runnable action) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.version = version;
            this.icon = icon;
            this.accent = accent;
            this.action = action;
        }
    }

    private ListContainer<Feature> featureList;

    public MainScreen() {
        super(Text.of("Wynncraft Explorer"), null);
    }

    @Override
    protected TemplateSettings templateSettings() {
        return new TemplateSettings().setFooter(true).setHeader(true);
    }

    @Override
    protected void initialise() {
        debug();
    }

    @Override
    protected BaseContainer createHeader() {
        return new FlexContainer(uiSystem, 0, 0, width, headerHeight)
                .addClass(StyleKey.FLEX_ROW, StyleKey.JUSTIFY_CENTER, StyleKey.ITEMS_CENTER,
                        StyleKey.SHADOW_MD, StyleKey.P_3)
                .setRenderBackground(true)
                .setBackgroundColor(ColorUtils.setOpacity(0xFF0F1218, 0.85f));
    }

    @Override
    protected BaseContainer createContent() {
        featureList = new ListContainer<Feature>(uiSystem, 0, 0, this.width, contentHeight)
                .setOrientation(ListContainer.Orientation.VERTICAL)
                .setFixedItemHeight(76)
                .setScrollable(true)
                .setScrollAxes(true, false)
                .setShowScrollbars(false)
                .setScrollStep(3)
                .setSelectionEnabled(false)
                .setItemFactory(this::createFeatureItem)
                .onItemClick(this::onFeatureClick)
                .addClass(StyleKey.ROUNDED_XL, StyleKey.SHADOW_LG, StyleKey.P_5, StyleKey.GAP_4)
                .setRenderBackground(true)
                .setBackgroundColor(ColorUtils.setOpacity(0xFF141925, 0.80f));

        List<Feature> features = createFeatureList();
        featureList.setItems(features);

        return featureList;
    }

    @Override
    protected BaseContainer createFooter() {
        return new FlexContainer(uiSystem, 0, 0, width, footerHeight)
                .addClass(StyleKey.FLEX_ROW, StyleKey.JUSTIFY_CENTER, StyleKey.ITEMS_CENTER,
                        StyleKey.SHADOW_MD, StyleKey.P_3)
                .setRenderBackground(true)
                .setBackgroundColor(ColorUtils.setOpacity(0xFF0F1218, 0.85f));
    }

    private List<Feature> createFeatureList() {
        List<Feature> features = new ArrayList<>();

        features.add(new Feature(
                "modules",
                "Modules manager",
                "Modules list of the mod.",
                "v1.0.0",
                Identifier.of(Main.MOD_ID, "textures/ui/module.png"),
                0xFF5B8CFF,
                () -> navigateTo(new ModulesScreen(this))
        ));

        for (FeatureEntry e : Main.getCoreApi().ui().all()) {
            features.add(new Feature(
                    e.id,
                    e.title != null ? e.title : e.id,
                    e.description != null ? e.description : "",
                    e.version != null ? e.version : "",
                    e.icon != null ? e.icon : Identifier.of(Main.MOD_ID, "icon.png"),
                    0xFF7ED957, // Default accent color for API features
                    e.openScreen != null ? () -> navigateTo(e.openScreen.get()) : null
            ));
        }

        return features;
    }

    private FeatureItem createFeatureItem(Feature feature) {
        return new FeatureItem(uiSystem, 0, 0, 0, 76)
                .withTitle(new TextComponent(feature.title).color(0xFFFFFFFF))
                .withDescription(new TextComponent(feature.description).color(0xFFB9C0C8))
                .withVersion(new TextComponent(feature.version).color(0xFF9AA4AE))
                .setIcon(feature.icon)
                .accent(feature.accent)
                .glass(true)
                .iconBadge(true)
                .addClass(StyleKey.ROUNDED_LG, StyleKey.SHADOW_SM, StyleKey.HOVER_SCALE, StyleKey.HOVER_BRIGHTEN);

    }

    private void onFeatureClick(ListContainer.ListItem<Feature> listItem) {
        Feature feature = listItem.getData();
        if (feature.action != null) {
            try {
                feature.action.run();
            } catch (Exception e) {
                Main.LOGGER.error("Error executing feature action for '{}': {}", feature.id, e.getMessage());
            }
        }
    }

    @Override
    protected void resizeEvent() {
        super.reflowLayout();
        if (featureList != null) {
            featureList.setWidth(this.width);
            featureList.setHeight(contentHeight);
            featureList.markConstraintsDirty();
        }
    }

    private void navigateTo(Screen screen) {
        try {
            MinecraftClient.getInstance().setScreen(screen);
        } catch (Exception e) {
            Main.LOGGER.error("Error while navigating: {} ", e.getMessage());
        }
    }

    public void addFeature(Feature feature) {
        if (featureList != null) {
            featureList.addItem(feature);
        }
    }

    public void removeFeature(String id) {
        if (featureList != null) {
            List<Feature> items = featureList.getItems();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).id.equals(id)) {
                    featureList.removeItem(i);
                    break;
                }
            }
        }
    }

    public void refreshFeatures() {
        if (featureList != null) {
            List<Feature> features = createFeatureList();
            featureList.setItems(features);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}