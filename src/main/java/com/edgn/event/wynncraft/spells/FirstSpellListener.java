package com.edgn.event.wynncraft.spells;

import com.edgn.event.Event;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface FirstSpellListener extends Listener {
    void onFirstSpell(FirstSpellEvent event);

    class FirstSpellEvent extends Event<FirstSpellListener> {

        @Override
        public void fire(ArrayList<FirstSpellListener> listeners) {
            for (FirstSpellListener listener : listeners)
                listener.onFirstSpell(this);
        }

        @Override
        public Class<FirstSpellListener> getListenerType() {
            return FirstSpellListener.class;
        }

    }

}