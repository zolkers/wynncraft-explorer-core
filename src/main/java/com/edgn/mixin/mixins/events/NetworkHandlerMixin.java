package com.edgn.mixin.mixins.events;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.BlockUpdateListener;
import com.edgn.event.listeners.ChunkListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

@Mixin(ClientPlayNetworkHandler.class)
public class NetworkHandlerMixin {
	
	@Inject(method = "onChunkData(Lnet/minecraft/network/packet/s2c/play/ChunkDataS2CPacket;)V", at = @At("TAIL"))
	private void onChunkData(ChunkDataS2CPacket packet, CallbackInfo info) {
		int x = packet.getChunkX();
		int z = packet.getChunkZ();

		EventManager.fire(new ChunkListener.ChunkDataEvent(x,z));

	}
	
	@Inject(method = "onChunkDeltaUpdate(Lnet/minecraft/network/packet/s2c/play/ChunkDeltaUpdateS2CPacket;)V", at = @At("TAIL"))
	private void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet, CallbackInfo info) {
		packet.visitUpdates((pos, state) -> {

			EventManager.fire(new BlockUpdateListener.BlockUpdateEvent(pos));
		});
	}
	
	@Inject(method = "onBlockUpdate(Lnet/minecraft/network/packet/s2c/play/BlockUpdateS2CPacket;)V", at = @At("TAIL"))
	private void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo info) {
		BlockPos pos = packet.getPos();
		
		EventManager.fire(new BlockUpdateListener.BlockUpdateEvent(pos));
	}
	
}
