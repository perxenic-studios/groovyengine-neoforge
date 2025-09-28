package io.github.luckymcdev.groovyengine.core;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.construct.client.editor.ConstructEditorWindow;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.WindowManager;
import io.github.luckymcdev.groovyengine.core.client.editor.windows.EditorControlWindow;
import io.github.luckymcdev.groovyengine.core.systems.module.Module;
import net.neoforged.bus.api.IEventBus;

public class CoreModule implements Module {
    @Override
    public void init(IEventBus modEventBus) {
        GE.CORE_LOG.info("Core Initialized");
    }

    @Override
    public void registerWindows() {
        WindowManager.registerWindow(new ConstructEditorWindow(), "test");
    }
}
