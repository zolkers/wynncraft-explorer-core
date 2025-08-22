package com.edgn.event.listeners;

import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public interface CameraPositionListener extends Listener {
    void onCameraPosition(CameraPositionEvent event);

    class CameraPositionEvent extends CancellableEvent<CameraPositionListener> {
        private Vec3d position;
        private float yaw;
        private float pitch;
        private final boolean ready;

        public CameraPositionEvent(Vec3d position, float yaw, float pitch, boolean ready) {
            this.position = position;
            this.yaw = yaw;
            this.pitch = pitch;
            this.ready = ready;
        }

        @Override
        public void fire(ArrayList<CameraPositionListener> listeners) {
            for (CameraPositionListener listener : listeners) {
                listener.onCameraPosition(this);
                if (isCancelled()) break;
            }
        }

        @Override
        public Class<CameraPositionListener> getListenerType() {
            return CameraPositionListener.class;
        }

        public Vec3d getPosition() { return position; }
        public float getYaw() { return yaw; }
        public float getPitch() { return pitch; }
        public boolean isReady() { return ready; }

        public void setPosition(Vec3d position) { this.position = position; }
        public void setYaw(float yaw) { this.yaw = yaw; }
        public void setPitch(float pitch) { this.pitch = pitch; }
    }
}