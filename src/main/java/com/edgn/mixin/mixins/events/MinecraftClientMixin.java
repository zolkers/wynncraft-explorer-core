package com.edgn.mixin.mixins.events;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.GameStopListener;
import com.edgn.event.listeners.OutlineListener;
import com.edgn.event.listeners.WorldListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(at = @At("HEAD"), method = "stop()V")
    private void onStop(CallbackInfo ci) {
        EventManager.fire(new GameStopListener.GameStopEvent());
    }

    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void outlineEntities(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        OutlineListener.OutlineEvent event = new OutlineListener.OutlineEvent(entity);
        EventManager.fire(event);
        if (event.isCancelled()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "joinWorld", at = @At("TAIL"))
    private void joinWorld(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason, CallbackInfo ci) {
        EventManager.fire(new WorldListener.JoinEvent(world));
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void disconnect(Screen screen, CallbackInfo info) {
        ClientWorld world = MinecraftClient.getInstance().world;

        if(world != null) EventManager.fire(new WorldListener.QuitEvent(world));

    }
}
