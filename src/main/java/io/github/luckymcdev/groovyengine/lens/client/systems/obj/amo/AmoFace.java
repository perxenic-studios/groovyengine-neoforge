package io.github.luckymcdev.groovyengine.lens.client.systems.obj.amo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.Face;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.Vertex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector3f;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Face for animated models with joint-influenced vertices.
 */
public class AmoFace extends Face {
    private final List<AmoVertex> amoVertices;

    public AmoFace(List<AmoVertex> vertices) {
        super(vertices.stream().map(AmoVertex::toVertex).collect(Collectors.toList()));
        this.amoVertices = vertices;
    }

    /**
     * Render the face with animation applied.
     */
    public void renderAnimated(PoseStack poseStack, RenderType renderType, int packedLight, Joint[] joints) {
        MultiBufferSource.BufferSource mcBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = mcBufferSource.getBuffer(renderType);

        int vertexCount = amoVertices.size();

        if (vertexCount == 4) {
            renderQuadAnimated(poseStack, buffer, packedLight, joints);
        } else if (vertexCount == 3) {
            renderTriangleAnimated(poseStack, buffer, packedLight, joints);
        } else {
            throw new RuntimeException("Face has invalid number of vertices. Supported vertex counts are 3 and 4.");
        }
    }

    private void renderTriangleAnimated(PoseStack poseStack, VertexConsumer buffer, int packedLight, Joint[] joints) {
        for (AmoVertex vertex : amoVertices) {
            addAnimatedVertex(buffer, vertex, poseStack, packedLight, joints);
        }
        // Duplicate first vertex to close triangle
        addAnimatedVertex(buffer, amoVertices.get(0), poseStack, packedLight, joints);
    }

    private void renderQuadAnimated(PoseStack poseStack, VertexConsumer buffer, int packedLight, Joint[] joints) {
        for (AmoVertex vertex : amoVertices) {
            addAnimatedVertex(buffer, vertex, poseStack, packedLight, joints);
        }
    }

    private void addAnimatedVertex(VertexConsumer buffer, AmoVertex vertex, PoseStack poseStack, int packedLight, Joint[] joints) {
        PoseStack.Pose pose = poseStack.last();

        // Get animated position by applying joint transformations
        Vector3f position = vertex.getAnimatedPosition(joints);
        Vector3f normal = vertex.normal();
        Vec2 uv = vertex.uv();

        buffer.addVertex(pose, position.x(), position.y(), position.z());
        buffer.setColor(255, 255, 255, 255);
        buffer.setUv(uv.x, -uv.y);
        buffer.setOverlay(OverlayTexture.NO_OVERLAY);
        buffer.setLight(packedLight);
        buffer.setNormal(pose, normal.x(), normal.y(), normal.z());
    }

    public List<AmoVertex> getAmoVertices() {
        return amoVertices;
    }
}