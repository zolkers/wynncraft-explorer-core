package com.edgn.event.listeners.wynntils;

import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface HadesServiceListener extends Listener {
    void onHaderService(HadesServiceListener.HadesServiceEvent event);

    class HadesServiceEvent extends CancellableEvent<HadesServiceListener> {

        @Override
        public void fire(ArrayList<HadesServiceListener> listeners) {
            for (HadesServiceListener listener : listeners) {
                listener.onHaderService(this);

                if(isCancelled()) break;
            }
        }

        @Override
        public Class<HadesServiceListener> getListenerType() {
            return HadesServiceListener.class;
        }
    }
}
