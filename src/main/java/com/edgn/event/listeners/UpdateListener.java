package com.edgn.event.listeners;


import com.edgn.event.Event;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface UpdateListener extends Listener {
    void onUpdate(UpdateEvent event);

    class UpdateEvent extends Event<UpdateListener> {
        private int tick;

        public UpdateEvent(int tick) {
            this.tick = tick;
        }

        @Override
        public void fire(ArrayList<UpdateListener> listeners) {
            tick++;
            for (UpdateListener listener : listeners)
                listener.onUpdate(this);
        }

        @Override
        public Class<UpdateListener> getListenerType() {
            return UpdateListener.class;
        }

        public int getTick() {
            return tick;
        }
    }

}
