package com.edgn.core.minecraft.ui.screens;

import com.edgn.uifw.components.TextComponent;
import com.edgn.uifw.elements.item.items.TextFieldItem;
import com.edgn.uifw.templates.HtmlTemplate;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.elements.container.BaseContainer;
import com.edgn.uifw.elements.container.containers.FlexContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class TestScreen extends HtmlTemplate {

    public TestScreen(Screen screen) {
        super(Text.literal("Test"), screen);
    }

    @Override
    protected BaseContainer createHeader() {
        return null;
    }

    @Override
    protected BaseContainer createContent() {
        FlexContainer content = new FlexContainer(uiSystem, 20, 20, width - 40, contentHeight - 40)
                .addClass(StyleKey.FLEX_COLUMN, StyleKey.BG_BACKGROUND, StyleKey.ROUNDED_LG,
                        StyleKey.P_4, StyleKey.GAP_4, StyleKey.SHADOW_LG);

        TextComponent placeholderStyle = new TextComponent("Un dégradé qui bouge...", textRenderer)
                .gradient(0xFF0D6EFD, 0xFF6F42C1, TextComponent.EffectMode.HORIZONTAL_LTR)
                .italic();

        TextComponent textInputStyle = new TextComponent("", textRenderer)
                .rainbow(TextComponent.EffectMode.HORIZONTAL_LTR)
                .bold();

        TextFieldItem textFieldItem = new TextFieldItem(uiSystem, 0, 0, 0, 40)
                .addClass(StyleKey.BG_GLASS, StyleKey.BG_OPACITY_13)
                .setPlaceholder(placeholderStyle)
                .setTextStyle(textInputStyle);

        return content.add(textFieldItem);
    }

    @Override
    protected BaseContainer createFooter() {
        return null;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}