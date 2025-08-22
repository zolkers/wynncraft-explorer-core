package com.edgn.mixin.mixins.events;


import com.edgn.mixin.mixininterface.IKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements IKeyBinding {
    @Shadow
    private InputUtil.Key boundKey;

    @Override
    @Unique
    public void __wynncraft_explorer_resetPressedState() {
        long handle = MinecraftClient.getInstance().getWindow().getHandle();
        int code = boundKey.getCode();

        if (boundKey.getCategory() == InputUtil.Type.MOUSE)
            setPressed(GLFW.glfwGetMouseButton(handle, code) == 1);
        else
            setPressed(InputUtil.isKeyPressed(handle, code));
    }

    @Override
    @Unique
    public void __wynncraft_explorer_simulatePress(boolean pressed) {
        MinecraftClient mc = MinecraftClient.getInstance();
        long window = mc.getWindow().getHandle();
        int action = pressed ? 1 : 0;

        switch (boundKey.getCategory()) {
            case KEYSYM:
                mc.keyboard.onKey(window, boundKey.getCode(), 0, action, 0);
                break;

            case SCANCODE:
                mc.keyboard.onKey(window, GLFW.GLFW_KEY_UNKNOWN, boundKey.getCode(),
                        action, 0);
                break;

            case MOUSE:
                mc.mouse.onMouseButton(window, boundKey.getCode(), action, 0);
                break;

            default:
                System.out.println("Unknown keybinding type: " + boundKey.getCategory());
                break;
        }
    }

    @Override
    @Shadow
    public abstract void setPressed(boolean pressed);
}
