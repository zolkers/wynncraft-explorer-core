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
        static final int BORDER = 0xFF2A2A2E;
    }
    private static final int ROW_H       = 44;
    private static final int EDITOR_H    = 36;
    private static final int ACTIONS_H   = 36;

    private final ListSetting<T> setting;
    private final List<T> temp = new ArrayList<>();
    private final Function<String, T> parse;
    private final Function<T, String> asString;

    private ListContainer listView;
    private FlexContainer editorRow;
    private TextFieldItem input;
    private ButtonItem addOrUpdateBtn;
    private ButtonItem cancelEditBtn;

    private int editingIndex = -1;

    public ListEditScreen(ISettingsScreen parent, ListSetting<T> setting) {
        super(Text.literal("Edit List: " + setting.getName()), (Screen) parent);
        this.setting = setting;
        if (setting.getValue() != null) temp.addAll(setting.getValue());
        this.parse = setting.getStringToValueParser();
        this.asString = setting.getValueToStringConverter();
    }

    @Override
    protected TemplateSettings templateSettings() {
        return new TemplateSettings().setHeader(true).setFooter(true);
    }

    @Override
    protected BaseContainer createHeader() {
        var container = new FlexContainer(uiSystem, 0, 0, this.width, footerHeight)
                .setRenderBackground(true).setBackgroundColor(Theme.SURFACE).addClass(StyleKey.PT_5);

        LabelItem labelItem = new LabelItem(uiSystem, 0, 0, 0, 0,
                new TextComponent(setting.getName())).align(TextComponent.TextAlign.CENTER)
                .color(Theme.FOREGROUND)
                .addClass(StyleKey.FLEX_BASIS_100);

        return container.addChild(labelItem);
    }

    @Override
    protected BaseContainer createContent() {
        FlexContainer root = new FlexContainer(uiSystem, 0, this.footerHeight, width, contentHeight)
                .setBackgroundColor(ColorUtils.setOpacity(Theme.BG_MAIN, 0.90f))
                .setRenderBackground(true);

        listView = new ListContainer(uiSystem, 0, 0, this.width, contentHeight)
                .addClass(StyleKey.P_2, StyleKey.GAP_2, StyleKey.FLEX_BASIS_100)
                .setScrollable(true).setScrollAxes(true, false).setShowScrollbars(true).setScrollStep(10);
        listView.setRenderBackground(false);

        input = new TextFieldItem(uiSystem, 0, 0, 1, EDITOR_H)
                .withPlaceholder("Add new item…")
                .setBackgroundColor(Theme.INPUT).textColor(Theme.FOREGROUND)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.P_2, StyleKey.FLEX_BASIS_75)
                .onEnter(this::addOrUpdate);

        addOrUpdateBtn = new ButtonItem(uiSystem, 0, 0, 116, EDITOR_H,
                new TextComponent("Add").color(Theme.PRIMARY_FG))
                .backgroundColor(Theme.PRIMARY)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_10)
                .onClick(this::addOrUpdate);

        cancelEditBtn = new ButtonItem(uiSystem, 0, 0, 116, EDITOR_H,
                new TextComponent("Cancel edit").color(Theme.SECONDARY_FG))
                .backgroundColor(Theme.SECONDARY)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_10)
                .onClick(this::cancelEdit)
                .setVisible(false);

        editorRow = new FlexContainer(uiSystem, 0, 0, 100, EDITOR_H)
                .addClass(StyleKey.FLEX_ROW, StyleKey.ITEMS_CENTER, StyleKey.GAP_2);

        editorRow.addChild(input).addChild(addOrUpdateBtn).addChild(cancelEditBtn);

        root.addChild(listView);
        root.addChild(editorRow);

        rebuildList();
        return root;
    }

    @Override
    protected void resizeEvent() {
        listView.setHeight(this.contentHeight);
    }

    @Override
    protected BaseContainer createFooter() {
        FlexContainer flex = new FlexContainer(uiSystem, 0, this.contentHeight, this.width, this.footerHeight)
                .setRenderBackground(true)
                .setBackgroundColor(Theme.SURFACE)
                .addClass(StyleKey.GAP_2)
                .setZIndex(ZIndex.Layer.OVERLAY, 0);

        ButtonItem save = new ButtonItem(uiSystem, 0, 0, 140, ACTIONS_H,
                new TextComponent("Save & Close").color(Theme.PRIMARY_FG))
                .backgroundColor(Theme.PRIMARY)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_40)
                .setZIndex(ZIndex.Layer.OVERLAY, 1)
                .onClick(this::saveAndClose);

        ButtonItem cancel = new ButtonItem(uiSystem, 0, 0, 120, ACTIONS_H,
                new TextComponent("Cancel").color(Theme.SECONDARY_FG))
                .backgroundColor(Theme.SECONDARY)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_40)
                .setZIndex(ZIndex.Layer.OVERLAY, 1)
                .onClick(this::close);

        return flex.addChild(cancel).addChild(save);
    }

    private void rebuildList() {
        listView.clearContentChildren();
        for (int i = 0; i < temp.size(); i++) {
            final int idx = i;
            String text = asString.apply(temp.get(i));

            FlexContainer row = new FlexContainer(uiSystem, 0, 0, 100, ROW_H)
                    .addClass(StyleKey.FLEX_ROW, StyleKey.JUSTIFY_BETWEEN, StyleKey.ITEMS_CENTER,
                            StyleKey.P_2, StyleKey.ROUNDED_MD);
            row.setBackgroundColor(Theme.SURFACE).setRenderBackground(true);

            LabelItem label = new LabelItem(uiSystem, 0, 0,
                    new TextComponent(text).color(Theme.FOREGROUND))
                    .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_BASIS_40);

            FlexContainer actions = new FlexContainer(uiSystem, 0, 0, 100, ROW_H)
                    .addClass(StyleKey.FLEX_ROW, StyleKey.GAP_2, StyleKey.ITEMS_CENTER, StyleKey.FLEX_BASIS_40);

            ButtonItem edit = new ButtonItem(uiSystem, 0, 0, 64, 28,
                    new TextComponent("Edit").color(Theme.SECONDARY_FG))
                    .backgroundColor(Theme.INPUT)
                    .addClass(StyleKey.ROUNDED_SM, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_20)
                    .onClick(() -> startEdit(idx));

            ButtonItem del = new ButtonItem(uiSystem, 0, 0, 40, 28,
                    new TextComponent("Delete").color(0xFFFFFFFF))
                    .backgroundColor(Theme.DANGER)
                    .addClass(StyleKey.ROUNDED_SM, StyleKey.HOVER_BRIGHTEN, StyleKey.FLEX_BASIS_20)
                    .onClick(() -> removeAt(idx));

            actions.addChild(edit).addChild(del);
            row.addChild(label).addChild(actions);
            listView.addChild(row);
        }
        listView.markConstraintsDirty();
        listView.updateConstraints();
    }

    private void startEdit(int idx) {
        editingIndex = idx;
        input.setText(asString.apply(temp.get(idx)));
        addOrUpdateBtn.setText(new TextComponent("Update").color(Theme.PRIMARY_FG));
        cancelEditBtn.setVisible(true);
    }

    private void cancelEdit() {
        editingIndex = -1;
        input.setText("");
        addOrUpdateBtn.setText(new TextComponent("Add").color(Theme.PRIMARY_FG));
        cancelEditBtn.setVisible(false);
    }

    private void addOrUpdate() {
        String raw = input.getText();
        if (raw == null || raw.trim().isEmpty()) return;

        try {
            T value = parse.apply(raw.trim());
            if (editingIndex >= 0 && editingIndex < temp.size()) {
                temp.set(editingIndex, value);
            } else {
                temp.add(value);
            }
            cancelEdit();
            rebuildList();
        } catch (Exception ignored) {
            addOrUpdateBtn.setText(new TextComponent("Invalid").color(0xFFFFFFFF));
        }
    }

    private void removeAt(int idx) {
        if (editingIndex == idx) cancelEdit();
        if (idx >= 0 && idx < temp.size()) {
            temp.remove(idx);
            rebuildList();
        }
    }

    private void saveAndClose() {
        setting.setValue(new ArrayList<>(temp));
        close();
    }
}
