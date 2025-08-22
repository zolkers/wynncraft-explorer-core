package com.edgn.core.minecraft.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

public class Render3D {

    public static final Identifier BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/beacon_beam.png");

    private static void setupRender() {
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
    }

    private static void endRender() {
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static VertexConsumerProvider.Immediate getVCP() {
        return MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
    }

    public static Vec3d getCrosshairTarget(double maxDistance) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null || client.world == null) {
            return new Vec3d(0, 0, 0);
        }

        Vec3d eyePos = player.getEyePos();
        Vec3d lookDirection = player.getRotationVec(1.0f);
        Vec3d endPos = eyePos.add(lookDirection.multiply(maxDistance));

        RaycastContext context = new RaycastContext(
                eyePos,
                endPos,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                player
        );

        HitResult hitResult = client.world.raycast(context);

        if (hitResult.getType() != HitResult.Type.MISS) {
            return hitResult.getPos();
        }

        return endPos;
    }


    public static void drawLine(MatrixStack matrices, Vec3d start, Vec3d end, int color) {
        float a = ((color >> 24) & 0xFF) / 255.0F;
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        buffer.vertex(matrix, (float) start.x, (float) start.y, (float) start.z).color(r, g, b, a);
        buffer.vertex(matrix, (float) end.x, (float) end.y, (float) end.z).color(r, g, b, a);

        setupRender();
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        endRender();
    }

    public static void drawBox(MatrixStack matrices, Box box, int color) {
        float a = ((color >> 24) & 0xFF) / 255.0F;
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(r, g, b, a);

        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(r, g, b, a);

        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a);

        setupRender();
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        endRender();
    }

    public static void drawFilledBox(MatrixStack matrices, Box box, int color) {
        float a = ((color >> 24) & 0xFF) / 255.0F;
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(r, g, b, a);

        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(r, g, b, a);

        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(r, g, b, a);

        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a);

        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(r, g, b, a);

        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a);
        buffer.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(r, g, b, a);

        setupRender();
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        endRender();
    }

    public static void drawCircle(MatrixStack matrices, Vec3d center, double radius, int segments, int color) {
        float a = ((color >> 24) & 0xFF) / 255.0F;
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        for (int i = 0; i <= segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            buffer.vertex(matrix, (float) x, (float) center.y, (float) z).color(r, g, b, a);
        }

        setupRender();
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        endRender();
    }

    public static void drawFilledCircle(MatrixStack matrices, Vec3d center, double radius, int segments, int color) {
        float a = ((color >> 24) & 0xFF) / 255.0F;
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        buffer.vertex(matrix, (float) center.x, (float) center.y, (float) center.z).color(r, g, b, a);

        for (int i = 0; i <= segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            buffer.vertex(matrix, (float) x, (float) center.y, (float) z).color(r, g, b, a);
        }

        setupRender();
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        endRender();
    }

    public static int rgb(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    public static int rgb(int r, int g, int b) {
        return rgb(r, g, b, 255);
    }

    public static Box getInterpolatedBoundingBox(LivingEntity entity, float partialTicks) {
        double interpolatedX = entity.prevX + (entity.getX() - entity.prevX) * partialTicks;
        double interpolatedY = entity.prevY + (entity.getY() - entity.prevY) * partialTicks;
        double interpolatedZ = entity.prevZ + (entity.getZ() - entity.prevZ) * partialTicks;

        Box box = entity.getBoundingBox();
        return new Box(
                box.minX - entity.getX() + interpolatedX,
                box.minY - entity.getY() + interpolatedY,
                box.minZ - entity.getZ() + interpolatedZ,
                box.maxX - entity.getX() + interpolatedX,
                box.maxY - entity.getY() + interpolatedY,
                box.maxZ - entity.getZ() + interpolatedZ
        );
    }

    public static void renderBeam(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, float x, float y, float z, boolean xRay) {
        int yOffset = 0;
        renderBeam(matrixStack, vertexConsumerProvider, yOffset, 0xFFFF0000, x, y, z, xRay);
    }

    private static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int yOffset, int color, float x, float y, float z, boolean xRay) {
        renderBeam(matrices, vertexConsumers, yOffset, color, 0.2F, x, y, z, xRay);
    }

    private static final RenderLayer BEAM_LAYER = RenderLayer.of(
            "beam_no_depth",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            256,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(RenderLayer.BEACON_BEAM_PROGRAM)
                    .texture(new RenderPhase.Texture(BEAM_TEXTURE, TriState.FALSE, false))
                    .transparency(RenderLayer.TRANSLUCENT_TRANSPARENCY)
                    .cull(RenderPhase.DISABLE_CULLING)
                    .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
                    .writeMaskState(new RenderPhase.WriteMaskState(true, true))
                    .build(false)
    );

    private static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int yOffset, int color, float innerRadius, float x, float y, float z, boolean xRay) {
        matrices.push();
        matrices.translate(x, y, z);

        VertexConsumer vertexConsumer = xRay ? vertexConsumers.getBuffer(BEAM_LAYER) : vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(BEAM_TEXTURE, true));

        renderBeamLayer(matrices,
                vertexConsumer,
                color, yOffset, yOffset + 300,
                innerRadius, innerRadius, -innerRadius, -innerRadius);

        matrices.pop();
    }


    private static void renderBeamLayer(MatrixStack matrices, VertexConsumer vertices, int color, int yOffset, int height, float z1, float x2, float x3, float z4) {
        MatrixStack.Entry entry = matrices.peek();
        renderBeamFace(entry, vertices, color, yOffset, height, (float) 0.0, z1, x2, (float) 0.0);
        renderBeamFace(entry, vertices, color, yOffset, height, (float) 0.0, z4, x3, (float) 0.0);
        renderBeamFace(entry, vertices, color, yOffset, height, x2, (float) 0.0, (float) 0.0, z4);
        renderBeamFace(entry, vertices, color, yOffset, height, x3, (float) 0.0, (float) 0.0, z1);
    }

    private static void renderBeamFace(MatrixStack.Entry matrix, VertexConsumer vertices, int color, int yOffset, int height, float x1, float z1, float x2, float z2) {
        renderBeamVertex(matrix, vertices, color, height, x1, z1, (float) 1.0);
        renderBeamVertex(matrix, vertices, color, yOffset, x1, z1, (float) 1.0);
        renderBeamVertex(matrix, vertices, color, yOffset, x2, z2, (float) 0.0);
        renderBeamVertex(matrix, vertices, color, height, x2, z2, (float) 0.0);
    }

    private static void renderBeamVertex(MatrixStack.Entry matrix, VertexConsumer vertices, int color, int y, float x, float z, float u) {
        vertices.vertex(matrix, x, (float) y, z).color(color).texture(u, (float) 0.0).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix, 0.0F, 0.0F, 0.0F);
    }

    private static Vec3d blockPosToRenderPos(int x, int y, int z) {
        return new Vec3d(x, y, z);
    }

    public static void drawLineFromBlocks(MatrixStack matrices, int x1, int y1, int z1, int x2, int y2, int z2, int color) {
        Vec3d start = blockPosToRenderPos(x1, y1, z1);
        Vec3d end = blockPosToRenderPos(x2, y2, z2);
        drawLine(matrices, start, end, color);
    }

    public static void drawPathLine(MatrixStack matrices, int x1, int y1, int z1, int x2, int y2, int z2, int color) {
        float a = ((color >> 24) & 0xFF) / 255.0F;
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        buffer.vertex(matrix, x1 + 0.5f, y1 + 0.1f, z1 + 0.5f).color(r, g, b, a);
        buffer.vertex(matrix, x2 + 0.5f, y2 + 0.1f, z2 + 0.5f).color(r, g, b, a);

        setupRender();
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        endRender();
    }

    public static void drawBoxAtBlock(MatrixStack matrices, int x, int y, int z, double sizeX, double sizeY, double sizeZ, int color) {
        float a = ((color >> 24) & 0xFF) / 255.0F;
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        float minX = (float)(x + (1 - sizeX) / 2);
        float minY = (float)y;
        float minZ = (float)(z + (1 - sizeZ) / 2);
        float maxX = (float)(x + (1 + sizeX) / 2);
        float maxY = (float)(y + sizeY);
        float maxZ = (float)(z + (1 + sizeZ) / 2);

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);

        setupRender();
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        endRender();
    }

    public static void drawFilledBoxAtBlock(MatrixStack matrices, int x, int y, int z, double sizeX, double sizeY, double sizeZ, int color) {
        float a = ((color >> 24) & 0xFF) / 255.0F;
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        float minX = (float)(x + (1 - sizeX) / 2);
        float minY = (float)y;
        float minZ = (float)(z + (1 - sizeZ) / 2);
        float maxX = (float)(x + (1 + sizeX) / 2);
        float maxY = (float)(y + sizeY);
        float maxZ = (float)(z + (1 + sizeZ) / 2);

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);

        setupRender();
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        endRender();
    }

    public static void drawCircleAtBlock(MatrixStack matrices, int x, int y, int z, double radius, int segments, int color) {
        Vec3d center = blockPosToRenderPos(x, y, z).add(0.5, 0.1, 0.5);
        drawCircle(matrices, center, radius, segments, color);
    }

    public static void drawFilledCircleAtBlock(MatrixStack matrices, int x, int y, int z, double radius, int segments, int color) {
        Vec3d center = blockPosToRenderPos(x, y, z).add(0.5, 0.1, 0.5);
        drawFilledCircle(matrices, center, radius, segments, color);
    }

    public static void drawText(MatrixStack matrixStack, String text, Vec3d vec) {
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.lineWidth(4f);

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        VertexConsumerProvider.Immediate vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();
        TextRenderer.TextLayerType layerType = TextRenderer.TextLayerType.POLYGON_OFFSET;
        int backgroundColor = 0x80000000;
        int light = LightmapTextureManager.MAX_LIGHT_COORDINATE;

        Camera camera = client.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();

        matrixStack.push();

        Vec3d adjustedVector3d = new Vec3d(vec.x + 0.5, vec.y + 0.5, vec.z + 0.5);

        matrixStack.translate(adjustedVector3d.x, adjustedVector3d.y + 0.5, adjustedVector3d.z);

        alignTextToCamera(matrixStack, adjustedVector3d, cameraPos);

        float scale = 0.02f;
        matrixStack.scale(-scale, -scale, scale);

        float textWidth = textRenderer.getWidth(text);
        float xOffset = -textWidth / 2.0f;

        textRenderer.draw(
                text,
                xOffset,
                0,
                0xFFFFFFFF,
                false,
                matrixStack.peek().getPositionMatrix(),
                vertexConsumers,
                layerType,
                backgroundColor,
                light
        );

        vertexConsumers.draw();
        matrixStack.pop();

        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }



    private static void alignTextToCamera(MatrixStack matrixStack, Vec3d textPos, Vec3d cameraPos) {
        Vec3d direction = cameraPos.subtract(textPos).normalize();

        double yaw = Math.atan2(direction.x, direction.z);
        double pitch = Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z));

        matrixStack.multiply(new Quaternionf().rotationY((float) (Math.PI + yaw)));
        matrixStack.multiply(new Quaternionf().rotationX((float) pitch));
    }

}