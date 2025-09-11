package dev.lucky.groovyengine;

import dev.lucky.groovyengine.construct.ConstructModule;
import dev.lucky.groovyengine.core.config.Config;
import dev.lucky.groovyengine.core.systems.packs.generator.GroovyDatagen;
import dev.lucky.groovyengine.core.systems.structure.FileTreeGenerator;
import dev.lucky.groovyengine.lens.LensModule;
import dev.lucky.groovyengine.scribe.ScribeModule;
import dev.lucky.groovyengine.threads.ThreadsModule;
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
import java.util.Arrays;
import java.util.List;

@Mod(GE.MODID)
public class GroovyEngine {
    private final List<dev.lucky.groovyengine.core.systems.module.Module> modules = Arrays.asList(
            new ThreadsModule(),
            new LensModule(),
            new ScribeModule(),
            new ConstructModule()
    );

    public GroovyEngine(IEventBus modEventBus, ModContainer modContainer) {
        FileTreeGenerator.generateFileStructure();

        modules.forEach(module -> module.init(modEventBus));


        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addPackRepoSource);
        NeoForge.EVENT_BUS.register(this);

        try {
            GroovyDatagen.run();
        } catch (IOException e) {
            e.printStackTrace();
        }

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        modules.forEach(module -> module.onCommonSetup());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        modules.forEach(module -> module.onServerStarting());
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
