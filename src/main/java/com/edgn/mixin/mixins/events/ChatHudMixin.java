package com.edgn.mixin.mixins.events;

import com.edgn.Main;
import com.edgn.event.EventManager;
import com.edgn.event.listeners.ChatMessageAddListener;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @Unique
    private static final ThreadLocal<ChatMessageAddListener.ChatMessageAddEvent> currentChatEvent = new ThreadLocal<>();

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void onAddMessageControl(Text message, CallbackInfo ci) {
        Main.OVERLAY_MANAGER.getLoggerOverlay().info(message.getString(), false);
        ChatMessageAddListener.ChatMessageAddEvent event = new ChatMessageAddListener.ChatMessageAddEvent(message);

        currentChatEvent.set(event);

        EventManager.fire(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), argsOnly = true)
    private Text onAddMessageModify(Text originalMessage) {
        ChatMessageAddListener.ChatMessageAddEvent event = currentChatEvent.get();

        currentChatEvent.remove();

        if (event != null && event.isModified()) {
            return event.getModifiedMessage();
        }

        return originalMessage;
    }

}