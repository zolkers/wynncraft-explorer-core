package com.edgn.event.listeners;


import com.edgn.core.utils.KeyAction;
import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface MouseClickListener extends Listener {
    void onMouseClick(MouseClickEvent event);

    class MouseClickEvent extends CancellableEvent<MouseClickListener> {

        private final long window;
        private final int button;
        private final int action;
        private final int mod;

        public MouseClickEvent(long window, int button, int action, int mod) {
            this.window = window;
            this.button = button;
            this.action = action;
            this.mod = mod;
        }


        @Override
        public void fire(ArrayList<MouseClickListener> listeners) {
            for (MouseClickListener listener : listeners) {
                listener.onMouseClick(this);

                if(isCancelled()) break;
            }
        }

        @Override
        public Class<MouseClickListener> getListenerType() {
            return MouseClickListener.class;
        }

        public long getWindow() {
            return window;
        }

        public int getButton() {
            return button;
        }

        public int getAction() {
            return action;
        }

        public KeyAction getKeyAction() {
            return KeyAction.get(this.action);
        }

        public int getMod() {
            return mod;
        }
    }
}
