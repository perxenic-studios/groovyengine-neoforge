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

package io.github.luckymcdev.groovyengine.lens.rendering.pipeline.post;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles world-space post-processing.
 * Based on vanilla {@link net.minecraft.client.renderer.PostChain} system, but allows the shader to access the world depth buffer.
 */
@EventBusSubscriber(value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class PostProcessManager {
    private static final List<PostProcessShader> finalInstances = new ArrayList<>();
    private static final Map<Stage, List<PostProcessShader>> stageInstances = new HashMap<>();

    private static boolean didCopyDepth = false;

    /**
     * Add an {@link PostProcessShader} for final composition (after all rendering)
     */
    public static void addInstance(PostProcessShader instance) {
        finalInstances.add(instance);
    }

    /**
     *
     * Add an {@link PostProcessShader} for a specific rendering stage
     */
    public static void addStageInstance(Stage stage, PostProcessShader instance) {
        stageInstances.computeIfAbsent(stage, k -> new ArrayList<>()).add(instance);
    }

    public static void addInstances(List<PostProcessShader> instances) {
        for (PostProcessShader shader : instances) {
            addInstance(shader);
        }
    }

    public static void render() {
        finalInstances.forEach(PostProcessShader::applyPostProcess);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }

    /**
     * Render shaders for a specific stage
     */
    public static void renderAtStage(Stage stage) {
        List<PostProcessShader> stageShaders = stageInstances.get(stage);
        if (stageShaders != null && !stageShaders.isEmpty()) {
            // Copy depth buffer for stage-specific shaders
            copyDepthBuffer();

            for (PostProcessShader shader : stageShaders) {
                if (shader.isActive()) {
                    shader.applyPostProcess();
                }
            }
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }
    }

    private static void _render() {
        copyDepthBuffer();
        render();
        didCopyDepth = false;
    }

    public static void copyDepthBuffer() {
        if (didCopyDepth) return;
        // Copy depth for both final and stage shaders
        finalInstances.forEach(PostProcessShader::copyDepthBuffer);
        stageInstances.values().forEach(shaders ->
                shaders.forEach(PostProcessShader::copyDepthBuffer)
        );
        didCopyDepth = true;
    }

    public static void resize(int width, int height) {
        finalInstances.forEach(i -> i.resize(width, height));
        stageInstances.values().forEach(shaders ->
                shaders.forEach(shader -> shader.resize(width, height))
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onWorldRenderLast(RenderLevelStageEvent event) {
        Stage stage = event.getStage();

        if (stage.equals(Stage.AFTER_PARTICLES)) {
            PostProcessShader.viewModelMatrix = RenderSystem.getModelViewMatrix();
        }

        // Render stage-specific shaders
        renderAtStage(stage);

        if (stage.equals(Stage.AFTER_LEVEL)) {
            _render();
        }
    }
}