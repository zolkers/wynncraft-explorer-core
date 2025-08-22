package com.edgn.event.listeners;

import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface MouseMoveListener extends Listener {

    void onMouseMove(MouseMoveEvent event);

    class MouseMoveEvent extends CancellableEvent<MouseMoveListener> {
        private final long window;
        private final double x;
        private final double y;

        public MouseMoveEvent(long window, double x, double y) {
            this.window = window;
            this.x = x;
            this.y = y;
        }

        public long getWindow() { return window; }
        public double getX() { return x; }
        public double getY() { return y; }

        @Override
        public void fire(ArrayList<MouseMoveListener> listeners) {
            for (MouseMoveListener listener : listeners) {
                listener.onMouseMove(this);
                if (isCancelled()) break;
            }
        }

        @Override
        public Class<MouseMoveListener> getListenerType() {
            return MouseMoveListener.class;
        }
    }
}