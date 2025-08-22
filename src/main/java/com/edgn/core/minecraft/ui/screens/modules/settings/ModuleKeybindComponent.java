package com.edgn.core.minecraft.ui.screens.modules.settings;

import com.edgn.Main;
import com.edgn.core.minecraft.system.keybinds.KeyBinding;
import com.edgn.core.minecraft.system.keybinds.KeyBindingManager;
import com.edgn.core.minecraft.system.keybinds.MinecraftKeybinds;
import com.edgn.core.module.basic.AbstractModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ModuleKeybindComponent extends ButtonWidget {
    private final AbstractModule module;
    private final boolean isDarkMode;
    private boolean isListening = false;
    private boolean hasConflict = false;
    private String conflictDescription = "";
    
    private static final int DARK_BG_SECONDARY = 0xFF2D2D30;
    private static final int DARK_BG_TERTIARY = 0xFF3E3E42;
    private static final int DARK_ACCENT = 0xFF007ACC;
    private static final int DARK_ACCENT_HOVER = 0xFF005A9E;
    private static final int DARK_TEXT_PRIMARY = 0xFFFFFFFF;
    private static final int DARK_ERROR = 0xFFF44336;

    private static final int WATERMELON_GREEN = 0xFF2ECC71;
    private static final int WATERMELON_DARK_GREEN = 0xFF27AE60;
    private static final int WATERMELON_RED = 0xFFE74C3C;
    private static final int WATERMELON_BLACK = 0xFF2C3E50;
    private static final int WATERMELON_WHITE = 0xFFF8F9FA;
    private static final int WATERMELON_LIGHT_GREEN = 0xFFE8F8E8;

    public ModuleKeybindComponent(AbstractModule module, int x, int y, int width, int height, boolean isDarkMode) {
        super(x, y, width, height, Text.literal(""), button -> {}, DEFAULT_NARRATION_SUPPLIER);
        this.module = module;
        this.isDarkMode = isDarkMode;
        updateButtonText();
        checkForConflicts();
    }

    private void updateButtonText() {
        KeyBinding moduleKeybind = getModuleKeybind();
        if (isListening) {
            this.setMessage(Text.literal(isDarkMode ? "🌙 Press a key..." : "🍉 Press a key..."));
        } else if (moduleKeybind != null) {
            String keyName = moduleKeybind.getKeyName();
            String icon = hasConflict ? "⚠️" : (isDarkMode ? "🌙" : "🍉");
            this.setMessage(Text.literal(icon + " " + keyName));
        } else {
            String icon = isDarkMode ? "🌙" : "🍉";
            this.setMessage(Text.literal(icon + " None"));
        }
    }

    private KeyBinding getModuleKeybind() {
        String keybindName = "toggle_" + module.getId().toLowerCase();
        return KeyBindingManager.getInstance().getKeyByName(keybindName);
    }

    private void checkForConflicts() {
        KeyBinding moduleKeybind = getModuleKeybind();
        if (moduleKeybind == null) {
            hasConflict = false;
            conflictDescription = "";
            return;
        }

        int keyCode = moduleKeybind.getKey();
        
        String minecraftConflict = MinecraftKeybinds.getConflictingKeybind(keyCode);
        if (minecraftConflict != null) {
            hasConflict = true;
            conflictDescription = "Conflicts with Minecraft: " + minecraftConflict;
            return;
        }

        for (KeyBinding otherKeybind : KeyBindingManager.getInstance().getKeys()) {
            if (otherKeybind != moduleKeybind && 
                otherKeybind.getKey() == keyCode && 
                !otherKeybind.getName().equals(moduleKeybind.getName())) {
                hasConflict = true;
                conflictDescription = "Conflicts with: " + otherKeybind.getName();
                return;
            }
        }

        hasConflict = false;
        conflictDescription = "";
    }

    @Override
    public void onPress() {
        if (isListening) {
            isListening = false;
            KeyBindingManager.getInstance().setStopKeybinding(false);
        } else {
            isListening = true;
            KeyBindingManager.getInstance().setStopKeybinding(true);
        }
        updateButtonText();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isListening) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                isListening = false;
                KeyBindingManager.getInstance().setStopKeybinding(false);
                updateButtonText();
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                removeKeybind();
                isListening = false;
                KeyBindingManager.getInstance().setStopKeybinding(false);
                updateButtonText();
                checkForConflicts();
                return true;
            } else {
                assignKeybind(keyCode);
                isListening = false;
                KeyBindingManager.getInstance().setStopKeybinding(false);
                updateButtonText();
                checkForConflicts();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isListening && button >= 0 && button < 7) {
            assignKeybind(button);
            isListening = false;
            KeyBindingManager.getInstance().setStopKeybinding(false);
            updateButtonText();
            checkForConflicts();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void assignKeybind(int keyCode) {
        String keybindName = "toggle_" + module.getId().toLowerCase();

        removeKeybind();

        KeyBinding newKeybind = KeyBindingManager.getInstance().registerKeyBinding(keybindName, keyCode);
        newKeybind.bindAction(() -> {
            if (MinecraftClient.getInstance().currentScreen == null) {
                module.toggle();

                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null && client.player != null) {
                    String statusIcon = module.isEnabled() ? "🟢" : "🔴";
                    String status = module.isEnabled() ? "ACTIVATED" : "DEACTIVATED";
                    String themeIcon = isDarkMode ? "🌙" : "🍉";

                    String message = String.format("%s %s %s", themeIcon, module.getName(), status);

                    client.player.sendMessage(Text.literal(statusIcon + message), false);
                }
            }
        });

        KeyBindingManager.getInstance().save();

        Main.OVERLAY_MANAGER.getLoggerOverlay().success(
                (isDarkMode ? "🌙 " : "🍉 ") + "Keybind assigned to " + module.getName() + ": " + newKeybind.getKeyName(),
                false
        );
    }

    private void removeKeybind() {
        String keybindName = "toggle_" + module.getId().toLowerCase();
        KeyBindingManager.getInstance().unregisterKeyBinding(keybindName);
        KeyBindingManager.getInstance().save();
        
        Main.OVERLAY_MANAGER.getLoggerOverlay().info(
            (isDarkMode ? "🌙 " : "🍉 ") + "Keybind removed from " + module.getName(), 
            false
        );
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int bgColor;
        int borderColor;
        int textColor;

        if (hasConflict && !isListening) {
            bgColor = isDarkMode ? DARK_ERROR : WATERMELON_RED;
            borderColor = isDarkMode ? DARK_ERROR : WATERMELON_RED;
            textColor = WATERMELON_WHITE;
        } else if (isListening) {
            bgColor = isDarkMode ? DARK_ACCENT : WATERMELON_GREEN;
            borderColor = isDarkMode ? DARK_ACCENT_HOVER : WATERMELON_DARK_GREEN;
            textColor = WATERMELON_WHITE;
        } else if (isHovered()) {
            bgColor = isDarkMode ? DARK_BG_TERTIARY : WATERMELON_LIGHT_GREEN;
            borderColor = isDarkMode ? DARK_ACCENT : WATERMELON_GREEN;
            textColor = isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_BLACK;
        } else {
            bgColor = isDarkMode ? DARK_BG_SECONDARY : WATERMELON_WHITE;
            borderColor = isDarkMode ? DARK_BG_TERTIARY : 0xFFDDDDDD;
            textColor = isDarkMode ? DARK_TEXT_PRIMARY : WATERMELON_BLACK;
        }

        context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bgColor);
        
        context.drawBorder(this.getX(), this.getY(), this.width, this.height, borderColor);

        int textX = this.getX() + this.width / 2;
        int textY = this.getY() + (this.height - 8) / 2;
        context.drawCenteredTextWithShadow(
            MinecraftClient.getInstance().textRenderer, 
            this.getMessage(), 
            textX, 
            textY, 
            textColor
        );

        if (hasConflict && !isListening) {
            context.fill(this.getX() + this.width - 8, this.getY() + 2, this.getX() + this.width - 2, this.getY() + 8, WATERMELON_RED);
        }
    }

    public boolean hasConflict() {
        return hasConflict;
    }

    public String getConflictDescription() {
        return conflictDescription;
    }

    public boolean isListening() {
        return isListening;
    }
}