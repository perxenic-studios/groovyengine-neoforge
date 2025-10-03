package io.github.luckymcdev.groovyengine;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.luckymcdev.groovyengine.core.client.imgui.core.ImGuiRenderer;
import io.github.luckymcdev.groovyengine.core.systems.module.ModuleManager;
import io.github.luckymcdev.groovyengine.lens.client.rendering.core.GeMaterials;
import io.github.luckymcdev.groovyengine.lens.client.rendering.core.VFXBuilders;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.compute.ComputeShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core.test.TestShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.PostProcessManager;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.CrtPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.SuperDuperPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.PoseScope;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.RenderUtils;
import io.github.luckymcdev.groovyengine.lens.client.rendering.vertex.CubeVertexData;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

import static io.github.luckymcdev.groovyengine.lens.client.rendering.core.GeMaterials.DEBUG_TRIANGLES;

@Mod(value = GE.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = GE.MODID, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class GroovyEngineClient {

    private static boolean initializedModuleWindows = false;

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
    }

    @SubscribeEvent
    static void onRegisterShader(RegisterShadersEvent event) {
        try {
            TestShader.INSTANCE.register(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
        registerModuleWindows();

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        PoseStack stack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();

        // Triangle
        new PoseScope(stack)
                .translate(100, 200, 100)
                .setWorld(true)
                .run(() -> {
                    renderVFXBuilderTriangle(stack, buffers);
                });

        // Sphere
        new PoseScope(stack)
                .translate(100, 200, 100)
                .setWorld(true)
                .run(() -> {
                    renderVFXBuilderSphere(stack, buffers);
                });

        // Torus
        new PoseScope(stack)
                .translate(100, 200, 100)
                .setWorld(true)
                .run(() -> {
                    renderVFXBuilderTorus(stack, buffers);
                });

        new PoseScope(stack)
                .run(() -> {
                    renderScreenVFX(stack);
                });
    }

    static void renderVFXBuilderTriangle(PoseStack poseStack, MultiBufferSource bufferSource) {
        RenderUtils.setupWorldRendering(poseStack);

        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld()
                .setMaterial(DEBUG_TRIANGLES)
                .replaceBufferSource(bufferSource)
                .setColor(0.0f, 1.0f, 0.0f, 1.0f)
                .setUV(0f, 0f, 1f, 1f);

        poseStack.pushPose();
        poseStack.translate(0, 50, 0);

        builder.placeVertex(poseStack, 0.0f, 1.0f, 0.0f);
        builder.placeVertex(poseStack, -1.0f, -1.0f, 0.0f);
        builder.placeVertex(poseStack, 1.0f, -1.0f, 0.0f);

        poseStack.popPose();
    }

    static void renderVFXBuilderSphere(PoseStack poseStack, MultiBufferSource bufferSource) {
        RenderUtils.setupWorldRendering(poseStack);

        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld()
                .setMaterial(DEBUG_TRIANGLES)
                .replaceBufferSource(bufferSource)
                .setColor(0.0f, 0.0f, 1.0f, 1.0f)
                .setUV(0f, 0f, 1f, 1f);

        poseStack.pushPose();
        poseStack.translate(-50, 0, 0);

        builder.renderSphere(poseStack, 15f, 8, 8);

        poseStack.popPose();
    }

    static void renderVFXBuilderTorus(PoseStack poseStack, MultiBufferSource bufferSource) {
        RenderUtils.setupWorldRendering(poseStack);

        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld()
                .setMaterial(DEBUG_TRIANGLES)
                .replaceBufferSource(bufferSource)
                .setColor(1.0f, 1.0f, 0.0f, 1.0f)
                .setUV(0f, 0f, 1f, 1f);

        poseStack.pushPose();
        poseStack.translate(0, 0, 50);

        // Render a torus
        builder.renderTorus(poseStack, 10f, 3f, 50, 30);

        poseStack.popPose();
    }

    static void renderVFXBuilderCube(PoseStack poseStack, MultiBufferSource bufferSource) {
        RenderUtils.setupWorldRendering(poseStack);

        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld()
                .setRenderType(RenderType.debugQuads())
                .replaceBufferSource(bufferSource)
                .setColor(1.0f, 0.5f, 0.0f, 1.0f) // Orange color
                .setUV(0f, 0f, 1f, 1f);

        poseStack.pushPose();
        poseStack.translate(-50, 50, 0);

        poseStack.popPose();
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