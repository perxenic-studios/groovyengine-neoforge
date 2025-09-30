package io.github.luckymcdev.groovyengine.scribe;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.WindowManager;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import io.github.luckymcdev.groovyengine.core.systems.module.Module;
import io.github.luckymcdev.groovyengine.scribe.client.editor.ScribeWindow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;

public class ScribeModule implements Module {
    @Override
    public void init(IEventBus modEventBus) {
        GE.SCRIBE_LOG.info("Scribe Initialization");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerWindows() {
        WindowManager.registerWindow(new ScribeWindow(), ImIcons.EDIT.get()+" Scribe");
    }
}