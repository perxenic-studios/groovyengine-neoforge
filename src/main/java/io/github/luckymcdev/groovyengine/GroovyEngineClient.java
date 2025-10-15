package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.core.client.imgui.core.ImGuiRenderer;
import io.github.luckymcdev.groovyengine.core.systems.module.ModuleManager;
import io.github.luckymcdev.groovyengine.lens.LensRendering;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.compute.ComputeShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.PostProcessManager;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.CrtPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.SuperDuperPostShader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
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
        ModuleManager.getInstance().runOnClientSetup();

        PostProcessManager.addInstances(List.of(SuperDuperPostShader.INSTANCE));
        PostProcessManager.addStageInstance(RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS, CrtPostShader.INSTANCE);

        CrtPostShader.INSTANCE.setActive(false);
        SuperDuperPostShader.INSTANCE.setActive(false);

        NeoForge.EVENT_BUS.addListener(ImGuiRenderer::onRender);

        // Initialize rendering system
        LensRendering.initialize();

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
    }

    private static void registerModuleWindows() {
        if (initializedModuleWindows) return;
        ModuleManager instance = ModuleManager.getInstance();
        instance.runRegisterWindows();
        initializedModuleWindows = true;
    }

    @SubscribeEvent
    static void tick(ClientTickEvent.Pre event) {
        LensRendering.onClientTick(event);
    }

    @SubscribeEvent
    static void onRegisterShader(RegisterShadersEvent event) {
        registerModuleWindows();
        LensRendering.onRegisterShaders(event);
    }
}