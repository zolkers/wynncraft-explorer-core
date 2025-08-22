package com.edgn.event.listeners;

import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface MouseScrollingListener extends Listener {
    void onMouseScroll(MouseScrollingEvent event);

    class MouseScrollingEvent extends CancellableEvent<MouseScrollingListener> {
        private final long window;
        private final double horizontal;
        private final double vertical;

        public MouseScrollingEvent(long window, double horizontal, double vertical) {
            this.window = window;
            this.horizontal = horizontal;
            this.vertical = vertical;
        }

        @Override
        public void fire(ArrayList<MouseScrollingListener> listeners) {
            for (MouseScrollingListener listener : listeners) {
                listener.onMouseScroll(this);

                if(isCancelled()) break;
            }
        }

        @Override
        public Class<MouseScrollingListener> getListenerType() {
            return MouseScrollingListener.class;
        }

        public long getWindow() {
            return window;
        }

        public double getHorizontal() {
            return horizontal;
        }

        public double getVertical() {
            return vertical;
        }
    }
}
