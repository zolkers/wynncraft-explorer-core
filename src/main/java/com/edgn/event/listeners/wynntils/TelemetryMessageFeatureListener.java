package com.edgn.event.listeners.wynntils;

import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface TelemetryMessageFeatureListener extends Listener {
    void onTelemetryMessage(TelemetryMessageFeatureEvent event);

    class TelemetryMessageFeatureEvent extends CancellableEvent<TelemetryMessageFeatureListener> {

        @Override
        public void fire(ArrayList<TelemetryMessageFeatureListener> listeners) {
            for (TelemetryMessageFeatureListener listener : listeners) {
                listener.onTelemetryMessage(this);

                if(isCancelled()) break;
            }
        }

        @Override
        public Class<TelemetryMessageFeatureListener> getListenerType() {
            return TelemetryMessageFeatureListener.class;
        }
    }
}
