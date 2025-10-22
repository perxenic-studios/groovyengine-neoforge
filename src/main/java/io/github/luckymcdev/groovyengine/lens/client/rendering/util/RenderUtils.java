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
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class RenderUtils {
    public static final int FULL_BRIGHT = 15728880;

    public static final String CONCRETE_RES_LOC_STRING = "textures/block/white_concrete.png";
    public static final ResourceLocation CONCRETE_RES_LOC = ResourceLocation.withDefaultNamespace(CONCRETE_RES_LOC_STRING);


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

    private static boolean isPointInsidePlane(Vec3 planePoint1, Vec3 planePoint2, Vec3 planePoint3, Vec3 testPoint) {
        Vec3 edge1 = planePoint2.subtract(planePoint1);
        Vec3 edge2 = planePoint3.subtract(planePoint1);
        Vec3 normal = edge1.cross(edge2).normalize();

        Vec3 toTestPoint = testPoint.subtract(planePoint1);
        double dotProduct = toTestPoint.dot(normal);

        return dotProduct <= 0.0;
    }

    public static void setupWorldRendering(PoseStack poseStack) {
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
    }

    public static void setPoseStackPosition(PoseStack stack, Vec3 worldPos) {
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        stack.setIdentity(); // clear old transformations
        stack.translate(worldPos.x - camPos.x, worldPos.y - camPos.y, worldPos.z - camPos.z);
    }

    public static void withPose(PoseStack stack, Runnable action) {
        stack.pushPose();
        try {
            action.run();
        } finally {
            stack.popPose();
        }
    }

    public static void withTranslated(PoseStack stack, double x, double y, double z, Runnable action) {
        withPose(stack, () -> {
            stack.translate(x, y, z);
            action.run();
        });
    }

    public static void withRotated(PoseStack stack, Quaternionf rotation, Runnable action) {
        withPose(stack, () -> {
            stack.mulPose(rotation);
            action.run();
        });
    }

    public static void withScaled(PoseStack stack, float x, float y, float z, Runnable action) {
        withPose(stack, () -> {
            stack.scale(x, y, z);
            action.run();
        });
    }


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

    public static Camera getMainCamera() {
        return Minecraft.getInstance().gameRenderer.getMainCamera();
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

    public static Vector3f parametricSphere(float u, float v, float r) {
        return new Vector3f(Mth.cos(u) * Mth.sin(v) * r, Mth.cos(v) * r, Mth.sin(u) * Mth.sin(v) * r);
    }

    public static Vector3f parametricTorus(float u, float v, float majorRadius, float minorRadius) {
        float x = (majorRadius + minorRadius * Mth.cos(v)) * Mth.cos(u);
        float y = minorRadius * Mth.sin(v);
        float z = (majorRadius + minorRadius * Mth.cos(v)) * Mth.sin(u);
        return new Vector3f(x, y, z);
    }

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

    public static float distSqr(float... a) {
        float d = 0.0F;
        for (float f : a) {
            d += f * f;
        }
        return d;
    }

    public static float distance(float... a) {
        return Mth.sqrt(distSqr(a));
    }

    public static Vector4f midpoint(Vector4f a, Vector4f b) {
        return new Vector4f((a.x() + b.x()) * 0.5F, (a.y() + b.y()) * 0.5F, (a.z() + b.z()) * 0.5F, (a.w() + b.w()) * 0.5F);
    }

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