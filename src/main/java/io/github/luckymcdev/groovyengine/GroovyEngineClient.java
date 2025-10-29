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

package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.core.client.imgui.core.ImGuiRenderer;
import io.github.luckymcdev.groovyengine.core.systems.module.ModuleManager;
import io.github.luckymcdev.groovyengine.lens.LensRendering;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.compute.ComputeShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.PostProcessManager;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.CrtPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.SuperDuperPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.event.RenderEvent;
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

    /**
     * Called when the client setup event is fired.
     * This method is responsible for setting up the rendering system,
     * initializing the module windows, and executing the compute shader test.
     *
     * @param event the client setup event
     */
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        ModuleManager.getInstance().runOnClientSetup();

        NeoForge.EVENT_BUS.post(new RenderEvent());

        PostProcessManager.addInstances(List.of(SuperDuperPostShader.INSTANCE));
        PostProcessManager.addStageInstance(RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS, CrtPostShader.INSTANCE);

        CrtPostShader.INSTANCE.setActive(false);
        SuperDuperPostShader.INSTANCE.setActive(false);

        NeoForge.EVENT_BUS.addListener(ImGuiRenderer::onRender);

        //LensRendering.initialize();
    }

    /**
     * Called when the client tick event is fired.
     * This method is responsible for registering the module windows and calling the
     * {@link LensRendering#onClientTick(ClientTickEvent.Pre)} method.
     *
     * @param event the client tick event
     */
    @SubscribeEvent
    static void tick(ClientTickEvent.Pre event) {
        registerModuleWindows();
        //LensRendering.onClientTick(event);
    }

    /**
     * Registers all module windows with the window manager.
     * This method should only be called on the client-side.
     * If the player or level is null, the method will not register any windows and will return immediately.
     * If the method has already been called, it will not register any windows and will return immediately.
     */
    private static void registerModuleWindows() {
        if (initializedModuleWindows) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        ModuleManager instance = ModuleManager.getInstance();
        instance.runRegisterWindows();
        initializedModuleWindows = true;
    }

    /**
     * Called when the register shader event is fired.
     * This method is responsible for calling the
     * {@link LensRendering#onRegisterShaders(RegisterShadersEvent)} method.
     *
     * @param event the register shader event
     */
    @SubscribeEvent
    static void onRegisterShader(RegisterShadersEvent event) {
        //LensRendering.onRegisterShaders(event);
    }
}