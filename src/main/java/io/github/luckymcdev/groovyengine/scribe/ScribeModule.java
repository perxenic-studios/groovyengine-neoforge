package io.github.luckymcdev.groovyengine.scribe;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.WindowManager;
import io.github.luckymcdev.groovyengine.core.systems.module.Module;
import io.github.luckymcdev.groovyengine.scribe.client.editor.ScribeWindow;
import net.neoforged.bus.api.IEventBus;

public class ScribeModule implements Module {
    @Override
    public void init(IEventBus modEventBus) {
        GE.SCRIBE_LOG.info("Scribe Initialization");
    }

    @Override
    public void registerWindows() {
        WindowManager.registerWindow(new ScribeWindow(), "Scribe");
    }
}