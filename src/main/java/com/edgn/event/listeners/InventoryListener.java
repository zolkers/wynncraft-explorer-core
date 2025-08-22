package com.edgn.event.listeners;

import com.edgn.event.Event;
import com.edgn.event.Listener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;

public interface InventoryListener extends Listener {
    void onInventoryUpdate(InventoryListener.UpdateEvent event);

    class UpdateEvent extends Event<InventoryListener> {
        private final DefaultedList<ItemStack> list;

        public UpdateEvent(DefaultedList<ItemStack> list) {
            this.list = list;
        }

        public DefaultedList<ItemStack> getList() {
            return list;
        }

        @Override
        public void fire(ArrayList<InventoryListener> listeners) {
            for (InventoryListener listener : listeners) {
                listener.onInventoryUpdate(this);
            }
        }

        @Override
        public Class<InventoryListener> getListenerType() {
            return InventoryListener.class;
        }

    }
}
