package com.edgn.core.module.basic.modules.utility;

import com.edgn.Main;
import com.edgn.core.config.ConfigManager;
import com.edgn.event.listeners.PlaySoundListener;
import com.edgn.mixin.mixins.accessor.AbstractSoundInstanceAccessor;
import com.edgn.core.module.basic.AbstractModule;
import com.edgn.core.module.basic.ModuleCategory;
import com.edgn.core.module.basic.ModuleInfo;
import com.edgn.core.module.settings.SettingsGroup;
import com.google.gson.annotations.Expose;
import net.minecraft.client.sound.SoundInstance;

@ModuleInfo(
        name = "Sound Controller",
        description = "Control specific sounds of your game (some sounds may affect others even if it's unlikely)",
        category = ModuleCategory.UTILITY,
        authors = {"YalcElBoon, EDGN"},
        version = "1.1.0"
)
public class SoundControllerModule extends AbstractModule implements PlaySoundListener {
    private static final String DING = "minecraft:entity.arrow.hit_player";
    private static final String EPOCH_SOUND = "minecraft:item.flintandsteel.use";

    @ConfigManager.SaveField
    @Expose
    private double dingVolume = 50.0;

    @ConfigManager.SaveField
    @Expose
    private double epochArrowVolume = 50.0;

    public SoundControllerModule() {
        super("Sound Controller");
    }

    @Override
    protected void initializeSettings() {
        SettingsGroup soundGroup = new SettingsGroup("General Panel", "Sound panel settings");
        SettingsGroup archerGroup = new SettingsGroup("Archer Panel", "Archer sound panel");

        soundGroup.addDoubleSlider("Spell noise controller", "Control the spell sound", this.dingVolume, 0, 100, 1)
                .setOnValueChanged((oldValue, newValue) -> this.dingVolume = (Double) newValue);

        archerGroup.addDoubleSlider("Tier stack controller", "Control the noise of archer tier stack", this.epochArrowVolume, 0, 100, 1)
                .setOnValueChanged((oldValue, newValue) -> this.epochArrowVolume = (Double) newValue);

        this.settingsGroups.add(soundGroup);
        this.settingsGroups.add(archerGroup);
    }


    @Override
    protected void onEnable() {
        Main.EVENT_MANAGER.add(PlaySoundListener.class, this);
    }

    @Override
    protected void onDisable() {
        Main.EVENT_MANAGER.remove(PlaySoundListener.class, this);
    }

    @Override
    public void onPlaySound(PlaySoundEvent event) {
        SoundInstance sound = event.getSound();
        if (sound instanceof AbstractSoundInstanceAccessor) {
            String soundId = sound.getId().toString();

            if (soundId.equalsIgnoreCase(DING)) {
                float volumeLevel = (float) (dingVolume / 100.0);
                ((AbstractSoundInstanceAccessor) sound).setVolume(volumeLevel);
            } else if (soundId.equalsIgnoreCase(EPOCH_SOUND)) {
                float volumeLevel = (float) (epochArrowVolume / 100.0);
                ((AbstractSoundInstanceAccessor) sound).setVolume(volumeLevel);
            }
        }
    }

    @Override
    public void onSettingsChanged() {

    }
}