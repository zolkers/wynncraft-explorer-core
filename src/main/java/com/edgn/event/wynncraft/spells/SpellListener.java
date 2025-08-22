package com.edgn.event.wynncraft.spells;

import com.edgn.event.Event;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface SpellListener extends Listener {
    void onSpell(SpellEvent event);

    class SpellEvent extends Event<SpellListener> {

        @Override
        public void fire(ArrayList<SpellListener> listeners) {
            for (SpellListener listener : listeners)
                listener.onSpell(this);
        }

        @Override
        public Class<SpellListener> getListenerType() {
            return SpellListener.class;
        }

    }

}