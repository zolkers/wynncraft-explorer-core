package com.edgn.mixin.mixins.events;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.PacketListener;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
	
	@Inject(method = "channelRead0*", at = @At("TAIL"))
	private void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo info) {
		ClientConnection connection = (ClientConnection) (Object) this;
		
		NetworkSide side = connection.getSide();
		
		if(side != NetworkSide.CLIENTBOUND) return;

		EventManager.fire(new PacketListener.ReceivedEvent(packet));
	}
	
	@Inject(method = "sendImmediately", at = @At("HEAD"))
	private void sendImmediately(Packet<?> packet, PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
		ClientConnection connection = (ClientConnection) (Object) this;
		
		NetworkSide side = connection.getSide();
		
		if(side != NetworkSide.CLIENTBOUND) return;

		EventManager.fire(new PacketListener.SentEvent(packet));

	}
	
}
