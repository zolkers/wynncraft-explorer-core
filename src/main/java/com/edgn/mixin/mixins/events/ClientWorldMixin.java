package com.edgn.mixin.mixins.events;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.BlockParticleListener;
import net.minecraft.block.Block;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Inject(at = @At("HEAD"),
            method = "getBlockParticle()Lnet/minecraft/block/Block;",
            cancellable = true)
    private void onGetBlockParticle(CallbackInfoReturnable<Block> cir) {
        BlockParticleListener.BlockParticleEvent event = new BlockParticleListener.BlockParticleEvent();
        EventManager.fire(event);

        if (event.hasCustomBlock()) {
            cir.setReturnValue(event.getCustomBlock());
        }
    }
}