package dev.lucky.groovyengine.threads;

import dev.lucky.groovyengine.core.systems.module.Module;
import dev.lucky.groovyengine.GE;
import dev.lucky.groovyengine.threads.scripting.ScriptManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.nio.file.Files;

public class ThreadsModule implements Module {
    private static final Path SCRIPT_PATH = Path.of(FMLPaths.GAMEDIR.get() + "/" + GE.MODID + "/src/scripts");

    @Override
    public void init(IEventBus modEventBus) {

        GE.LOG.info("Initializing Threads module");

        ScriptManager.initialize();

    }

    @Override
    public void onServerStarting() {
        ScriptManager.reloadScripts();
    }
}