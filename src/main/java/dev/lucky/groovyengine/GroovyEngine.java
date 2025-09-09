package dev.lucky.groovyengine;

import dev.lucky.groovyengine.construct.Construct;
import dev.lucky.groovyengine.core.impl.config.Config;
import dev.lucky.groovyengine.core.internal.packs.generator.GroovyDatagen;
import dev.lucky.groovyengine.lens.Lens;
import dev.lucky.groovyengine.scribe.Scribe;
import dev.lucky.groovyengine.threads.Threads;
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

@Mod(GE.MODID)
public class GroovyEngine {

    public GroovyEngine(IEventBus modEventBus, ModContainer modContainer) {

        Threads.initThreads(modEventBus);
        Lens.initLens(modEventBus);
        Scribe.initScribe(modEventBus);
        Construct.initConstruct(modEventBus);

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
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
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
