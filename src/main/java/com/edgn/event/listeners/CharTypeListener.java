package com.edgn.event.listeners;

import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface CharTypeListener extends Listener {

    void onCharType(CharTypeEvent event);

    class CharTypeEvent extends CancellableEvent<CharTypeListener> {
        private final char character;
        private final int modifiers;

        public CharTypeEvent(char character, int modifiers) {
            this.character = character;
            this.modifiers = modifiers;
        }

        public char getCharacter() { return character; }
        public int getModifiers() { return modifiers; }

        @Override
        public void fire(ArrayList<CharTypeListener> listeners) {
            for (CharTypeListener listener : listeners) {
                listener.onCharType(this);
                if (isCancelled()) break;
            }
        }

        @Override
        public Class<CharTypeListener> getListenerType() {
            return CharTypeListener.class;
        }
    }
}