package com.edgn.event.listeners;

import com.edgn.event.Event;
import com.edgn.event.Listener;
import net.minecraft.block.Block;

import java.util.ArrayList;

public interface BlockParticleListener extends Listener {
    void onBlockParticle(BlockParticleListener.BlockParticleEvent event);

    class BlockParticleEvent extends Event<BlockParticleListener> {
        private Block customBlock = null;

        public BlockParticleEvent() {}

        public void setCustomBlock(Block block) {
            this.customBlock = block;
        }

        public Block getCustomBlock() {
            return this.customBlock;
        }

        public boolean hasCustomBlock() {
            return this.customBlock != null;
        }

        @Override
        public void fire(ArrayList<BlockParticleListener> listeners) {
            for (BlockParticleListener listener : listeners) {
                listener.onBlockParticle(this);
            }
        }

        @Override
        public Class<BlockParticleListener> getListenerType() {
            return BlockParticleListener.class;
        }
    }
}
