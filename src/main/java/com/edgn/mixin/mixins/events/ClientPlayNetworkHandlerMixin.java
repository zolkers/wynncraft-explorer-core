package com.edgn.mixin.mixins.events;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.ScoreboardListener;
import com.edgn.event.listeners.TitleListener;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onTitle", at = @At("HEAD"), cancellable = true)
    private void onTitle(TitleS2CPacket packet, CallbackInfo info) {
        if(packet.text() != null) {
            TitleListener.TitleEvent event = new TitleListener.TitleEvent(packet.text());
            EventManager.fire(event);

            if(event.isCancelled()) info.cancel();
        }
    }

    @Inject(method = "onScoreboardScoreUpdate", at = @At("TAIL"))
    private void onScoreboardScoreUpdate(ScoreboardScoreUpdateS2CPacket packet, CallbackInfo ci) {
        ScoreboardListener.ScoreboardUpdateEvent event = new ScoreboardListener.ScoreboardUpdateEvent(
                null,
                packet.scoreHolderName(),
                packet.objectiveName(),
                packet.score(),
                packet.display().isPresent() ? packet.display().get() : null
        );
        EventManager.fire(event);
    }

    @Inject(method = "onScoreboardScoreReset", at = @At("TAIL"))
    private void onScoreboardScoreResetUpdate(ScoreboardScoreResetS2CPacket packet, CallbackInfo ci) {
        ScoreboardListener.ScoreboardScoreResetEvent event = new ScoreboardListener.ScoreboardScoreResetEvent(
                packet.scoreHolderName(),
                packet.objectiveName()
        );
        EventManager.fire(event);
    }
}