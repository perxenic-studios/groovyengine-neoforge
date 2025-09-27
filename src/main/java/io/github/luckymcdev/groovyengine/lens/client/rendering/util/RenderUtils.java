package io.github.luckymcdev.groovyengine.lens.client.rendering.util;

import com.mojang.blaze3d.vertex.*;
import io.github.luckymcdev.groovyengine.lens.client.rendering.core.GERenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderUtils {

    public static void setupWorldRendering(PoseStack poseStack) {
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
    }

    public static VertexConsumer getConsumer(MultiBufferSource bufferSource) {
        return bufferSource.getBuffer(GERenderTypes.CUSTOM_QUADS);
    }

    public static void renderQuad(VertexConsumer consumer, Matrix4f matrix,
                                  float x1, float y1, float z1,
                                  float x2, float y2, float z2,
                                  float x3, float y3, float z3,
                                  float x4, float y4, float z4,
                                  float u1, float v1, float u2, float v2,
                                  Vector3f normal) {
        consumer.addVertex(matrix, x1, y1, z1).setUv(u1, v1).setNormal(normal.x(), normal.y(), normal.z());
        consumer.addVertex(matrix, x2, y2, z2).setUv(u2, v1).setNormal(normal.x(), normal.y(), normal.z());
        consumer.addVertex(matrix, x3, y3, z3).setUv(u2, v2).setNormal(normal.x(), normal.y(), normal.z());
        consumer.addVertex(matrix, x4, y4, z4).setUv(u1, v2).setNormal(normal.x(), normal.y(), normal.z());
    }

    public static void renderCenteredQuad(VertexConsumer consumer, Matrix4f matrix,
                                          float centerX, float centerY, float centerZ,
                                          float sizeX, float sizeY, float sizeZ,
                                          float u1, float v1, float u2, float v2,
                                          Vector3f normal) {
        float halfX = sizeX / 2f;
        float halfY = sizeY / 2f;
        float halfZ = sizeZ / 2f;

        renderQuad(consumer, matrix,
                centerX - halfX, centerY - halfY, centerZ - halfZ,
                centerX + halfX, centerY - halfY, centerZ - halfZ,
                centerX + halfX, centerY + halfY, centerZ + halfZ,
                centerX - halfX, centerY + halfY, centerZ + halfZ,
                u1, v1, u2, v2, normal);
    }

    public static void setPoseStackPosition(PoseStack stack, Vec3 worldPos) {
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        stack.setIdentity(); // clear old transformations
        stack.translate(worldPos.x - camPos.x, worldPos.y - camPos.y, worldPos.z - camPos.z);
    }

}