package com.edgn.mixin.mixins.events;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.CameraPositionListener;
import com.edgn.event.listeners.CameraUpdateListener;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {

    @Shadow private Vec3d pos;
    @Shadow private float pitch;
    @Shadow private float yaw;
    @Shadow private boolean ready;

    @Inject(method = "update", at = @At("HEAD"))
    private void onUpdateHead(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        CameraUpdateListener.CameraUpdateEvent updateEvent = new CameraUpdateListener.CameraUpdateEvent(
                (Camera)(Object)this, area, focusedEntity, thirdPerson, inverseView, tickDelta
        );
        EventManager.fire(updateEvent);
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void onUpdateTail(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        CameraPositionListener.CameraPositionEvent positionEvent = new CameraPositionListener.CameraPositionEvent(
                this.pos, this.yaw, this.pitch, this.ready
        );
        EventManager.fire(positionEvent);

        this.pos = positionEvent.getPosition();
        this.yaw = positionEvent.getYaw();
        this.pitch = positionEvent.getPitch();
    }
}
