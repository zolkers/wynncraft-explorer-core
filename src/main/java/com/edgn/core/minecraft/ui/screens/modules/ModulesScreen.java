package com.edgn.core.minecraft.ui.screens.modules;

import com.edgn.api.uifw.ui.core.components.TextComponent;
import com.edgn.api.uifw.ui.core.container.BaseContainer;
import com.edgn.api.uifw.ui.core.container.containers.FlexContainer;
import com.edgn.api.uifw.ui.core.container.containers.GridContainer;
import com.edgn.api.uifw.ui.core.container.containers.ListContainer;
import com.edgn.api.uifw.ui.core.item.items.ButtonItem;
import com.edgn.api.uifw.ui.core.item.items.LabelItem;
import com.edgn.api.uifw.ui.core.item.items.TextFieldItem;
import com.edgn.api.uifw.ui.css.StyleKey;
import com.edgn.api.uifw.ui.template.BaseTemplate;
import com.edgn.api.uifw.ui.template.TemplateSettings;
import com.edgn.api.uifw.ui.utils.ColorUtils;
import com.edgn.core.config.configs.ModulesScreenConfig;
import com.edgn.api.uifw.ui.core.item.items.ModuleCardItem;
import com.edgn.core.minecraft.ui.screens.modules.settings.ModuleSettingsScreen;
import com.edgn.core.module.basic.AbstractModule;
import com.edgn.core.module.basic.ModuleCategory;
import com.edgn.core.module.basic.ModuleInfo;
import com.edgn.core.module.basic.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.*;

public final class ModulesScreen extends BaseTemplate {

    private static final class Theme {
        private Theme() { /* utility class */}
        public static final int BACKGROUND   = ColorUtils.setOpacity(0xFF0F1115, 0.80f); // content (principal)
        public static final int SURFACE      = 0xFF111113; // header/footer (principal)
        public static final int FOREGROUND   = 0xFFE5E7EB;
        public static final int INPUT        = 0xFF18181B;
        public static final int CARD         = 0xFF111113;
        public static final int SECONDARY    = 0xFF27272A;
        public static final int SECONDARY_FG = 0xFFE5E7EB;
        public static final int PRIMARY      = 0xFFA78BFA;
        public static final int PRIMARY_FG   = 0xFF0B0B0F;
        public static final int BORDER       = 0xFF2A2A2E;
    }

    private String searchQuery;

    private static final int PADDING   = 16;
    private static final int GAP       = 16;
    private static final int SIDEBAR_W = 240;

    private FlexContainer rootContent;
    private ListContainer sidebar;
    private FlexContainer rightCol;
    private TextFieldItem searchField;
    private GridContainer grid;

    private final ModulesScreenConfig config;
    private ModuleCategory selectedCategory;

    private final Screen prev;

    public ModulesScreen(Screen prev) {
        super(Text.of("Modules Manager"), null);
        this.prev = prev;

        this.config = ModulesScreenConfig.load();
        this.searchQuery = Optional.ofNullable(config.getLastSearchQuery()).orElse("");

        if (config.getLastSelectedCategory() != null) {
            try { this.selectedCategory = ModuleCategory.valueOf(config.getLastSelectedCategory()); }
            catch (IllegalArgumentException ignored) { this.selectedCategory = null; }
        }

        AbstractModule.initSaveManager();
        AbstractModule.loadAllModules();
    }

    @Override
    protected TemplateSettings templateSettings() {
        return new TemplateSettings().setHeader(true).setFooter(true);
    }

    @Override
    protected BaseContainer createHeader() {
        FlexContainer header = new FlexContainer(uiSystem, 0, 0, width, headerHeight)
                .addClass(StyleKey.FLEX_ROW, StyleKey.JUSTIFY_START, StyleKey.ITEMS_CENTER, StyleKey.SHADOW_MD, StyleKey.PB_1, StyleKey.PT_4);

        header.setBackgroundColor(Theme.SURFACE).setRenderBackground(true);

        header.addChild(new LabelItem(uiSystem, 0, 0,
                new TextComponent("🌙 Modules Manager").color(Theme.FOREGROUND)).align(TextComponent.TextAlign.CENTER).addClass(StyleKey.FLEX_BASIS_100));
        return header;
    }

    @Override
    protected BaseContainer createContent() {
        rootContent = new FlexContainer(uiSystem, 0, 0, width, contentHeight)
                .setBackgroundColor(Theme.BACKGROUND)
                .setRenderBackground(true)
                .addClass(StyleKey.P_0);

        sidebar = new ListContainer(uiSystem, 0, 0, SIDEBAR_W, contentHeight)
                .addClass(StyleKey.ROUNDED_LG, StyleKey.SHADOW_MD, StyleKey.P_2, StyleKey.GAP_2, StyleKey.FLEX_GROW_0)
                .setScrollable(true).setScrollAxes(true, false).setShowScrollbars(false).setScrollStep(3);

        buildSidebar();

        rightCol = new FlexContainer(uiSystem, 0, 0, 100, contentHeight)
                .addClass(StyleKey.FLEX_COLUMN, StyleKey.GAP_3, StyleKey.P_2, StyleKey.FLEX_GROW_2);

        searchField = new TextFieldItem(uiSystem, 0, 0, 420, 28)
                .setPlaceholder(new TextComponent("Type the name of a module...")
                        .pulse()
                        .color(ColorUtils.NamedColor.GRAY.toInt())
                        .italic())
                .addClass(StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM)
                .setBackgroundColor(Theme.INPUT)
                .textPulse()
                .textColor(ColorUtils.NamedColor.WHITE.toInt());

        searchField
                .setText(searchQuery)
                .onChange(text -> {
                    this.searchQuery = text != null ? text : "";
                    rebuildGrid();
                });

        rightCol.addChild(searchField);

        grid = new GridContainer(uiSystem, 0, 36, 100, contentHeight - 60)
                .addClass(StyleKey.GAP_5, StyleKey.SHADOW_SM, StyleKey.ROUNDED_MD);

        grid.setScrollable(true).setShowScrollbars(true).setScrollAxes(true, false).setScrollStep(20);
        rightCol.addChild(grid);

        rootContent.addChild(sidebar);
        rootContent.addChild(rightCol);

        layoutContent();
        rebuildGrid();
        return rootContent;
    }

    @Override
    protected BaseContainer createFooter() {
        return new FlexContainer(uiSystem, 0, contentHeight, width, footerHeight)
                .setBackgroundColor(Theme.SURFACE).setRenderBackground(true)
                .addClass(StyleKey.FLEX_ROW, StyleKey.JUSTIFY_START, StyleKey.ITEMS_CENTER, StyleKey.SHADOW_MD);
    }

    private void layoutContent() {
        if (rootContent == null) return;

        int usableX = PADDING;
        int usableY = PADDING;
        int usableW = Math.max(0, width - 2*PADDING);
        int usableH = Math.max(0, contentHeight);

        sidebar.setX(usableX);
        sidebar.setY(usableY);
        sidebar.setWidth(SIDEBAR_W);
        sidebar.setHeight(usableH);
        sidebar.markConstraintsDirty();

        int rightX = usableX + SIDEBAR_W + GAP;
        int rightW = Math.max(0, usableW - SIDEBAR_W - GAP);
        rightCol.setX(rightX);
        rightCol.setY(usableY);
        rightCol.setWidth(rightW);
        rightCol.setHeight(usableH);
        rightCol.markConstraintsDirty();

        int searchH = 28;
        searchField.setX(0);
        searchField.setY(0);
        searchField.setWidth(Math.min(420, rightW));
        searchField.setHeight(searchH);
        searchField.markConstraintsDirty();

        int gridY = searchH + 8;
        int gridH = Math.max(0, usableH - gridY);
        grid.setX(0);
        grid.setY(gridY);
        grid.setWidth(rightW);
        grid.setHeight(gridH);
        grid.markConstraintsDirty();

        int columns = Math.max(1, rightW / 340);
        grid.setColumns(columns);
    }

    private void buildSidebar() {
        sidebar.clearChildren();

        int total = ModuleManager.getInstance().getModules().size();
        sidebar.addChild(categoryButton(null, "All (" + total + ")"));

        for (ModuleCategory c : ModuleCategory.values()) {
            int count = countCategory(c);
            if (count == 0) continue;
            String label = emoji(c) + " " + c.getDisplayName() + " (" + count + ")";
            sidebar.addChild(categoryButton(c, label));
        }
    }

    private ButtonItem categoryButton(ModuleCategory cat, String text) {
        boolean selected = Objects.equals(cat, selectedCategory);

        int btnW = Math.max(0, (sidebar.getViewportWidth() > 0 ? sidebar.getViewportWidth() : SIDEBAR_W) - 8);
        TextComponent tc = new TextComponent(text);
        ButtonItem btn = new ButtonItem(uiSystem, 0, 0, btnW, 30, tc)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.HOVER_BRIGHTEN)
                .onClick(() -> {
                    selectedCategory = cat;
                    saveConfig();
                    rebuildGrid();
                    buildSidebar();
                });

        if (selected) {
            btn.backgroundColor(Theme.PRIMARY);
            tc.color(Theme.PRIMARY_FG);
        } else {
            btn.backgroundColor(Theme.SECONDARY);
            tc.color(Theme.SECONDARY_FG);
        }
        return btn;
    }

    private void rebuildGrid() {
        if (grid == null) return;
        grid.clearChildren();

        List<AbstractModule> modules = new ArrayList<>(ModuleManager.getInstance().getModules());
        if (selectedCategory != null) {
            modules = modules.stream().filter(m -> {
                ModuleInfo i = m.getClass().getAnnotation(ModuleInfo.class);
                return i != null && i.category() == selectedCategory;
            }).toList();
        }

        String q = (searchQuery == null ? "" : searchQuery).toLowerCase(Locale.ROOT).trim();
        if (!q.isEmpty()) {
            modules = modules.stream().filter(m -> {
                ModuleInfo i = m.getClass().getAnnotation(ModuleInfo.class);
                String name = m.getName().toLowerCase(Locale.ROOT);
                String desc = (i != null && i.description() != null) ? i.description().toLowerCase(Locale.ROOT) : "";
                return name.contains(q) || desc.contains(q);
            }).toList();
        }

        for (AbstractModule m : modules) {
            ModuleCardItem card = new ModuleCardItem(uiSystem, 0, 0, 300, 140, m)
                    .addClass(StyleKey.ROUNDED_LG, StyleKey.SHADOW_MD, StyleKey.P_3, StyleKey.HOVER_BRIGHTEN)
                    .setBackgroundColor(Theme.CARD)
                    .onOpenSettings(() -> openModuleSettings(m));

            grid.addChild(card);
        }
    }

    private void openModuleSettings(AbstractModule module) {
        if (module == null) return;
        MinecraftClient.getInstance().setScreen(
                new ModuleSettingsScreen(this, module, true)
        );
    }

    private void saveConfig() {
        config.setDarkMode(true);
        config.setAnimationsEnabled(false);
        config.setLastSearchQuery(searchQuery);
        config.setLastSelectedCategory(selectedCategory != null ? selectedCategory.name() : null);
        config.save();
    }

    private int countCategory(ModuleCategory c) {
        return (int) ModuleManager.getInstance().getModules().stream().filter(m -> {
            ModuleInfo i = m.getClass().getAnnotation(ModuleInfo.class);
            return i != null && i.category() == c;
        }).count();
    }

    private static String emoji(ModuleCategory c) {
        return switch (c) {
            case MOVEMENT -> "\uD83D\uDE80";
            case UTILITY -> "🔧";
            case CHAT -> "💬";
            case LOOTRUN -> "💎";
            case RENDER -> "🎨";
            case RAID -> "⚔️";
            case FARMING -> "\uD83D\uDE9C";
        };
    }

    @Override
    protected void resizeEvent() {
        layoutContent();
        if (grid != null) {
            int rightW = rightCol != null ? rightCol.getCalculatedWidth() : (width - 2*PADDING - SIDEBAR_W - GAP);
            int columns = Math.max(1, rightW / 340);
            grid.setColumns(columns);
            grid.markConstraintsDirty();
        }
    }

    @Override
    public void close() {
        saveConfig();
        AbstractModule.saveAllModules();
        MinecraftClient.getInstance().setScreen(prev);
    }
}
