package com.edgn.mixin.mixins.events;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.EntityDeathListener;
import com.edgn.event.listeners.EntitySleptListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	
	@Inject(method = "setHealth(F)V", at = @At("TAIL"))
	private void setHealth(float health, CallbackInfo info) {
		if(health <= 0) {
			
			LivingEntity entity = (LivingEntity) (Object) this;
			
			EventManager.fire(new EntityDeathListener.DeathEvent(entity));
		}
	}
	
	@Inject(method = "wakeUp()V", at = @At("TAIL"))
	private void wakeUp(CallbackInfo info) {
		LivingEntity entity = (LivingEntity) (Object) this;

		EventManager.fire(new EntitySleptListener.SleptEvent(entity));

	}
	
}
