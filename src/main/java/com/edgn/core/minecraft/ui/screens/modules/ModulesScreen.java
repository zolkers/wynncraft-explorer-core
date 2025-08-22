package com.edgn.core.minecraft.ui.screens.modules;

import com.edgn.Main;
import com.edgn.uifw.utils.Render2D;
import com.edgn.core.config.configs.ModulesScreenConfig;
import com.edgn.core.module.basic.AbstractModule;
import com.edgn.core.module.basic.ModuleCategory;
import com.edgn.core.module.basic.ModuleInfo;
import com.edgn.core.module.basic.ModuleManager;
import com.edgn.uifw.templates.BaseScreen;
import com.edgn.core.minecraft.ui.screens.modules.settings.ModuleSettingsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class ModulesScreen extends BaseScreen {
    private static final int WATERMELON_GREEN = 0xFF2ECC71;
    private static final int WATERMELON_DARK_GREEN = 0xFF27AE60;
    private static final int WATERMELON_RED = 0xFFE74C3C;
    private static final int WATERMELON_PINK = 0xFFFF6B9D;
    private static final int WATERMELON_BLACK = 0xFF2C3E50;
    private static final int WATERMELON_WHITE = 0xFFF8F9FA;

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

    private static final int SIDEBAR_WIDTH = 240;
    private static final int CARD_WIDTH = 300;
    private static final int CARD_HEIGHT = 140;
    private static final int CARD_SPACING = 20;

    private final List<ModuleCard> moduleCards = new ArrayList<>();
    private final List<CategoryButton> categoryButtons = new ArrayList<>();
    private final Map<AbstractModule, Float> cardAnimations = new HashMap<>();
    private final ModulesScreenConfig config;

    private ModuleCategory selectedCategory = null;
    private String searchQuery;
    private double scrollOffset = 0;
    private double maxScrollOffset = 0;
    private long animationTime = 0;
    private boolean animationsEnabled;
    private boolean customFieldFocused = false;
    private String customSearchText;
    private int[] searchFieldArea;
    private boolean isDarkMode;
    private int[] darkModeButtonArea;
    private int[] animationsButtonArea;
    private int[] saveButtonArea;
    private Screen prev;

    public ModulesScreen(Screen prev) {
        super(Text.literal("🍉 Modules Manager"));
        this.prev = prev;

        this.config = ModulesScreenConfig.load();
        this.isDarkMode = config.isDarkMode();
        this.animationsEnabled = config.isAnimationsEnabled();
        this.searchQuery = config.getLastSearchQuery();
        this.customSearchText = config.getLastSearchQuery();

        if (config.getLastSelectedCategory() != null) {
            try {
                this.selectedCategory = ModuleCategory.valueOf(config.getLastSelectedCategory());
            } catch (IllegalArgumentException e) {
                this.selectedCategory = null;
            }
        }

        AbstractModule.initSaveManager();
        AbstractModule.loadAllModules();

        initializeData();
    }

    private void initializeData() {
        moduleCards.clear();
        for (AbstractModule module : ModuleManager.getInstance().getModules()) {
            moduleCards.add(new ModuleCard(module));
            cardAnimations.put(module, 0.0f);
        }
        moduleCards.sort(Comparator.comparing(card -> card.module.getName()));
        initializeCategoryButtons();
    }

    private void initializeCategoryButtons() {
        categoryButtons.clear();
        int totalCount = ModuleManager.getInstance().getModules().size();
        String allIcon = isDarkMode ? "🌙 Tous" : "🍉 Tous";
        int allColor = isDarkMode ? DARK_ACCENT : WATERMELON_GREEN;
        categoryButtons.add(new CategoryButton(null, allIcon, totalCount, allColor));

        for (ModuleCategory category : ModuleCategory.values()) {
            int count = getModulesCountForCategory(category);
            if (count > 0) {
                String emoji = getCategoryEmoji(category);
                String name = emoji + " " + category.getDisplayName();
                int color = getCategoryColor(category);
                categoryButtons.add(new CategoryButton(category, name, count, color));
            }
        }
    }

    private String getCategoryEmoji(ModuleCategory category) {
        return switch (category) {
            case MOVEMENT -> "\uD83D\uDE80";
            case UTILITY -> "🔧";
            case CHAT -> "💬";
            case LOOTRUN  -> "💎";
            case RENDER -> "🎨";
            case RAID -> "⚔️";
            case FARMING -> "\uD83D\uDE9C";
        };
    }

    private int getCategoryColor(ModuleCategory category) {
        if (isDarkMode) {
            return switch (category) {
                case MOVEMENT -> 0xFF00AFFF;
                case UTILITY  -> 0xFFFF9800;
                case CHAT     -> 0xFFE91E63;
                case LOOTRUN  -> 0xFFFFD600;
                case RENDER   -> 0xFFFF5722;
                case RAID     -> 0xFFFF4444;
                case FARMING  -> 0xFF2E7D32;
            };
        } else {
            return switch (category) {
                case MOVEMENT -> 0xFF007BFF;
                case UTILITY  -> 0xFF795548;
                case CHAT     -> 0xFFD81B60;
                case LOOTRUN  -> 0xFF4CAF50;
                case RENDER   -> 0xFFE64A19;
                case RAID     -> 0xFFD32F2F;
                case FARMING  -> 0xFF1B5E20;
            };
        }
    }

    private int getBgPrimary() { return isDarkMode ? DARK_BG_PRIMARY : WATERMELON_WHITE; }
    private int getBgSecondary() { return isDarkMode ? DARK_BG_SECONDARY : 0xFFF0F8F0; }
    private int getBgTertiary() { return isDarkMode ? DARK_BG_TERTIARY : 0xFFF8F8F8; }
    private int getAccentColor() { return isDarkMode ? DARK_ACCENT : WATERMELON_GREEN; }
    private int getAccentHoverColor() { return isDarkMode ? DARK_ACCENT_HOVER : WATERMELON_DARK_GREEN; }
    private int getTextPrimary() { return isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_BLACK; }
    private int getTextSecondary() { return isDarkMode ? DARK_TEXT_SECONDARY : 0xFF666666; }
    private int getTextMuted() { return isDarkMode ? DARK_TEXT_MUTED : 0xFF888888; }
    private int getSuccessColor() { return isDarkMode ? DARK_SUCCESS : WATERMELON_GREEN; }
    private int getErrorColor() { return isDarkMode ? DARK_ERROR : WATERMELON_RED; }

    @Override
    protected void init() {
        super.init();
        assert this.client != null;
        this.client.execute(this::updateLayout);
    }

    private void updateLayout() {
        List<ModuleCard> visibleCards = getFilteredModuleCards();
        if (width <= 0 || height <= 0) return;

        int contentWidth = width - SIDEBAR_WIDTH - 60;
        int cardsPerRow = Math.max(1, contentWidth / (CARD_WIDTH + CARD_SPACING));
        int totalRows = (int) Math.ceil((double) visibleCards.size() / cardsPerRow);

        int contentHeight = height - headerHeight - footerHeight - 120;
        int totalContentHeight = totalRows * (CARD_HEIGHT + CARD_SPACING);
        maxScrollOffset = Math.max(0, totalContentHeight - contentHeight);

        if (scrollOffset > maxScrollOffset) scrollOffset = 0;
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));

        int startY = headerHeight + 80;
        for (int i = 0; i < visibleCards.size(); i++) {
            int row = i / cardsPerRow;
            int col = i % cardsPerRow;
            int x = SIDEBAR_WIDTH + 30 + col * (CARD_WIDTH + CARD_SPACING);
            int y = startY + row * (CARD_HEIGHT + CARD_SPACING) - (int) scrollOffset;
            visibleCards.get(i).setPosition(x, y);
        }
    }

    @Override
    protected void renderHeader(DrawContext context, int mouseX, int mouseY, float delta) {
        Render2D.drawGradient(context, 0, 0, width, headerHeight, getAccentColor(), getAccentHoverColor());

        String titleText = isDarkMode ? "🌙 Modules Manager" : "🍉 Modules Manager";
        context.drawCenteredTextWithShadow(textRenderer, titleText, width / 2, headerHeight / 2 - 8,
                isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_WHITE);

        String subtitle = getFilteredModuleCards().size() + " modules availables";
        int subtitleColor = isDarkMode ? DARK_TEXT_SECONDARY : 0xFFE8F5E8;
        context.drawCenteredTextWithShadow(textRenderer, subtitle, width / 2, headerHeight / 2 + 8, subtitleColor);
        context.fill(0, headerHeight - 2, width, headerHeight, getAccentHoverColor());
    }

    @Override
    protected void renderContent(DrawContext context, int mouseX, int mouseY, float delta) {
        animationTime = System.currentTimeMillis();
        updateAnimations(delta);
        renderBackground(context);
        renderSidebar(context, mouseX, mouseY);
        renderSearchSection(context, mouseX, mouseY);
        renderMainContent(context, mouseX, mouseY);
    }

    private void renderBackground(DrawContext context) {
        if (isDarkMode) {
            Render2D.drawGradient(context, 0, headerHeight, width, height - footerHeight - headerHeight, DARK_BG_PRIMARY, DARK_BG_SECONDARY);
            for (int i = 0; i < 15; i++) {
                int x = (int) ((Math.sin(animationTime * 0.001 + i) * 200 + (double) width / 2) % width);
                int y = (int) ((Math.cos(animationTime * 0.0008 + i * 2) * 100 + (double) height / 2) % (height - headerHeight - footerHeight)) + headerHeight;
                int alpha = (int) (Math.sin(animationTime * 0.002 + i) * 20 + 30);
                context.fill(x, y, x + 2, y + 3, (alpha << 24) | (DARK_ACCENT & 0xFFFFFF));
            }
        } else {
            Render2D.drawGradient(context, 0, headerHeight, width, height - footerHeight - headerHeight, 0xFFF8FFF8, 0xFFE8F8E8);
            for (int i = 0; i < 20; i++) {
                int x = (int) ((Math.sin(animationTime * 0.001 + i) * 200 + (double) width / 2) % width);
                int y = (int) ((Math.cos(animationTime * 0.0008 + i * 2) * 100 + (double) height / 2) % (height - headerHeight - footerHeight)) + headerHeight;
                int alpha = (int) (Math.sin(animationTime * 0.002 + i) * 15 + 25);
                context.fill(x, y, x + 3, y + 5, (alpha << 24) | (WATERMELON_BLACK & 0xFFFFFF));
            }
        }
    }

    private void renderSidebar(DrawContext context, int mouseX, int mouseY) {
        Render2D.drawGradient(context, 0, headerHeight, SIDEBAR_WIDTH, height - footerHeight - headerHeight, getBgPrimary(), getBgSecondary());
        context.drawVerticalLine(SIDEBAR_WIDTH, headerHeight, height - footerHeight, getAccentColor());

        String categoriesTitle = isDarkMode ? "🌙 Categories" : "🍉 Categories";
        context.drawTextWithShadow(textRenderer, categoriesTitle, 15, headerHeight + 15, getTextPrimary());

        int y = headerHeight + 50;
        for (CategoryButton button : categoryButtons) {
            boolean selected = Objects.equals(button.category, selectedCategory);
            boolean hovered = Render2D.isPointInRect(mouseX, mouseY, 10, y, SIDEBAR_WIDTH - 20, 35);
            renderCategoryButton(context, button, y, selected, hovered);
            button.y = y;
            y += 40;
        }
    }

    private void renderCategoryButton(DrawContext context, CategoryButton button, int y, boolean selected, boolean hovered) {
        int bgColor;
        if (selected) bgColor = button.color | 0xCC000000;
        else if (hovered) bgColor = (button.color & 0x80FFFFFF) | 0x40000000;
        else bgColor = isDarkMode ? 0x30FFFFFF : 0x20000000;

        // Utilisation de Render2D pour le bouton de catégorie
        Render2D.drawRoundedRect(context, 10, y, 220, 35, 6, bgColor);

        if (button.category != null) {
            context.fill(10 + 8, y + 35 /2 - 3, 10 + 14, y + 35 /2 + 3, button.color);
        }

        int textX = button.category != null ? 10 + 22 : 10 + 8;
        int textColor = selected ? (isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_WHITE) : getTextPrimary();
        context.drawText(textRenderer, button.name, textX, y + 35 /2 - 4, textColor, false);

        String countText = "(" + button.count + ")";
        int countWidth = textRenderer.getWidth(countText);
        int countColor = selected ? getTextSecondary() : getTextMuted();
        context.drawText(textRenderer, countText, 10 + 220 - countWidth - 8, y + 35 /2 - 4, countColor, false);
    }

    private void renderSearchSection(DrawContext context, int mouseX, int mouseY) {
        int searchSectionY = headerHeight;
        int searchSectionHeight = 60;
        context.fill(SIDEBAR_WIDTH + 1, searchSectionY, width, searchSectionY + searchSectionHeight, getBgPrimary());
        context.fill(SIDEBAR_WIDTH + 1, searchSectionY, width, searchSectionY + 2, getAccentColor());
        context.fill(SIDEBAR_WIDTH + 1, searchSectionY + searchSectionHeight - 2, width,
                searchSectionY + searchSectionHeight, getAccentHoverColor());

        int fieldX = SIDEBAR_WIDTH + 30;
        int fieldY = headerHeight + 18;
        int fieldWidth = 300;
        int fieldHeight = 24;
        boolean fieldHovered = Render2D.isPointInRect(mouseX, mouseY, fieldX, fieldY, fieldWidth, fieldHeight);

        // Utilisation de Render2D pour le champ de recherche
        Render2D.drawShadow(context, fieldX, fieldY, fieldWidth, fieldHeight, 2, 2, 0x30000000);

        int fieldBg = customFieldFocused ? getBgPrimary() : (fieldHovered ? getBgTertiary() : getBgSecondary());
        int borderColor = customFieldFocused ? getAccentColor() : (fieldHovered ? getAccentHoverColor() : getTextMuted());

        Render2D.drawPanel(context, fieldX, fieldY, fieldWidth, fieldHeight, 4, fieldBg, borderColor, 1);

        context.drawText(textRenderer, "🔍", fieldX + 8, fieldY + 8, getAccentHoverColor(), false);

        String displayText = customSearchText.isEmpty() ? "Type the name of a module..." : customSearchText;
        int textColor = customSearchText.isEmpty() ? getTextMuted() : getTextPrimary();
        int textX = fieldX + 30;
        int textY = fieldY + 8;

        String clippedText = displayText;
        int maxTextWidth = fieldWidth - 40;
        while (textRenderer.getWidth(clippedText) > maxTextWidth && !clippedText.isEmpty()) {
            clippedText = clippedText.substring(0, clippedText.length() - 1);
        }

        context.drawText(textRenderer, clippedText, textX, textY, textColor, false);

        if (customFieldFocused && !customSearchText.isEmpty()) {
            long cursorBlinkTime = System.currentTimeMillis();
            if ((cursorBlinkTime / 500) % 2 == 0) {
                int cursorX = textX + textRenderer.getWidth(clippedText);
                context.fill(cursorX, textY, cursorX + 1, textY + 8, getTextPrimary());
            }
        }

        searchFieldArea = new int[]{fieldX, fieldY, fieldWidth, fieldHeight};

        String helpText = "Look among " + ModuleManager.getInstance().getModules().size() + " modules";
        context.drawText(textRenderer, helpText, fieldX + fieldWidth + 20, fieldY + 4, getTextMuted(), false);

        int resultCount = getFilteredModuleCards().size();
        String resultText = resultCount + " found" + (resultCount > 1 ? "s" : "");
        context.drawText(textRenderer, resultText, fieldX + fieldWidth + 20, fieldY + 16, getAccentHoverColor(), false);

        // Éléments décoratifs avec Render2D
        for (int i = 0; i < 3; i++) {
            int decorX = width - 80 + i * 20;
            int decorY = headerHeight + 25 + (int)(Math.sin(animationTime * 0.003 + i) * 2);
            if (isDarkMode) {
                Render2D.drawRoundedRect(context, decorX, decorY, 4, 6, 2, DARK_ACCENT);
                context.fill(decorX + 1, decorY + 1, decorX + 3, decorY + 2, DARK_BG_PRIMARY);
            } else {
                Render2D.drawRoundedRect(context, decorX, decorY, 4, 6, 2, WATERMELON_RED);
                context.fill(decorX + 1, decorY + 1, decorX + 3, decorY + 2, WATERMELON_BLACK);
            }
        }
    }

    private void renderMainContent(DrawContext context, int mouseX, int mouseY) {
        int clipStartY = headerHeight + 70;
        Render2D.enableClipping(context, SIDEBAR_WIDTH, clipStartY, width - SIDEBAR_WIDTH, height - footerHeight - clipStartY);

        List<ModuleCard> visibleCards = getFilteredModuleCards();
        for (ModuleCard card : visibleCards) {
            if (card.y + CARD_HEIGHT >= clipStartY && card.y <= height - footerHeight) {
                renderModuleCard(context, card, mouseX, mouseY);
            }
        }

        Render2D.disableClipping(context);
        renderScrollbar(context);
    }

    private void renderModuleCard(DrawContext context, ModuleCard card, int mouseX, int mouseY) {
        AbstractModule module = card.module;
        ModuleInfo info = module.getClass().getAnnotation(ModuleInfo.class);
        boolean hovered = Render2D.isPointInRect(mouseX, mouseY, card.x, card.y, CARD_WIDTH, CARD_HEIGHT);

        float cardAnimation = animationsEnabled ? cardAnimations.getOrDefault(module, 0.0f) : 0;
        int animOffset = animationsEnabled ? (int) (Math.sin(cardAnimation) * 2) : 0;

        // Utilisation de Render2D pour la carte
        int shadowColor = isDarkMode ? 0x80000000 : 0x40000000;
        Render2D.drawShadow(context, card.x, card.y + animOffset, CARD_WIDTH, CARD_HEIGHT, 4, 4, shadowColor);

        int bgColor = hovered ? getBgTertiary() : getBgPrimary();
        int borderColor = module.isEnabled() ? getSuccessColor() : getTextMuted();

        Render2D.drawPanel(context, card.x, card.y + animOffset, CARD_WIDTH, CARD_HEIGHT, 8, bgColor, borderColor, 1);

        if (info != null) {
            int categoryColor = getCategoryColor(info.category());
            context.fill(card.x, card.y + animOffset, card.x + 6, card.y + CARD_HEIGHT + animOffset, categoryColor);
        }

        renderModuleIcon(context, card.x + 15, card.y + 15 + animOffset, info);

        String moduleName = module.getName();
        context.drawText(textRenderer, moduleName, card.x + 60, card.y + 15 + animOffset, getTextPrimary(), false);

        String description = info != null ? info.description() : "No description";
        renderWrappedText(context, description, card.x + 15, card.y + 55 + animOffset, getTextSecondary());

        renderStatusBadge(context, card, module, animOffset);
        renderModuleButtons(context, card, module, mouseX, mouseY, hovered, animOffset);

        if (info != null) {
            context.drawText(textRenderer, "v" + info.version(), card.x + 15, card.y + CARD_HEIGHT - 20 + animOffset, getTextMuted(), false);

            if (info.authors().length > 0) {
                String author = "by " + info.authors()[0];
                int authorWidth = textRenderer.getWidth(author);
                context.drawText(textRenderer, author, card.x + CARD_WIDTH - authorWidth - 15,
                        card.y + CARD_HEIGHT - 20 + animOffset, getTextMuted(), false);
            }
        }
    }

    private void renderModuleIcon(DrawContext context, int x, int y, ModuleInfo info) {
        if (isDarkMode) {
            Render2D.drawRoundedRect(context, x, y, 32, 32, 6, DARK_ACCENT);
            Render2D.drawRoundedRect(context, x + 2, y + 2, 28, 28, 4, DARK_BG_TERTIARY);
            Render2D.drawRoundedRect(context, x + 4, y + 4, 24, 24, 2, DARK_BG_SECONDARY);
            context.fill(x + 10, y + 10, x + 12, y + 14, DARK_TEXT_PRIMARY);
            context.fill(x + 18, y + 12, x + 20, y + 16, DARK_TEXT_PRIMARY);
            context.fill(x + 14, y + 18, x + 16, y + 22, DARK_TEXT_PRIMARY);
        } else {
            Render2D.drawRoundedRect(context, x, y, 32, 32, 6, WATERMELON_GREEN);
            Render2D.drawRoundedRect(context, x + 2, y + 2, 28, 28, 4, WATERMELON_RED);
            Render2D.drawRoundedRect(context, x + 4, y + 4, 24, 24, 2, WATERMELON_PINK);
            context.fill(x + 10, y + 10, x + 12, y + 14, WATERMELON_BLACK);
            context.fill(x + 18, y + 12, x + 20, y + 16, WATERMELON_BLACK);
            context.fill(x + 14, y + 18, x + 16, y + 22, WATERMELON_BLACK);
        }

        if (info != null) {
            String letter = info.name().substring(0, 1).toUpperCase();
            int letterWidth = textRenderer.getWidth(letter);
            int letterColor = isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_WHITE;
            context.drawText(textRenderer, letter, x + 16 - letterWidth/2, y + 12, letterColor, false);
        }
    }

    private void renderStatusBadge(DrawContext context, ModuleCard card, AbstractModule module, int animOffset) {
        String status = module.isEnabled() ? "🟢 ON" : "🔴 OFF";
        int statusColor = module.isEnabled() ? getSuccessColor() : getErrorColor();

        int badgeWidth = textRenderer.getWidth(status) + 16;
        int badgeX = card.x + CARD_WIDTH - badgeWidth - 15;
        int badgeY = card.y + 15 + animOffset;

        // Utilisation de Render2D pour le badge
        int badgeBackground = (statusColor & 0x40FFFFFF) | 0x40000000;
        Render2D.drawPanel(context, badgeX, badgeY, badgeWidth, 20, 4, badgeBackground, statusColor, 1);
        context.drawText(textRenderer, status, badgeX + 8, badgeY + 6, statusColor, false);
    }

    private void renderModuleButtons(DrawContext context, ModuleCard card, AbstractModule module, int mouseX, int mouseY, boolean cardHovered, int animOffset) {
        if (!cardHovered) return;

        int buttonY = card.y + CARD_HEIGHT - 50 + animOffset;

        // Bouton Toggle avec Render2D
        int toggleX = card.x + CARD_WIDTH - 120;
        boolean toggleHovered = Render2D.isPointInRect(mouseX, mouseY, toggleX, buttonY, 50, 25);

        int toggleBg = module.isEnabled() ? getSuccessColor() : getErrorColor();
        if (toggleHovered) toggleBg = (toggleBg & 0xCCFFFFFF);

        Render2D.drawRoundedRect(context, toggleX, buttonY, 50, 25, 4, toggleBg);
        context.drawText(textRenderer, module.isEnabled() ? "ON" : "OFF", toggleX + 15, buttonY + 8,
                isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_WHITE, false);

        // Bouton Settings avec Render2D
        int settingsX = card.x + CARD_WIDTH - 60;
        boolean settingsHovered = Render2D.isPointInRect(mouseX, mouseY, settingsX, buttonY, 45, 25);

        int settingsBg = settingsHovered ? getAccentHoverColor() : getAccentColor();
        Render2D.drawRoundedRect(context, settingsX, buttonY, 45, 25, 4, settingsBg);
        context.drawText(textRenderer, "⚙️", settingsX + 15, buttonY + 8,
                isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_WHITE, false);

        card.toggleButtonArea = new int[]{toggleX, buttonY, 50, 25};
        card.settingsButtonArea = new int[]{settingsX, buttonY, 45, 25};
    }

    private void renderScrollbar(DrawContext context) {
        if (maxScrollOffset <= 0) return;

        int scrollbarX = width - 12;
        int scrollbarY = headerHeight + 90;
        int scrollbarHeight = height - headerHeight - footerHeight - 90;

        // Track de la scrollbar avec Render2D
        Render2D.drawRoundedRect(context, scrollbarX, scrollbarY, 8, scrollbarHeight, 4,
                isDarkMode ? 0x40FFFFFF : 0x40000000);

        double handleHeight = Math.max(20, scrollbarHeight * (height - headerHeight - footerHeight - 90) / (maxScrollOffset + height - headerHeight - footerHeight - 90));
        double handleY = scrollbarY + (scrollOffset / maxScrollOffset) * (scrollbarHeight - handleHeight);

        // Thumb de la scrollbar avec Render2D
        Render2D.drawRoundedRect(context, scrollbarX, (int) handleY, 8, (int) handleHeight, 4, getAccentColor());
    }

    private void renderWrappedText(DrawContext context, String text, int x, int y, int color) {
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int currentY = y;
        int lineHeight = 12;
        int maxLines = 3;
        int currentLine_count = 0;

        for (String word : words) {
            if (currentLine_count >= maxLines) break;

            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;

            if (textRenderer.getWidth(testLine) <= 270) {
                currentLine = new StringBuilder(testLine);
            } else {
                if (!currentLine.isEmpty()) {
                    context.drawText(textRenderer, currentLine.toString(), x, currentY, color, false);
                    currentY += lineHeight;
                    currentLine_count++;
                    currentLine = new StringBuilder(word);
                } else {
                    context.drawText(textRenderer, word, x, currentY, color, false);
                    currentY += lineHeight;
                    currentLine_count++;
                }
            }
        }

        if (!currentLine.isEmpty() && currentLine_count < maxLines) {
            String finalLine = currentLine.toString();
            if (currentLine_count == maxLines - 1 && textRenderer.getWidth(finalLine) > 270) {
                finalLine = finalLine.substring(0, Math.min(finalLine.length(), 270 / 6)) + "...";
            }
            context.drawText(textRenderer, finalLine, x, currentY, color, false);
        }
    }

    private void updateAnimations(float delta) {
        if (animationsEnabled) {
            for (AbstractModule module : ModuleManager.getInstance().getModules()) {
                float current = cardAnimations.getOrDefault(module, 0.0f);
                cardAnimations.put(module, current + delta);
            }
        }
    }

    @Override
    protected void renderFooter(DrawContext context, int mouseX, int mouseY, float delta) {
        Render2D.drawGradient(context, 0, height - footerHeight, width, footerHeight, getAccentColor(), getAccentHoverColor());

        renderDarkModeToggle(context, mouseX, mouseY);
        renderAnimationsToggle(context, mouseX, mouseY);
        renderSaveButton(context, mouseX, mouseY);

        String footerIcon = isDarkMode ? "🌙" : "🍉";
        String footerText = footerIcon + " Modules Manager - " + getFilteredModuleCards().size() + " modules";
        int footerTextColor = isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_WHITE;
        context.drawCenteredTextWithShadow(textRenderer, footerText, width / 2, height - footerHeight/2 - 4, footerTextColor);
    }

    private void renderDarkModeToggle(DrawContext context, int mouseX, int mouseY) {
        int toggleX = 20;
        int toggleY = height - footerHeight + 3;
        int toggleWidth = 120;
        int toggleHeight = 19;

        boolean toggleHovered = Render2D.isPointInRect(mouseX, mouseY, toggleX, toggleY, toggleWidth, toggleHeight);

        int bgColor = toggleHovered ? getBgTertiary() : getBgSecondary();
        int borderColor = toggleHovered ? getAccentColor() : getTextMuted();

        Render2D.drawPanel(context, toggleX, toggleY, toggleWidth, toggleHeight, 4, bgColor, borderColor, 1);

        String toggleText = isDarkMode ? "🌙 Dark Mode" : "☀️ Light Mode";
        int textColor = getTextPrimary();
        int textX = toggleX + (toggleWidth - textRenderer.getWidth(toggleText)) / 2;
        int textY = toggleY + (toggleHeight - 8) / 2;

        context.drawText(textRenderer, toggleText, textX, textY, textColor, false);

        darkModeButtonArea = new int[]{toggleX, toggleY, toggleWidth, toggleHeight};
    }

    private void renderAnimationsToggle(DrawContext context, int mouseX, int mouseY) {
        int toggleX = 160;
        int toggleY = height - footerHeight + 3;
        int toggleWidth = 120;
        int toggleHeight = 19;

        boolean toggleHovered = Render2D.isPointInRect(mouseX, mouseY, toggleX, toggleY, toggleWidth, toggleHeight);

        int bgColor = toggleHovered ? getBgTertiary() : getBgSecondary();
        int borderColor = toggleHovered ? getAccentColor() : getTextMuted();

        Render2D.drawPanel(context, toggleX, toggleY, toggleWidth, toggleHeight, 4, bgColor, borderColor, 1);

        String toggleText = animationsEnabled ? "✨ Animations ON" : "🚫 Animations OFF";
        int textColor = getTextPrimary();
        int textX = toggleX + (toggleWidth - textRenderer.getWidth(toggleText)) / 2;
        int textY = toggleY + (toggleHeight - 8) / 2;

        context.drawText(textRenderer, toggleText, textX, textY, textColor, false);

        animationsButtonArea = new int[]{toggleX, toggleY, toggleWidth, toggleHeight};
    }

    private void renderSaveButton(DrawContext context, int mouseX, int mouseY) {
        int toggleWidth = 100;
        int toggleHeight = 19;
        int toggleX = width - toggleWidth - 20;
        int toggleY = height - footerHeight + 3;

        boolean toggleHovered = Render2D.isPointInRect(mouseX, mouseY, toggleX, toggleY, toggleWidth, toggleHeight);

        int bgColor = toggleHovered ? getBgTertiary() : getBgSecondary();
        int borderColor = toggleHovered ? getAccentColor() : getTextMuted();

        Render2D.drawPanel(context, toggleX, toggleY, toggleWidth, toggleHeight, 4, bgColor, borderColor, 1);

        String toggleText = isDarkMode ? "🌙 Save All" : "🍉 Save All";
        int textColor = getTextPrimary();
        int textX = toggleX + (toggleWidth - textRenderer.getWidth(toggleText)) / 2;
        int textY = toggleY + (toggleHeight - 8) / 2;

        context.drawText(textRenderer, toggleText, textX, textY, textColor, false);

        saveButtonArea = new int[]{toggleX, toggleY, toggleWidth, toggleHeight};
    }

    @Override
    protected void renderOverridElements(DrawContext context, int mouseX, int mouseY, float delta) {
        int closeX = width - 35;
        int closeY = 5;
        boolean closeHovered = Render2D.isPointInRect(mouseX, mouseY, closeX, closeY, 30, 20);

        int closeBg = closeHovered ? getErrorColor() : getTextMuted();
        Render2D.drawRoundedRect(context, closeX, closeY, 30, 20, 4, closeBg);
        context.drawText(textRenderer, "✕", closeX + 11, closeY + 6,
                isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_WHITE, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Render2D.isPointInRect(mouseX, mouseY, width - 35, 5, 30, 20)) {
            this.close();
            return true;
        }

        if (saveButtonArea != null) {
            int[] save = saveButtonArea;
            if (Render2D.isPointInRect(mouseX, mouseY, save[0], save[1], save[2], save[3])) {
                saveAllModules();
                return true;
            }
        }

        if (darkModeButtonArea != null) {
            int[] toggle = darkModeButtonArea;
            if (Render2D.isPointInRect(mouseX, mouseY, toggle[0], toggle[1], toggle[2], toggle[3])) {
                toggleDarkMode();
                return true;
            }
        }

        if (animationsButtonArea != null) {
            int[] toggle = animationsButtonArea;
            if (Render2D.isPointInRect(mouseX, mouseY, toggle[0], toggle[1], toggle[2], toggle[3])) {
                animationsEnabled = !animationsEnabled;
                saveConfig();
                String message = animationsEnabled ? "✨ Animations enabled" : "🚫 Animations disabled";
                Main.OVERLAY_MANAGER.getLoggerOverlay().info(message, false);
                return true;
            }
        }

        for (CategoryButton categoryButton : categoryButtons) {
            if (Render2D.isPointInRect(mouseX, mouseY, 10, categoryButton.y, SIDEBAR_WIDTH - 20, 35)) {
                selectedCategory = categoryButton.category;
                saveConfig();
                updateLayout();
                return true;
            }
        }

        for (ModuleCard card : getFilteredModuleCards()) {
            if (card.toggleButtonArea != null) {
                int[] toggle = card.toggleButtonArea;
                if (Render2D.isPointInRect(mouseX, mouseY, toggle[0], toggle[1], toggle[2], toggle[3])) {
                    card.module.toggle();
                    return true;
                }
            }

            if (card.settingsButtonArea != null) {
                int[] settings = card.settingsButtonArea;
                if (Render2D.isPointInRect(mouseX, mouseY, settings[0], settings[1], settings[2], settings[3])) {
                    openModuleSettings(card.module);
                    return true;
                }
            }
        }

        if (searchFieldArea != null) {
            boolean clickedOnField = Render2D.isPointInRect(mouseX, mouseY, searchFieldArea[0], searchFieldArea[1], searchFieldArea[2], searchFieldArea[3]);

            if (clickedOnField) {
                customFieldFocused = true;
                return true;
            } else {
                customFieldFocused = false;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void saveConfig() {
        config.setDarkMode(isDarkMode);
        config.setAnimationsEnabled(animationsEnabled);
        config.setLastSearchQuery(searchQuery);
        String categoryName = selectedCategory != null ? selectedCategory.name() : null;
        config.setLastSelectedCategory(categoryName);
        config.save();
    }

    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        saveConfig();
        initializeCategoryButtons();
        String message = isDarkMode ? "🌙 Dark mode activated" : "☀️ Light mode activated";
        Main.OVERLAY_MANAGER.getLoggerOverlay().info(message, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (customFieldFocused) {
            if (keyCode == 256) {
                customFieldFocused = false;
                return true;
            }

            if (keyCode == 259 && !customSearchText.isEmpty()) {
                customSearchText = customSearchText.substring(0, customSearchText.length() - 1);
                onSearchChanged(customSearchText);
                return true;
            }

            if (keyCode == 261 && (modifiers & 2) != 0 && !customSearchText.isEmpty()) {
                customSearchText = deleteWordForward(customSearchText);
                onSearchChanged(customSearchText);
                return true;
            }

            return true;
        }

        if (keyCode == 68 && (modifiers & 2) != 0) {
            toggleDarkMode();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private String deleteWordForward(String text) {
        if (text.isEmpty()) return text;

        String trimmed = text.trim();
        int lastSpaceIndex = trimmed.lastIndexOf(' ');

        if (lastSpaceIndex == -1) {
            return "";
        } else {
            return trimmed.substring(0, lastSpaceIndex);
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (customFieldFocused) {
            if (chr >= 32 && customSearchText.length() < 50) {
                customSearchText += chr;
                onSearchChanged(customSearchText);
                return true;
            }
        }

        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX > SIDEBAR_WIDTH) {
            scrollOffset -= verticalAmount * 25;
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
            updateLayout();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private List<ModuleCard> getFilteredModuleCards() {
        return moduleCards.stream()
                .filter(card -> {
                    ModuleInfo info = card.module.getClass().getAnnotation(ModuleInfo.class);

                    if (selectedCategory != null) {
                        if (info == null || info.category() != selectedCategory) {
                            return false;
                        }
                    }

                    if (!searchQuery.isEmpty()) {
                        String name = card.module.getName().toLowerCase();
                        String description = info != null ? info.description().toLowerCase() : "";
                        String query = searchQuery.toLowerCase();

                        return name.contains(query) || description.contains(query);
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    private void onSearchChanged(String query) {
        this.searchQuery = query;
        saveConfig();
        updateLayout();
    }

    private void openModuleSettings(AbstractModule module) {
        if (module != null) {
            assert this.client != null;
            this.client.setScreen(new ModuleSettingsScreen(this, module, isDarkMode));
        } else {
            String message = isDarkMode ? "🌙 This module has no settings" : "🍉 This module has no settings";
            Main.OVERLAY_MANAGER.getLoggerOverlay().info(message, false);
        }
    }

    private int getModulesCountForCategory(ModuleCategory category) {
        return (int) ModuleManager.getInstance().getModules().stream()
                .filter(module -> {
                    ModuleInfo info = module.getClass().getAnnotation(ModuleInfo.class);
                    return info != null && info.category() == category;
                })
                .count();
    }

    private static class ModuleCard {
        final AbstractModule module;
        int x, y;
        int[] toggleButtonArea;
        int[] settingsButtonArea;

        ModuleCard(AbstractModule module) {
            this.module = module;
        }

        void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class CategoryButton {
        final ModuleCategory category;
        final String name;
        final int count;
        final int color;
        int y;

        CategoryButton(ModuleCategory category, String name, int count, int color) {
            this.category = category;
            this.name = name;
            this.count = count;
            this.color = color;
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        updateLayout();
    }

    private void saveAllModules() {
        AbstractModule.saveAllModules();
        String message = isDarkMode ? "🌙 All modules saved!" : "🍉 All modules saved!";
        Main.OVERLAY_MANAGER.getLoggerOverlay().success(message, true);
    }

    @Override
    public void close() {
        saveConfig();
        AbstractModule.saveAllModules();
        MinecraftClient.getInstance().setScreen(prev);
    }
}