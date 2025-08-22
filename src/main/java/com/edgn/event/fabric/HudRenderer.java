package com.edgn.event.fabric;

import com.edgn.Main;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.util.Identifier;

public class HudRenderer {
    private static final Identifier LOGGER_LAYER_ID = Identifier.of("edgn", "logger_overlay");

    public static void register() {
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> {
            LayeredDrawer.Layer loggerLayer = (context, delta) -> {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null) {
                    Main.OVERLAY_MANAGER.getLoggerOverlay().render(context, client);
                }
            };

            layeredDrawer.attachLayerAfter(
                    IdentifiedLayer.CHAT,
                    LOGGER_LAYER_ID,
                    loggerLayer
            );
        });
    }
}