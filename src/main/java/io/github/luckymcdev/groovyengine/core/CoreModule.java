package io.github.luckymcdev.groovyengine.core;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.construct.client.editor.ConstructEditorWindow;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.WindowManager;
import io.github.luckymcdev.groovyengine.core.systems.module.Module;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;

public class CoreModule implements Module {
    /**
     * Initializes the Core module.
     * This method is called when the module is initialized and is responsible for initializing
     * all Core-related systems.
     * @param modEventBus the event bus for the module
     */
    @Override
    public void init(IEventBus modEventBus) {
        GE.CORE_LOG.info("Core Initialized");
    }
}
