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

package io.github.luckymcdev.groovyengine.lens.client.editor;

import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.ImGe;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import io.github.luckymcdev.groovyengine.lens.LensRendering;

/**
 * Window to modify the Animations for Amo Model.
 */
public class AnimationWindow extends EditorWindow {

    /**
     * Creates a new AnimationWindow.
     */
    public AnimationWindow() {
        super(ImIcons.ANIMATION.get() + " AnimationWindow");
        this.setEnabled(false);
    }

    /**
     * Renders the animation window.
     * This method renders two sections: a section to control the animation of the animated model and a section to control the animation of the Chupacabra animations.
     * The first section contains three buttons: "Play animation", "Resume animation", and "Stop animation".
     * The second section contains a slider to adjust the speed of the Chupacabra animations and a text display of the current speed.
     *
     * @param io the ImGuiIO to use for rendering
     */
    @Override
    public void render(ImGuiIO io) {
        renderAmoController();
    }

    /**
     * Renders the animation window.
     * This method renders three buttons: "Play animation", "Resume animation", and "Stop animation".
     * The "Play animation" button plays the "rotate_x" animation of the animated model.
     * The "Resume animation" button resumes the animation of the animated model.
     * The "Stop animation" button stops the animation of the animated model.
     *
     * @param io the ImGuiIO to use for rendering
     */
    private void renderAmoController() {
        ImGe.window(ImIcons.ANIMATION.get() + " Animation Controller", () -> {
            ImGe.button("Play animation", () -> LensRendering.animatedModel.playAnimation("rotate_x"));

            ImGe.button("Resume animation", LensRendering.animatedModel::resumeAnimation);

            ImGe.button("Stop animation", LensRendering.animatedModel::stopAnimation);
        });
    }
}
