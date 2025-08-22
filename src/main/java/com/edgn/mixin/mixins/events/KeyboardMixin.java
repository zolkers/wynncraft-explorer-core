package com.edgn.mixin.mixins.events;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.KeyPressListener;
import com.edgn.event.listeners.CharTypeListener;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(at = @At("HEAD"), method = "onKey(JIIII)V", cancellable = true)
    private void onOnKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        KeyPressListener.KeyPressEvent event = new KeyPressListener.KeyPressEvent(key, scancode, action, modifiers);
        EventManager.fire(event);

        if(event.isCancelled()) ci.cancel();


    }

    @Inject(method = "onChar", at = @At("HEAD"))
    private void onChar(long window, int codepoint, int modifiers, CallbackInfo ci) {
        CharTypeListener.CharTypeEvent event = new CharTypeListener.CharTypeEvent((char) codepoint, modifiers);
        EventManager.fire(event);
    }
}