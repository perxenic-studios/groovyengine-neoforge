package io.github.luckymcdev.groovyengine.lens.client.rendering.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class GeometryBuilder {

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