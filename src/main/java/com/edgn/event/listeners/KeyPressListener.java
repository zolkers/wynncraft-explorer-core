package com.edgn.event.listeners;


import com.edgn.core.utils.KeyAction;
import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface KeyPressListener extends Listener {
    void onKeyPress(KeyPressEvent event);

    class KeyPressEvent extends CancellableEvent<KeyPressListener> {
        private final int keyCode;
        private final int scanCode;
        private final int action;
        private final int modifiers;

        public KeyPressEvent(int keyCode, int scanCode, int action, int modifiers) {
            this.keyCode = keyCode;
            this.scanCode = scanCode;
            this.action = action;
            this.modifiers = modifiers;
        }

        @Override
        public void fire(ArrayList<KeyPressListener> listeners) {
            for (KeyPressListener listener : listeners) {
                listener.onKeyPress(this);

                if(isCancelled()) break;
            }

        }

        @Override
        public Class<KeyPressListener> getListenerType() {
            return KeyPressListener.class;
        }

        public int getKeyCode() {
            return keyCode;
        }

        public int getScanCode() {
            return scanCode;
        }

        public int getAction() {
            return action;
        }

        public KeyAction getKeyAction() {
            return KeyAction.get(this.action);
        }

        public int getModifiers() {
            return modifiers;
        }
    }
}
