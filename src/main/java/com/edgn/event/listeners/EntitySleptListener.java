package com.edgn.event.listeners;

import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;
import net.minecraft.entity.Entity;

import java.util.ArrayList;

public interface EntitySleptListener extends Listener {
    void onSleep(SleptEvent event);

    class SleptEvent extends CancellableEvent<EntitySleptListener> {
        private final Entity entity;

        public SleptEvent(Entity entity) {
            this.entity = entity;
        }

        @Override
        public void fire(ArrayList<EntitySleptListener> listeners) {
            for (EntitySleptListener listener : listeners) {
                listener.onSleep(this);

                if (isCancelled())
                    break;
            }
        }

        @Override
        public Class<EntitySleptListener> getListenerType() {
            return EntitySleptListener.class;
        }

        public Entity getEntity() {
            return this.entity;
        }
    }
}