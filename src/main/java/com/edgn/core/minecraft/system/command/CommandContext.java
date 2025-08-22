package com.edgn.core.minecraft.system.command;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public interface CommandContext {
    void sendMessage(String message);

    void sendError(String message);

    void sendRawMessage(Text message);

    ClientPlayerEntity getPlayer();

    <T> T getArgument(String name, Class<T> clazz);

    String getPartialInput();

}