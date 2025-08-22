package com.edgn.event.listeners;

import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;
import net.minecraft.text.Text;

import java.util.ArrayList;

public interface TitleListener extends Listener {
    void onTitle(TitleEvent event);

    class TitleEvent extends CancellableEvent<TitleListener> {

        private final Text title;

        public TitleEvent(Text title) {
            this.title = title;
        }

        @Override
        public void fire(ArrayList<TitleListener> listeners) {
            for (TitleListener listener : listeners) {
                listener.onTitle(this);

                if(isCancelled()) break;
            }
        }

        @Override
        public Class<TitleListener> getListenerType() {
            return TitleListener.class;
        }

        public Text getTitle() {
            return title;
        }
    }
}