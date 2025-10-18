package io.github.luckymcdev.groovyengine.lens;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.lens.client.rendering.core.VFXBuilders;
import io.github.luckymcdev.groovyengine.lens.client.rendering.material.Material;
import io.github.luckymcdev.groovyengine.lens.client.rendering.particle.ParticleBuilder;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core.test.TestShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.core.Renderer;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.PoseScope;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.RenderUtils;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.ObjModel;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.ObjModelManager;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.amo.AmoModel;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation.ChupacabraAnimations;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.io.IOException;
import java.util.List;

import static io.github.luckymcdev.groovyengine.lens.client.rendering.core.GeMaterials.DEBUG_TRIANGLES;
import static io.github.luckymcdev.groovyengine.lens.client.rendering.core.GeMaterials.OBJ_MODEL;
import static io.github.luckymcdev.groovyengine.lens.client.systems.obj.ObjModelManager.registerObjModel;

@EventBusSubscriber
public class LensRendering {

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

    // Add AMO model registration
    public static final AmoModel animatedModel = new AmoModel(GE.id("animated"));

    private static final Renderer renderer = Renderer.getInstance();

    public static void initialize() {
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

    public static void onClientTick(ClientTickEvent.Pre event) {
        float deltaTime = Minecraft.getInstance().getTimer().getRealtimeDeltaTicks();
        if (animatedModel.isModelLoaded()) {
            animatedModel.updateAnimation(deltaTime * 0.05f); // Scale to seconds
        }
    }

    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            TestShader.INSTANCE.register(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    private static void setupRendering(RenderLevelStageEvent event) {
        ClientLevel level = Minecraft.getInstance().level;

        // Particle effects
        createParticleEffects(level);

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        PoseStack stack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();

        // Render VFX shapes
        renderVFXShapes(stack, buffers);

        // Render models
        renderModels(stack);
    }

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
                    renderObjModelAnimated(poseStack, chupacabraModel, new Vec3(35, 140, 20), OBJ_MODEL.withTexture(chupacabraTexture));
                });

        // Add AMO model rendering with offset
        new PoseScope(stack)
                .world()
                .run(poseStack -> {
                    renderAmoModel(poseStack, animatedModel, new Vec3(35, 140, 40));
                });
    }

    static void renderAllObjModels(PoseStack stack, Vec3 startPosition) {
        new PoseScope(stack).world().run(p -> renderObjModel(p, cube, startPosition.add(0, 0, 0)));
        new PoseScope(stack).world().run(p -> renderObjModel(p, sphere, startPosition.add(30, 0, 0)));
        new PoseScope(stack).world().run(p -> renderObjModel(p, cylinder, startPosition.add(60, 0, 0)));
        new PoseScope(stack).world().run(p -> renderObjModel(p, cone, startPosition.add(90, 0, 0)));

        new PoseScope(stack).world().run(p -> renderObjModel(p, plane, startPosition.add(0, 0, 30)));
        new PoseScope(stack).world().run(p -> renderObjModel(p, circle, startPosition.add(30, 0, 30)));
        new PoseScope(stack).world().run(p -> renderObjModel(p, icosphere, startPosition.add(60, 0, 30)));
        new PoseScope(stack).world().run(p -> renderObjModel(p, torus, startPosition.add(90, 0, 30)));

        new PoseScope(stack).world().run(p -> renderObjModel(p, suzanne, startPosition.add(30, 0, 60)));

    }


    static void renderAmoModel(PoseStack stack, AmoModel model, Vec3 position) {
        Minecraft mc = Minecraft.getInstance();

        stack.pushPose();
        stack.translate(position.x, position.y, position.z);
        stack.scale(5, 5, 5);

        int packedLight = RenderUtils.FULL_BRIGHT;

        try {
            model.renderAnimated(stack, OBJ_MODEL.withTexture(RenderUtils.CONCRETE_RES_LOC), packedLight);
        } catch (Exception e) {
            GE.LENS_LOG.error("Error rendering AMO model: {}", e.getMessage(), e);
        }

        stack.popPose();
    }

    static void renderObjModel(PoseStack stack, ObjModel model, Vec3 position) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();

        // Model position in world
        stack.translate(position.x, position.y, position.z);
        stack.scale(10f, 10f, 10f);

        // Get proper lighting at the model's position
        int packedLight = RenderUtils.FULL_BRIGHT;

        model.renderModel(stack, OBJ_MODEL, packedLight);
    }

    static void renderObjModel(PoseStack stack, ObjModel model, Vec3 position, Material material) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();

        // Model position in world
        stack.translate(position.x, position.y, position.z);
        stack.scale(10f, 10f, 10f);

        // Get proper lighting at the model's position
        int packedLight = RenderUtils.FULL_BRIGHT;

        model.renderModel(stack, material, packedLight);
    }

    static void renderObjModelAnimated(PoseStack stack, ObjModel model, Vec3 position, Material material) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();

        // Model position in world
        stack.translate(position.x, position.y, position.z);
        stack.scale(10f, 10f, 10f);

        // Get proper lighting at the model's position
        int packedLight = RenderUtils.FULL_BRIGHT;

        model.renderModelAnimated(stack, material, packedLight);
    }

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

    static void renderVFXBuilderSphere(PoseStack poseStack, MultiBufferSource bufferSource) {
        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld()
                .setMaterial(DEBUG_TRIANGLES)
                .replaceBufferSource(bufferSource)
                .setColor(0.0f, 0.0f, 1.0f, 1.0f)
                .setUV(0f, 0f, 1f, 1f);

        poseStack.translate(-50, 0, 0);

        builder.renderSphere(poseStack, 15f, 8, 8);
    }

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

    static void renderVFXBuilderCube(PoseStack poseStack, MultiBufferSource bufferSource) {
        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld()
                .setRenderType(RenderType.debugQuads())
                .replaceBufferSource(bufferSource)
                .setColor(1.0f, 0.5f, 0.0f, 1.0f) // Orange color
                .setUV(0f, 0f, 1f, 1f);

        poseStack.translate(-50, 50, 0);
    }

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