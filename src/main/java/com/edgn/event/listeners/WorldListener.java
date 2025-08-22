package com.edgn.event.listeners;

import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;
import net.minecraft.client.world.ClientWorld;

import java.util.ArrayList;

public interface WorldListener extends Listener {
    void onJoin(JoinEvent event);
    void onQuit(QuitEvent event);

    class JoinEvent extends CancellableEvent<WorldListener> {
        private final ClientWorld world;

        public JoinEvent(ClientWorld world) {
            this.world = world;
        }

        @Override
        public void fire(ArrayList<WorldListener> listeners) {
            for (WorldListener listener : listeners) {
                listener.onJoin(this);

                if (isCancelled())
                    break;
            }
        }

        @Override
        public Class<WorldListener> getListenerType() {
            return WorldListener.class;
        }

        public ClientWorld getClientWorld() {
            return this.world;
        }
    }

    class QuitEvent extends CancellableEvent<WorldListener> {
        private final ClientWorld world;

        public QuitEvent(ClientWorld world) {
            this.world = world;
        }

        @Override
        public void fire(ArrayList<WorldListener> listeners) {
            for (WorldListener listener : listeners) {
                listener.onQuit(this);

                if (isCancelled())
                    break;
            }
        }

        @Override
        public Class<WorldListener> getListenerType() {
            return WorldListener.class;
        }

        public ClientWorld getClientWorld() {
            return this.world;
        }
    }
}