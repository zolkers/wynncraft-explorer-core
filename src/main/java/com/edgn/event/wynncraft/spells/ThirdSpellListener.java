package com.edgn.event.wynncraft.spells;

import com.edgn.event.Event;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface ThirdSpellListener extends Listener {
    void onThirdSpell(ThirdSpellEvent event);

    class ThirdSpellEvent extends Event<ThirdSpellListener> {

        @Override
        public void fire(ArrayList<ThirdSpellListener> listeners) {
            for (ThirdSpellListener listener : listeners)
                listener.onThirdSpell(this);
        }

        @Override
        public Class<ThirdSpellListener> getListenerType() {
            return ThirdSpellListener.class;
        }

    }

}