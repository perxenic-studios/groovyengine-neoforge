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
    /**
     * Initializes the Construct module.
     * This method is called when the module is initialized and is responsible for initializing
     * all Construct-related systems.
     *
     * @param modEventBus the event bus for the module
     */
    @Override
    public void init(IEventBus modEventBus) {
        // Construct initialization code here
        GE.CONSTRUCT_LOG.info("Construct Initialization");
    }

    /**
     * Registers the Construct windows with the Window Manager.
     * This method is called on the client side to register the Construct windows with the Window Manager.
     * It registers the ConstructChanges window and the ConstructEditorWindow with their respective display names.
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerWindows() {
        WindowManager.registerWindow(new ConstructChanges(), ImIcons.WRENCH.get() + " Construct");
        WindowManager.registerWindow(new ConstructEditorWindow(), ImIcons.WRENCH.get() + " Construct");
    }
}
