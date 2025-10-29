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
 *
 * Last modified: 2025-10-29 16:54:39
 */

package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.construct.ConstructModule;
import io.github.luckymcdev.groovyengine.construct.registry.ConstructRegistry;
import io.github.luckymcdev.groovyengine.core.CoreModule;
import io.github.luckymcdev.groovyengine.core.config.Config;
import io.github.luckymcdev.groovyengine.core.registry.ModRegistry;
import io.github.luckymcdev.groovyengine.core.systems.module.ModuleManager;
import io.github.luckymcdev.groovyengine.core.systems.module.RegisterModuleEvent;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.GroovyDatagen;
import io.github.luckymcdev.groovyengine.core.systems.structure.FileTreeGenerator;
import io.github.luckymcdev.groovyengine.lens.LensModule;
import io.github.luckymcdev.groovyengine.threads.ThreadsModule;
import io.github.luckymcdev.groovyengine.threads.core.logging.LogCapture;
import io.github.luckymcdev.groovyengine.threads.core.scripting.attachment.AttachmentEventDispatcher;
import io.github.luckymcdev.groovyengine.threads.core.scripting.core.ScriptManager;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.stream.Collectors;

@Mod(GE.MODID)
public class GroovyEngine {
    private static final ModuleManager moduleManager = ModuleManager.getInstance();

    public GroovyEngine(IEventBus modEventBus, ModContainer modContainer) {
        setupLogging();
        setupFilesystem();

        // Register events
        modEventBus.addListener(EventPriority.LOWEST, this::onCommonSetup);
        modEventBus.addListener(this::onAddPackFinders);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new AttachmentEventDispatcher());

        setupRegistries(modEventBus);
        setupModules(modEventBus);

        logGCInfo();
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    /**
     * Sets up the logging system by hooking into the root Logger of Log4j.
     * This method should be called before any logging is done.
     */
    private void setupLogging() {
        LogCapture.hookLog4j();
    }

    /**
     * Sets up the file system by generating the directory structure for the mod.
     * This method should be called before any file operations are performed.
     */
    private void setupFilesystem() {
        FileTreeGenerator.generateFileStructure();
    }

    /**
     * Registers the mod's registries with the given event bus.
     * <p>
     * This method is responsible for registering the mod's registries with the event bus.
     * It registers the ModRegistry and ConstructRegistry with the given event bus.
     *
     * @param modEventBus the event bus to register to
     */
    private void setupRegistries(IEventBus modEventBus) {
        ModRegistry.register(modEventBus);
        ConstructRegistry.register(modEventBus);
    }

    /**
     * Sets up the modules with the given event bus.
     * This method is responsible for registering and initializing the modules with the given event bus.
     * It registers the modules with the module manager and then initializes them by calling the
     * {@link ModuleManager#runInit(IEventBus)} method.
     *
     * @param modEventBus the event bus to register to
     */
    private void setupModules(IEventBus modEventBus) {

        NeoForge.EVENT_BUS.post(RegisterModuleEvent.INSTANCE);

        ModuleManager.registerModule(new CoreModule());
        ModuleManager.registerModule(new ThreadsModule());
        ModuleManager.registerModule(new LensModule());
        ModuleManager.registerModule(new ConstructModule());

        moduleManager.runInit(modEventBus);
    }

    /**
     * Logs the current garbage collector information.
     * This method retrieves the current garbage collector information using the
     * {@link java.lang.management.ManagementFactory#getGarbageCollectorMXBeans()} method and
     * logs the information to the logger.
     */
    private void logGCInfo() {
        String gc = ManagementFactory.getGarbageCollectorMXBeans()
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        GE.LOG.info("Current Garbage Collector: " + gc);
    }

    /**
     * Called on both the server-side and client-side after the module has been initialized.
     * This method is responsible for calling the {@link ModuleManager#runOnCommonSetup()} method on each registered module.
     * The {@link ModuleManager#runOnCommonSetup()} method is called on both the server-side and client-side after the module has been initialized.
     * It is called automatically by the module system when the module is initialized on both the server-side and client-side.
     * Modules should not call this method manually, as it may interfere with the module system's internal workings.
     *
     * @param event The event that triggered this method call.
     */
    private void onCommonSetup(FMLCommonSetupEvent event) {
        moduleManager.runOnCommonSetup();
    }

    /**
     * Called when the mod is initializing and the pack finders need to be registered.
     * This method is responsible for registering the mod's pack finders with the given event.
     * It registers the mod's pack finders with the event by checking the pack type of the event and
     * adding the appropriate pack finder to the event.
     *
     * @param event The event that triggered this method call.
     */
    private void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addRepositorySource(GE.RESOURCE_SOURCE.get());
        } else if (event.getPackType() == PackType.SERVER_DATA) {
            event.addRepositorySource(GE.DATA_SOURCE.get());
        }
    }


    /**
     * Called when the server is starting.
     * This method is responsible for calling the {@link ModuleManager#runOnServerStarting()} method on each registered module.
     * The {@link ModuleManager#runOnServerStarting()} method is called on the server-side after the module has been initialized.
     * It is called automatically by the module system when the server is starting.
     * Modules should not call this method manually, as it may interfere with the module system's internal workings.
     * <p>
     * This method also fires the on server start event for the attachment event listener, which allows scripts to run once the server has started.
     * <p>
     * This method also attempts to run the Groovy data generator, which populates the data folder with generated data based on the scripts in the script folder.
     * If the data generator encounters an exception, it logs the exception to the logger.
     *
     * @param event The event that triggered this method call.
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        moduleManager.runOnServerStarting();
        try {
            GroovyDatagen.run();
        } catch (IOException e) {
            GE.CORE_LOG.error(e.toString());
        }
    }

    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<>() {
            @Override
            protected Object prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
                return null;
            }

            @Override
            protected void apply(@NotNull Object object, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
                ScriptManager.reloadScripts();
            }
        });
    }
}
