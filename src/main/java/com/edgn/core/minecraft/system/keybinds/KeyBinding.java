package com.edgn.core.minecraft.system.keybinds;

import com.google.gson.annotations.Expose;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {
    @Expose private String name;
    @Expose private int key;
    @Expose private boolean repeatable;

    private Runnable action;
    private long lastPress = System.currentTimeMillis();
    private boolean hasBeenSet = false;

    public KeyBinding() {}

    protected KeyBinding(String name, int key) {
        this.name = name;
        this.key = key;
    }

    public boolean isKeyPressed() {
        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
        boolean isPressed;

        if (key >= 0 && key < 7) {
            isPressed = GLFW.glfwGetMouseButton(windowHandle, key) == GLFW.GLFW_PRESS;
        } else if (key >= 7) {
            isPressed = InputUtil.isKeyPressed(windowHandle, key);
        } else {
            return false;
        }

        if (isPressed) {
            long currentTime = System.currentTimeMillis();
            int cooldown = 200;
            if (currentTime - lastPress < cooldown && (!this.isRepeatable() || hasBeenSet)) {
                return false;
            } else {
                hasBeenSet = false;
            }
            lastPress = currentTime;
        }

        return isPressed;
    }

    public boolean isPressed(boolean isPressed) {
        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();

        if (key >= 0 && key < 7) {
            return GLFW.glfwGetMouseButton(windowHandle, key) == GLFW.GLFW_PRESS || isPressed;
        } else if (key >= 7) {
            return InputUtil.isKeyPressed(windowHandle, key) || isPressed;
        } else {
            return false;
        }
    }

    public String getKeyName() {
        if (key < 0) {
            return "UNKNOWN";
        } else if (key < 7) {
            return "BUTTON_" + key;
        }
        return InputUtil.fromKeyCode(key, 0).getLocalizedText().getString();
    }

    public String getName() {
        return name;
    }

    public int getKey() {
        return key;
    }

    public Runnable getAction() {
        return action;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKey(int key) {
        lastPress = System.currentTimeMillis();
        hasBeenSet = true;
        this.key = key;
    }

    public void bindAction(Runnable action) {
        this.action = action;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public long getLastPress() {
        return lastPress;
    }

    public void setLastPress(long lastPress) {
        this.lastPress = lastPress;
    }
}