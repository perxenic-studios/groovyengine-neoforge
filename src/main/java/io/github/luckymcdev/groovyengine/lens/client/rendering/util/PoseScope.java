package io.github.luckymcdev.groovyengine.lens.client.rendering.util;

import com.mojang.blaze3d.vertex.PoseStack;

public class PoseScope {
    private final PoseStack stack;
    private boolean setupWorldRendering = false;
    private Runnable preAction = null;

    public PoseScope(PoseStack stack) {
        this.stack = stack;
    }

    public PoseScope world() {
        this.setupWorldRendering = true;
        return this;
    }

    public PoseScope translate(double x, double y, double z) {
        return addPreAction(() -> stack.translate(x, y, z));
    }

    public PoseScope scale(float x, float y, float z) {
        return addPreAction(() -> stack.scale(x, y, z));
    }

    private PoseScope addPreAction(Runnable action) {
        if (preAction == null) {
            preAction = action;
        } else {
            Runnable oldAction = preAction;
            preAction = () -> {
                oldAction.run();
                action.run();
            };
        }
        return this;
    }

    public void run(StackAction action) {
        stack.pushPose();
        stack.setIdentity();

        if (setupWorldRendering) {
            RenderUtils.setupWorldRendering(stack);
        }
        if (preAction != null) {
            preAction.run();
        }

        action.execute(stack);

        stack.popPose();

    }

    @FunctionalInterface
    public interface StackAction {
        void execute(PoseStack stack);
    }
}