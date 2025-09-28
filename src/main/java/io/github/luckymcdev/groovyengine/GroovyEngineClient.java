package io.github.luckymcdev.groovyengine;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.luckymcdev.groovyengine.core.client.imgui.core.ImGuiRenderer;
import io.github.luckymcdev.groovyengine.core.systems.module.ModuleManager;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.compute.ComputeShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core.test.TestShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.PostProcessManager;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.CrtPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.SuperDuperPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.GeometryBuilder;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

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
            // Create a JSON config file for your compute shader
            ResourceLocation shaderConfig = GE.id("compute_test");

            int elementCount = 128 * 4;
            ByteBuffer data = MemoryUtil.memAlloc(elementCount * Float.BYTES);

            try {
                // Initialize with some test values
                FloatBuffer floatBuffer = data.asFloatBuffer();
                for (int i = 0; i < elementCount; i++) {
                    floatBuffer.put(i, i * 0.1f); // 0.0, 0.1, 0.2, 0.3, etc.
                }

                // Print before values
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

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || player.getY() < 200) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        poseStack.pushPose();
        RenderUtils.setupWorldRendering(poseStack);

        // Render other geometry
        poseStack.translate(0, 120, 0);

        renderGraphicsWithCustomShader(poseStack, bufferSource);

        renderGraphicsTriangleThing(poseStack, bufferSource);

        poseStack.popPose();
    }

    static void renderGraphicsWithCustomShader(PoseStack poseStack, MultiBufferSource bufferSource) {
        GeometryBuilder.renderCube(
                RenderUtils.getConsumer(bufferSource),
                poseStack.last().pose(),
                0, 0, 0, // Position
                40f, // Size
                0f, 0f, 1f, 1f // UV coordinates
        );
    }

    static void renderHugeQuad(PoseStack poseStack, MultiBufferSource bufferSource, float size) {
        GeometryBuilder.renderPlane(
                RenderUtils.getConsumer(bufferSource),
                poseStack.last().pose(),
                0, 0, 0, // Position
                size, size, // Width and height
                new Vector3f(0, 1, 0), // Normal (facing up)
                0f, 0f, 1f, 1f // UV coordinates
        );
    }

    static void renderGraphicsTriangleThing(PoseStack poseStack, MultiBufferSource bufferSource) {
        GeometryBuilder.renderTriangle(
                RenderUtils.getConsumer(bufferSource),
                poseStack.last().pose(),
                0.0f, 1.0f, 0.0f, // Vertex 1
                -1.0f, -1.0f, 0.0f, // Vertex 2
                1.0f, -1.0f, 0.0f, // Vertex 3
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, // UV coordinates
                new Vector3f(0, 0, 1) // Normal
        );
    }
}