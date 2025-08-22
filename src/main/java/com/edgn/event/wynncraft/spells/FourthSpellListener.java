package com.edgn.event.wynncraft.spells;

import com.edgn.event.Event;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface FourthSpellListener extends Listener {
    void onFourthSpell(FourthSpellEvent event);

    class FourthSpellEvent extends Event<FourthSpellListener> {

        @Override
        public void fire(ArrayList<FourthSpellListener> listeners) {
            for (FourthSpellListener listener : listeners)
                listener.onFourthSpell(this);
        }

        @Override
        public Class<FourthSpellListener> getListenerType() {
            return FourthSpellListener.class;
        }

    }

}