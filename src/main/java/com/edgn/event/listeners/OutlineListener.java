package com.edgn.event.listeners;

import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;
import net.minecraft.entity.Entity;

import java.util.ArrayList;

public interface OutlineListener extends Listener {
    void onHasOutline(OutlineEvent event);

    class OutlineEvent extends CancellableEvent<OutlineListener> {
        private final Entity entity;

        public OutlineEvent(Entity entity) {
            this.entity = entity;
        }

        @Override
        public void fire(ArrayList<OutlineListener> listeners) {
            for (OutlineListener listener : listeners) {
                listener.onHasOutline(this);

                if (isCancelled())
                    break;
            }
        }

        @Override
        public Class<OutlineListener> getListenerType() {
            return OutlineListener.class;
        }

        public Entity getEntity() {
            return this.entity;
        }
    }
}