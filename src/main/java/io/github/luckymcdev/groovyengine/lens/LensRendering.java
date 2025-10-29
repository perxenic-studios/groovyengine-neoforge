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

package io.github.luckymcdev.groovyengine.lens;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.lens.client.editor.RenderingDebuggingWindow;
import io.github.luckymcdev.groovyengine.lens.client.rendering.core.GeMaterials;
import io.github.luckymcdev.groovyengine.lens.client.rendering.core.VFXBuilders;
import io.github.luckymcdev.groovyengine.lens.client.rendering.material.Material;
import io.github.luckymcdev.groovyengine.lens.client.rendering.particle.ParticleBuilder;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.PostProcessChain;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.compute.ComputeShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core.test.TestShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.effect.ShaderEffectTest;
import io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.core.Renderer;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.PoseScope;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.RenderUtils;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.ObjModel;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.amo.AmoModel;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation.ChupacabraAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

import static io.github.luckymcdev.groovyengine.lens.client.rendering.core.GeMaterials.DEBUG_TRIANGLES;
import static io.github.luckymcdev.groovyengine.lens.client.rendering.core.GeMaterials.OBJ_MODEL;
import static io.github.luckymcdev.groovyengine.lens.client.systems.obj.ObjModelManager.registerObjModel;

@EventBusSubscriber
public class LensRendering {

    // Add AMO model registration
    public static final AmoModel animatedModel = new AmoModel(GE.id("animated"));
    public static final PostProcessChain POST_CHAIN = new PostProcessChain();
    private static final ObjModel circle = new ObjModel(GE.id("mesh/circle/circle"));
    private static final ObjModel cone = new ObjModel(GE.id("mesh/cone/cone"));
    private static final ObjModel cube = new ObjModel(GE.id("mesh/cube/cube"));
    private static final ObjModel cylinder = new ObjModel(GE.id("mesh/cylinder/cylinder"));
    private static final ObjModel icosphere = new ObjModel(GE.id("mesh/icosphere/ico_sphere"));
    private static final ObjModel plane = new ObjModel(GE.id("mesh/plane/plane"));
    private static final ObjModel sphere = new ObjModel(GE.id("mesh/sphere/sphere"));
    private static final ObjModel suzanne = new ObjModel(GE.id("mesh/suzanne/suzanne"));
    private static final ObjModel torus = new ObjModel(GE.id("mesh/torus/torus"));
    private static final ObjModel chupacabraModel = new ObjModel(GE.id("chupacabra"));
    private static final ResourceLocation chupacabraTexture = GE.id("obj/chupacabra.png");
    private static final Renderer renderer = Renderer.getInstance();

    /**
     * Initializes the rendering system by registering all OBJ models and initializing animations.
     */
    public static void initialize() {

        // Execute compute shader test
        Minecraft.getInstance().execute(() -> {
            ResourceLocation shaderConfig = GE.id("compute_test");
            int elementCount = 128;
            ByteBuffer data = MemoryUtil.memAlloc(elementCount * Float.BYTES);

            try {
                FloatBuffer floatBuffer = data.asFloatBuffer();
                for (int i = 0; i < elementCount; i++) {
                    floatBuffer.put(i, i * 0.1f);
                }

                float[] beforeArray = new float[16];
                floatBuffer.position(0);
                floatBuffer.get(beforeArray, 0, 16);
                System.err.println("BEFORE: " + Arrays.toString(beforeArray));

                try (ComputeShader shader = new ComputeShader(data, Float.BYTES, shaderConfig)) {
                    shader.dispatch();
                    float[] result = shader.readBackFloats();
                    System.err.println("COMPUTE READBACK:");
                    System.err.println("AFTER:  " + Arrays.toString(Arrays.copyOf(result, 16)));
                }
            } finally {
                MemoryUtil.memFree(data);
            }
        });

        // Register models
        registerObjModel(circle);
        registerObjModel(cone);
        registerObjModel(cube);
        registerObjModel(cylinder);
        registerObjModel(icosphere);
        registerObjModel(plane);
        registerObjModel(sphere);
        registerObjModel(suzanne);
        registerObjModel(torus);

        registerObjModel(chupacabraModel);
        registerObjModel(animatedModel);

        // Initialize animations
        ChupacabraAnimations.initialize(chupacabraModel);
    }

    /**
     * Updates the animation time for the animated model on the client tick event.
     * This method is called automatically on the client tick event.
     * It scales the delta time to seconds and updates the animation time of the animated model.
     *
     * @param event The client tick event.
     */
    public static void onClientTick(ClientTickEvent.Pre event) {
        float deltaTime = Minecraft.getInstance().getTimer().getRealtimeDeltaTicks();
        if (animatedModel.isModelLoaded()) {
            animatedModel.updateAnimation(deltaTime * 0.05f); // Scale to seconds
        }
    }

    /**
     * Registers the test shader with the given register shaders event.
     * This method is called automatically on the register shaders event.
     * It registers the test shader with the event and logs any errors during registration.
     *
     * @param event The register shaders event to register the test shader with.
     */
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            TestShader.INSTANCE.register(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up rendering for a given render level stage event.
     * This method is called automatically on the render level stage event.
     * It renders the final composition of the level if the event stage is AFTER_LEVEL,
     * and renders particle effects, VFX shapes, and models if the event stage is AFTER_TRANSLUCENT_BLOCKS.
     *
     * @param event The render level stage event to set up rendering for.
     */
    @SubscribeEvent
    private static void setupRendering(RenderLevelStageEvent event) {
        ClientLevel level = Minecraft.getInstance().level;

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            renderFinal(event);
        }

        /*
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            createParticleEffects(level);
            renderVFXShapes(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource());
            renderModels(event.getPoseStack());
        }

         */
    }

    /**
     * Renders the final composition of the level after all rendering is complete.
     * This method is called automatically on the render level stage event.
     * It renders the final composition of the level with any active shader effects.
     *
     * @param event The render level stage event to render the final composition for.
     */
    private static void renderFinal(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;

        if (RenderingDebuggingWindow.isChromaticAberrationEnabled()) {
            POST_CHAIN.addEffect(
                    ShaderEffectTest.createChromaticEffect(
                            RenderingDebuggingWindow.getChromaticAmount(),
                            RenderingDebuggingWindow.getVignetteStrength()
                    )
            );
        }

        if (RenderingDebuggingWindow.isWaveEffectEnabled()) {
            POST_CHAIN.addEffect(
                    ShaderEffectTest.createWaveEffect(
                            RenderingDebuggingWindow.getWaveStrength(),
                            RenderingDebuggingWindow.getWaveFrequency()
                    )
            );
        }

        if (RenderingDebuggingWindow.isGlitchEffectEnabled()) {
            POST_CHAIN.addEffect(
                    ShaderEffectTest.createGlitchEffect(
                            RenderingDebuggingWindow.getGlitchIntensity()
                    )
            );
        }

        if (RenderingDebuggingWindow.isDepthVisualizationEnabled()) {
            POST_CHAIN.addEffect(
                    ShaderEffectTest.createDepthVisualizationEffect(
                            RenderingDebuggingWindow.getCurrentDepthTexture(),
                            RenderingDebuggingWindow.getDepthVisualizationMode()
                    )
            );
        }
    }

    /**
     * Resets the post-processing chain by clearing all effects on the render level stage event.
     * This method is automatically called on the render level stage event with the highest priority.
     *
     * @param event The render level stage event to reset the post-processing chain for.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    private static void resetPostChain(RenderLevelStageEvent event) {
        POST_CHAIN.clearEffects();
    }

    /**
     * Renders the entire post-processing chain on the render level stage event.
     * This method is automatically called on the render level stage event with the lowest priority.
     * It executes the entire post-processing chain and then disposes all shader effects.
     *
     * @param event The render level stage event to render the post-processing chain for.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    private static void renderPostChain(RenderLevelStageEvent event) {
        POST_CHAIN.execute();

        ShaderEffectTest.dispose();
    }

    /**
     * Creates a variety of particle effects at different positions in the world.
     * This method is used to test the particle system and is not intended to be used in production code.
     * It creates a range of effects, from simple flames and smoke to more complex effects like portals and notes.
     * The effects are spawned at different positions in the world, with some being spawned in a circle pattern.
     *
     * @param level The client level to create the particle effects in.
     */
    private static void createParticleEffects(ClientLevel level) {
        ParticleBuilder.create(level, 100, 130, 100)
                .flame()
                .spawn();

        ParticleBuilder.create(level, 105, 135, 95)
                .smoke()
                .setMotion(0.1, 0.2, 0.1)
                .spawn();

        ParticleBuilder.create(level, 95, 140, 105)
                .portal()
                .setMotion(0, 0.1, 0)
                .alwaysRender()
                .spawn();

        ParticleBuilder.create(level, 110, 125, 90)
                .flame()
                .spawnMultiple(10);

        ParticleBuilder.create(level, 90, 145, 110)
                .note()
                .spawnBurst(20, 0.5);

        ParticleBuilder.create(level, 115, 120, 85)
                .flame()
                .setMotion(0, 0.1, 0)
                .andSpawn()
                .smoke()
                .setMotion(0, 0.05, 0)
                .spawn();

        double centerX = 100;
        double centerY = 130;
        double centerZ = 100;
        double radius = 3.0;
        int circleParticles = 16;

        for (int i = 0; i < circleParticles; i++) {
            double angle = 2 * Math.PI * i / circleParticles;
            double particleX = centerX + radius * Math.cos(angle);
            double particleZ = centerZ + radius * Math.sin(angle);

            ParticleBuilder.create(level, particleX, centerY, particleZ)
                    .flame()
                    .setMotion(0, 0.1, 0)
                    .spawn();
        }

        for (int i = 0; i < 10; i++) {
            ParticleBuilder.create(level, 120 + i, 130, 100)
                    .smoke()
                    .setMotion(0, 0.05, 0)
                    .spawn();
        }

        for (int i = 0; i < 15; i++) {
            double randomX = 100 + (Math.random() - 0.5) * 10;
            double randomY = 130 + (Math.random() - 0.5) * 10;
            double randomZ = 100 + (Math.random() - 0.5) * 10;

            ParticleBuilder.create(level, randomX, randomY, randomZ)
                    .note()
                    .randomMotion(0.1)
                    .spawn();
        }
    }

    /**
     * Renders various shapes using VFXBuilders.
     * <p>
     * The shapes that are rendered are a triangle, a sphere, and a torus.
     * These shapes are rendered at position (100, 200, 100) in the world.
     * After rendering the shapes, the screen is rendered with a screen VFX.
     *
     * @param stack   The pose stack to use for rendering.
     * @param buffers The buffer source to use for rendering.
     */
    private static void renderVFXShapes(PoseStack stack, MultiBufferSource.BufferSource buffers) {
        // Triangle
        new PoseScope(stack)
                .world()
                .run(poseStack -> {
                    poseStack.translate(100, 200, 100);
                    renderVFXBuilderTriangle(poseStack, buffers);
                });

        // Sphere
        new PoseScope(stack)
                .world()
                .run(poseStack -> {
                    poseStack.translate(100, 200, 100);
                    renderVFXBuilderSphere(poseStack, buffers);
                });

        // Torus
        new PoseScope(stack)
                .world()
                .run(poseStack -> {
                    poseStack.translate(100, 200, 100);
                    renderVFXBuilderTorus(poseStack, buffers);
                });

        new PoseScope(stack)
                .run(poseStack -> {
                    renderScreenVFX(poseStack);
                });
    }

    /**
     * Renders all primitive models in a grid at position (35, 140, 0).
     * Additionally, renders an animated Chupacabra model at position (35, 140, 60)
     * and an AMO model at position (65, 140, 60) with an offset.
     *
     * @param stack The pose stack to use for rendering.
     */
    private static void renderModels(PoseStack stack) {
        // Render all primitive models in a grid at position (35, 140, 0)
        new PoseScope(stack)
                .world()
                .run(poseStack -> {
                    renderAllObjModels(poseStack, new Vec3(35, 140, 0));
                });

        new PoseScope(stack)
                .world()
                .run(poseStack -> {
                    renderObjModelAnimated(poseStack, chupacabraModel, new Vec3(35, 140, 60), OBJ_MODEL.withTexture(chupacabraTexture));
                });

        // Add AMO model rendering with offset
        new PoseScope(stack)
                .world()
                .run(poseStack -> {
                    renderAmoModel(poseStack, animatedModel, new Vec3(65, 140, 60));
                });
    }

    /**
     * Renders all primitive models in a grid at the given start position.
     * <p>
     * The models that are rendered are cube, sphere, cylinder, cone, plane, circle, icosphere, torus, and suzanne.
     * These models are rendered at the given start position with a spacing of 30 units on the x-axis.
     *
     * @param stack         The pose stack to use for rendering.
     * @param startPosition The start position of the grid.
     */
    static void renderAllObjModels(PoseStack stack, Vec3 startPosition) {
        new PoseScope(stack).world().run(p -> renderObjModel(p, cube, startPosition.add(0, 0, 0)));
        new PoseScope(stack).world().run(p -> renderObjModel(p, sphere, startPosition.add(30, 0, 0)));
        new PoseScope(stack).world().run(p -> renderObjModel(p, cylinder, startPosition.add(60, 0, 0)));
        new PoseScope(stack).world().run(p -> renderObjModel(p, cone, startPosition.add(90, 0, 0)));

        new PoseScope(stack).world().run(p -> renderObjModel(p, plane, startPosition.add(0, 0, 30)));
        new PoseScope(stack).world().run(p -> renderObjModel(p, circle, startPosition.add(30, 0, 30)));
        new PoseScope(stack).world().run(p -> renderObjModel(p, icosphere, startPosition.add(60, 0, 30)));
        new PoseScope(stack).world().run(p -> renderObjModel(p, torus, startPosition.add(90, 0, 30)));

        new PoseScope(stack).world().run(p -> renderObjModel(p, suzanne, startPosition.add(30, 0, 30)));

    }


    /**
     * Renders an animated model with the given pose stack and position.
     * The model is scaled by a factor of 5 and translated to the given position.
     * The packed light value at the given position is used for rendering.
     * If an exception occurs while rendering the model, an error message is logged.
     *
     * @param stack    The pose stack to use for rendering.
     * @param model    The animated model to render.
     * @param position The position to render the model at.
     */
    static void renderAmoModel(PoseStack stack, AmoModel model, Vec3 position) {

        stack.pushPose();
        stack.translate(position.x, position.y, position.z);
        stack.scale(5, 5, 5);

        int packedLight = RenderUtils.getPackedLightAt(position);

        try {
            model.renderAnimated(stack, OBJ_MODEL.withTexture(RenderUtils.CONCRETE_RES_LOC), packedLight);
        } catch (Exception e) {
            GE.LENS_LOG.error("Error rendering AMO model: {}", e.getMessage(), e);
        }

        stack.popPose();
    }

    /**
     * Renders an OBJ model with the given pose stack and position.
     * The model is translated to the given position and scaled by a factor of 10.
     * The packed light value at the given position is used for rendering.
     *
     * @param stack    The pose stack to use for rendering.
     * @param model    The OBJ model to render.
     * @param position The position to render the model at.
     */
    static void renderObjModel(PoseStack stack, ObjModel model, Vec3 position) {

        stack.translate(position.x, position.y, position.z);
        stack.scale(10f, 10f, 10f);

        int packedLight = RenderUtils.getPackedLightAt(position);

        model.renderModel(stack, OBJ_MODEL, packedLight);
    }

    /**
     * Renders an OBJ model with the given pose stack, model, position, and material.
     * The model is translated to the given position and scaled by a factor of 10.
     * The packed light value at the given position is used for rendering.
     *
     * @param stack    The pose stack to use for rendering.
     * @param model    The OBJ model to render.
     * @param position The position to render the model at.
     * @param material The material to use for rendering.
     */
    static void renderObjModel(PoseStack stack, ObjModel model, Vec3 position, Material material) {
        stack.translate(position.x, position.y, position.z);
        stack.scale(10f, 10f, 10f);

        int packedLight = RenderUtils.getPackedLightAt(position);

        model.renderModel(stack, material, packedLight);
    }

    /**
     * Renders an animated OBJ model with the given pose stack, model, position, and material.
     * The model is translated to the given position and scaled by a factor of 10.
     * The packed light value at the given position is used for rendering.
     *
     * @param stack    The pose stack to use for rendering.
     * @param model    The animated OBJ model to render.
     * @param position The position to render the model at.
     * @param material The material to use for rendering.
     */
    static void renderObjModelAnimated(PoseStack stack, ObjModel model, Vec3 position, Material material) {

        stack.translate(position.x, position.y, position.z);
        stack.scale(10f, 10f, 10f);

        int packedLight = RenderUtils.getPackedLightAt(position);

        model.renderModelAnimated(stack, material, packedLight);
    }

    /**
     * Renders a triangle using VFXBuilders.
     * <p>
     * The triangle is rendered with material {@link GeMaterials#DEBUG_TRIANGLES}, color (0.0f, 1.0f, 0.0f, 1.0f), and UV coordinates (0f, 0f, 1f, 1f).
     * The triangle is placed at position (0, 50, 0) in the world.
     *
     * @param poseStack    The pose stack to use for rendering.
     * @param bufferSource The buffer source to use for rendering.
     */
    static void renderVFXBuilderTriangle(PoseStack poseStack, MultiBufferSource bufferSource) {
        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld()
                .setMaterial(DEBUG_TRIANGLES)
                .replaceBufferSource(bufferSource)
                .setColor(0.0f, 1.0f, 0.0f, 1.0f)
                .setUV(0f, 0f, 1f, 1f);

        poseStack.translate(0, 50, 0);

        builder.placeVertex(poseStack, 0.0f, 1.0f, 0.0f);
        builder.placeVertex(poseStack, -1.0f, -1.0f, 0.0f);
        builder.placeVertex(poseStack, 1.0f, -1.0f, 0.0f);
    }

    /**
     * Renders a sphere using VFXBuilders.
     * <p>
     * The sphere is rendered with material {@link GeMaterials#DEBUG_TRIANGLES}, color (0.0f, 0.0f, 1.0f, 1.0f), and UV coordinates (0f, 0f, 1f, 1f).
     * The sphere is placed at position (-50, 0, 0) in the world, with radius 15f, and is rendered with 8x8 segments.
     *
     * @param poseStack    The pose stack to use for rendering.
     * @param bufferSource The buffer source to use for rendering.
     */
    static void renderVFXBuilderSphere(PoseStack poseStack, MultiBufferSource bufferSource) {
        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld()
                .setMaterial(DEBUG_TRIANGLES)
                .replaceBufferSource(bufferSource)
                .setColor(0.0f, 0.0f, 1.0f, 1.0f)
                .setUV(0f, 0f, 1f, 1f);

        poseStack.translate(-50, 0, 0);

        builder.renderSphere(poseStack, 15f, 8, 8);
    }

    /**
     * Renders a torus using VFXBuilders.
     * <p>
     * The torus is rendered with material {@link GeMaterials#DEBUG_TRIANGLES}, color (1.0f, 1.0f, 0.0f, 1.0f), and UV coordinates (0f, 0f, 1f, 1f).
     * The torus is placed at position (0, 0, 50) in the world, with radius 10f, inner radius 3f, and is rendered with 50x30 segments.
     *
     * @param poseStack    The pose stack to use for rendering.
     * @param bufferSource The buffer source to use for rendering.
     */
    static void renderVFXBuilderTorus(PoseStack poseStack, MultiBufferSource bufferSource) {
        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld()
                .setMaterial(DEBUG_TRIANGLES)
                .replaceBufferSource(bufferSource)
                .setColor(1.0f, 1.0f, 0.0f, 1.0f)
                .setUV(0f, 0f, 1f, 1f);

        poseStack.translate(0, 0, 50);

        // Render a torus
        builder.renderTorus(poseStack, 10f, 3f, 50, 30);
    }

    /**
     * Renders a cube using VFXBuilders.
     * <p>
     * The cube is rendered with render type {@link RenderType#debugQuads()}, material {@link RenderType#debugQuads()}, color (1.0f, 0.5f, 0.0f, 1.0f), and UV coordinates (0f, 0f, 1f, 1f).
     * The cube is placed at position (-50, 50, 0) in the world.
     *
     * @param poseStack    The pose stack to use for rendering.
     * @param bufferSource The buffer source to use for rendering.
     */
    static void renderVFXBuilderCube(PoseStack poseStack, MultiBufferSource bufferSource) {
        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld()
                .setRenderType(RenderType.debugQuads())
                .replaceBufferSource(bufferSource)
                .setColor(1.0f, 0.5f, 0.0f, 1.0f) // Orange color
                .setUV(0f, 0f, 1f, 1f);

        poseStack.translate(-50, 50, 0);
    }

    /**
     * Renders a screen-space VFX using VFXBuilders.
     * <p>
     * The VFX is rendered with the blit shader, the dirt texture, and a white color.
     * The VFX is placed at position (100, 100) in the screen, with a size of 200x200 pixels.
     * The UV coordinates are set to (0f, 0f, 1f, 1f).
     *
     * @param poseStack The pose stack to use for rendering.
     */
    static void renderScreenVFX(PoseStack poseStack) {
        VFXBuilders.ScreenVFXBuilder builder = VFXBuilders.createScreen()
                .setShader(() -> Minecraft.getInstance().gameRenderer.blitShader)
                .setShaderTexture(ResourceLocation.withDefaultNamespace("textures/block/dirt.png"))
                .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setPosition(100, 100, 200, 200)
                .setUV(0f, 0f, 1f, 1f);

        builder.blit(poseStack);
    }
}