package com.edgn.mixin.mixins.events;



import com.edgn.event.EventManager;
import com.edgn.event.listeners.PlaySoundListener;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"))
    public void play(SoundInstance sound, CallbackInfo ci) {
        EventManager.fire(new PlaySoundListener.PlaySoundEvent(sound));
    }
}