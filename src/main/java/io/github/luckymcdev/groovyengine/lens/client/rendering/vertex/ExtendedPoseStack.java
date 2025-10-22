package io.github.luckymcdev.groovyengine.lens.client.rendering.vertex;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public record ExtendedPoseStack(PoseStack stack) {

    public PoseStack getVanilla() {
        return stack;
    }

    public void translate(Vec3 pos) {
        stack.translate(pos.x, pos.y, pos.z);
    }

    public void translateInverse(Vec3 pos) {
        stack.translate(-pos.x, -pos.y, -pos.z);
    }

    // Vanilla methods
    public void pushPose() {
        stack.pushPose();
    }

    public void popPose() {
        stack.popPose();
    }

    public void mulPose(Matrix4f matrix) {
        stack.mulPose(matrix);
    }

    public void mulPose(Quaternionf quat) {
        stack.mulPose(quat);
    }

    public void scale(float x, float y, float z) {
        stack.scale(x, y, z);
    }

    public void translate(float x, float y, float z) {
        stack.translate(x, y, z);
    }
}
