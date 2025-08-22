package com.edgn.event.listeners;

import com.edgn.event.Event;
import com.edgn.event.Listener;
import net.minecraft.client.sound.SoundInstance;

import java.util.ArrayList;

public interface PlaySoundListener extends Listener {
    void onPlaySound(PlaySoundEvent event);

    class PlaySoundEvent extends Event<PlaySoundListener> {
        private final SoundInstance sound;

        public PlaySoundEvent(SoundInstance sound) {
            this.sound = sound;
        }

        public SoundInstance getSound() {
            return sound;
        }

        @Override
        public void fire(ArrayList<PlaySoundListener> listeners) {
            for (PlaySoundListener listener : listeners) {
                listener.onPlaySound(this);
            }
        }

        @Override
        public Class<PlaySoundListener> getListenerType() {
            return PlaySoundListener.class;
        }

    }
}
