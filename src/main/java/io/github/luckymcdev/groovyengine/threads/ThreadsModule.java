package io.github.luckymcdev.groovyengine.threads;

import io.github.luckymcdev.groovyengine.core.client.editor.core.window.WindowManager;
import io.github.luckymcdev.groovyengine.core.systems.module.Module;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.threads.client.editor.ThreadsWindow;
import io.github.luckymcdev.groovyengine.threads.core.scripting.core.ScriptManager;
import net.neoforged.bus.api.IEventBus;

public class ThreadsModule implements Module {

    @Override
    public void init(IEventBus modEventBus) {

        GE.LOG.info("Initializing Threads module");

        ScriptManager.initialize();

    }

    @Override
    public void onServerStarting() {
        ScriptManager.reloadScripts();
    }

    @Override
    public void registerWindows() {
        WindowManager.registerWindow(new ThreadsWindow(), "Threads");
    }
}