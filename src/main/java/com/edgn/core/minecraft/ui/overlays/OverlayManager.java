package com.edgn.core.minecraft.ui.overlays;

import com.edgn.Main;
import com.edgn.core.minecraft.ui.overlays.overlays.LoggerOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.HashMap;
import java.util.Map;

public class OverlayManager {
    private static OverlayManager instance;

    public static final String LOGGER = "logger";
    public static final String MOB_INFO = "mobinfo";

    private final Map<String, IOverlay> overlays;

    private OverlayManager() {
        overlays = new HashMap<>();
        initializeOverlays();
    }

    public static OverlayManager getInstance() {
        if (instance == null) {
            instance = new OverlayManager();
        }
        return instance;
    }

    private void initializeOverlays() {
        registerOverlay(LOGGER, LoggerOverlay::new);
    }

    private void registerOverlay(String name, OverlayFactory factory) {
        try {
            IOverlay overlay = factory.create();
            overlay.initialize();
            overlays.put(name, overlay);
        } catch (Exception e) {
            Main.LOGGER.error("Erreur lors de l'initialisation de l'overlay '{}': {}", name, e.getMessage());
        }
    }

    @FunctionalInterface
    private interface OverlayFactory {
        IOverlay create();
    }

    public IOverlay getOverlay(String name) {
        return overlays.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends IOverlay> T getOverlay(String name, Class<T> type) {
        IOverlay overlay = overlays.get(name);
        if (type.isInstance(overlay)) {
            return (T) overlay;
        }
        return null;
    }

    public LoggerOverlay getLoggerOverlay() {
        return getOverlay(LOGGER, LoggerOverlay.class);
    }

    public void showOverlay(String name) {
        executeOnOverlay(name, IOverlay::show);
    }

    public void hideOverlay(String name) {
        executeOnOverlay(name, IOverlay::hide);
    }

    public void toggleOverlay(String name) {
        executeOnOverlay(name, IOverlay::toggle);
    }

    private void executeOnOverlay(String name, OverlayAction action) {
        IOverlay overlay = getOverlay(name);
        if (overlay != null) {
            action.execute(overlay);
        }
    }

    @FunctionalInterface
    private interface OverlayAction {
        void execute(IOverlay overlay);
    }

    public boolean isOverlayVisible(String name) {
        IOverlay overlay = getOverlay(name);
        return overlay != null && overlay.isVisible();
    }

    public void renderAll(DrawContext context, MinecraftClient client) {
        overlays.values().stream()
                .filter(IOverlay::isVisible)
                .forEach(overlay -> overlay.render(context, client));
    }

    public void hideAll() {
        overlays.values().forEach(IOverlay::hide);
    }

    public void addOverlay(String name, IOverlay overlay) {
        overlay.initialize();
        overlays.put(name, overlay);
    }

    public void removeOverlay(String name) {
        overlays.remove(name);
    }

    public String[] getOverlayNames() {
        return overlays.keySet().toArray(new String[0]);
    }

    public int getOverlayCount() {
        return overlays.size();
    }

    public boolean hasOverlay(String name) {
        return overlays.containsKey(name);
    }
}