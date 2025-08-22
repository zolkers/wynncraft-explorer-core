package com.edgn.event.listeners.wynntils;

import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;

import java.util.ArrayList;

public interface DataCrowdSourcingMessageFeatureListener extends Listener {
    void onCrowdSourcingMessage(DataCrowdSourcingMessageFeatureEvent event);

    class DataCrowdSourcingMessageFeatureEvent extends CancellableEvent<DataCrowdSourcingMessageFeatureListener> {

        @Override
        public void fire(ArrayList<DataCrowdSourcingMessageFeatureListener> listeners) {
            for (DataCrowdSourcingMessageFeatureListener listener : listeners) {
                listener.onCrowdSourcingMessage(this);

                if(isCancelled()) break;
            }
        }

        @Override
        public Class<DataCrowdSourcingMessageFeatureListener> getListenerType() {
            return DataCrowdSourcingMessageFeatureListener.class;
        }
    }
}
