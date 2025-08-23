package com.edgn.api.uifw.ui.core.item.items.settings;

import com.edgn.Main;
import com.edgn.api.uifw.ui.core.item.items.ButtonItem;
import com.edgn.api.uifw.ui.css.StyleKey;
import com.edgn.api.uifw.ui.css.UIStyleSystem;
import com.edgn.api.uifw.ui.utils.DrawingUtils;
import com.edgn.core.minecraft.system.keybinds.KeyBinding;
import com.edgn.core.minecraft.system.keybinds.KeyBindingManager;
import com.edgn.core.minecraft.system.keybinds.MinecraftKeybinds;
import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.edgn.core.module.basic.AbstractModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings({"unused"})
public class ModuleKeybindItem extends ButtonItem {
    private final AbstractModule module;
    private final ISettingsScreen screen;

    private boolean isListening = false;
    private boolean hasConflict = false;
    private String conflictDescription = "";

    public ModuleKeybindItem(UIStyleSystem ui,
                             int x, int y, int width, int height,
                             AbstractModule module,
                             ISettingsScreen screen) {
        super(ui, x, y, width, height);
        this.module = module;
        this.screen = screen;

        backgroundColor(screen.getBgSecondary());
        addClass(StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM, StyleKey.HOVER_BRIGHTEN, StyleKey.P_2);

        updateLabel();
        checkForConflicts();

        onClick(() -> {
            isListening = !isListening;
            KeyBindingManager.getInstance().setStopKeybinding(isListening);
            updateLabel();
        });
    }

    @Override
    public void render(DrawContext context) {
        super.render(context);

        int cx = getCalculatedX();
        int cy = getCalculatedY();
        int cw = getCalculatedWidth();
        int ch = getCalculatedHeight();

        int radius = getBorderRadius();

        if (isListening) {
            DrawingUtils.drawRoundedRectBorder(context, cx, cy, cw, ch, radius, screen.getAccentColor(), 2);
        } else if (hasConflict) {
            DrawingUtils.drawRoundedRectBorder(context, cx, cy, cw, ch, radius, 0xFFE74C3C, 2); // rouge
        }
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (!isListening) return false;

        if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            removeKeybind();
            isListening = false;
            KeyBindingManager.getInstance().setStopKeybinding(false);
            updateLabel();
            checkForConflicts();
            return true;
        } else {
            assignKeybind(keyCode);
            isListening = false;
            KeyBindingManager.getInstance().setStopKeybinding(false);
            updateLabel();
            checkForConflicts();
            return true;
        }
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (isListening && button >= 0 && button < 7 && canInteract(mouseX, mouseY)) {
            assignKeybind(button);
            isListening = false;
            KeyBindingManager.getInstance().setStopKeybinding(false);
            updateLabel();
            checkForConflicts();
            return true;
        }
        return super.onMouseClick(mouseX, mouseY, button);
    }

    private void updateLabel() {
        KeyBinding kb = getModuleKeybind();
        if (isListening) {
            setText("⌨ Press a key...");
            textColor(screen.getTextPrimary());
        } else if (kb != null) {
            String icon = hasConflict ? "⚠️" : "⌨";
            setText(icon + " " + kb.getKeyName());
            textColor(screen.getTextPrimary());
        } else {
            setText("⌨ None");
            textColor(screen.getTextSecondary());
        }
    }

    private KeyBinding getModuleKeybind() {
        String keybindName = "toggle_" + module.getId().toLowerCase();
        return KeyBindingManager.getInstance().getKeyByName(keybindName);
    }

    private void removeKeybind() {
        String keybindName = "toggle_" + module.getId().toLowerCase();
        KeyBindingManager.getInstance().unregisterKeyBinding(keybindName);
        KeyBindingManager.getInstance().save();

        Main.OVERLAY_MANAGER.getLoggerOverlay().info(
                "⌨ Keybind removed from " + module.getName(), false
        );
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
                    String message = module.getName() + " " + status;
                    client.player.sendMessage(Text.literal(statusIcon + " " + message), false);
                }
            }
        });

        KeyBindingManager.getInstance().save();

        Main.OVERLAY_MANAGER.getLoggerOverlay().info(
                "⌨ Keybind set for " + module.getName(), false
        );
    }

    private void checkForConflicts() {
        KeyBinding kb = getModuleKeybind();
        if (kb == null) {
            hasConflict = false;
            conflictDescription = "";
            return;
        }

        int keyCode = kb.getKey();

        String mcConflict = MinecraftKeybinds.getConflictingKeybind(keyCode);
        if (mcConflict != null) {
            hasConflict = true;
            conflictDescription = "Conflicts with Minecraft: " + mcConflict;
            return;
        }

        for (KeyBinding other : KeyBindingManager.getInstance().getKeys()) {
            if (other != kb && other.getKey() == keyCode && !other.getName().equals(kb.getName())) {
                hasConflict = true;
                conflictDescription = "Conflicts with: " + other.getName();
                return;
            }
        }

        hasConflict = false;
        conflictDescription = "";
    }

    @Override
    public ModuleKeybindItem addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }

    public boolean hasConflict() { return hasConflict; }
    public String getConflictDescription() { return conflictDescription; }
    public boolean isListening() { return isListening; }
}
