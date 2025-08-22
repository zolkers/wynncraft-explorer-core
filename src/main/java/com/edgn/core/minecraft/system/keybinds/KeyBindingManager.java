package com.edgn.core.minecraft.system.keybinds;

import com.edgn.Main;
import com.edgn.core.config.ConfigManager;
import com.edgn.event.listeners.KeyPressListener;
import com.edgn.event.listeners.MouseClickListener;
import com.edgn.event.listeners.UpdateListener;
import com.edgn.core.module.basic.AbstractModule;
import com.edgn.core.module.basic.ModuleManager;
import com.google.gson.annotations.Expose;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class KeyBindingManager implements KeyPressListener, MouseClickListener, UpdateListener {
    private static KeyBindingManager keyBindingManager;

    @Expose
    private ArrayList<KeyBinding> keys = new ArrayList<>();
    private boolean stopKeybinding = false;

    private KeyBindingManager() {
    }

    @Override
    public void onKeyPress(KeyPressListener.KeyPressEvent event) {
        if (stopKeybinding) return;
        this.update();
    }

    @Override
    public void onMouseClick(MouseClickListener.MouseClickEvent event) {
        if (stopKeybinding) return;
        this.update();
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (stopKeybinding) return;
        this.updateRepeatable();
    }

    public static KeyBindingManager getInstance() {
        if (keyBindingManager == null) {
            keyBindingManager = new KeyBindingManager();
        }
        return keyBindingManager;
    }

    public void init() {
        Main.EVENT_MANAGER.add(KeyPressListener.class, this);
        Main.EVENT_MANAGER.add(MouseClickListener.class, this);
        Main.EVENT_MANAGER.add(UpdateListener.class, this);
        load();
    }

    public void update() {
        for (KeyBinding keyBinding : keys) {
            if (keyBinding.isKeyPressed() && keyBinding.getAction() != null) {
                keyBinding.getAction().run();
            }
        }
    }

    public void updateRepeatable() {
        for (KeyBinding keyBinding : keys) {
            if (keyBinding.isKeyPressed() && keyBinding.isRepeatable() && keyBinding.getAction() != null) {
                keyBinding.getAction().run();
            }
        }
    }

    public KeyBinding registerKeyBinding(KeyBinding key) {
        return registerKeyBinding(key.getName(), key.getKey());
    }

    public KeyBinding registerKeyBinding(String name, int keyCode) {
        KeyBinding keyBinding = new KeyBinding(name, keyCode);
        KeyBinding existing = getKeyByName(name);

        if (existing == null) {
            keys.add(keyBinding);
        } else {
            existing.setKey(keyCode);
            if (keyBinding.getAction() != null) {
                existing.bindAction(keyBinding.getAction());
            }
            keyBinding = existing;
        }

        return keyBinding;
    }

    public void unregisterKeyBinding(String name) {
        KeyBinding keyBinding = getKeyByName(name);
        if (keyBinding != null) {
            keys.remove(keyBinding);
        }
    }

    /**
     * Nettoie tous les keybindings de macros
     */
    public void clearAllMacroKeybindings() {
        keys.removeIf(keyBinding -> keyBinding.getName().startsWith("macro_"));
    }

    /**
     * Rebuild les actions pour les modules après chargement
     */
    public void rebuildActiveKeyBindings() {
        rebindModuleActions();
    }

    public ArrayList<KeyBinding> getKeys() {
        return keys;
    }

    public void setKeys(ArrayList<KeyBinding> keys) {
        this.keys = keys;
    }

    public KeyBinding getKeyByName(String name) {
        return keys.stream()
                .filter(keyBinding -> keyBinding.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public boolean isStopKeybinding() {
        return stopKeybinding;
    }

    public void setStopKeybinding(boolean stopKeybinding) {
        this.stopKeybinding = stopKeybinding;
    }

    public void save() {
        ConfigManager.saveObject("keybinds", this, KeyBindingManager.class);
    }

    public void load() {
        KeyBindingManager loaded = ConfigManager.loadObject("keybinds", KeyBindingManager.class, KeyBindingManager::new);
        if (loaded != null && loaded.keys != null) {
            this.keys = loaded.keys;
            rebindModuleActions();
        }
    }

    public void rebindModuleActions() {
        for (KeyBinding keyBinding : keys) {
            String keybindName = keyBinding.getName();

            if (keybindName.startsWith("toggle_")) {
                String moduleId = keybindName.substring("toggle_".length());

                AbstractModule module = ModuleManager.getInstance().getModule(moduleId);
                if (module != null) {
                    keyBinding.bindAction(() -> {
                        if (MinecraftClient.getInstance().currentScreen == null) {
                            module.toggle();

                            MinecraftClient client = MinecraftClient.getInstance();
                            if (client != null && client.player != null) {
                                String statusIcon = module.isEnabled() ? "🟢" : "🔴";
                                String status = module.isEnabled() ? "ACTIVATED" : "DEACTIVATED";
                                String themeIcon = "🍉";

                                String message = String.format("%s %s %s", themeIcon, module.getName(), status);
                                client.player.sendMessage(Text.literal(statusIcon + " " + message), false);
                            }
                        }
                    });
                }
            }
        }
    }

    public String getKeyName(int keyCode) {
        if (keyCode == -1) return "None";

        if (keyCode >= 0 && keyCode <= 7) {
            switch (keyCode) {
                case 0:
                    return "Left Click";
                case 1:
                    return "Right Click";
                case 2:
                    return "Middle Click";
                case 3:
                    return "Mouse 4";
                case 4:
                    return "Mouse 5";
                case 5:
                    return "Mouse 6";
                case 6:
                    return "Mouse 7";
                case 7:
                    return "Mouse 8";
            }
        }

        try {
            String keyName = org.lwjgl.glfw.GLFW.glfwGetKeyName(keyCode, 0);
            if (keyName != null && !keyName.isEmpty()) {
                return keyName.toUpperCase();
            }

            return switch (keyCode) {
                case 256 -> "ESC";
                case 257 -> "ENTER";
                case 258 -> "TAB";
                case 259 -> "BACKSPACE";
                case 260 -> "INSERT";
                case 261 -> "DELETE";
                case 262 -> "RIGHT";
                case 263 -> "LEFT";
                case 264 -> "DOWN";
                case 265 -> "UP";
                case 266 -> "PAGE UP";
                case 267 -> "PAGE DOWN";
                case 268 -> "HOME";
                case 269 -> "END";
                case 280 -> "CAPS LOCK";
                case 281 -> "SCROLL LOCK";
                case 282 -> "NUM LOCK";
                case 283 -> "PRINT SCREEN";
                case 284 -> "PAUSE";
                case 290 -> "F1";
                case 291 -> "F2";
                case 292 -> "F3";
                case 293 -> "F4";
                case 294 -> "F5";
                case 295 -> "F6";
                case 296 -> "F7";
                case 297 -> "F8";
                case 298 -> "F9";
                case 299 -> "F10";
                case 300 -> "F11";
                case 301 -> "F12";
                case 320 -> "NUMPAD 0";
                case 321 -> "NUMPAD 1";
                case 322 -> "NUMPAD 2";
                case 323 -> "NUMPAD 3";
                case 324 -> "NUMPAD 4";
                case 325 -> "NUMPAD 5";
                case 326 -> "NUMPAD 6";
                case 327 -> "NUMPAD 7";
                case 328 -> "NUMPAD 8";
                case 329 -> "NUMPAD 9";
                case 330 -> "NUMPAD .";
                case 331 -> "NUMPAD /";
                case 332 -> "NUMPAD *";
                case 333 -> "NUMPAD -";
                case 334 -> "NUMPAD +";
                case 335 -> "NUMPAD ENTER";
                case 336 -> "NUMPAD =";
                case 340 -> "LEFT SHIFT";
                case 341 -> "LEFT CTRL";
                case 342 -> "LEFT ALT";
                case 343 -> "LEFT SUPER";
                case 344 -> "RIGHT SHIFT";
                case 345 -> "RIGHT CTRL";
                case 346 -> "RIGHT ALT";
                case 347 -> "RIGHT SUPER";
                case 348 -> "MENU";
                default -> "KEY " + keyCode;
            };
        } catch (Exception e) {
            return "KEY " + keyCode;
        }
    }
}