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

import com.mojang.blaze3d.vertex.PoseStack;

public class PoseScope {
    private final PoseStack stack;
    private boolean setupWorldRendering = false;
    private Runnable preAction = null;

    public PoseScope(PoseStack stack) {
        this.stack = stack;
    }


    /**
     * Enables world rendering for the pose stack.
     * <p>
     * This method will return a new PoseScope instance with world rendering
     * enabled for the pose stack before the action is executed.
     *
     * @return A new PoseScope instance with world rendering enabled.
     */
    public PoseScope world() {
        this.setupWorldRendering = true;
        return this;
    }

    /**
     * Apply a translation transformation to the pose stack.
     * <p>
     * This method will return a new PoseScope instance with the given translation
     * applied to the pose stack before the action is executed.
     *
     * @param x The x component of the translation vector.
     * @param y The y component of the translation vector.
     * @param z The z component of the translation vector.
     * @return A new PoseScope instance with the given translation applied.
     */
    public PoseScope translate(double x, double y, double z) {
        return addPreAction(() -> stack.translate(x, y, z));
    }

    /**
     * Apply a scaling transformation to the pose stack.
     *
     * @param x The x component of the scaling vector.
     * @param y The y component of the scaling vector.
     * @param z The z component of the scaling vector.
     * @return This PoseScope instance.
     */
    public PoseScope scale(float x, float y, float z) {
        return addPreAction(() -> stack.scale(x, y, z));
    }

    /**
     * Adds a pre-action to the PoseScope.
     * <p>
     * A pre-action is an action that is executed before the main action of
     * the PoseScope. Pre-actions are executed in the order they are added.
     *
     * @param action The action to add as a pre-action.
     * @return This PoseScope instance.
     */
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

    /**
     * Runs the given action on the pose stack.
     * <p>
     * This method will first push the current pose and set the identity matrix.
     * Then, it will execute the given action on the pose stack. Finally, it will pop
     * the pose that was pushed at the beginning of this method.
     * <p>
     * If world rendering is enabled using the {@link #world()} method, it will
     * be setup before the action is executed.
     * <p>
     * If a pre-action is specified using the {@link #addPreAction(Runnable)}
     * method, it will be executed before the main action is executed.
     *
     * @param action The action to execute on the pose stack.
     */
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