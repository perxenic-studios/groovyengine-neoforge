package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles world-space post-processing.
 * Based on vanilla {@link net.minecraft.client.renderer.PostChain} system, but allows the shader to access the world depth buffer.
 */
@EventBusSubscriber(value = Dist.CLIENT)
public class PostProcessManager {
    private static final List<PostProcessShader> instances = new ArrayList<>();

    private static boolean didCopyDepth = false;

    /**
     * Add an {@link PostProcessShader} for it to be handled automatically.
     * IMPORTANT: processors has to be added in the right order!!!
     * There's no way of getting an instance, so you need to keep the instance yourself.
     */
    public static void addInstance(PostProcessShader instance) {
        instances.add(instance);
    }
    public static void addInstances(List<PostProcessShader> instances) {
        for(PostProcessShader shader : instances) {
            addInstance(shader);
        }
    }

    public static void render() {
        instances.forEach(PostProcessShader::applyPostProcess);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }

    public static void copyDepthBuffer() {
        if (didCopyDepth) return;
        instances.forEach(PostProcessShader::copyDepthBuffer);
        didCopyDepth = true;
    }

    public static void resize(int width, int height) {
        instances.forEach(i -> i.resize(width, height));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onWorldRenderLast(RenderLevelStageEvent event) {
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_PARTICLES)) {
            PostProcessShader.viewModelMatrix = RenderSystem.getModelViewMatrix(); // Copy viewModelMatrix from RenderSystem
        }
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_LEVEL)) {
            copyDepthBuffer(); // copy the depth buffer if the mixin didn't trigger

            render();

            didCopyDepth = false; // reset for next frame
        }
    }
}