package com.edgn.core.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;


public enum PopUpUtils {
    INSTANCE;

    public void showNotification(String title, String message) {
        if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
            ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
            SystemToast toast = new SystemToast(
                    SystemToast.Type.CHUNK_LOAD_FAILURE,
                    Text.literal(title),
                    Text.literal(message)
            );
            toastManager.add(toast);
        }
    }
}

