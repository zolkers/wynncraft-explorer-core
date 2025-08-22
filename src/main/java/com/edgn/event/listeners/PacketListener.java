package com.edgn.event.listeners;

import com.edgn.event.Event;
import com.edgn.event.Listener;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;

public interface PacketListener extends Listener {
    void onSent(PacketListener.SentEvent event);
    void onReceived(PacketListener.ReceivedEvent event);

    class SentEvent extends Event<PacketListener> {
        private final Packet<?> packet;

        public SentEvent(Packet<?> packet) {
            this.packet = packet;
        }

        @Override
        public void fire(ArrayList<PacketListener> listeners) {
            for (PacketListener listener : listeners) {
                listener.onSent(this);

            }
        }

        @Override
        public Class<PacketListener> getListenerType() {
            return PacketListener.class;
        }

        public Packet<?> getPacket() {
            return packet;
        }
    }

    class ReceivedEvent extends Event<PacketListener> {
        private final Packet<?> packet;

        public ReceivedEvent(Packet<?> packet) {
            this.packet = packet;
        }

        @Override
        public void fire(ArrayList<PacketListener> listeners) {
            for (PacketListener listener : listeners) {
                listener.onReceived(this);

            }
        }

        @Override
        public Class<PacketListener> getListenerType() {
            return PacketListener.class;
        }

        public Packet<?> getPacket() {
            return packet;
        }
    }
}
