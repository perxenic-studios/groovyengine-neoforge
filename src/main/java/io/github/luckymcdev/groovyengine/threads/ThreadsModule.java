package io.github.luckymcdev.groovyengine.threads;

import io.github.luckymcdev.groovyengine.core.systems.module.Module;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.threads.scripting.core.ScriptManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

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