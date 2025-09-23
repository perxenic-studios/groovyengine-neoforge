package io.github.luckymcdev.groovyengine.lens;

import io.github.luckymcdev.groovyengine.core.client.editor.core.window.WindowManager;
import io.github.luckymcdev.groovyengine.core.systems.module.Module;
import io.github.luckymcdev.groovyengine.lens.client.editor.RenderingDebuggingWindow;
import net.neoforged.bus.api.IEventBus;

public class LensModule implements Module {
    @Override
    public void init(IEventBus modEventBus) {
        // Construct initialization code here
    }

    @Override
    public void registerWindows() {
        WindowManager.registerWindow(new RenderingDebuggingWindow(), "Lens");
    }
}