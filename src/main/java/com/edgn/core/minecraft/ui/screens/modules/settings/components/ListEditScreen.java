package com.edgn.core.minecraft.ui.screens.modules.settings.components;

import com.edgn.api.uifw.ui.utils.DrawingUtils;
import com.edgn.core.minecraft.ui.screens.BaseScreen;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.module.settings.ListSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ListEditScreen<T> extends BaseScreen {
    private final ISettingsScreen parentScreen;
    private final ListSetting<T> listSetting;
    private final List<T> tempList;

    private final Function<String, T> stringToValueParser;
    private final Function<T, String> valueToStringConverter;

    private int panelX, panelY, panelWidth, panelHeight;
    private double scrollOffset = 0;
    private double maxScroll = 0;

    private TextFieldWidget newItemField;
    private ButtonWidget addButton;
    private boolean inputError = false;

    // Animation de défilement du texte
    private final Map<Integer, Long> textStartTimes = new HashMap<>();
    private final Map<Integer, Integer> textOffsets = new HashMap<>();
    private static final int SCROLL_DELAY = 2000; // 2 secondes avant de commencer le défilement
    private static final int SCROLL_SPEED = 30; // pixels par seconde
    private static final int SCROLL_PAUSE = 1000; // pause à la fin avant de recommencer

    public ListEditScreen(ISettingsScreen parent, ListSetting<T> setting) {
        super(Text.literal("Edit List: " + setting.getName()));
        this.parentScreen = parent;
        this.listSetting = setting;

        List<T> initialList = setting.getValue();
        this.tempList = (initialList != null) ? new ArrayList<>(initialList) : new ArrayList<>();

        this.stringToValueParser = setting.getStringToValueParser();
        this.valueToStringConverter = setting.getValueToStringConverter();
    }

    @Override
    protected void init() {
        super.init();

        panelWidth = 500; // Augmenté pour plus d'espace
        panelHeight = 400; // Augmenté pour plus d'espace
        panelX = (this.width - panelWidth) / 2;
        panelY = (this.height - panelHeight) / 2;

        int bottomMargin = 15;
        int widgetHeight = 20;
        int spacing = 5;

        // Position des boutons "Save" et "Cancel"
        int actionButtonY = panelY + panelHeight - bottomMargin - widgetHeight;
        int buttonWidth = (panelWidth / 2) - (bottomMargin + spacing / 2);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close"), b -> saveAndClose())
                .dimensions(panelX + bottomMargin, actionButtonY, buttonWidth, widgetHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), b -> this.close())
                .dimensions(panelX + bottomMargin + buttonWidth + spacing, actionButtonY, buttonWidth, widgetHeight).build());

        // Position du champ d'ajout et de son bouton
        int addFieldY = actionButtonY - widgetHeight - spacing;
        int addButtonWidth = 60;
        int addFieldWidth = panelWidth - (bottomMargin * 2) - addButtonWidth - spacing;

        newItemField = new TextFieldWidget(textRenderer, panelX + bottomMargin, addFieldY, addFieldWidth, widgetHeight, Text.literal("Add new item..."));
        newItemField.setChangedListener(s -> inputError = false);
        newItemField.setMaxLength(10000); // Permet jusqu'à 10000 caractères
        this.addDrawableChild(newItemField);

        addButton = ButtonWidget.builder(Text.literal("Add"), b -> addItem())
                .dimensions(panelX + bottomMargin + addFieldWidth + spacing, addFieldY, addButtonWidth, widgetHeight).build();
        this.addDrawableChild(addButton);

        calculateMaxScroll();

        // Initialiser les temps de défilement
        for (int i = 0; i < tempList.size(); i++) {
            textStartTimes.put(i, System.currentTimeMillis());
        }
    }

    private void addItem() {
        String text = newItemField.getText();
        if (text != null && !text.trim().isEmpty()) {
            try {
                T newItem = stringToValueParser.apply(text);
                tempList.add(newItem);
                newItemField.setText("");
                inputError = false;
                calculateMaxScroll();

                // Initialiser le temps de défilement pour le nouvel élément
                textStartTimes.put(tempList.size() - 1, System.currentTimeMillis());
            } catch (Exception e) {
                inputError = true;
            }
        }
    }

    private void saveAndClose() {
        listSetting.setValue(new ArrayList<>(this.tempList));
        this.close();
    }

    @Override
    public void close() {
        assert this.client != null;
        // Retourner à l'écran parent (qui peut être un AIModuleSettingsScreen ou ModuleSettingsScreen)
        if (parentScreen instanceof net.minecraft.client.gui.screen.Screen) {
            this.client.setScreen((net.minecraft.client.gui.screen.Screen) parentScreen);
        } else {
            super.close();
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x90000000);
    }

    @Override
    protected void renderHeader(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    protected void renderFooter(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    protected void renderOverridElements(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    protected void renderContent(DrawContext context, int mouseX, int mouseY, float delta) {
        int bgColor = parentScreen.getBgPrimary();
        int accentColor = parentScreen.getAccentColor();
        int textColor = parentScreen.getTextPrimary();

        // Panel principal avec DrawingUtils
        DrawingUtils.drawPanelWithShadow(context, panelX, panelY, panelWidth, panelHeight, 12, bgColor, accentColor, 2, 0x60000000);

        // Titre
        context.drawText(textRenderer, this.title, panelX + 10, panelY + 10, textColor, false);
        context.fill(panelX + 5, panelY + 25, panelX + panelWidth - 5, panelY + 26, accentColor);

        // Bordure d'erreur pour le champ de saisie
        if (inputError) {
            DrawingUtils.drawRoundedRectBorder(context, newItemField.getX() - 2, newItemField.getY() - 2,
                    newItemField.getWidth() + 4, newItemField.getHeight() + 4, 6, 0xFFFF5555, 2);
        }

        // Zone de liste
        int listStartY = panelY + 30;
        int listEndY = newItemField.getY() - 10;
        int listHeight = listEndY - listStartY;
        int listEndX = panelX + panelWidth - 5;
        int itemHeight = 22;

        DrawingUtils.enableClipping(context, panelX + 5, listStartY, listEndX - (panelX + 5), listHeight);

        for (int i = 0; i < tempList.size(); i++) {
            T item = tempList.get(i);
            String itemText = valueToStringConverter.apply(item);
            int itemY = listStartY - (int) scrollOffset + (i * itemHeight);

            if (itemY >= listStartY - itemHeight && itemY < listEndY) {
                // Effet de survol sur l'élément
                if (DrawingUtils.isPointInRect(mouseX, mouseY, panelX + 10, itemY, panelWidth - 50, itemHeight - 2)) {
                    DrawingUtils.drawRoundedRect(context, panelX + 6, itemY, listEndX - (panelX + 6) - 1, itemHeight - 2, 4, 0x44FFFFFF);
                }

                // Rendu du texte avec défilement
                renderScrollingText(context, itemText, i, panelX + 10, itemY + 7, panelWidth - 60, textColor);

                // Bouton de suppression avec DrawingUtils
                int deleteX = panelX + panelWidth - 30;
                boolean deleteHovered = DrawingUtils.isPointInRect(mouseX, mouseY, deleteX, itemY, 20, 20);
                int deleteColor = deleteHovered ? 0xFFFF5555 : 0xFFE74C3C;

                DrawingUtils.drawRoundedRect(context, deleteX, itemY + 1, 20, 18, 4, deleteColor);
                context.drawText(textRenderer, "×", deleteX + 7, itemY + 6, 0xFFFFFFFF, true);
            }
        }

        DrawingUtils.disableClipping(context);

        renderScrollbar(context, listStartY, listHeight);
    }

    private void renderScrollingText(DrawContext context, String text, int index, int x, int y, int maxWidth, int color) {
        int textWidth = textRenderer.getWidth(text);

        if (textWidth <= maxWidth) {
            // Le texte tient dans la largeur, pas besoin de défilement
            context.drawText(textRenderer, text, x, y, color, false);
            return;
        }

        // Calcul du défilement
        long currentTime = System.currentTimeMillis();
        long startTime = textStartTimes.getOrDefault(index, currentTime);
        long elapsedTime = currentTime - startTime;

        int scrollOffset = 0;

        if (elapsedTime > SCROLL_DELAY) {
            // Commencer le défilement après le délai
            long scrollTime = elapsedTime - SCROLL_DELAY;
            int maxScrollDistance = textWidth - maxWidth;

            // Calculer la position de défilement avec animation cyclique
            long cycleDuration = (maxScrollDistance * 1000L / SCROLL_SPEED) + SCROLL_PAUSE;
            long cyclePosition = scrollTime % cycleDuration;

            if (cyclePosition < maxScrollDistance * 1000L / SCROLL_SPEED) {
                // Phase de défilement
                scrollOffset = (int) (cyclePosition * SCROLL_SPEED / 1000);
            } else {
                // Phase de pause à la fin
                scrollOffset = maxScrollDistance;
            }
        }

        // Activer le clipping pour la zone de texte
        DrawingUtils.enableClipping(context, x, y - 2, maxWidth, 12);

        // Dessiner le texte avec l'offset de défilement
        context.drawText(textRenderer, text, x - scrollOffset, y, color, false);

        // Désactiver le clipping
        DrawingUtils.disableClipping(context);
    }

    private void renderScrollbar(DrawContext context, int listY, int listHeight) {
        if (maxScroll <= listHeight) return;

        int scrollbarX = panelX + panelWidth - 10;
        int accentColor = parentScreen.getAccentColor();

        // Track de la scrollbar avec DrawingUtils
        DrawingUtils.drawRoundedRect(context, scrollbarX, listY, 5, listHeight, 3, 0x40FFFFFF);

        double handleHeight = Math.max(20, listHeight * ((double)listHeight / maxScroll));
        double handleY = listY + (scrollOffset / (maxScroll - listHeight)) * (listHeight - handleHeight);

        // Handle de la scrollbar avec DrawingUtils
        DrawingUtils.drawRoundedRect(context, scrollbarX, (int) handleY, 5, (int) handleHeight, 3, accentColor);
    }

    private void calculateMaxScroll() {
        int listHeight = newItemField.getY() - 10 - (panelY + 30);
        maxScroll = tempList.size() * 22;
        if (maxScroll < listHeight) scrollOffset = 0;
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll - listHeight));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double hAmount, double vAmount) {
        int listHeight = newItemField.getY() - 10 - (panelY + 30);
        if (maxScroll > listHeight) {
            scrollOffset -= vAmount * 10;
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll - listHeight));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, hAmount, vAmount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int listStartY = panelY + 30;
        int listEndY = newItemField.getY() - 10;
        int itemHeight = 22;

        if (DrawingUtils.isPointInRect(mouseX, mouseY, panelX, listStartY, panelWidth, listEndY - listStartY)) {
            int index = (int) ((mouseY - listStartY + scrollOffset) / itemHeight);
            if (index >= 0 && index < tempList.size()) {
                int deleteX = panelX + panelWidth - 30;
                int itemY = listStartY - (int) scrollOffset + (index * itemHeight);

                if (DrawingUtils.isPointInRect(mouseX, mouseY, deleteX, itemY, 20, 20)) {
                    tempList.remove(index);

                    textStartTimes.remove(index);
                    textOffsets.remove(index);

                    Map<Integer, Long> newStartTimes = new HashMap<>();
                    Map<Integer, Integer> newOffsets = new HashMap<>();

                    for (Map.Entry<Integer, Long> entry : textStartTimes.entrySet()) {
                        int oldIndex = entry.getKey();
                        if (oldIndex > index) {
                            newStartTimes.put(oldIndex - 1, entry.getValue());
                            if (textOffsets.containsKey(oldIndex)) {
                                newOffsets.put(oldIndex - 1, textOffsets.get(oldIndex));
                            }
                        } else if (oldIndex < index) {
                            newStartTimes.put(oldIndex, entry.getValue());
                            if (textOffsets.containsKey(oldIndex)) {
                                newOffsets.put(oldIndex, textOffsets.get(oldIndex));
                            }
                        }
                    }

                    textStartTimes.clear();
                    textOffsets.clear();
                    textStartTimes.putAll(newStartTimes);
                    textOffsets.putAll(newOffsets);

                    calculateMaxScroll();
                    return true;
                } else {
                    // Clic sur l'élément - redémarrer l'animation de défilement
                    textStartTimes.put(index, System.currentTimeMillis());
                    textOffsets.put(index, 0);
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (newItemField.isFocused() && (keyCode == 257 || keyCode == 335)) {
            addItem();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}