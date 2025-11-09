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

package io.github.luckymcdev.groovyengine.lens.editor;

import imgui.ImGuiIO;
import imgui.type.ImInt;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.ImGe;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import io.github.luckymcdev.groovyengine.core.client.imgui.styles.ImGraphics;
import io.github.luckymcdev.groovyengine.lens.rendering.fbo.LensRenderTargets;
import io.github.luckymcdev.groovyengine.lens.rendering.pipeline.post.test.CrtPostShader;
import io.github.luckymcdev.groovyengine.lens.rendering.pipeline.post.test.SuperDuperPostShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderingDebuggingWindow extends EditorWindow {

    public static boolean lightingChangesEnabled = false;

    // Shader effect states
    private static boolean chromaticAberrationEnabled = false;
    private static float chromaticAmount = 0.02f;
    private static float vignetteStrength = 0.4f;

    private static boolean waveEffectEnabled = false;
    private static float waveStrength = 0.01f;
    private static float waveFrequency = 20.0f;

    private static boolean glitchEffectEnabled = false;
    private static float glitchIntensity = 0.05f;

    private static boolean depthVisualizationEnabled = false;
    private static int depthVisualizationMode = 0; // 0: Linear, 1: Non-linear, 2: Heat map, 3: Overlay
    private static int currentDepthTexture = 0; // Changed from -1 to 0 for valid default

    public RenderingDebuggingWindow() {
        super(ImIcons.CAMERA.get() + " Rendering Debug", "rendering_debug");
    }

    // Getters for the shader states
    public static boolean isChromaticAberrationEnabled() {
        return chromaticAberrationEnabled;
    }

    /**
     * Returns the current chromatic aberration amount.
     *
     * @return The current chromatic aberration amount.
     */
    public static float getChromaticAmount() {
        return chromaticAmount;
    }

    /**
     * Returns the current vignette strength.
     *
     * @return The current vignette strength.
     */
    public static float getVignetteStrength() {
        return vignetteStrength;
    }

    public static boolean isWaveEffectEnabled() {
        return waveEffectEnabled;
    }

    /**
     * Returns the current wave distortion strength.
     *
     * @return The current wave distortion strength.
     */
    public static float getWaveStrength() {
        return waveStrength;
    }


    /**
     * Returns the current wave frequency.
     *
     * @return The current wave frequency.
     */
    public static float getWaveFrequency() {
        return waveFrequency;
    }


    /**
     * Returns whether the glitch effect is currently enabled.
     *
     * @return Whether the glitch effect is currently enabled.
     */
    public static boolean isGlitchEffectEnabled() {
        return glitchEffectEnabled;
    }

    /**
     * Returns the current intensity of the glitch effect.
     *
     * @return The current intensity of the glitch effect.
     */
    public static float getGlitchIntensity() {
        return glitchIntensity;
    }

    /**
     * Returns whether depth visualization is currently enabled.
     *
     * @return Whether depth visualization is currently enabled.
     */
    public static boolean isDepthVisualizationEnabled() {
        return depthVisualizationEnabled;
    }

    /**
     * Returns the current depth visualization mode.
     *
     * @return The current depth visualization mode.
     */
    public static int getDepthVisualizationMode() {
        return depthVisualizationMode;
    }

    /**
     * Returns the current depth texture ID based on the combo box selection.
     *
     * @return The current depth texture ID.
     */
    public static int getCurrentDepthTexture() {
        // Map the combo box selection to actual texture IDs
        switch (currentDepthTexture) {
            case 0:
                return Minecraft.getInstance().getMainRenderTarget().getDepthTextureId();
            case 1:
                return LensRenderTargets.getAfterSkyDepthTextureId();
            case 2:
                return LensRenderTargets.getAfterSolidBlocksDepthTextureId();
            case 3:
                return LensRenderTargets.getAfterTranslucentBlocksDepthTextureId();
            default:
                return Minecraft.getInstance().getMainRenderTarget().getDepthTextureId();
        }
    }

    /**
     * Renders the rendering debugging window.
     *
     * @param io The ImGuiIO instance.
     */
    @Override
    public void render(ImGuiIO io) {
        renderMainWindow();
    }

    /**
     * Renders the rendering debugging window.
     * This window is used to display and manipulate rendering related data such as textures, render targets, and shader effects.
     */
    private void renderMainWindow() {
        ImGe.window(title, () -> {
            ImGe.collapsingHeader(ImIcons.TUNE.get() + " Post Processing", () -> {
                // Existing post shaders
                ImGe.text(ImIcons.FOLDER.get() + " Custom Post Shaders:");
                ImGe.button(ImIcons.APERTURE.get() + " Toggle CrtPostShader", () -> {
                    CrtPostShader.INSTANCE.setActive(!CrtPostShader.INSTANCE.isActive());
                });
                ImGe.sameLine();
                ImGe.text(CrtPostShader.INSTANCE.isActive() ? "ON" : "OFF");

                ImGe.button(ImIcons.APERTURE.get() + " Toggle SuperDuperPostShader", () -> {
                    SuperDuperPostShader.INSTANCE.setActive(!SuperDuperPostShader.INSTANCE.isActive());
                });
                ImGe.sameLine();
                ImGe.text(SuperDuperPostShader.INSTANCE.isActive() ? "ON" : "OFF");

                ImGe.separator();

                // Test shader effects
                ImGe.text(ImIcons.ADD_CIRCLE.get() + " Test Shader Effects:");

                // Chromatic Aberration
                ImGe.button(ImIcons.BLUR.get() + " Chromatic Aberration", () -> {
                    chromaticAberrationEnabled = !chromaticAberrationEnabled;
                });
                ImGe.sameLine();
                ImGe.text(chromaticAberrationEnabled ? "ON" : "OFF");

                if (chromaticAberrationEnabled) {
                    ImGe.indent();
                    float[] aberrationArray = {chromaticAmount};
                    if (ImGe.sliderFloat("Aberration Amount", aberrationArray, 0.0f, 0.1f)) {
                        chromaticAmount = aberrationArray[0];
                    }

                    float[] vignetteArray = {vignetteStrength};
                    if (ImGe.sliderFloat("Vignette Strength", vignetteArray, 0.0f, 1.0f)) {
                        vignetteStrength = vignetteArray[0];
                    }
                    ImGe.unindent();
                }

                // Wave Effect
                ImGe.button(ImIcons.BLOCK.get() + " Wave Distortion", () -> {
                    waveEffectEnabled = !waveEffectEnabled;
                });
                ImGe.sameLine();
                ImGe.text(waveEffectEnabled ? "ON" : "OFF");

                if (waveEffectEnabled) {
                    ImGe.indent();
                    float[] strengthArray = {waveStrength};
                    if (ImGe.sliderFloat("Wave Strength", strengthArray, 0.0f, 0.05f)) {
                        waveStrength = strengthArray[0];
                    }

                    float[] frequencyArray = {waveFrequency};
                    if (ImGe.sliderFloat("Wave Frequency", frequencyArray, 1.0f, 50.0f)) {
                        waveFrequency = frequencyArray[0];
                    }
                    ImGe.unindent();
                }

                // Glitch Effect
                ImGe.button(ImIcons.ANIMATION.get() + " Glitch Effect", () -> {
                    glitchEffectEnabled = !glitchEffectEnabled;
                });
                ImGe.sameLine();
                ImGe.text(glitchEffectEnabled ? "ON" : "OFF");

                if (glitchEffectEnabled) {
                    ImGe.indent();
                    float[] intensityArray = {glitchIntensity};
                    if (ImGe.sliderFloat("Glitch Intensity", intensityArray, 0.0f, 0.2f)) {
                        glitchIntensity = intensityArray[0];
                    }
                    ImGe.unindent();
                }

                ImGe.separator();
                ImGe.text(ImIcons.LAYERS.get() + " Depth Visualization:");

                ImGe.button(ImIcons.SQUARE.get() + " Depth Visualization", () -> {
                    depthVisualizationEnabled = !depthVisualizationEnabled;
                });
                ImGe.sameLine();
                ImGe.text(depthVisualizationEnabled ? "ON" : "OFF");

                if (depthVisualizationEnabled) {
                    ImGe.indent();

                    // Depth texture selection - FIXED: Use ImInt instead of int[]
                    String[] depthTextures = {
                            "Main Depth Buffer",
                            "After Sky",
                            "After Solid Blocks",
                            "After Translucent Blocks"
                    };

                    ImInt currentDepth = new ImInt(currentDepthTexture);
                    if (ImGe.combo("Depth Source", currentDepth, depthTextures)) {
                        currentDepthTexture = currentDepth.get();
                    }

                    // Visualization mode - FIXED: Use ImInt instead of int[]
                    String[] modes = {"Linear", "Non-linear", "Heat Map", "Overlay"};
                    imgui.type.ImInt mode = new ImInt(depthVisualizationMode);
                    if (ImGe.combo("Visualization Mode", mode, modes)) {
                        depthVisualizationMode = mode.get();
                    }

                    ImGe.unindent();
                }

                // Reset all button
                ImGe.separator();
                ImGe.button(ImIcons.STOP.get() + " Disable All Effects", () -> {
                    chromaticAberrationEnabled = false;
                    waveEffectEnabled = false;
                    glitchEffectEnabled = false;
                    depthVisualizationEnabled = false;
                    CrtPostShader.INSTANCE.setActive(false);
                    SuperDuperPostShader.INSTANCE.setActive(false);
                });
            });

            ImGe.collapsingHeader(ImIcons.TEXTURE.get() + " Texture Preview", () -> {
                ImGe.text(ImIcons.VISIBLE.get() + " Render Minecraft Textures in imgui:");
                float width = 64;
                float height = 64;
                ImGraphics.texture(ResourceLocation.withDefaultNamespace("textures/block/dirt.png"), width, height);
            });

            ImGe.collapsingHeader(ImIcons.LAYERS.get() + " Render Targets", () -> {
                if (ImGe.beginTabBar("RenderTargetTabs")) {
                    renderRenderTargetTab(ImIcons.SUN.get() + " After Sky", LensRenderTargets.getAfterSkyTextureId());
                    renderRenderTargetTab(ImIcons.BLOCK.get() + " After Solid Blocks", LensRenderTargets.getAfterSolidBlocksTextureId());
                    renderRenderTargetTab(ImIcons.LAYERS.get() + " After Cutout Mipped Blocks", LensRenderTargets.getAfterCutoutMippedBlocksTextureId());
                    renderRenderTargetTab(ImIcons.PERSON.get() + " After Entities", LensRenderTargets.getAfterEntitiesTextureId());
                    renderRenderTargetTab(ImIcons.FRAMED_CUBE.get() + " After Block Entities", LensRenderTargets.getAfterBlockEntitiesTextureId());
                    renderRenderTargetTab(ImIcons.BLUR.get() + " After Translucent Blocks", LensRenderTargets.getAfterTranslucentBlocksTextureId());
                    renderRenderTargetTab(ImIcons.SLASH.get() + " After Tripwire Blocks", LensRenderTargets.getAfterTripwireBlocksTextureId());
                    renderRenderTargetTab(ImIcons.ANIMATION.get() + " After Particles", LensRenderTargets.getAfterParticlesTextureId());
                    renderRenderTargetTab(ImIcons.WEATHER.get() + " After Weather", LensRenderTargets.getAfterWeatherTextureId());
                    renderRenderTargetTab(ImIcons.WORLD.get() + " After Level", LensRenderTargets.getAfterLevelTextureId());
                    renderRenderTargetTabFlipped(ImIcons.CAMERA.get() + " After PostProcessing", Minecraft.getInstance().getMainRenderTarget().getColorTextureId());
                    ImGe.endTabBar();
                }
            });

            ImGe.collapsingHeader(ImIcons.LIGHTBULB.get() + " Lighting Changes", () -> {
                ImGe.button(ImIcons.TOGGLE_ON.get() + " Toggle Lighting Changes", () -> {
                    lightingChangesEnabled = !lightingChangesEnabled;
                });
                ImGe.sameLine();
                ImGe.text("" + lightingChangesEnabled);

                ImGe.text("Current Light Texture: ");
                LightTexture lightText = Minecraft.getInstance().gameRenderer.lightTexture();
                float lightWidth = 500;
                float lightHeight = 500;
                ImGe.image(lightText.lightTexture.getId(), lightWidth, lightHeight);
            });
        });
    }

    /**
     * Renders a tab item with the given label and texture ID.
     * If the tab item is active, it calls {@link #renderRenderTarget(String, int)} to render the texture.
     *
     * @param label     The label of the tab item.
     * @param textureId The ID of the texture to render.
     */
    private void renderRenderTargetTab(String label, int textureId) {
        if (ImGe.beginTabItem(label)) {
            renderRenderTarget(label, textureId);
            ImGe.endTabItem();
        }
    }

    /**
     * Renders a tab item with the given label and texture ID, and calls
     * {@link #renderRenderTargetFlipped(String, int)} to render the texture.
     * If the tab item is active, it calls {@link #renderRenderTargetFlipped(String, int)}
     * to render the texture.
     *
     * @param label     The label of the tab item.
     * @param textureId The ID of the texture to render.
     */
    private void renderRenderTargetTabFlipped(String label, int textureId) {
        if (ImGe.beginTabItem(label)) {
            renderRenderTargetFlipped(label, textureId);
            ImGe.endTabItem();
        }
    }

    /**
     * Renders a texture with the given label and texture ID.
     * The texture is rendered in the center of the window with a size of half the window's width and height.
     * If the texture ID is valid, a tooltip is displayed on hover with information about the texture ID, size, and label.
     * If the texture ID is invalid, a dummy texture is rendered with the given size and a tooltip is displayed on hover with the label "[ No texture captured ]".
     *
     * @param label     The label of the texture.
     * @param textureId The ID of the texture to render.
     */
    private void renderRenderTarget(String label, int textureId) {
        float mcWidth = Minecraft.getInstance().getWindow().getWidth();
        float mcHeight = Minecraft.getInstance().getWindow().getHeight();
        float displayWidth = mcWidth / 2;
        float displayHeight = mcHeight / 2;

        ImGe.text(label + " (ID: " + textureId + ")");

        if (textureId > 0) {
            ImGe.image(textureId, displayWidth, displayHeight);

            if (ImGe.isItemHovered()) {
                ImGe.beginTooltip();
                ImGe.text("Texture ID: " + textureId);
                ImGe.text("Size: " + (int) displayWidth + "x" + (int) displayHeight);
                ImGe.text(label);
                ImGe.endTooltip();
            }
        } else {
            ImGe.text("[ No texture captured ]");
            ImGe.dummy(displayWidth, displayHeight);
        }
    }

    /**
     * Renders a texture with the given label and texture ID, flipped horizontally.
     * The texture is rendered in the center of the window with a size of half the window's width and height.
     * If the texture ID is valid, a tooltip is displayed on hover with information about the texture ID, size, and label.
     * If the texture ID is invalid, a dummy texture is rendered with the given size and a tooltip is displayed on hover with the label "[ No texture captured ]".
     *
     * @param label     The label of the texture.
     * @param textureId The ID of the texture to render.
     */
    private void renderRenderTargetFlipped(String label, int textureId) {
        float mcWidth = Minecraft.getInstance().getWindow().getWidth();
        float mcHeight = Minecraft.getInstance().getWindow().getHeight();
        float displayWidth = mcWidth / 2;
        float displayHeight = mcHeight / 2;

        ImGe.text(label + " (ID: " + textureId + ")");

        if (textureId > 0) {
            ImGe.image(textureId, displayWidth, displayHeight, 0f, 1f, 1f, 0f);

            if (ImGe.isItemHovered()) {
                ImGe.beginTooltip();
                ImGe.text("Texture ID: " + textureId);
                ImGe.text("Size: " + (int) displayWidth + "x" + (int) displayHeight);
                ImGe.text(label);
                ImGe.endTooltip();
            }
        } else {
            ImGe.text("[ No texture captured ]");
            ImGe.dummy(displayWidth, displayHeight);
        }
    }
}