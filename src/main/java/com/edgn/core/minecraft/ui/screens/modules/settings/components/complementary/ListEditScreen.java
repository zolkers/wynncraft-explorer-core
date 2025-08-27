package com.edgn.core.minecraft.ui.screens.modules.settings.components.complementary;

import com.edgn.api.uifw.ui.core.components.TextComponent;
import com.edgn.api.uifw.ui.core.container.BaseContainer;
import com.edgn.api.uifw.ui.core.container.containers.FlexContainer;
import com.edgn.api.uifw.ui.core.container.containers.ListContainer;
import com.edgn.api.uifw.ui.core.item.items.ButtonItem;
import com.edgn.api.uifw.ui.core.item.items.LabelItem;
import com.edgn.api.uifw.ui.core.item.items.TextFieldItem;
import com.edgn.api.uifw.ui.css.StyleKey;
import com.edgn.api.uifw.ui.layout.ZIndex;
import com.edgn.api.uifw.ui.template.BaseTemplate;
import com.edgn.api.uifw.ui.template.TemplateSettings;
import com.edgn.api.uifw.ui.utils.ColorUtils;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.module.settings.ListSetting;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ListEditScreen<T> extends BaseTemplate {

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
        static final int DANGER = 0xFFE74C3C;
        static final int SUCCESS = 0xFF22C55E;
        static final int BORDER = 0xFF2A2A2E;
        static final int SELECTED = 0xFF3730A3;
    }

    private static final int ROW_H = 44;
    private static final int EDITOR_H = 36;
    private static final int ACTIONS_H = 36;

    private final ListSetting<T> setting;
    private final List<T> workingCopy = new ArrayList<>();
    private final Function<String, T> parse;
    private final Function<T, String> asString;

    private ListContainer<T> listView;
    private FlexContainer editorRow;
    private TextFieldItem input;
    private ButtonItem addOrUpdateBtn;
    private ButtonItem cancelEditBtn;

    private int editingIndex = -1;

    public ListEditScreen(ISettingsScreen parent, ListSetting<T> setting) {
        super(Text.literal("Edit List: " + setting.getName()), (Screen) parent);
        this.setting = setting;
        if (setting.getValue() != null) {
            workingCopy.addAll(setting.getValue());
        }
        this.parse = setting.getStringToValueParser();
        this.asString = setting.getValueToStringConverter();
    }

    @Override
    protected TemplateSettings templateSettings() {
        return new TemplateSettings().setHeader(true).setFooter(true);
    }

    @Override
    protected BaseContainer createHeader() {
        var container = new FlexContainer(uiSystem, 0, 0, this.width, headerHeight)
                .setRenderBackground(true)
                .setBackgroundColor(Theme.SURFACE)
                .addClass(StyleKey.PT_5, StyleKey.JUSTIFY_CENTER, StyleKey.ITEMS_CENTER);

        LabelItem labelItem = new LabelItem(uiSystem, 0, 0, 0, 0,
                new TextComponent(setting.getName())
                        .color(Theme.FOREGROUND)
                        .align(TextComponent.TextAlign.CENTER))
                .addClass(StyleKey.FLEX_BASIS_100);

        return container.addChild(labelItem);
    }

    @Override
    protected BaseContainer createContent() {
        FlexContainer root = new FlexContainer(uiSystem, 0, headerHeight, width, contentHeight)
                .setBackgroundColor(ColorUtils.setOpacity(Theme.BG_MAIN, 0.90f))
                .setRenderBackground(true)
                .addClass(StyleKey.FLEX_COLUMN, StyleKey.P_2, StyleKey.GAP_2);

        listView = new ListContainer<T>(uiSystem, 0, 0, width, contentHeight - EDITOR_H - 16)
                .setOrientation(ListContainer.Orientation.VERTICAL)
                .setFixedItemHeight(ROW_H)
                .setScrollable(true)
                .setScrollAxes(true, false)
                .setShowScrollbars(true)
                .setScrollStep(10)
                .setSelectionEnabled(false)
                .setMultiSelection(false)
                .setItemFactory(this::createListItemElement)
                .onItemSelect(this::onItemSelect)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_BASIS_100);

        listView.setItems(workingCopy);

        createEditorRow();

        root.addChild(listView).addChild(editorRow);
        return root;
    }

    private void createEditorRow() {
        input = new TextFieldItem(uiSystem, 0, 0, 1, EDITOR_H)
                .withPlaceholder("Add new item…")
                .setBackgroundColor(Theme.INPUT)
                .textColor(Theme.FOREGROUND)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.P_2, StyleKey.FLEX_BASIS_75)
                .onEnter(this::addOrUpdate);

        addOrUpdateBtn = new ButtonItem(uiSystem, 0, 0, 116, EDITOR_H,
                new TextComponent("Add").color(Theme.PRIMARY_FG))
                .backgroundColor(Theme.PRIMARY)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_10)
                .onClick(this::addOrUpdate);

        cancelEditBtn = new ButtonItem(uiSystem, 0, 0, 116, EDITOR_H,
                new TextComponent("Cancel").color(Theme.SECONDARY_FG))
                .backgroundColor(Theme.SECONDARY)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_10)
                .onClick(this::cancelEdit)
                .setVisible(false);

        editorRow = new FlexContainer(uiSystem, 0, 0, 100, EDITOR_H)
                .addClass(StyleKey.FLEX_ROW, StyleKey.ITEMS_CENTER, StyleKey.GAP_2);

        editorRow.addChild(input).addChild(addOrUpdateBtn).addChild(cancelEditBtn);
    }

    private FlexContainer createListItemElement(T item) {
        String text = asString.apply(item);
        int itemIndex = workingCopy.indexOf(item);

        FlexContainer row = new FlexContainer(uiSystem, 0, 0, 100, ROW_H)
                .addClass(StyleKey.FLEX_ROW, StyleKey.JUSTIFY_BETWEEN, StyleKey.ITEMS_CENTER,
                        StyleKey.P_2, StyleKey.ROUNDED_MD)
                .setBackgroundColor(Theme.SURFACE)
                .setRenderBackground(true);

        LabelItem label = new LabelItem(uiSystem, 0, 0,
                new TextComponent(text).color(Theme.FOREGROUND))
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_BASIS_40);

        FlexContainer actions = new FlexContainer(uiSystem, 0, 0, 100, ROW_H)
                .addClass(StyleKey.FLEX_ROW, StyleKey.GAP_2, StyleKey.ITEMS_CENTER, StyleKey.FLEX_BASIS_40);

        ButtonItem edit = new ButtonItem(uiSystem, 0, 0, 64, 28,
                new TextComponent("Edit").color(Theme.SECONDARY_FG))
                .backgroundColor(Theme.INPUT)
                .addClass(StyleKey.ROUNDED_SM, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_20)
                .onClick(() -> startEdit(itemIndex));

        ButtonItem delete = new ButtonItem(uiSystem, 0, 0, 60, 28,
                new TextComponent("Delete").color(0xFFFFFFFF))
                .backgroundColor(Theme.DANGER)
                .addClass(StyleKey.ROUNDED_SM, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_20)
                .onClick(() -> removeAt(itemIndex));

        actions.addChild(edit).addChild(delete);
        row.addChild(label).addChild(actions);

        return row;
    }

    private void onItemSelect(ListContainer.ListItem<T> listItem) {
        FlexContainer itemElement = (FlexContainer) listItem.getElement();
        FlexContainer actions = (FlexContainer) itemElement.getChildren().get(1);

        ButtonItem editBtn = (ButtonItem) actions.getChildren().get(0);
        ButtonItem deleteBtn = (ButtonItem) actions.getChildren().get(1);

        editBtn.onClick(() -> startEdit(listItem.getIndex()));
        deleteBtn.onClick(() -> removeAt(listItem.getIndex()));
    }

    private void startEdit(int index) {
        if (index >= 0 && index < workingCopy.size()) {
            editingIndex = index;
            input.setText(asString.apply(workingCopy.get(index)));
            addOrUpdateBtn.setText(new TextComponent("Update").color(Theme.PRIMARY_FG));
            cancelEditBtn.setVisible(true);

            listView.selectItem(index);
        }
    }

    private void cancelEdit() {
        editingIndex = -1;
        input.setText("");
        addOrUpdateBtn.setText(new TextComponent("Add").color(Theme.PRIMARY_FG));
        cancelEditBtn.setVisible(false);
        listView.clearSelection();
    }

    private void addOrUpdate() {
        String raw = input.getText();
        if (raw == null || raw.trim().isEmpty()) return;

        try {
            T value = parse.apply(raw.trim());

            if (editingIndex >= 0 && editingIndex < workingCopy.size()) {
                // Update existing item
                workingCopy.set(editingIndex, value);
                listView.updateItem(editingIndex, value);
            } else {
                // Add new item
                workingCopy.add(value);
                listView.addItem(value);
            }

            cancelEdit();
            listView.refresh(); // Refresh the entire list to ensure proper rendering

        } catch (Exception e) {
            // Show error feedback
            addOrUpdateBtn.setText(new TextComponent("Invalid").color(0xFFFFFFFF))
                    .backgroundColor(Theme.DANGER);

            // Reset button after a delay (you might want to implement a proper timer)
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    addOrUpdateBtn.setText(editingIndex >= 0 ?
                                    new TextComponent("Update").color(Theme.PRIMARY_FG) :
                                    new TextComponent("Add").color(Theme.PRIMARY_FG))
                            .backgroundColor(Theme.PRIMARY);
                } catch (InterruptedException ignored) {
                }
            }).start();
        }
    }

    private void removeAt(int index) {
        if (index >= 0 && index < workingCopy.size()) {
            if (editingIndex == index) {
                cancelEdit();
            } else if (editingIndex > index) {
                editingIndex--;
            }

            workingCopy.remove(index);
            listView.removeItem(index);
        }
    }

    @Override
    protected void resizeEvent() {
        if (listView != null) {
            listView.setHeight(contentHeight - EDITOR_H - 16);
            listView.markConstraintsDirty();
        }
    }

    @Override
    protected BaseContainer createFooter() {
        FlexContainer flex = new FlexContainer(uiSystem, 0, contentHeight, width, footerHeight)
                .setRenderBackground(true)
                .setBackgroundColor(Theme.SURFACE)
                .addClass(StyleKey.FLEX_ROW, StyleKey.JUSTIFY_CENTER, StyleKey.ITEMS_CENTER, StyleKey.GAP_2)
                .setZIndex(ZIndex.Layer.OVERLAY, 0);

        ButtonItem cancel = new ButtonItem(uiSystem, 0, 0, 120, ACTIONS_H,
                new TextComponent("Cancel").color(Theme.SECONDARY_FG))
                .backgroundColor(Theme.SECONDARY)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_40)
                .setZIndex(ZIndex.Layer.OVERLAY, 1)
                .onClick(this::close);

        ButtonItem save = new ButtonItem(uiSystem, 0, 0, 140, ACTIONS_H,
                new TextComponent("Save & Close").color(Theme.PRIMARY_FG))
                .backgroundColor(Theme.PRIMARY)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_40)
                .setZIndex(ZIndex.Layer.OVERLAY, 1)
                .onClick(this::saveAndClose);

        return flex.addChild(cancel).addChild(save);
    }

    private void saveAndClose() {
        setting.setValue(new ArrayList<>(workingCopy));
        close();
    }

    public ListContainer<T> getListView() {
        return listView;
    }

    public List<T> getWorkingCopy() {
        return new ArrayList<>(workingCopy);
    }

    public boolean hasUnsavedChanges() {
        List<T> original = setting.getValue();
        if (original == null) return !workingCopy.isEmpty();
        return !original.equals(workingCopy);
    }

    public void refreshList() {
        listView.setItems(workingCopy);
    }
}