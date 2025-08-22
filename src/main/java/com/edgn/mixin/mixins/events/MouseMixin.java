package com.edgn.mixin.mixins.events;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.MouseClickListener;
import com.edgn.event.listeners.MouseScrollingListener;
import com.edgn.event.listeners.MouseMoveListener; // Nouveau
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        MouseScrollingListener.MouseScrollingEvent event = new MouseScrollingListener.MouseScrollingEvent(window, horizontal, vertical);
        EventManager.fire(event);

        if(event.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "onMouseButton", cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        MouseClickListener.MouseClickEvent event = new MouseClickListener.MouseClickEvent(window, button, action, mods);
        EventManager.fire(event);

        if(event.isCancelled()) ci.cancel();
    }

    @Inject(method = "onCursorPos", at = @At("HEAD"), cancellable = true)
    private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
        MouseMoveListener.MouseMoveEvent event = new MouseMoveListener.MouseMoveEvent(window, x, y);
        EventManager.fire(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}