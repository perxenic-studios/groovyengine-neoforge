package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.construct.ConstructModule;
import io.github.luckymcdev.groovyengine.core.CoreModule;
import io.github.luckymcdev.groovyengine.core.config.Config;
import io.github.luckymcdev.groovyengine.core.registry.ModRegistry;
import io.github.luckymcdev.groovyengine.core.systems.module.ModuleManager;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.GroovyDatagen;
import io.github.luckymcdev.groovyengine.core.systems.structure.FileTreeGenerator;
import io.github.luckymcdev.groovyengine.lens.LensModule;
import io.github.luckymcdev.groovyengine.scribe.ScribeModule;
import io.github.luckymcdev.groovyengine.threads.ThreadsModule;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.stream.Collectors;

@Mod(GE.MODID)
public class GroovyEngine {
    private static final ModuleManager moduleManager = ModuleManager.getInstance();

    public GroovyEngine(IEventBus modEventBus, ModContainer modContainer) {
        FileTreeGenerator.generateFileStructure();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addPackRepoSource);
        NeoForge.EVENT_BUS.register(this);

        ModRegistry.register(modEventBus);


        ModuleManager.registerModule(new CoreModule());
        ModuleManager.registerModule(new ThreadsModule());
        ModuleManager.registerModule(new LensModule());
        ModuleManager.registerModule(new ScribeModule());
        ModuleManager.registerModule(new ConstructModule());


        String gc = ManagementFactory.getGarbageCollectorMXBeans().stream().map(Object::toString).collect(Collectors.joining(", "));
        GE.LOG.info("Current Garbage Collector: "+gc);

        try {
            GroovyDatagen.run();
        } catch (IOException e) {
            e.printStackTrace();
        }

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        moduleManager.runOnCommonSetup();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        moduleManager.runOnServerStarting();
    }

    private void addPackRepoSource(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addRepositorySource(GE.RESOURCE_SOURCE.get());
        }
        else if (event.getPackType() == PackType.SERVER_DATA) {
            event.addRepositorySource(GE.DATA_SOURCE.get());
        }
    }
}
