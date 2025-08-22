package com.edgn.mixin.mixins.accessor;

import net.minecraft.client.sound.AbstractSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSoundInstance.class)
public interface AbstractSoundInstanceAccessor {
    @Mutable
    @Accessor("volume")
    void setVolume(float value);
}
