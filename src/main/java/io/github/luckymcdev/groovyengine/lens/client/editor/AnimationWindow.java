package io.github.luckymcdev.groovyengine.lens.client.editor;

import imgui.ImGui;
import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.GroovyEngineClient;
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

    @Override
    public void render(ImGuiIO io) {
        //renderChupacabraAnimationController(io);
        renderAmoController(io);
    }

    private void renderAmoController(ImGuiIO io) {
        ImGe.window(ImIcons.ANIMATION.get() + " Animation Controller", () -> {
            ImGe.button("Play animation", () -> LensRendering.animatedModel.playAnimation("rotate_x"));

            ImGe.button("Resume animation", LensRendering.animatedModel::resumeAnimation);

            ImGe.button("Stop animation", LensRendering.animatedModel::stopAnimation);
        });
    }

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
