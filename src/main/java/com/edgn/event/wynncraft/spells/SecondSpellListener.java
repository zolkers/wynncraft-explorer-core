package com.edgn.event.wynncraft.spells;

import com.edgn.event.Event;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface SecondSpellListener extends Listener {
    void onSecondSpell(SecondSpellEvent event);

    class SecondSpellEvent extends Event<SecondSpellListener> {

        @Override
        public void fire(ArrayList<SecondSpellListener> listeners) {
            for (SecondSpellListener listener : listeners)
                listener.onSecondSpell(this);
        }

        @Override
        public Class<SecondSpellListener> getListenerType() {
            return SecondSpellListener.class;
        }

    }

}