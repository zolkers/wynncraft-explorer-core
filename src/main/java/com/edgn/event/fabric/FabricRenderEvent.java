package com.edgn.event.fabric;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.RenderListener;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class FabricRenderEvent implements WorldRenderEvents.Last {
    @Override
    public void onLast(WorldRenderContext context) {
        RenderListener.RenderEvent event = new RenderListener.RenderEvent(context.matrixStack(), context.camera().getLastTickDelta());
        EventManager.fire(event);
    }
}
