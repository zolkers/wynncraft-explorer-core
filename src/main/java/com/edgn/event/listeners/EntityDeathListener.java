package com.edgn.event.listeners;

import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;
import net.minecraft.entity.Entity;

import java.util.ArrayList;

public interface EntityDeathListener extends Listener {
    void onDeath(DeathEvent event);

    class DeathEvent extends CancellableEvent<EntityDeathListener> {
        private final Entity entity;

        public DeathEvent(Entity entity) {
            this.entity = entity;
        }

        @Override
        public void fire(ArrayList<EntityDeathListener> listeners) {
            for (EntityDeathListener listener : listeners) {
                listener.onDeath(this);

                if (isCancelled())
                    break;
            }
        }

        @Override
        public Class<EntityDeathListener> getListenerType() {
            return EntityDeathListener.class;
        }

        public Entity getEntity() {
            return this.entity;
        }
    }
}