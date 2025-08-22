package com.edgn.event.fabric;

import com.edgn.Main;
import com.edgn.event.EventManager;
import com.edgn.event.listeners.UpdateListener;
import com.edgn.exceptions.EventException;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class FabricTickEvent implements ClientTickEvents.StartTick {
    private int tick = 0;
    @Override
    public void onStartTick(MinecraftClient minecraftClient) {
        tick++;
        try {
            UpdateListener.UpdateEvent event = new UpdateListener.UpdateEvent(tick);
            EventManager.fire(event);
        } catch (EventException e) {
            Main.LOGGER.error("CRITICAL ERROR WHEN FIRING TICK EVENT: {}", e.getMessage());
        }
    }
}
