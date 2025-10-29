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

package io.github.luckymcdev.groovyengine.lens.client.systems.obj.amo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.Face;
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

    /**
     * Render a triangle face with animation applied.
     * <p>
     * This method takes a list of animated vertices and renders them as a triangle.
     * The first vertex is duplicated to close the triangle.
     *
     * @param poseStack   The pose stack.
     * @param buffer      The vertex consumer buffer.
     * @param packedLight The packed light.
     * @param joints      The joints to apply to the vertices for animation.
     */
    private void renderTriangleAnimated(PoseStack poseStack, VertexConsumer buffer, int packedLight, Joint[] joints) {
        for (AmoVertex vertex : amoVertices) {
            addAnimatedVertex(buffer, vertex, poseStack, packedLight, joints);
        }
        // Duplicate first vertex to close triangle
        addAnimatedVertex(buffer, amoVertices.get(0), poseStack, packedLight, joints);
    }

    /**
     * Render a quad face with animation applied.
     *
     * @param poseStack   The pose stack to render with
     * @param buffer      The buffer to render to
     * @param packedLight The packed light
     * @param joints      The joints to apply animation to
     */
    private void renderQuadAnimated(PoseStack poseStack, VertexConsumer buffer, int packedLight, Joint[] joints) {
        for (AmoVertex vertex : amoVertices) {
            addAnimatedVertex(buffer, vertex, poseStack, packedLight, joints);
        }
    }

    /**
     * Add an animated vertex to the given buffer.
     *
     * @param buffer      The buffer to add the vertex to
     * @param vertex      The animated vertex to add
     * @param poseStack   The pose stack to use for rendering
     * @param packedLight The packed light value to use for rendering
     * @param joints      The joints to apply to the vertex for animation
     */
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

    /**
     * Gets the list of animated vertices that make up this face.
     *
     * @return The list of animated vertices
     */
    public List<AmoVertex> getAmoVertices() {
        return amoVertices;
    }
}