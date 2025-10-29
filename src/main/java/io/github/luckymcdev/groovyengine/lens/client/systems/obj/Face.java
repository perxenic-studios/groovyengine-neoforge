/*
 * Copyright 2025 LuckyMcDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    /**
     * Render a face with the given pose stack, render type, and packed light.
     *
     * This method will render the face as a quad if it has 4 vertices, as a triangle if it has 3 vertices,
     * and as an ngon if it has more than 4 vertices. If the face has less than 3 vertices, it will be skipped.
     *
     * @param poseStack The pose stack to use for rendering
     * @param renderType The render type to use for rendering
     * @param packedLight The packed light value to use for rendering
     */
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

    /**
     * Render a triangle face with the given pose stack, buffer, and packed light.
     *
     * This method will render the face as a triangle by adding all vertices to the buffer, then adding the first vertex again to close the triangle.
     *
     * @param poseStack The pose stack to use for rendering
     * @param buffer The vertex consumer buffer to render to
     * @param packedLight The packed light value to use for rendering
     */
    public void renderTriangle(PoseStack poseStack, VertexConsumer buffer, int packedLight) {
        this.vertices.forEach(vertex -> addVertex(buffer, vertex, poseStack, packedLight));
        addVertex(buffer, this.vertices.get(0), poseStack, packedLight);
    }

    /**
     * Render a quad face with the given pose stack, buffer, and packed light.
     *
     * This method will render the face as a quad by adding all vertices to the buffer.
     *
     * @param poseStack The pose stack to use for rendering
     * @param buffer The vertex consumer buffer to render to
     * @param packedLight The packed light value to use for rendering
     */
    public void renderQuad(PoseStack poseStack, VertexConsumer buffer, int packedLight) {
        this.vertices.forEach(vertex -> addVertex(buffer, vertex, poseStack, packedLight));
    }

    /**
     * Renders a face with N vertices (an Ngon) using simple fan triangulation.
     *
     * This method will render the face as a series of connected triangles, using the first vertex as the anchor.
     * For example, if the face has 4 vertices, it will render the triangles (0,1,2), (0,2,3), (0,3,4).
     *
     * @param poseStack The pose stack to use for rendering
     * @param buffer The vertex consumer buffer to render to
     * @param packedLight The packed light value to use for rendering
     */
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

    /**
     * Adds a vertex to the given buffer, using the given pose stack, vertex, and packed light.
     *
     * This method will add the vertex to the buffer, using the position, normal, and UV coordinates from the given vertex.
     * The light value will be set to the given packed light.
     *
     * @param buffer The vertex consumer buffer to add the vertex to
     * @param vertex The vertex to add to the buffer
     * @param poseStack The pose stack to use for rendering
     * @param packedLight The packed light value to use for rendering
     */
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

    /**
     * Returns the centroid of all vertices in this face.
     *
     * This method will calculate the centroid by summing all vertex positions and dividing by the number of vertices.
     *
     * @return The centroid of all vertices in this face
     */
    public Vector3f getCentroid() {
        Vector3f centroid = new Vector3f();
        for (Vertex vertex : vertices) centroid.add(vertex.position());
        centroid.mul(1f / vertices.size());
        return centroid;
    }
}