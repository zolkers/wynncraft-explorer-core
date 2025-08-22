package com.edgn.event.listeners;

import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;

import java.util.ArrayList;

public interface CameraUpdateListener extends Listener {
    void onCameraUpdate(CameraUpdateEvent event);

    class CameraUpdateEvent extends CancellableEvent<CameraUpdateListener> {
        private final Camera camera;
        private final BlockView area;
        private final Entity focusedEntity;
        private final boolean thirdPerson;
        private final boolean inverseView;
        private final float tickDelta;

        public CameraUpdateEvent(Camera camera, BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
            this.camera = camera;
            this.area = area;
            this.focusedEntity = focusedEntity;
            this.thirdPerson = thirdPerson;
            this.inverseView = inverseView;
            this.tickDelta = tickDelta;
        }

        @Override
        public void fire(ArrayList<CameraUpdateListener> listeners) {
            for (CameraUpdateListener listener : listeners) {
                listener.onCameraUpdate(this);
                if (isCancelled()) break;
            }
        }

        @Override
        public Class<CameraUpdateListener> getListenerType() {
            return CameraUpdateListener.class;
        }

        public Camera getCamera() { return camera; }
        public BlockView getArea() { return area; }
        public Entity getFocusedEntity() { return focusedEntity; }
        public boolean isThirdPerson() { return thirdPerson; }
        public boolean isInverseView() { return inverseView; }
        public float getTickDelta() { return tickDelta; }
    }
}