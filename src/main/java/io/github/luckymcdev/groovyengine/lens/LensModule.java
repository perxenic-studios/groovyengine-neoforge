package io.github.luckymcdev.groovyengine.lens;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.WindowManager;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import io.github.luckymcdev.groovyengine.core.systems.module.Module;
import io.github.luckymcdev.groovyengine.lens.client.editor.AnimationWindow;
import io.github.luckymcdev.groovyengine.lens.client.editor.RenderingDebuggingWindow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;

public class LensModule implements Module {
    private static final String LENS_CATEGORY = ImIcons.CAMERA.get() + " Lens";

    @Override
    public void init(IEventBus modEventBus) {
        GE.LENS_LOG.info("Lens Initialization");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerWindows() {
        WindowManager.registerWindow(new RenderingDebuggingWindow(), LENS_CATEGORY);
        WindowManager.registerWindow(new AnimationWindow(), LENS_CATEGORY);
    }
}