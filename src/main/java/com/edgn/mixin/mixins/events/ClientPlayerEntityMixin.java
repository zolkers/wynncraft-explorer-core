package com.edgn.mixin.mixins.events;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.KnockbackListener;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Override
    public void setVelocityClient(double x, double y, double z) {
        KnockbackListener.KnockbackEvent event = new KnockbackListener.KnockbackEvent(x, y, z);
        EventManager.fire(event);
        super.setVelocityClient(event.getX(), event.getY(), event.getZ());
    }
}
