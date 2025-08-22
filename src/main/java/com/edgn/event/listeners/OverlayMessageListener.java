package com.edgn.event.listeners;

import com.edgn.event.Event;
import com.edgn.event.Listener;
import net.minecraft.text.Text;

import java.util.ArrayList;

public interface OverlayMessageListener extends Listener {
    void onOverlayMessage(OverlayMessageEvent event);

    class OverlayMessageEvent extends Event<OverlayMessageListener> {
        private final Text message;

        public OverlayMessageEvent(Text message) {
            this.message = message;
        }

        @Override
        public void fire(ArrayList<OverlayMessageListener> listeners) {
            for (OverlayMessageListener listener : listeners) {
                listener.onOverlayMessage(this);
            }
        }

        @Override
        public Class<OverlayMessageListener> getListenerType() {
            return OverlayMessageListener.class;
        }

        public Text getMessage() {
            return message;
        }
    }
}