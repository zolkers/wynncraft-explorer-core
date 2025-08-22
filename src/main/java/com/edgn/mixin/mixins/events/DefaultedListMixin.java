package com.edgn.mixin.mixins.events;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.InventoryListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@Mixin(DefaultedList.class)
public class DefaultedListMixin<T> {
	
	@SuppressWarnings("unchecked")
	@Inject(method = "set", at = @At("TAIL"))
	private void set(int index, @Coerce T element, CallbackInfoReturnable<T> info) {
		if(!(element instanceof ItemStack)) return;
		
		DefaultedList<ItemStack> list = (DefaultedList<ItemStack>) (Object) this;

		EventManager.fire(new InventoryListener.UpdateEvent(list));
	}
	
}
