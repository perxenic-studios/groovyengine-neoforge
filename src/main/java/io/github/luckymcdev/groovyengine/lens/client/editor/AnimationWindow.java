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
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation.AnimationController;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation.ChupacabraAnimations;

public class AnimationWindow extends EditorWindow {

    private final float[] animationSpeed = new float[]{1.0f};

    public AnimationWindow() {
        super(ImIcons.ANIMATION.get() + " AnimationWindow");
    }

    /**
     * Renders the animation window.
     * This method renders two sections: a section to control the animation of the animated model and a section to control the animation of the Chupacabra animations.
     * The first section contains three buttons: "Play animation", "Resume animation", and "Stop animation".
     * The second section contains a slider to adjust the speed of the Chupacabra animations and a text display of the current speed.
     * @param io the ImGuiIO to use for rendering
     */
    @Override
    public void render(ImGuiIO io) {
        //renderChupacabraAnimationController(io);
        renderAmoController(io);
    }

    /**
     * Renders the animation window.
     * This method renders three buttons: "Play animation", "Resume animation", and "Stop animation".
     * The "Play animation" button plays the "rotate_x" animation of the animated model.
     * The "Resume animation" button resumes the animation of the animated model.
     * The "Stop animation" button stops the animation of the animated model.
     * @param io the ImGuiIO to use for rendering
     */
    private void renderAmoController(ImGuiIO io) {
        ImGe.window(ImIcons.ANIMATION.get() + " Animation Controller", () -> {
            ImGe.button("Play animation", () -> LensRendering.animatedModel.playAnimation("rotate_x"));

            ImGe.button("Resume animation", LensRendering.animatedModel::resumeAnimation);

            ImGe.button("Stop animation", LensRendering.animatedModel::stopAnimation);
        });
    }

    /**
     * UNUSED / DEPRECATED
     * <p>
     * Renders the Chupacabra animation window.
     * This method renders several sections: a section to adjust the speed of the Chupacabra animations, a section to display the current animation, a section for idle animations, a section for locomotion animations, a section for actions, and a section for playback controls.
     * The speed section contains a slider to adjust the speed of the Chupacabra animations and a text display of the current speed.
     * The current animation section displays the current animation and whether it is playing.
     * The idle animations section contains buttons to play the idle animations: "Idle Breathing", "Idle Look Around", and "Idle Look Around".
     * The locomotion animations section contains buttons to play the locomotion animations: "Walk", "Run".
     * The actions section contains buttons to play the actions: "Attack Bite", "Howl", "Sniff", "Crouch", "Jump", "Tail Wag".
     * The playback controls section contains buttons to pause, resume, stop, and reset the Chupacabra animations.
     * @param io the ImGuiIO to use for rendering
     */
    private void renderChupacabraAnimationController(ImGuiIO io) {
        ImGe.window(ImIcons.ANIMATION.get() + " Animation Controller", () -> {
            ImGe.collapsingHeader("Chupacabra Animations", () -> {
                AnimationController chupacabraController = ChupacabraAnimations.getController();

                // Slider to adjust speed
                if (ImGe.sliderFloat("Speed", animationSpeed, 0.0f, 2.0f)) {
                    chupacabraController.setPlaybackSpeed(animationSpeed[0]);
                }

                // Display current speed
                ImGe.sameLine();
                ImGe.text(String.format("%.2f", animationSpeed[0]));

                ImGe.separator();

                // Display current animation
                String currentAnim = ChupacabraAnimations.getCurrentAnimationName();
                ImGe.text("Current: " + (currentAnim != null ? currentAnim : "None"));
                ImGe.text("Playing: " + ChupacabraAnimations.isPlaying());

                ImGe.separator();
                ImGe.text("Idle Animations");
                ImGe.separator();

                ImGe.button("Idle Breathing", () -> {
                    chupacabraController.setPlaybackSpeed(animationSpeed[0]);
                    chupacabraController.play(ChupacabraAnimations.IDLE_BREATHING);
                });

                ImGe.button("Idle Look Around", () -> {
                    chupacabraController.setPlaybackSpeed(animationSpeed[0]);
                    chupacabraController.play(ChupacabraAnimations.IDLE_LOOK_AROUND);
                });

                ImGe.separator();
                ImGe.text("Locomotion");
                ImGe.separator();

                ImGe.button("Walk", () -> {
                    chupacabraController.setPlaybackSpeed(animationSpeed[0]);
                    chupacabraController.play(ChupacabraAnimations.WALK);
                });

                ImGe.button("Run", () -> {
                    chupacabraController.setPlaybackSpeed(animationSpeed[0]);
                    chupacabraController.play(ChupacabraAnimations.RUN);
                });

                ImGe.separator();
                ImGe.text("Actions");
                ImGe.separator();

                ImGe.button("Attack Bite", () -> {
                    chupacabraController.setPlaybackSpeed(animationSpeed[0]);
                    chupacabraController.play(ChupacabraAnimations.ATTACK_BITE);
                });

                ImGe.button("Howl", () -> {
                    chupacabraController.setPlaybackSpeed(animationSpeed[0]);
                    chupacabraController.play(ChupacabraAnimations.HOWL);
                });

                ImGe.button("Sniff", () -> {
                    chupacabraController.setPlaybackSpeed(animationSpeed[0]);
                    chupacabraController.play(ChupacabraAnimations.SNIFF);
                });

                ImGe.button("Crouch", () -> {
                    chupacabraController.setPlaybackSpeed(animationSpeed[0]);
                    chupacabraController.play(ChupacabraAnimations.CROUCH);
                });

                ImGe.button("Jump", () -> {
                    chupacabraController.setPlaybackSpeed(animationSpeed[0]);
                    chupacabraController.play(ChupacabraAnimations.JUMP);
                });

                ImGe.button("Tail Wag", () -> {
                    chupacabraController.setPlaybackSpeed(animationSpeed[0]);
                    chupacabraController.play(ChupacabraAnimations.TAIL_WAG);
                });

                ImGe.separator();
                ImGe.text("Playback Controls");
                ImGe.separator();

                ImGe.button("Pause", chupacabraController::pause);
                ImGe.sameLine();

                ImGe.button("Resume", chupacabraController::resume);
                ImGe.sameLine();

                ImGe.button("Stop", chupacabraController::stop);
                ImGe.sameLine();

                ImGe.button("Reset", chupacabraController::reset);
            });
        });
    }
}
