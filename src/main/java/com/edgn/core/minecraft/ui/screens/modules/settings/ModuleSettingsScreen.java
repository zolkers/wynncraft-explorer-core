package com.edgn.core.minecraft.ui.screens.modules.settings;

import com.edgn.Main;
import com.edgn.api.uifw.ui.core.components.TextComponent;
import com.edgn.api.uifw.ui.core.container.BaseContainer;
import com.edgn.api.uifw.ui.core.container.containers.FlexContainer;
import com.edgn.api.uifw.ui.core.container.containers.ListContainer;
import com.edgn.api.uifw.ui.core.item.BaseItem;
import com.edgn.api.uifw.ui.core.item.items.ButtonItem;
import com.edgn.api.uifw.ui.core.item.items.LabelItem;
import com.edgn.core.minecraft.ui.screens.modules.settings.components.settings.*;
import com.edgn.core.minecraft.ui.screens.modules.settings.components.settings.ModuleKeybindItem;
import com.edgn.api.uifw.ui.css.StyleKey;
import com.edgn.api.uifw.ui.utils.ColorUtils;
import com.edgn.api.uifw.ui.template.BaseTemplate;
import com.edgn.api.uifw.ui.template.TemplateSettings;
import com.edgn.core.module.basic.AbstractModule;
import com.edgn.core.module.basic.ISettingsModule;
import com.edgn.core.module.basic.ModuleInfo;
import com.edgn.core.module.settings.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ModuleSettingsScreen extends BaseTemplate implements ISettingsScreen {

    private static final class Theme {
        static final int BG_MAIN = 0xFF0F1115;
        static final int SURFACE = 0xFF111113;
        static final int FOREGROUND = 0xFFE5E7EB;
        static final int INPUT = 0xFF18181B;
        static final int CARD = 0xFF111113;
        static final int SECONDARY = 0xFF27272A;
        static final int SECONDARY_FG = 0xFFE5E7EB;
        static final int PRIMARY = 0xFFA78BFA;
        static final int PRIMARY_FG = 0xFF0B0B0F;
        static final int BORDER = 0xFF2A2A2E;
    }

    private static final int PADDING = 16;
    private static final int GAP = 16;
    private static final int SIDEBAR_W = 260;
    private static final int ROW_H = 64;

    private final Screen prev;
    private final ISettingsModule settingsModule;
    private final AbstractModule module;
    private final boolean darkMode;
    private final List<SettingsGroup> groups = new ArrayList<>();

    private SettingsGroup selectedGroup;

    private FlexContainer rootContent;
    private ListContainer sidebar;
    private FlexContainer rightCol;
    private ListContainer settingsList;
    private ButtonItem btnBack;
    private ButtonItem btnSave;
    private ButtonItem btnReset;
    private LabelItem groupTitle;

    public ModuleSettingsScreen(Screen prev, ISettingsModule settingsModule, boolean darkMode) {
        super(Text.of((darkMode ? "🌙 " : "🍉 ") + ((AbstractModule) settingsModule).getName() + " - Settings"), prev);
        this.prev = prev;
        this.settingsModule = settingsModule;
        this.module = (AbstractModule) settingsModule;
        this.darkMode = darkMode;
        if (settingsModule.getSettingsGroups() != null) {
            this.groups.addAll(settingsModule.getSettingsGroups());
        }
        if (!groups.isEmpty()) {
            this.selectedGroup = groups.getFirst();
        }
    }

    @Override
    protected TemplateSettings templateSettings() {
        return new TemplateSettings()
                .setHeader(true)
                .setFooter(true);
    }

    @Override
    protected BaseContainer createHeader() {
        FlexContainer header = new FlexContainer(uiSystem, 0, 0, width, headerHeight)
                .addClass(StyleKey.FLEX_ROW, StyleKey.JUSTIFY_BETWEEN, StyleKey.ITEMS_CENTER,
                        StyleKey.PR_3, StyleKey.PT_2, StyleKey.SHADOW_MD);
        header.setBackgroundColor(Theme.SURFACE).setRenderBackground(true);
        String icon = darkMode ? "🌙" : "🍉";
        LabelItem title = new LabelItem(uiSystem, 0, 0,
                new TextComponent(icon + " " + module.getName()).color(Theme.FOREGROUND))
                .addClass(StyleKey.FLEX_BASIS_40);
        createActionButtons(icon);
        FlexContainer actions = new FlexContainer(uiSystem, 0, 0, 200, 24)
                .addClass(StyleKey.FLEX_ROW, StyleKey.GAP_2, StyleKey.JUSTIFY_END, StyleKey.ITEMS_CENTER);
        actions.addChild(btnReset).addChild(btnSave).addChild(btnBack);
        header.addChild(title);
        header.addChild(actions.addClass(StyleKey.FLEX_BASIS_40));
        return header;
    }

    private void createActionButtons(String icon) {
        btnBack = new ButtonItem(uiSystem, 0, 0, 88, 24,
                new TextComponent(icon + " Back").color(Theme.PRIMARY_FG))
                .backgroundColor(Theme.PRIMARY)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_25)
                .onClick(this::close);
        btnReset = new ButtonItem(uiSystem, 0, 0, 88, 24,
                new TextComponent("Reset").color(Theme.SECONDARY_FG))
                .backgroundColor(Theme.SECONDARY)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_25)
                .onClick(this::resetSettings);
        btnSave = new ButtonItem(uiSystem, 0, 0, 88, 24,
                new TextComponent("Save").color(Theme.PRIMARY_FG))
                .backgroundColor(Theme.PRIMARY)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_25)
                .onClick(this::saveSettings);
    }

    @Override
    protected BaseContainer createContent() {
        rootContent = new FlexContainer(uiSystem, 0, 0, width, contentHeight)
                .setBackgroundColor(ColorUtils.setOpacity(Theme.BG_MAIN, 0.90f))
                .setRenderBackground(true)
                .addClass(StyleKey.FLEX_ROW, StyleKey.GAP_4, StyleKey.P_3);

        createSidebar();
        createRightColumn();

        rootContent.addChild(sidebar);
        rootContent.addChild(rightCol);

        layoutContent();
        rebuildSettingsList();
        return rootContent;
    }

    private void createSidebar() {
        sidebar = new ListContainer(uiSystem, 0, 0, SIDEBAR_W, contentHeight)
                .addClass(StyleKey.P_2, StyleKey.GAP_2, StyleKey.ROUNDED_LG, StyleKey.SHADOW_MD)
                .setScrollable(true)
                .setScrollAxes(true, false)
                .setShowScrollbars(false)
                .setScrollStep(10);
        sidebar.setBackgroundColor(Theme.CARD).setRenderBackground(true);
        sidebar.setBackgroundColor(Theme.CARD).setRenderBackground(true);
        buildSidebar();
    }

    private ListContainer newSettingsList() {
        return new ListContainer(uiSystem, 0, 0, 100, contentHeight - 40)
                .addClass(StyleKey.GAP_2, StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.P_1, StyleKey.PR_3)
                .setScrollable(true)
                .setShowScrollbars(true)
                .setScrollAxes(true, false)
                .setScrollStep(18)
                .setBackgroundColor(Theme.CARD)
                .setRenderBackground(true);
    }

    private void createRightColumn() {
        rightCol = new FlexContainer(uiSystem, 0, 0, 100, contentHeight)
                .addClass(StyleKey.FLEX_COLUMN, StyleKey.GAP_3, StyleKey.FLEX_GROW_2);

        String groupName = (selectedGroup != null ? selectedGroup.getName() : "No group");
        groupTitle = new LabelItem(uiSystem, 0, 0,
                new TextComponent(groupName).color(Theme.FOREGROUND));

        settingsList = newSettingsList();

        rightCol.addChild(groupTitle);
        rightCol.addChild(settingsList);
    }

    @Override
    protected BaseContainer createFooter() {
        FlexContainer footer = new FlexContainer(uiSystem, 0, 0, width, footerHeight)
                .addClass(StyleKey.FLEX_ROW, StyleKey.JUSTIFY_CENTER, StyleKey.ITEMS_CENTER, StyleKey.SHADOW_MD);
        footer.setBackgroundColor(Theme.SURFACE).setRenderBackground(true);
        String status = module.isEnabled() ? "✅ ACTIVATED" : "❌ DEACTIVATED";
        footer.addChild(new LabelItem(uiSystem, 0, 0,
                new TextComponent(status).color(Theme.FOREGROUND))
                .align(TextComponent.TextAlign.CENTER));
        return footer;
    }

    //maybe gotta rework this as it's not perfectly coded but it's not too bad and quite safe to do
    private void layoutContent() {
        if (rootContent == null) return;
        int usableX = PADDING;
        int usableY = PADDING;
        int usableW = Math.max(0, width - 2 * PADDING);
        int usableH = Math.max(0, contentHeight - PADDING);

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

        settingsList.setX(0);
        settingsList.setY(28);
        settingsList.setWidth(rightW);
        settingsList.setHeight(Math.max(0, usableH - 28));
        settingsList.markConstraintsDirty();
    }

    private void buildSidebar() {
        sidebar.clearChildren();
        FlexContainer modInfo = createModuleInfoCard();
        sidebar.addChild(modInfo);
        sidebar.addChild(new LabelItem(uiSystem, 0, 0,
                new TextComponent("Groups").color(Theme.FOREGROUND))
                .addClass(StyleKey.MT_2, StyleKey.MB_1));
        createGroupTabs();
    }

    private FlexContainer createModuleInfoCard() {
        FlexContainer modInfo = new FlexContainer(uiSystem, 0, 0, SIDEBAR_W - 16, 84)
                .addClass(StyleKey.FLEX_COLUMN, StyleKey.GAP_1, StyleKey.P_2,
                        StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM);
        modInfo.setBackgroundColor(Theme.SECONDARY).setRenderBackground(true);
        ModuleInfo info = module.getClass().getAnnotation(ModuleInfo.class);
        String icon = darkMode ? "🌙" : "🍉";
        modInfo.addChild(new LabelItem(uiSystem, 0, 0,
                new TextComponent(icon + " " + module.getName()).color(Theme.SECONDARY_FG)));
        if (info != null) {
            modInfo.addChild(new LabelItem(uiSystem, 0, 0,
                    new TextComponent("v" + info.version()).color(Theme.SECONDARY_FG)));
            modInfo.addChild(new LabelItem(uiSystem, 0, 0,
                    new TextComponent(info.category().getDisplayName()).color(Theme.SECONDARY_FG)));
        }
        ModuleKeybindItem keybind = new ModuleKeybindItem(uiSystem, 0, 0, SIDEBAR_W - 32, 24, module, this)
                .addClass(StyleKey.MT_1);
        modInfo.addChild(keybind);
        return modInfo;
    }

    private void createGroupTabs() {
        for (SettingsGroup group : groups) {
            boolean selected = Objects.equals(group, selectedGroup);
            ButtonItem tab = new ButtonItem(uiSystem, 0, 0, SIDEBAR_W - 16, 30,
                    new TextComponent((selected ? "▶ " : "") + group.getName())
                            .color(selected ? Theme.PRIMARY_FG : Theme.SECONDARY_FG))
                    .addClass(StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.HOVER_BRIGHTEN)
                    .backgroundColor(selected ? Theme.PRIMARY : Theme.SECONDARY)
                    .onClick(() -> selectGroup(group));
            sidebar.addChild(tab);
        }
    }

    private void selectGroup(SettingsGroup group) {
        if (selectedGroup == group) return;
        selectedGroup = group;

        uiSystem.getEventManager().resetAllElements();

        if (groupTitle != null) {
            groupTitle.setText(group.getName());
        }

        settingsList = newSettingsList();
        rightCol.clearChildren();
        rightCol.addChild(groupTitle);
        rightCol.addChild(settingsList);

        rebuildSettingsList();
        buildSidebar();

        layoutContent();
        settingsList.markConstraintsDirty();
        rightCol.markConstraintsDirty();
        sidebar.markConstraintsDirty();
        rootContent.markConstraintsDirty();
    }

    private void rebuildSettingsList() {
        settingsList.clearChildren();
        if (selectedGroup == null) return;
        for (Setting<?> setting : selectedGroup.getSettings()) {
            if (setting.isVisible()) {
                settingsList.addChild(buildSettingRow(setting));
            }
        }
        settingsList.markConstraintsDirty();
    }

    private BaseContainer buildSettingRow(Setting<?> setting) {
        FlexContainer row = new FlexContainer(uiSystem, 0, 0, 1, ROW_H)
                .addClass(StyleKey.FLEX_ROW, StyleKey.JUSTIFY_BETWEEN, StyleKey.ITEMS_CENTER, StyleKey.FLEX_GROW_2,
                        StyleKey.PR_3, StyleKey.PT_2, StyleKey.ROUNDED_MD, StyleKey.SHADOW_MD,
                        StyleKey.HOVER_BRIGHTEN);
        row.setBackgroundColor(Theme.CARD).setRenderBackground(true);
        FlexContainer left = createSettingDescription(setting);
        FlexContainer right = createSettingControl(setting);
        row.addChild(left);
        row.addChild(right);
        return row;
    }

    private FlexContainer createSettingDescription(Setting<?> setting) {
        FlexContainer left = new FlexContainer(uiSystem, 0, 0, 100, ROW_H)
                .addClass(StyleKey.FLEX_COLUMN, StyleKey.JUSTIFY_CENTER, StyleKey.GAP_1, StyleKey.FLEX_GROW_2);
        left.addChild(new LabelItem(uiSystem, 0, 0,
                new TextComponent(setting.getName()).color(Theme.FOREGROUND)));
        String desc = setting.getDescription();
        if (desc != null && !desc.isEmpty()) {
            left.addChild(new LabelItem(uiSystem, 0, 0,
                    new TextComponent(desc).color(ColorUtils.NamedColor.GRAY.toInt()).italic()));
        }
        return left;
    }

    private FlexContainer createSettingControl(Setting<?> setting) {
        BaseItem control = createItemForSetting(setting);
        control.setWidth(240);
        control.setHeight(28);
        FlexContainer right = new FlexContainer(uiSystem, 0, 0, 260, ROW_H)
                .addClass(StyleKey.FLEX_ROW, StyleKey.JUSTIFY_END, StyleKey.ITEMS_CENTER);
        right.addChild(control);
        return right;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private BaseItem createItemForSetting(Setting<?> setting) {
        return switch (setting) {
            case BooleanSetting bs ->
                    new BooleanSettingItem(uiSystem, 0, 0, 60, 28, bs, this);
            case DoubleSetting ds ->
                    new DoubleSettingItem(uiSystem, 0, 0, 240, 28, ds, this);
            case EnumSetting<?> es ->
                    new EnumSettingItem(uiSystem, 0, 0, 240, 28,  es, this);
            case ListSetting<?> ls ->
                    new ListSettingItem(uiSystem, 0, 0, 240, 28,  ls, this);
            case ColorSetting cs ->
                    new ColorSettingItem(uiSystem, 0, 0, 240, 28, cs, this);
            case StringSetting ss ->
                    new StringSettingItem(uiSystem, 0, 0, 260, 28, ss, this);
            default ->
                    new ButtonItem(uiSystem, 0, 0, 160, 24,
                            new TextComponent("Unsupported").color(0xFFAAAAAA))
                            .backgroundColor(Theme.SECONDARY)
                            .setEnabled(false);
        };
    }

    private void saveSettings() {
        settingsModule.onSettingsChanged();
        module.save();
        String message = (darkMode ? "🌙 " : "🍉 ") + "Settings saved for " + module.getName();
        Main.OVERLAY_MANAGER.getLoggerOverlay().success(message, true);
    }

    private void resetSettings() {
        if (selectedGroup != null) {
            for (Setting<?> s : selectedGroup.getSettings()) {
                s.reset();
            }
            rebuildSettingsList();
            String message = (darkMode ? "🌙 " : "🍉 ") + "Settings reset for " + selectedGroup.getName();
            Main.OVERLAY_MANAGER.getLoggerOverlay().info(message, false);
        }
    }

    @Override
    public void close() {
        saveSettings();
        MinecraftClient.getInstance().setScreen(prev);
    }

    @Override
    public int getBgPrimary() {
        return darkMode ? 0xFF1A1A1A : 0xFFF8F9FA;
    }

    @Override
    public int getBgSecondary() {
        return darkMode ? 0xFF2D2D30 : 0xFFE8F8E8;
    }

    @Override
    public int getAccentColor() {
        return darkMode ? 0xFF007ACC : 0xFF2ECC71;
    }

    @Override
    public int getAccentHoverColor() {
        return darkMode ? 0xFF005A9E : 0xFF27AE60;
    }

    @Override
    public int getTextPrimary() {
        return darkMode ? 0xFFFFFFFF : 0xFF2C3E50;
    }

    @Override
    public int getTextSecondary() {
        return darkMode ? 0xFFCCCCCC : 0xFF666666;
    }

    @Override
    public int getTextMuted() {
        return darkMode ? 0xFF999999 : 0xFF888888;
    }

    @Override
    public boolean isDarkMode() {
        return darkMode;
    }

    @Override
    protected void resizeEvent() {
        layoutContent();
    }
}
