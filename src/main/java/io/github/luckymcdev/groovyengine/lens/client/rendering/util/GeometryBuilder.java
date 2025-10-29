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

package io.github.luckymcdev.groovyengine.lens.client.rendering.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class GeometryBuilder {

    /**
     * Render a cube with a given size and center position.
     *
     * @param consumer Vertex consumer to render the cube
     * @param matrix   Matrix4f to use when rendering the cube
     * @param centerX  Center x-coordinate of the cube
     * @param centerY  Center y-coordinate of the cube
     * @param centerZ  Center z-coordinate of the cube
     * @param size     Size of the cube
     * @param u1       UV coordinate of the first vertex of the cube
     * @param v1       UV coordinate of the second vertex of the cube
     * @param u2       UV coordinate of the third vertex of the cube
     * @param v2       UV coordinate of the fourth vertex of the cube
     */
    public static void renderCube(VertexConsumer consumer, Matrix4f matrix,
                                  float centerX, float centerY, float centerZ,
                                  float size, float u1, float v1, float u2, float v2) {

        float halfSize = size / 2f;
        float minX = centerX - halfSize;
        float minY = centerY - halfSize;
        float minZ = centerZ - halfSize;
        float maxX = centerX + halfSize;
        float maxY = centerY + halfSize;
        float maxZ = centerZ + halfSize;

        // Front face
        RenderUtils.renderQuad(consumer, matrix,
                minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ,
                u1, v1, u2, v2, new Vector3f(0, 0, 1));

        // Back face
        RenderUtils.renderQuad(consumer, matrix,
                maxX, minY, minZ, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ,
                u1, v1, u2, v2, new Vector3f(0, 0, -1));

        // Top face
        RenderUtils.renderQuad(consumer, matrix,
                minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, minX, maxY, minZ,
                u1, v1, u2, v2, new Vector3f(0, 1, 0));

        // Bottom face
        RenderUtils.renderQuad(consumer, matrix,
                minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ,
                u1, v1, u2, v2, new Vector3f(0, -1, 0));

        // Right face
        RenderUtils.renderQuad(consumer, matrix,
                maxX, minY, maxZ, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ,
                u1, v1, u2, v2, new Vector3f(1, 0, 0));

        // Left face
        RenderUtils.renderQuad(consumer, matrix,
                minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ,
                u1, v1, u2, v2, new Vector3f(-1, 0, 0));
    }

    /**
     * Render a plane with a given size, center position, and normal.
     *
     * @param consumer Vertex consumer to render the plane
     * @param matrix   Matrix4f to use when rendering the plane
     * @param centerX  Center x-coordinate of the plane
     * @param centerY  Center y-coordinate of the plane
     * @param centerZ  Center z-coordinate of the plane
     * @param width    Width of the plane
     * @param height   Height of the plane
     * @param normal   Normal of the plane
     * @param u1       UV coordinate of the first vertex of the plane
     * @param v1       UV coordinate of the second vertex of the plane
     * @param u2       UV coordinate of the third vertex of the plane
     * @param v2       UV coordinate of the fourth vertex of the plane
     */
    public static void renderPlane(VertexConsumer consumer, Matrix4f matrix,
                                   float centerX, float centerY, float centerZ,
                                   float width, float height, Vector3f normal,
                                   float u1, float v1, float u2, float v2) {

        // Calculate tangents based on normal
        Vector3f tangent = new Vector3f(normal.y, normal.z, normal.x); // Simple perpendicular
        Vector3f bitangent = new Vector3f(normal.z, normal.x, normal.y); // Another perpendicular

        float halfWidth = width / 2f;
        float halfHeight = height / 2f;

        Vector3f v1Pos = new Vector3f(centerX, centerY, centerZ)
                .add(tangent.mul(-halfWidth))
                .add(bitangent.mul(-halfHeight));

        Vector3f v2Pos = new Vector3f(centerX, centerY, centerZ)
                .add(tangent.mul(halfWidth))
                .add(bitangent.mul(-halfHeight));

        Vector3f v3Pos = new Vector3f(centerX, centerY, centerZ)
                .add(tangent.mul(halfWidth))
                .add(bitangent.mul(halfHeight));

        Vector3f v4Pos = new Vector3f(centerX, centerY, centerZ)
                .add(tangent.mul(-halfWidth))
                .add(bitangent.mul(halfHeight));

        consumer.addVertex(matrix, v1Pos.x, v1Pos.y, v1Pos.z).setUv(u1, v1).setNormal(normal.x, normal.y, normal.z);
        consumer.addVertex(matrix, v2Pos.x, v2Pos.y, v2Pos.z).setUv(u2, v1).setNormal(normal.x, normal.y, normal.z);
        consumer.addVertex(matrix, v3Pos.x, v3Pos.y, v3Pos.z).setUv(u2, v2).setNormal(normal.x, normal.y, normal.z);
        consumer.addVertex(matrix, v4Pos.x, v4Pos.y, v4Pos.z).setUv(u1, v2).setNormal(normal.x, normal.y, normal.z);
    }

    /**
     * Render a triangle with the given vertices, UV coordinates, and normal.
     *
     * @param consumer Vertex consumer to render the triangle
     * @param matrix   Matrix4f to use when rendering the triangle
     * @param x1       X-coordinate of the first vertex of the triangle
     * @param y1       Y-coordinate of the first vertex of the triangle
     * @param z1       Z-coordinate of the first vertex of the triangle
     * @param x2       X-coordinate of the second vertex of the triangle
     * @param y2       Y-coordinate of the second vertex of the triangle
     * @param z2       Z-coordinate of the second vertex of the triangle
     * @param x3       X-coordinate of the third vertex of the triangle
     * @param y3       Y-coordinate of the third vertex of the triangle
     * @param z3       Z-coordinate of the third vertex of the triangle
     * @param u1       UV coordinate of the first vertex of the triangle
     * @param v1       UV coordinate of the second vertex of the triangle
     * @param u2       UV coordinate of the third vertex of the triangle
     * @param v2       UV coordinate of the fourth vertex of the triangle
     * @param u3       UV coordinate of the fifth vertex of the triangle
     * @param v3       UV coordinate of the sixth vertex of the triangle
     * @param normal   Normal of the triangle
     */
    public static void renderTriangle(VertexConsumer consumer, Matrix4f matrix,
                                      float x1, float y1, float z1,
                                      float x2, float y2, float z2,
                                      float x3, float y3, float z3,
                                      float u1, float v1, float u2, float v2, float u3, float v3,
                                      Vector3f normal) {

        consumer.addVertex(matrix, x1, y1, z1).setUv(u1, v1).setNormal(normal.x, normal.y, normal.z);
        consumer.addVertex(matrix, x2, y2, z2).setUv(u2, v2).setNormal(normal.x, normal.y, normal.z);
        consumer.addVertex(matrix, x3, y3, z3).setUv(u3, v3).setNormal(normal.x, normal.y, normal.z);
        consumer.addVertex(matrix, x1, y1, z1).setUv(u1, v1).setNormal(normal.x, normal.y, normal.z); // Close the triangle
    }
}