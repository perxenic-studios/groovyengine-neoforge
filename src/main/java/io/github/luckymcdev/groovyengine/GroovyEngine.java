package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.construct.ConstructModule;
import io.github.luckymcdev.groovyengine.construct.registry.ConstructRegistry;
import io.github.luckymcdev.groovyengine.core.CoreModule;
import io.github.luckymcdev.groovyengine.core.config.Config;
import io.github.luckymcdev.groovyengine.core.registry.ModRegistry;
import io.github.luckymcdev.groovyengine.core.systems.module.ModuleManager;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.GroovyDatagen;
import io.github.luckymcdev.groovyengine.core.systems.structure.FileTreeGenerator;
import io.github.luckymcdev.groovyengine.lens.LensModule;
import io.github.luckymcdev.groovyengine.threads.ThreadsModule;
import io.github.luckymcdev.groovyengine.threads.core.logging.LogCapture;
import io.github.luckymcdev.groovyengine.threads.core.scripting.attachment.AttachmentEventManager;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
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

        // Module setup pipeline
        setupRegistries(modEventBus);
        setupModules(modEventBus);

        logGCInfo();
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void setupLogging() {
        LogCapture.hookLog4j();
    }

    private void setupFilesystem() {
        FileTreeGenerator.generateFileStructure();
    }

    private void setupRegistries(IEventBus modEventBus) {
        ModRegistry.register(modEventBus);
        ConstructRegistry.register(modEventBus);
    }

    private void setupModules(IEventBus modEventBus) {
        ModuleManager.registerModules(List.of(
                new CoreModule(),
                new ThreadsModule(),
                new LensModule(),
                new ConstructModule()
        ));

        moduleManager.runInit(modEventBus);
    }

    private void logGCInfo() {
        String gc = ManagementFactory.getGarbageCollectorMXBeans()
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        GE.LOG.info("Current Garbage Collector: " + gc);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        moduleManager.runOnCommonSetup();
    }

    private void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addRepositorySource(GE.RESOURCE_SOURCE.get());
        } else if (event.getPackType() == PackType.SERVER_DATA) {
            event.addRepositorySource(GE.DATA_SOURCE.get());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        moduleManager.runOnServerStarting();
        AttachmentEventManager.getInstance().fireServerStart();

        try {
            GroovyDatagen.run();
        } catch (IOException e) {
            GE.CORE_LOG.error(e.toString());
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        AttachmentEventManager.getInstance().fireServerStop();
    }
}
