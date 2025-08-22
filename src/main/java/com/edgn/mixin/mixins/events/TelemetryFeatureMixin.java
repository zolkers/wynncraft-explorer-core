package com.edgn.mixin.mixins.events;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.wynntils.TelemetryMessageFeatureListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.wynntils.features.wynntils.TelemetryFeature")
public class TelemetryFeatureMixin {
    @Inject(at = @At("HEAD"), method = "onWorldChange", cancellable = true)
    private void onWynntilsCrowdSourcingFeature(CallbackInfo ci){
        TelemetryMessageFeatureListener.TelemetryMessageFeatureEvent e = new TelemetryMessageFeatureListener.TelemetryMessageFeatureEvent();
        EventManager.fire(e);

        if(e.isCancelled()) ci.cancel();

    }
}
