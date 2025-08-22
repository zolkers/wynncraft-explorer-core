package com.edgn.core.minecraft.system.keybinds;

import com.edgn.Main;
import com.edgn.core.minecraft.ui.screens.MainScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class MinecraftKeybinds {
    private static final String CATEGORY = "Wynncraft Explorer";
    private static final KeyBinding openMainScreenKey = new KeyBinding(
            "com.edgn.core.minecraft.system.keybinds.openMainScreenKey",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_1,
            CATEGORY
    );

    private static final KeyBinding toggleLoggerKey = new KeyBinding(
            "com.edgn.core.minecraft.system.keybinds.toggleLoggerKey",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_L,
            CATEGORY
    );

    public static void initialize() {
        KeyBindingHelper.registerKeyBinding(openMainScreenKey);
        KeyBindingHelper.registerKeyBinding(toggleLoggerKey);
        KeyBindingManager.getInstance().init();
    }

    public static void onKeyPressed(MinecraftClient client) {
        if(client.player == null) return;

        if (openMainScreenKey.wasPressed()) {
            client.setScreen(new MainScreen());
        }

        if (toggleLoggerKey.wasPressed()) {
            Main.OVERLAY_MANAGER.getLoggerOverlay().toggle();
        }

        KeyBindingManager.getInstance().update();
    }

    public static String getConflictingKeybind(int keyCode) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            return null;
        }
        return null;
    }


}