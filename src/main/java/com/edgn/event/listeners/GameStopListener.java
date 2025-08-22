package com.edgn.event.listeners;


import com.edgn.event.Event;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface GameStopListener extends Listener {
    void onStop(GameStopEvent event);

    class GameStopEvent extends Event<GameStopListener> {

        public GameStopEvent() {}

        @Override
        public void fire(ArrayList<GameStopListener> listeners) {
            for (GameStopListener listener : listeners)
                listener.onStop(this);
        }

        @Override
        public Class<GameStopListener> getListenerType() {
            return GameStopListener.class;
        }
    }

}
