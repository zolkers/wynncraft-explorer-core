package com.edgn.event.listeners;

import com.edgn.event.Event;
import com.edgn.event.Listener;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public interface RenderListener extends Listener {
    void onRender(RenderEvent event);

    class RenderEvent extends Event<RenderListener> {
        private final MatrixStack matrixStack;
        private final float partialTicks;

        public RenderEvent(MatrixStack matrixStack, float partialTicks) {
            this.matrixStack = matrixStack;
            this.partialTicks = partialTicks;
        }

        public MatrixStack getMatrixStack() {
            return matrixStack;
        }

        public float getPartialTicks() {
            return partialTicks;
        }

        @Override
        public void fire(ArrayList<RenderListener> listeners) {

            GL11.glEnable(GL11.GL_LINE_SMOOTH);

            for (RenderListener listener : listeners)
                listener.onRender(this);


            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }

        @Override
        public Class<RenderListener> getListenerType() {
            return RenderListener.class;
        }
    }
}
