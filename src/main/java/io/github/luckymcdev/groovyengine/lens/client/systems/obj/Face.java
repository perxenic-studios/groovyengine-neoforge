package io.github.luckymcdev.groovyengine.lens.client.systems.obj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.luckymcdev.groovyengine.GE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector3f;

import java.util.List;

public class Face {
    private final List<Vertex> vertices;

    public Face(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public List<Vertex> vertices() {
        return vertices;
    }

    public void renderFace(PoseStack poseStack, RenderType renderType, int packedLight) {
        MultiBufferSource.BufferSource mcBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = mcBufferSource.getBuffer(renderType);
        int vertexCount = vertices.size();

        if (vertexCount == 4) {
            renderQuad(poseStack, buffer, packedLight);
        } else if (vertexCount == 3) {
            renderTriangle(poseStack, buffer, packedLight);
        } else if (vertexCount > 4) {
            // Handle Ngons by triangulating them
            renderNgon(poseStack, buffer, packedLight);
        } else {
            // Skip invalid faces (less than 3 vertices)
            GE.LENS_LOG.warn("Skipping face with invalid vertex count: {}", vertexCount);
        }
    }

    public void renderTriangle(PoseStack poseStack, VertexConsumer buffer, int packedLight) {
        this.vertices.forEach(vertex -> addVertex(buffer, vertex, poseStack, packedLight));
        addVertex(buffer, this.vertices.get(0), poseStack, packedLight);
    }

    public void renderQuad(PoseStack poseStack, VertexConsumer buffer, int packedLight) {
        this.vertices.forEach(vertex -> addVertex(buffer, vertex, poseStack, packedLight));
    }

    public void renderNgon(PoseStack poseStack, VertexConsumer buffer, int packedLight) {
        // Simple fan triangulation for Ngons
        // Use vertex 0 as the anchor and create triangles: (0,1,2), (0,2,3), (0,3,4), etc.
        Vertex anchor = vertices.get(0);

        for (int i = 1; i < vertices.size() - 1; i++) {
            Vertex v1 = vertices.get(i);
            Vertex v2 = vertices.get(i + 1);

            // Render each triangle
            addVertex(buffer, anchor, poseStack, packedLight);
            addVertex(buffer, v1, poseStack, packedLight);
            addVertex(buffer, v2, poseStack, packedLight);
        }
    }

    protected void addVertex(VertexConsumer buffer, Vertex vertex, PoseStack poseStack, int packedLight) {
        PoseStack.Pose pose = poseStack.last();

        Vector3f position = vertex.position();
        Vector3f normal = vertex.normal();
        Vec2 uv = vertex.uv();

        buffer.addVertex(pose, position.x(), position.y(), position.z());
        buffer.setColor(255, 255, 255, 255);
        buffer.setUv(uv.x, -uv.y);
        buffer.setOverlay(OverlayTexture.NO_OVERLAY);
        buffer.setLight(packedLight);
        buffer.setNormal(pose, normal.x(), normal.y(), normal.z());
    }

    public Vector3f getCentroid() {
        Vector3f centroid = new Vector3f();
        for (Vertex vertex : vertices) centroid.add(vertex.position());
        centroid.mul(1f / vertices.size());
        return centroid;
    }
}