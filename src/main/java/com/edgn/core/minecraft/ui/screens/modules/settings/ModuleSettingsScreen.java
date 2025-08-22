package com.edgn.core.minecraft.ui.screens.modules.settings;

import com.edgn.Main;
import com.edgn.uifw.utils.Render2D;
import com.edgn.core.module.basic.AbstractModule;
import com.edgn.core.module.basic.ISettingsModule;
import com.edgn.core.module.basic.ModuleInfo;
import com.edgn.core.module.settings.Setting;
import com.edgn.core.module.settings.SettingsGroup;
import com.edgn.uifw.templates.BaseScreen;
import com.edgn.core.minecraft.ui.screens.modules.settings.components.SettingComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModuleSettingsScreen extends BaseScreen implements ISettingsScreen{

    private static final int WATERMELON_GREEN = 0xFF2ECC71;
    private static final int WATERMELON_DARK_GREEN = 0xFF27AE60;
    private static final int WATERMELON_RED = 0xFFE74C3C;
    private static final int WATERMELON_PINK = 0xFFFF6B9D;
    private static final int WATERMELON_BLACK = 0xFF2C3E50;
    private static final int WATERMELON_WHITE = 0xFFF8F9FA;
    private static final int WATERMELON_LIGHT_GREEN = 0xFFE8F8E8;
    private static final int DARK_BG_PRIMARY = 0xFF1A1A1A;
    private static final int DARK_BG_SECONDARY = 0xFF2D2D30;
    private static final int DARK_BG_TERTIARY = 0xFF3E3E42;
    private static final int DARK_ACCENT = 0xFF007ACC;
    private static final int DARK_ACCENT_HOVER = 0xFF005A9E;
    private static final int DARK_TEXT_PRIMARY = 0xFFFFFFFF;
    private static final int DARK_TEXT_SECONDARY = 0xFFCCCCCC;
    private static final int DARK_TEXT_MUTED = 0xFF999999;
    private static final int DARK_SUCCESS = 0xFF4CAF50;
    private static final int DARK_ERROR = 0xFFF44336;

    private static final int SIDEBAR_WIDTH = 260;
    private static final int SETTING_HEIGHT = 60;
    private static final int SETTING_SPACING = 15;
    private static final int CONTROL_WIDTH = 180;
    private static final int CONTROL_HEIGHT = 24;
    private static final int CONTROL_PADDING_RIGHT = 20;

    private final Screen parentScreen;
    private final ISettingsModule settingsModule;
    private final AbstractModule module;
    private final boolean isDarkMode;

    private final List<SettingsGroupTab> groupTabs = new ArrayList<>();
    private SettingsGroup selectedGroup = null;
    private double scrollOffset = 0;
    private double maxScrollOffset = 0;

    private long animationTime = 0;
    private float sidebarSlideAnimation = 0.0f;

    private ModuleKeybindComponent moduleKeybindComponent;

    private final List<SettingComponent> settingComponents = new ArrayList<>();

    public ModuleSettingsScreen(Screen parentScreen, ISettingsModule settingsModule, boolean isDarkMode) {
        super(Text.literal((isDarkMode ? "🌙 " : "🍉 ") + ((AbstractModule) settingsModule).getName() + " - Settings"));
        this.parentScreen = parentScreen;
        this.settingsModule = settingsModule;
        this.module = (AbstractModule) settingsModule;
        this.isDarkMode = isDarkMode;

        initializeSettings();
    }

    public int getBgPrimary() { return isDarkMode ? DARK_BG_PRIMARY : WATERMELON_WHITE; }
    public int getBgSecondary() { return isDarkMode ? DARK_BG_SECONDARY : WATERMELON_LIGHT_GREEN; }
    public int getBgTertiary() { return isDarkMode ? DARK_BG_TERTIARY : 0xFFF8F8F8; }
    public int getAccentColor() { return isDarkMode ? DARK_ACCENT : WATERMELON_GREEN; }
    public int getAccentHoverColor() { return isDarkMode ? DARK_ACCENT_HOVER : WATERMELON_DARK_GREEN; }
    public int getTextPrimary() { return isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_BLACK; }
    public int getTextSecondary() { return isDarkMode ? DARK_TEXT_SECONDARY : 0xFF666666; }
    public int getTextMuted() { return isDarkMode ? DARK_TEXT_MUTED : 0xFF888888; }
    public int getSuccessColor() { return isDarkMode ? DARK_SUCCESS : WATERMELON_GREEN; }
    public int getErrorColor() { return isDarkMode ? DARK_ERROR : WATERMELON_RED; }

    private void initializeSettings() {
        groupTabs.clear();

        List<SettingsGroup> groups = settingsModule.getSettingsGroups();
        if (!groups.isEmpty()) {
            selectedGroup = groups.getFirst();
        }

        for (int i = 0; i < groups.size(); i++) {
            SettingsGroup group = groups.get(i);
            String emoji = getGroupEmoji(i);
            groupTabs.add(new SettingsGroupTab(group, emoji + " " + group.getName()));
        }
    }

    private void updateSettingComponents() {
        this.clearChildren();
        this.settingComponents.clear();

        moduleKeybindComponent = null;

        if (selectedGroup != null) {
            for (Setting<?> setting : selectedGroup.getSettings()) {
                if (setting.isVisible()) {
                    SettingComponent component = setting.createComponent(this, 0, 0, CONTROL_WIDTH, CONTROL_HEIGHT);
                    if (component != null) {
                        this.settingComponents.add(component);
                        this.addSelectableChild(component);
                    }
                }
            }
        }

        if (height > 0) {
            int totalHeight = settingComponents.size() * (SETTING_HEIGHT + SETTING_SPACING);
            int availableHeight = height - headerHeight - footerHeight - 80;
            maxScrollOffset = Math.max(0, totalHeight - availableHeight);
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
        }

        layoutSettingComponents();
    }

    private void layoutSettingComponents() {
        if (width <= 0 || height <= 0) return;

        int startY = headerHeight + 80 - (int) scrollOffset;
        int contentWidth = width - SIDEBAR_WIDTH - 60;
        int settingX = SIDEBAR_WIDTH + 30;

        for (SettingComponent component : settingComponents) {
            component.setX(settingX + contentWidth - CONTROL_WIDTH - CONTROL_PADDING_RIGHT);
            component.setY(startY + (SETTING_HEIGHT - CONTROL_HEIGHT) / 2);
            startY += SETTING_HEIGHT + SETTING_SPACING;
        }
    }

    private String getGroupEmoji(int index) {
        String[] emojis = isDarkMode ? new String[]{"🌙", "⭐", "🔧", "🎨", "⚙️", "💎", "🌟", "🚀"} : new String[]{"🍉", "🌱", "⚙️", "🎨", "🔧", "💎", "🌟", "🚀"};
        return emojis[index % emojis.length];
    }

    @Override
    protected void init() {
        super.init();
        createButtons();
        updateSettingComponents();
    }

    private void createButtons() {
        this.addDrawableChild(ButtonWidget.builder(Text.literal((isDarkMode ? "🌙" : "🍉") + " Back"), button -> this.close()).dimensions(20, 5, 100, 25).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("💾 Save"), button -> saveSettings()).dimensions(width - 130, 5, 110, 25).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🔄 Reset"), button -> resetSettings()).dimensions(width - 250, 5, 100, 25).build());
    }

    @Override
    protected void renderContent(DrawContext context, int mouseX, int mouseY, float delta) {
        animationTime = System.currentTimeMillis();
        updateAnimations(delta);

        renderContentBackground(context);
        renderSidebar(context, mouseX, mouseY);
        renderSettingsArea(context, mouseX, mouseY, delta);
        renderScrollbar(context);
    }

    private void renderSettingsArea(DrawContext context, int mouseX, int mouseY, float delta) {
        if (selectedGroup == null) return;

        String groupIcon = isDarkMode ? "🌙" : "🍉";
        context.drawText(textRenderer, groupIcon + " " + selectedGroup.getName(), SIDEBAR_WIDTH + 30, headerHeight + 30, getTextPrimary(), false);
        if (!selectedGroup.getDescription().isEmpty()) {
            context.drawText(textRenderer, selectedGroup.getDescription(), SIDEBAR_WIDTH + 30, headerHeight + 45, getTextSecondary(), false);
        }

        int clipStartY = headerHeight + 70;
        Render2D.enableClipping(context, SIDEBAR_WIDTH, clipStartY, width - SIDEBAR_WIDTH, height - footerHeight - clipStartY);

        for (SettingComponent component : settingComponents) {
            int rowY = component.getY() - (SETTING_HEIGHT - CONTROL_HEIGHT) / 2;

            Setting<?> setting = component.getSetting();
            int settingX = SIDEBAR_WIDTH + 30;
            int contentWidth = width - SIDEBAR_WIDTH - 60;

            boolean isRowHovered = Render2D.isPointInRect(mouseX, mouseY, settingX, rowY, contentWidth, SETTING_HEIGHT);
            int bgColor = isRowHovered ? getBgTertiary() : getBgSecondary();

            // Utilisation de Render2D pour les lignes de paramètres
            Render2D.drawPanel(context, settingX, rowY, contentWidth, SETTING_HEIGHT, 6, bgColor, getAccentColor(), 1);

            context.drawText(textRenderer, setting.getName(), settingX + 10, rowY + 12, getTextPrimary(), false);
            if (!setting.getDescription().isEmpty()) {
                context.drawText(textRenderer, setting.getDescription(), settingX + 10, rowY + 28, getTextMuted(), false);
            }

            component.render(context, mouseX, mouseY, delta);
        }

        Render2D.disableClipping(context);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (SettingsGroupTab tab : groupTabs) {
            int sidebarX = (int) (-SIDEBAR_WIDTH * (1.0f - sidebarSlideAnimation));
            if (Render2D.isPointInRect(mouseX, mouseY, sidebarX + 10, tab.y, SIDEBAR_WIDTH - 20, 35)) {
                if (selectedGroup != tab.group) {
                    selectedGroup = tab.group;
                    scrollOffset = 0;
                    updateSettingComponents();
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (maxScrollOffset > 0 && mouseX > SIDEBAR_WIDTH) {
            scrollOffset -= verticalAmount * 20;
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
            layoutSettingComponents();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.getFocused() == null && keyCode == 256) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void close() {
        saveSettings();
        assert this.client != null;
        this.client.setScreen(parentScreen);
    }

    private void saveSettings() {
        settingsModule.onSettingsChanged();
        module.save();
        Main.OVERLAY_MANAGER.getLoggerOverlay().success((isDarkMode ? "🌙 " : "🍉 ") + "Settings saved for " + module.getName(), true);
    }

    private void resetSettings() {
        if (selectedGroup != null) {
            for (Setting<?> setting : selectedGroup.getSettings()) {
                setting.reset();
            }

            updateSettingComponents();
            Main.OVERLAY_MANAGER.getLoggerOverlay().info((isDarkMode ? "🌙 " : "🍉 ") + "Settings reset for " + selectedGroup.getName(), false);
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        updateSettingComponents();
    }

    private static class SettingsGroupTab {
        final SettingsGroup group;
        final String name;
        int y;
        SettingsGroupTab(SettingsGroup group, String name) {
            this.group = group;
            this.name = name;
        }
    }

    protected void renderHeader(DrawContext context, int mouseX, int mouseY, float delta) {
        Render2D.drawGradient(context, 0, 0, width, headerHeight, getAccentColor(), getAccentHoverColor());
        renderHeaderDecorations(context, 0, width, headerHeight, 0.3f);

        String moduleIcon = isDarkMode ? "🌙" : "🍉";
        String moduleTitle = moduleIcon + " " + module.getName();
        context.drawCenteredTextWithShadow(textRenderer, moduleTitle, width / 2, headerHeight / 2 - 12, isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_WHITE);

        String status = module.isEnabled() ? "✅ ACTIVATED" : "❌ DEACTIVATED";
        int statusColor = module.isEnabled() ? (isDarkMode ? DARK_TEXT_SECONDARY : WATERMELON_LIGHT_GREEN) : (isDarkMode ? DARK_TEXT_MUTED : 0xFFFFCCCC);
        context.drawCenteredTextWithShadow(textRenderer, status, width / 2, headerHeight / 2 + 4, statusColor);
        context.fill(0, headerHeight - 3, width, headerHeight, getAccentHoverColor());
    }

    private void renderContentBackground(DrawContext context) {
        if (isDarkMode) {
            Render2D.drawGradient(context, 0, headerHeight, width, height - footerHeight - headerHeight, DARK_BG_PRIMARY, DARK_BG_SECONDARY);
            renderFloatingStars(context);
        } else {
            Render2D.drawGradient(context, 0, headerHeight, width, height - footerHeight - headerHeight, WATERMELON_WHITE, WATERMELON_LIGHT_GREEN);
            renderFloatingSeeds(context);
        }
    }

    private void renderFloatingStars(DrawContext context) {
        for (int i = 0; i < 12; i++) {
            double x = (Math.sin(animationTime * 0.0008 + i) * 150 + width / 2.0) % width;
            double y = (Math.cos(animationTime * 0.0006 + i * 1.5) * 80 + (height / 2.0)) % (height - headerHeight - footerHeight) + headerHeight;
            float alpha = (float) (Math.sin(animationTime * 0.001 + i) * 0.3 + 0.4);
            int alphaInt = (int) (alpha * 255) << 24;
            context.fill((int) x, (int) y, (int) x + 3, (int) y + 3, alphaInt | (DARK_ACCENT & 0xFFFFFF));
        }
    }

    private void renderFloatingSeeds(DrawContext context) {
        for (int i = 0; i < 15; i++) {
            double x = (Math.sin(animationTime * 0.0008 + i) * 150 + width / 2.0) % width;
            double y = (Math.cos(animationTime * 0.0006 + i * 1.5) * 80 + (height / 2.0)) % (height - headerHeight - footerHeight) + headerHeight;
            float alpha = (float) (Math.sin(animationTime * 0.001 + i) * 0.2 + 0.3);
            int alphaInt = (int) (alpha * 255) << 24;
            context.fill((int) x, (int) y, (int) x + 4, (int) y + 6, alphaInt | (WATERMELON_BLACK & 0xFFFFFF));
        }
    }

    private void renderHeaderDecorations(DrawContext context, int y, int width, int height, float density) {
        int decorCount = (int) (width * height * density / 10000);
        for (int i = 0; i < decorCount; i++) {
            int decorX = (i * 47) % width;
            int decorY = y + (i * 31) % height;
            if (isDarkMode) {
                context.fill(decorX, decorY, decorX + 2, decorY + 3, 0x40FFFFFF);
            } else {
                context.fill(decorX, decorY, decorX + 3, decorY + 5, 0x40000000 | WATERMELON_BLACK);
            }
        }
    }

    private void renderSidebar(DrawContext context, int mouseX, int mouseY) {
        int sidebarX = (int) (-SIDEBAR_WIDTH * (1.0f - sidebarSlideAnimation));

        // Utilisation de Render2D pour la sidebar
        Render2D.drawGradient(context, sidebarX, headerHeight, SIDEBAR_WIDTH, height - footerHeight - headerHeight, getBgPrimary(), getBgSecondary());
        context.fill(sidebarX + SIDEBAR_WIDTH - 4, headerHeight, sidebarX + SIDEBAR_WIDTH, height - footerHeight, getAccentColor());

        renderModuleInfo(context, sidebarX);
        renderGroupTabs(context, sidebarX, mouseX, mouseY);
    }

    private void renderModuleInfo(DrawContext context, int sidebarX) {
        ModuleInfo info = module.getClass().getAnnotation(ModuleInfo.class);
        renderModuleIcon(context, sidebarX + 15, headerHeight + 20);
        context.drawText(textRenderer, module.getName(), sidebarX + 60, headerHeight + 25, getTextPrimary(), false);

        String statusText = module.isEnabled() ? "🟢 Activated" : "🔴 Deactivated";
        int statusColor = module.isEnabled() ? getSuccessColor() : getErrorColor();
        context.drawText(textRenderer, statusText, sidebarX + 60, headerHeight + 40, statusColor, false);

        if (moduleKeybindComponent == null) {
            moduleKeybindComponent = new ModuleKeybindComponent(module, sidebarX + 150, headerHeight + 40, 80, 20, isDarkMode);
            this.addSelectableChild(moduleKeybindComponent);
        } else {
            moduleKeybindComponent.setX(sidebarX + 150);
            moduleKeybindComponent.setY(headerHeight + 37);
        }

        if (moduleKeybindComponent != null) {
            moduleKeybindComponent.render(context, -1, -1, 0);
        }

        context.fill(sidebarX + 15, headerHeight + 65, sidebarX + SIDEBAR_WIDTH - 15, headerHeight + 67, getAccentColor());

        if (info != null) {
            context.drawText(textRenderer, "🏷️ v" + info.version(), sidebarX + 15, height - footerHeight - 50, getTextMuted(), false);
            if (info.authors().length > 0) {
                context.drawText(textRenderer, "👤 " + info.authors()[0], sidebarX + 15, height - footerHeight - 35, getTextMuted(), false);
            }
            context.drawText(textRenderer, "📂 " + info.category().getDisplayName(), sidebarX + 15, height - footerHeight - 20, getTextMuted(), false);
        }
    }

    private void renderModuleIcon(DrawContext context, int x, int y) {
        if (isDarkMode) {
            Render2D.drawRoundedRect(context, x, y, 32, 32, 6, DARK_ACCENT);
            Render2D.drawRoundedRect(context, x + 3, y + 3, 26, 26, 4, DARK_BG_TERTIARY);
            Render2D.drawRoundedRect(context, x + 6, y + 6, 20, 20, 2, DARK_BG_SECONDARY);
            context.fill(x + 12, y + 10, x + 14, y + 14, DARK_TEXT_PRIMARY);
            context.fill(x + 20, y + 12, x + 22, y + 16, DARK_TEXT_PRIMARY);
            context.fill(x + 16, y + 20, x + 18, y + 24, DARK_TEXT_PRIMARY);
        } else {
            Render2D.drawRoundedRect(context, x, y, 32, 32, 6, WATERMELON_GREEN);
            Render2D.drawRoundedRect(context, x + 3, y + 3, 26, 26, 4, WATERMELON_WHITE);
            Render2D.drawRoundedRect(context, x + 6, y + 6, 20, 20, 2, WATERMELON_PINK);
            context.fill(x + 12, y + 10, x + 14, y + 14, WATERMELON_BLACK);
            context.fill(x + 20, y + 12, x + 22, y + 16, WATERMELON_BLACK);
            context.fill(x + 16, y + 20, x + 18, y + 24, WATERMELON_BLACK);
        }

        String letter = module.getName().substring(0, 1).toUpperCase();
        int letterWidth = textRenderer.getWidth(letter);
        int letterColor = isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_WHITE;
        context.drawText(textRenderer, letter, x + 16 - letterWidth / 2, y + 12, letterColor, false);
    }

    private void renderGroupTabs(DrawContext context, int sidebarX, int mouseX, int mouseY) {
        String groupTitle = isDarkMode ? "🌙 Groups of settings" : "🍉 Groups of settings";
        context.drawText(textRenderer, groupTitle, sidebarX + 15, headerHeight + 85, getTextPrimary(), false);

        int y = headerHeight + 110;
        for (SettingsGroupTab tab : groupTabs) {
            boolean selected = tab.group == selectedGroup;
            boolean hovered = Render2D.isPointInRect(mouseX, mouseY, sidebarX + 10, y, SIDEBAR_WIDTH - 20, 35);
            renderGroupTab(context, tab, sidebarX + 10, y, selected, hovered);
            tab.y = y;
            y += 40;
        }
    }

    private void renderGroupTab(DrawContext context, SettingsGroupTab tab, int x, int y, boolean selected, boolean hovered) {
        int bgColor;
        if (selected) {
            bgColor = getAccentColor() | 0xDD000000;
        } else if (hovered) {
            bgColor = getBgTertiary() | 0x80000000;
        } else {
            bgColor = isDarkMode ? 0x30FFFFFF : 0x20000000;
        }

        // Utilisation de Render2D pour les onglets
        Render2D.drawRoundedRect(context, x, y, 240, 35, 6, bgColor);

        if (selected) {
            int indicatorColor = isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_WHITE;
            context.fill(x + 5, y + 35 / 2 - 4, x + 9, y + 35 / 2 + 4, indicatorColor);
        }

        int textColor = selected ? (isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_WHITE) : getTextPrimary();
        context.drawText(textRenderer, tab.name, x + 15, y + 35 / 2 - 4, textColor, false);

        String countText = "(" + tab.group.getSettings().size() + ")";
        int countWidth = textRenderer.getWidth(countText);
        int countColor = selected ? getTextSecondary() : getTextMuted();
        context.drawText(textRenderer, countText, x + 240 - countWidth - 8, y + 35 / 2 - 4, countColor, false);
    }

    private void renderScrollbar(DrawContext context) {
        if (maxScrollOffset <= 0) return;

        int scrollbarX = width - 15;
        int scrollbarY = headerHeight + 70;
        int scrollbarHeight = height - headerHeight - footerHeight - 70;

        // Utilisation de Render2D pour la scrollbar
        int trackColor = isDarkMode ? 0x40FFFFFF : 0x40000000;
        Render2D.drawRoundedRect(context, scrollbarX, scrollbarY, 10, scrollbarHeight, 5, trackColor | getAccentColor());

        double handleHeight = Math.max(20, scrollbarHeight * (double) (height - headerHeight - footerHeight - 70) / (maxScrollOffset + height - headerHeight - footerHeight - 70));
        double handleY = scrollbarY + (scrollOffset / maxScrollOffset) * (scrollbarHeight - handleHeight);

        Render2D.drawRoundedRect(context, scrollbarX + 1, (int) handleY, 8, (int) handleHeight, 4, getAccentColor());

        if (!isDarkMode) {
            context.fill(scrollbarX + 3, (int) handleY + 5, scrollbarX + 5, (int) handleY + 8, WATERMELON_BLACK);
            context.fill(scrollbarX + 6, (int) handleY + 10, scrollbarX + 8, (int) handleY + 13, WATERMELON_BLACK);
        }
    }

    @Override
    protected void renderFooter(DrawContext context, int mouseX, int mouseY, float delta) {
        Render2D.drawGradient(context, 0, height - footerHeight, width, footerHeight, getAccentHoverColor(), getAccentColor());
        renderHeaderDecorations(context, height - footerHeight, width, footerHeight, 0.2f);

        String footerIcon = isDarkMode ? "🌙" : "🍉";
        String footerText = footerIcon + " Settings of " + module.getName() + " - " + (selectedGroup != null ? selectedGroup.getSettings().size() + " settings" : "");
        int footerTextColor = isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_WHITE;
        context.drawCenteredTextWithShadow(textRenderer, footerText, width / 2, height - footerHeight / 2 - 4, footerTextColor);
    }

    @Override
    protected void renderOverridElements(DrawContext context, int mouseX, int mouseY, float delta) {
        renderSettingTooltip(context, mouseX, mouseY);
    }

    private void renderSettingTooltip(DrawContext context, int mouseX, int mouseY) {
        for (SettingComponent component : settingComponents) {
            int settingY = component.getY() - (SETTING_HEIGHT - CONTROL_HEIGHT) / 2;
            if(Render2D.isPointInRect(mouseX, mouseY, SIDEBAR_WIDTH, settingY, width - SIDEBAR_WIDTH, SETTING_HEIGHT)) {
                if(!component.getSetting().getDescription().isEmpty()) {
                    renderTooltip(context, List.of(component.getSetting().getDescription().split("\n")), mouseX, mouseY);
                    return;
                }
            }
        }
    }

    private void renderTooltip(DrawContext context, List<String> lines, int mouseX, int mouseY) {
        if (lines.isEmpty()) return;

        String tooltipIcon = isDarkMode ? "🌙" : "🍉";
        int iconWidth = textRenderer.getWidth(tooltipIcon + " ");
        int maxTextWidth = lines.stream().mapToInt(textRenderer::getWidth).max().orElse(0);
        int tooltipWidth = iconWidth + maxTextWidth + 20;
        int tooltipHeight = lines.size() * 12 + 12;

        int tooltipX = mouseX + 12;
        int tooltipY = mouseY - tooltipHeight - 12;
        if (tooltipX + tooltipWidth > width) {
            tooltipX = mouseX - tooltipWidth - 12;
        }
        if (tooltipY < 0) {
            tooltipY = mouseY + 12;
        }

        int tooltipBg = isDarkMode ? (0xF0000000 | DARK_BG_TERTIARY) : (0xF0000000 | WATERMELON_WHITE);

        Render2D.drawPanel(context, tooltipX, tooltipY, tooltipWidth, tooltipHeight, 6, tooltipBg, getAccentColor(), 1);

        context.drawText(textRenderer, tooltipIcon, tooltipX + 5, tooltipY + 5, getAccentColor(), false);
        for (int i = 0; i < lines.size(); i++) {
            context.drawText(textRenderer, lines.get(i), tooltipX + iconWidth + 5, tooltipY + 8 + i * 12, getTextPrimary(), false);
        }
    }

    private void updateAnimations(float delta) {
        if (sidebarSlideAnimation < 1.0f) {
            sidebarSlideAnimation += delta * 3.0f;
            sidebarSlideAnimation = Math.min(1.0f, sidebarSlideAnimation);
        }
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }
}