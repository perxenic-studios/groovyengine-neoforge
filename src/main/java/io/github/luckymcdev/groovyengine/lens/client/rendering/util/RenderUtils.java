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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.luckymcdev.groovyengine.lens.client.rendering.core.GeMaterials;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class RenderUtils {
    public static final int FULL_BRIGHT = 15728880;

    public static final String CONCRETE_RES_LOC_STRING = "textures/block/white_concrete.png";
    public static final ResourceLocation CONCRETE_RES_LOC = ResourceLocation.withDefaultNamespace(CONCRETE_RES_LOC_STRING);


    /**
     * Check if a given point is inside the camera's frustum.
     *
     * @param camera   The camera to check against
     * @param worldPos The point to check in world coordinates
     * @return true if the point is inside the camera's frustum, false otherwise
     */
    public static boolean isInCameraFrustum(Camera camera, Vec3 worldPos) {
        Camera.NearPlane nearPlane = camera.getNearPlane();
        Vec3 cameraPos = camera.getPosition();

        Vec3[] frustumCorners = {
                cameraPos.add(nearPlane.getTopLeft()),
                cameraPos.add(nearPlane.getTopRight()),
                cameraPos.add(nearPlane.getBottomLeft()),
                cameraPos.add(nearPlane.getBottomRight())
        };

        return isPointInPyramidFrustum(cameraPos, frustumCorners, worldPos);
    }

    /**
     * Check if a given point is inside a pyramid frustum.
     * A pyramid frustum is a 3D shape formed by a pyramid with its apex at the given position,
     * and its base corners at the given positions.
     *
     * @param apex        The position of the apex of the pyramid
     * @param baseCorners The positions of the corners of the base of the pyramid
     * @param point       The point to check if it is inside the pyramid frustum
     * @return true if the point is inside the pyramid frustum, false otherwise
     */
    private static boolean isPointInPyramidFrustum(Vec3 apex, Vec3[] baseCorners, Vec3 point) {
        for (int i = 0; i < baseCorners.length; i++) {
            Vec3 corner1 = baseCorners[i];
            Vec3 corner2 = baseCorners[(i + 1) % baseCorners.length];

            if (!isPointInsidePlane(apex, corner1, corner2, point)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if a given point is inside a plane defined by three points.
     *
     * @param planePoint1 The first point defining the plane
     * @param planePoint2 The second point defining the plane
     * @param planePoint3 The third point defining the plane
     * @param testPoint   The point to check if it is inside the plane
     * @return true if the point is inside the plane, false otherwise
     */
    private static boolean isPointInsidePlane(Vec3 planePoint1, Vec3 planePoint2, Vec3 planePoint3, Vec3 testPoint) {
        Vec3 edge1 = planePoint2.subtract(planePoint1);
        Vec3 edge2 = planePoint3.subtract(planePoint1);
        Vec3 normal = edge1.cross(edge2).normalize();

        Vec3 toTestPoint = testPoint.subtract(planePoint1);
        double dotProduct = toTestPoint.dot(normal);

        return dotProduct <= 0.0;
    }

    /**
     * Translate the given pose stack to the position of the main camera.
     * This is used to set up rendering of the world, so that the camera's position is ignored.
     *
     * @param poseStack The pose stack to translate
     */
    public static void setupWorldRendering(PoseStack poseStack) {
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
    }

    /**
     * Sets the position of a pose stack to the given position in world coordinates.
     * This is used to set up rendering of the world, so that the camera's position is ignored.
     *
     * @param stack    The pose stack to set the position of
     * @param worldPos The position in world coordinates to set the pose stack to
     */
    public static void setPoseStackPosition(PoseStack stack, Vec3 worldPos) {
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        stack.setIdentity(); // clear old transformations
        stack.translate(worldPos.x - camPos.x, worldPos.y - camPos.y, worldPos.z - camPos.z);
    }


    /**
     * Returns a vertex consumer for the given multi buffer source.
     * The returned consumer is for the custom quads render type.
     *
     * @param bufferSource The multi buffer source to get a vertex consumer for
     * @return A vertex consumer for the given multi buffer source
     */
    public static VertexConsumer getConsumer(MultiBufferSource bufferSource) {
        return bufferSource.getBuffer(GeMaterials.CUSTOM_QUADS.renderType());
    }

    // Add overloaded methods for different render types
    public static VertexConsumer getConsumer(MultiBufferSource bufferSource, RenderType renderType) {
        return bufferSource.getBuffer(renderType);
    }

    public static VertexConsumer getMaterialConsumer(MultiBufferSource bufferSource) {
        return bufferSource.getBuffer(GeMaterials.CUSTOM_QUADS.renderType());
    }

    public static int getPackedLightAt(Vec3 pos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return FULL_BRIGHT;

        BlockPos blockPos = BlockPos.containing(pos);
        return LevelRenderer.getLightColor(mc.level, blockPos);
    }

    /**
     * Gets the main camera object for the current game renderer.
     *
     * @return the main camera object
     */
    public static Camera getMainCamera() {
        return Minecraft.getInstance().gameRenderer.getMainCamera();
    }

    /**
     * Renders a quad using the given vertex consumer, matrix, and vertices.
     * The quad will have the given normal, and the given UV coordinates.
     *
     * @param consumer The vertex consumer to render with
     * @param matrix   The matrix to transform the vertices with
     * @param x1       The x coordinate of the first vertex
     * @param y1       The y coordinate of the first vertex
     * @param z1       The z coordinate of the first vertex
     * @param x2       The x coordinate of the second vertex
     * @param y2       The y coordinate of the second vertex
     * @param z2       The z coordinate of the second vertex
     * @param x3       The x coordinate of the third vertex
     * @param y3       The y coordinate of the third vertex
     * @param z3       The z coordinate of the third vertex
     * @param x4       The x coordinate of the fourth vertex
     * @param y4       The y coordinate of the fourth vertex
     * @param z4       The z coordinate of the fourth vertex
     * @param u1       The u coordinate of the first vertex
     * @param v1       The v coordinate of the first vertex
     * @param u2       The u coordinate of the second vertex
     * @param v2       The v coordinate of the second vertex
     * @param normal   The normal vector of the quad
     */
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

    /**
     * Renders a centered quad at the given position, with the given size and normal.
     * The quad will have the given UV coordinates.
     *
     * @param consumer The vertex consumer to render with
     * @param matrix   The matrix to transform the vertices with
     * @param centerX  The x coordinate of the center of the quad
     * @param centerY  The y coordinate of the center of the quad
     * @param centerZ  The z coordinate of the center of the quad
     * @param sizeX    The size of the quad in the x direction
     * @param sizeY    The size of the quad in the y direction
     * @param sizeZ    The size of the quad in the z direction
     * @param u1       The u coordinate of the first vertex
     * @param v1       The v coordinate of the first vertex
     * @param u2       The u coordinate of the second vertex
     * @param v2       The v coordinate of the second vertex
     * @param normal   The normal vector of the quad
     */
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

    /**
     * Computes the parametric points of a 3D sphere
     *
     * @param u the u parameter of the sphere
     * @param v the v parameter of the sphere
     * @param r the radius of the sphere
     * @return the parametric points of the sphere
     */
    public static Vector3f parametricSphere(float u, float v, float r) {
        return new Vector3f(Mth.cos(u) * Mth.sin(v) * r, Mth.cos(v) * r, Mth.sin(u) * Mth.sin(v) * r);
    }

    /**
     * Computes the parametric points of a 3D torus
     *
     * @param u           the u parameter of the torus
     * @param v           the v parameter of the torus
     * @param majorRadius the major radius of the torus
     * @param minorRadius the minor radius of the torus
     * @return the parametric points of the torus
     */
    public static Vector3f parametricTorus(float u, float v, float majorRadius, float minorRadius) {
        float x = (majorRadius + minorRadius * Mth.cos(v)) * Mth.cos(u);
        float y = minorRadius * Mth.sin(v);
        float z = (majorRadius + minorRadius * Mth.cos(v)) * Mth.sin(u);
        return new Vector3f(x, y, z);
    }

    /**
     * Computes the perpendicular trail points of a 3D line segment
     *
     * @param start the start point of the line segment
     * @param end   the end point of the line segment
     * @param width the width of the trail
     * @return the perpendicular trail points of the line segment
     */
    public static Vec2 perpendicularTrailPoints(Vector4f start, Vector4f end, float width) {
        float x = -start.x();
        float y = -start.y();
        if (Math.abs(start.z()) > 0) {
            float ratio = end.z() / start.z();
            x = end.x() + x * ratio;
            y = end.y() + y * ratio;
        } else if (Math.abs(end.z()) <= 0) {
            x += end.x();
            y += end.y();
        }
        if (start.z() > 0) {
            x = -x;
            y = -y;
        }
        if (x * x + y * y > 0F) {
            float normalize = width * 0.5F / distance(x, y);
            x *= normalize;
            y *= normalize;
        }
        return new Vec2(-y, x);
    }

    /**
     * Returns the sum of the squares of the given float values.
     * This is useful for computing the squared Euclidean distance between two points.
     *
     * @param a A variable number of float values
     * @return The sum of the squares of the given values
     */
    public static float distSqr(float... a) {
        float d = 0.0F;
        for (float f : a) {
            d += f * f;
        }
        return d;
    }

    /**
     * Returns the Euclidean distance between two points.
     * The Euclidean distance is the square root of the sum of the squares of the differences between corresponding coordinates.
     *
     * @param a A variable number of float values, where the number of values should be even and the values should be paired to represent coordinates.
     * @return The Euclidean distance between the two points.
     */
    public static float distance(float... a) {
        return Mth.sqrt(distSqr(a));
    }

    /**
     * Returns the midpoint of two vectors.
     * The midpoint is the vector that is halfway between the two given vectors.
     *
     * @param a The first vector
     * @param b The second vector
     * @return The midpoint of the two vectors
     */
    public static Vector4f midpoint(Vector4f a, Vector4f b) {
        return new Vector4f((a.x() + b.x()) * 0.5F, (a.y() + b.y()) * 0.5F, (a.z() + b.z()) * 0.5F, (a.w() + b.w()) * 0.5F);
    }

    /**
     * Transforms a world position into a texture coordinate using the given view model matrix and projection matrix.
     *
     * @param worldPos       The world position to transform
     * @param viewModelStack The pose stack containing the view model matrix
     * @return The resulting texture coordinate
     */
    public static Vec2 worldPosToTexCoord(Vector3f worldPos, PoseStack viewModelStack) {
        Matrix4f viewMat = viewModelStack.last().pose();
        Matrix4f projMat = RenderSystem.getProjectionMatrix();

        Vector3f localPos = new Vector3f(worldPos);
        localPos.sub(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().toVector3f());

        Vector4f pos = new Vector4f(localPos, 0);
        pos.mul(viewMat);
        pos.mul(projMat);
        VecHelper.Vector4fHelper.perspectiveDivide(pos);

        return new Vec2((pos.x() + 1F) / 2F, (pos.y() + 1F) / 2F);
    }

    /**
     * Draw a segmented line between two points, subdividing the line into a number of segments
     *
     * @param buffer    The buffer to draw to
     * @param ps        The pose stack to draw with
     * @param lineWidth The width of the line
     * @param points    The points to draw between
     * @param r red
     * @param g green
     * @param b blue
     * @param a alpha
     **/
    public static void drawSteppedLineBetween(MultiBufferSource buffer, PoseStack ps, List<Vec3> points, float lineWidth, int r, int g, int b, int a) {
        Vec3 origin = points.getFirst();
        for (int i = 1; i < points.size(); i++) {
            Vec3 target = points.get(i);
            drawLineBetween(buffer, ps, origin, target, lineWidth, r, g, b, a);
            origin = target;
        }
    }

    /**
     * Draw a segmented line between two points, subdividing the line into a number of segments
     *
     * @param buffer        The buffer to draw to
     * @param ps            The pose stack to draw with
     * @param start         The start point
     * @param end           The end point
     * @param steps         The number of steps to divide the line into
     * @param lineWidth     The width of the line
     * @param pointConsumer A consumer to call for each point in the line
     * @param r red
     * @param g green
     * @param b blue
     * @param a alpha
     */
    public static void drawSteppedLineBetween(MultiBufferSource buffer, PoseStack ps, Vec3 start, Vec3 end, int steps, float lineWidth, int r, int g, int b, int a, Consumer<Vec3> pointConsumer) {
        Vec3 origin = start;
        for (int i = 1; i <= steps; i++) {
            Vec3 target = start.add(end.subtract(start).scale(i / (float) steps));
            pointConsumer.accept(target);
            drawLineBetween(buffer, ps, origin, target, lineWidth, r, g, b, a);
            origin = target;
        }
    }

    /**
     * Draw a line between two points
     *
     * @param buffer    The buffer to draw to
     * @param ps        The pose stack to draw with
     * @param local     The start point
     * @param target    The end point
     * @param lineWidth The width of the line
     * @param r red
     * @param g green
     * @param b blue
     * @param a alpha
     */
    public static void drawLineBetween(MultiBufferSource buffer, PoseStack ps, Vec3 local, Vec3 target, float lineWidth, int r, int g, int b, int a) {
        VertexConsumer builder = buffer.getBuffer(RenderType.leash());

        //Calculate yaw
        float rotY = (float) Mth.atan2(target.x - local.x, target.z - local.z);

        //Calculate pitch
        double distX = target.x - local.x;
        double distZ = target.z - local.z;
        float rotX = (float) Mth.atan2(target.y - local.y, Mth.sqrt((float) (distX * distX + distZ * distZ)));

        ps.pushPose();

        //Translate to start point
        ps.translate(local.x, local.y, local.z);
        //Rotate to point towards end point
        ps.mulPose(VecHelper.Vector3fHelper.rotation(rotY, VecHelper.Vector3fHelper.YP));
        ps.mulPose(VecHelper.Vector3fHelper.rotation(rotX, VecHelper.Vector3fHelper.XN));

        //Calculate distance between points -> length of the line
        float distance = (float) local.distanceTo(target);

        Matrix4f matrix = ps.last().pose();
        float halfWidth = lineWidth / 2F;

        //Draw horizontal quad
        builder.addVertex(matrix, -halfWidth, 0, 0).setColor(r, g, b, a).setLight(0xF000F0);
        builder.addVertex(matrix, halfWidth, 0, 0).setColor(r, g, b, a).setLight(0xF000F0);
        builder.addVertex(matrix, halfWidth, 0, distance).setColor(r, g, b, a).setLight(0xF000F0);
        builder.addVertex(matrix, -halfWidth, 0, distance).setColor(r, g, b, a).setLight(0xF000F0);

        //Draw vertical Quad
        builder.addVertex(matrix, 0, -halfWidth, 0).setColor(r, g, b, a).setLight(0xF000F0);
        builder.addVertex(matrix, 0, halfWidth, 0).setColor(r, g, b, a).setLight(0xF000F0);
        builder.addVertex(matrix, 0, halfWidth, distance).setColor(r, g, b, a).setLight(0xF000F0);
        builder.addVertex(matrix, 0, -halfWidth, distance).setColor(r, g, b, a).setLight(0xF000F0);

        ps.popPose();
    }
}