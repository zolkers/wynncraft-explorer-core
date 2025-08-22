package com.edgn.event.listeners;

import com.edgn.event.Event;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface ChunkListener extends Listener {
    void onChunkData(ChunkDataEvent event);

    class ChunkDataEvent extends Event<ChunkListener> {
        int x;
        int z;

        public ChunkDataEvent(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }

        @Override
        public void fire(ArrayList<ChunkListener> listeners) {
            for (ChunkListener listener : listeners) {
                listener.onChunkData(this);
            }
        }

        @Override
        public Class<ChunkListener> getListenerType() {
            return ChunkListener.class;
        }

    }
}
