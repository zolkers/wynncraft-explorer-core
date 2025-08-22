package com.edgn.mixin.mixins.events;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.OverlayMessageListener;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class IngameHudMixin {

    @Inject(at = @At("TAIL"), method = "setOverlayMessage")
    public void onSetOverlayMessage(Text message, boolean tinted, CallbackInfo ci) {
        OverlayMessageListener.OverlayMessageEvent event = new OverlayMessageListener.OverlayMessageEvent(message);
        EventManager.fire(event);
    }
}