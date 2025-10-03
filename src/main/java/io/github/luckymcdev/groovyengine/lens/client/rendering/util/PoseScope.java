package io.github.luckymcdev.groovyengine.lens.client.rendering.util;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PoseScope {
    private final PoseStack stack;
    private Vector3f translation;
    private Vector3f scale = new Vector3f(1,1,1);
    private Quaternionf rotation;
    private boolean world;

    public PoseScope(PoseStack stack) {
        this.stack = stack;
    }

    public PoseScope translate(double x, double y, double z) {
        this.translation = new Vector3f((float) x, (float) y, (float) z);
        return this;
    }

    public PoseScope setWorld(boolean enabled) {
        world = enabled;
        return this;
    }

    public PoseScope scale(float x, float y, float z) {
        this.scale = new Vector3f(x, y, z);
        return this;
    }

    public PoseScope rotate(Quaternionf rotation) {
        this.rotation = rotation;
        return this;
    }

    public void run(Runnable action) {
        stack.pushPose();
        try {
            if (world) RenderUtils.setupWorldRendering(stack);

            if( translation != null) stack.translate(translation.x, translation.y, translation.z);

            stack.scale(scale.x, scale.y, scale.z);

            if (rotation != null) stack.mulPose(rotation);

            action.run();

        } finally {
            stack.popPose();
        }
    }
}
