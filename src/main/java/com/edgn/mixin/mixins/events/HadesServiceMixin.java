package com.edgn.mixin.mixins.events;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.wynntils.HadesServiceListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.wynntils.services.hades.HadesService")
public abstract class HadesServiceMixin {

    @Inject(at = @At("HEAD"), method = "tryCreateConnection()V", cancellable = true)
    private void tryCreateConnection(CallbackInfo ci) {
        HadesServiceListener.HadesServiceEvent event = new HadesServiceListener.HadesServiceEvent();
        EventManager.fire(event);

        if(event.isCancelled()) ci.cancel();
    }
}