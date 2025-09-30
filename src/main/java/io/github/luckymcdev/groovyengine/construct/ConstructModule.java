package io.github.luckymcdev.groovyengine.construct;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.construct.client.editor.ConstructChanges;
import io.github.luckymcdev.groovyengine.construct.client.editor.ConstructEditorWindow;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.WindowManager;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import io.github.luckymcdev.groovyengine.core.systems.module.Module;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;

public class ConstructModule implements Module {
    @Override
    public void init(IEventBus modEventBus) {
        // Construct initialization code here
        GE.CONSTRUCT_LOG.info("Construct Initialization");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerWindows() {
        WindowManager.registerWindow(new ConstructChanges(), ImIcons.WRENCH.get()+" Construct");
        WindowManager.registerWindow(new ConstructEditorWindow(), ImIcons.WRENCH.get()+" Construct");
    }
}
