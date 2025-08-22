package com.edgn.event.listeners;

import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public interface BlockUpdateListener extends Listener {
    void onBlockUpdate(BlockUpdateEvent event);

    class BlockUpdateEvent extends CancellableEvent<BlockUpdateListener> {
        private final BlockPos pos;

        public BlockUpdateEvent(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public void fire(ArrayList<BlockUpdateListener> listeners) {
            for (BlockUpdateListener listener : listeners) {
                listener.onBlockUpdate(this);

                if (isCancelled())
                    break;
            }
        }

        @Override
        public Class<BlockUpdateListener> getListenerType() {
            return BlockUpdateListener.class;
        }

        public BlockPos getPos() {
            return pos;
        }
    }
}