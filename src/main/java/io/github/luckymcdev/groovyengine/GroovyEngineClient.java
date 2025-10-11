package io.github.luckymcdev.groovyengine;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.luckymcdev.groovyengine.core.client.imgui.core.ImGuiRenderer;
import io.github.luckymcdev.groovyengine.core.systems.module.ModuleManager;
import io.github.luckymcdev.groovyengine.lens.client.rendering.core.VFXBuilders;
import io.github.luckymcdev.groovyengine.lens.client.rendering.material.Material;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.compute.ComputeShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core.test.TestShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.PostProcessManager;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.CrtPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.SuperDuperPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.core.Renderer;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.PoseScope;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.RenderUtils;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.ObjModel;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.ObjModelManager;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.amo.AmoModel;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation.ChupacabraAnimations;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

import static io.github.luckymcdev.groovyengine.lens.client.rendering.core.GeMaterials.DEBUG_TRIANGLES;
import static io.github.luckymcdev.groovyengine.lens.client.rendering.core.GeMaterials.OBJ_MODEL;

@Mod(value = GE.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = GE.MODID, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class GroovyEngineClient {

    private static boolean initializedModuleWindows = false;

    private static final ObjModel suzanneModel = new ObjModel(GE.id("suzanne"));
    private static final ObjModel cubeModel = new ObjModel(GE.id("cube"));
    private static final ObjModel chupacabraModel = new ObjModel(GE.id("chupacabra"));
    private static final ResourceLocation chupacabraTexture = GE.id("obj/chupacabra.png");

    // Add AMO model registration
    public static final AmoModel animatedModel = new AmoModel(GE.id("animated"));

    private static final Renderer renderer = Renderer.getInstance();

    public GroovyEngineClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        PostProcessManager.addInstances(List.of(SuperDuperPostShader.INSTANCE));
        PostProcessManager.addStageInstance(RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS, CrtPostShader.INSTANCE);

        CrtPostShader.INSTANCE.setActive(false);
        SuperDuperPostShader.INSTANCE.setActive(false);

        NeoForge.EVENT_BUS.addListener(ImGuiRenderer::onRender);

        rendering();

        ObjModelManager.registerObjModel(suzanneModel);
        ObjModelManager.registerObjModel(cubeModel);
        ObjModelManager.registerObjModel(chupacabraModel);

        ObjModelManager.registerObjModel(animatedModel);

        // Add this line after model registration:
        ChupacabraAnimations.initialize(chupacabraModel);

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
    }

    private static void registerModuleWindows() {
        if (initializedModuleWindows) return;
        ModuleManager instance = ModuleManager.getInstance();
        instance.runRegisterWindows();
        initializedModuleWindows = true;
    }

    @SubscribeEvent
    static void tick(ClientTickEvent.Pre event) {
        float deltaTime = Minecraft.getInstance().getTimer().getRealtimeDeltaTicks();
        if (animatedModel.isModelLoaded()) {
            animatedModel.updateAnimation(deltaTime * 0.05f); // Scale to seconds
        }
    }

    @SubscribeEvent
    static void onRegisterShader(RegisterShadersEvent event) {
        try {
            TestShader.INSTANCE.register(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void rendering() {
        renderer.getWorldRenderer().onRenderLevelStage(event -> {
            registerModuleWindows();

            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

            Minecraft mc = Minecraft.getInstance();
            PoseStack stack = event.getPoseStack();
            MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();

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

            new PoseScope(stack)
                    .world()
                    .run(poseStack -> {
                        renderObjModel(poseStack, suzanneModel, new Vec3(0, 100, 0));
                    });

            new PoseScope(stack)
                    .world()
                    .run(poseStack -> {
                        renderObjModel(poseStack, cubeModel, new Vec3(0, 125, 0));
                    });

            new PoseScope(stack)
                    .world()
                    .run(poseStack -> {
                        renderObjModelAnimated(poseStack, chupacabraModel, new Vec3(0, 150, 0), OBJ_MODEL.withTexture(chupacabraTexture));
                    });

            // Add AMO model rendering at position (45, 120, 0)
            new PoseScope(stack)
                    .world()
                    .run(poseStack -> {
                        renderAmoModel(poseStack, animatedModel, new Vec3(45, 120, 0));
                    });

        });
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