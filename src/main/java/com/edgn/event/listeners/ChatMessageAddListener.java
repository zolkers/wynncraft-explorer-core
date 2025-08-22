package com.edgn.event.listeners;

import com.edgn.event.CancellableEvent;
import com.edgn.event.Listener;
import net.minecraft.text.Text;

import java.util.ArrayList;

public interface ChatMessageAddListener extends Listener {

    void onAddMessage(ChatMessageAddEvent event);

    class ChatMessageAddEvent extends CancellableEvent<ChatMessageAddListener> {
        private final Text originalMessage;
        private Text modifiedMessage;

        public ChatMessageAddEvent(Text message) {
            this.originalMessage = message;
            this.modifiedMessage = null;
        }

        public void setNewMessage(Text newMessage) {
            this.modifiedMessage = newMessage;
        }

        public Text getOriginalMessage() {
            return this.originalMessage;
        }

        public Text getModifiedMessage() {
            return this.modifiedMessage;
        }

        public boolean isModified() {
            return this.modifiedMessage != null;
        }

        @Override
        public void fire(ArrayList<ChatMessageAddListener> listeners) {
            for (ChatMessageAddListener listener : listeners) {
                listener.onAddMessage(this);
                if(isCancelled()) {
                    break;
                }
            }
        }

        @Override
        public Class<ChatMessageAddListener> getListenerType() {
            return ChatMessageAddListener.class;
        }
    }
}