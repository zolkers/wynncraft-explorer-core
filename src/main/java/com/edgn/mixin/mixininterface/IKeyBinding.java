package com.edgn.mixin.mixininterface;

import net.minecraft.client.option.KeyBinding;

public interface IKeyBinding {

    default void resetPressedState() {
        __wynncraft_explorer_resetPressedState();
    }

    @SuppressWarnings("unused")
    default void simulatePress(boolean pressed) {
        __wynncraft_explorer_simulatePress(pressed);
    }

    default void setPressed(boolean pressed) {
        asVanilla().setPressed(pressed);
    }

    default KeyBinding asVanilla() {
        return (KeyBinding) this;
    }

    static IKeyBinding get(KeyBinding kb) {
        return (IKeyBinding) kb;
    }

    void __wynncraft_explorer_resetPressedState();

    void __wynncraft_explorer_simulatePress(boolean pressed);
}