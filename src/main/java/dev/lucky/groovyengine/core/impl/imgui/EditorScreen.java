package dev.lucky.groovyengine.core.impl.imgui;

import dev.lucky.groovyengine.core.impl.editor.EditorState;
import dev.lucky.groovyengine.core.impl.editor.GroovyEngineEditor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class EditorScreen extends Screen {

    protected EditorScreen() {
        super(Component.literal("EditorScreen"));
    }

    @Override
    protected void init() {
        super.init();

        //set imgui enable on open
        GroovyEngineEditor.setEditorState(EditorState.ENABLED);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

    }

    @Override
    public void onClose() {
        super.onClose();

        //set imgui disable on close
        GroovyEngineEditor.setEditorState(EditorState.DISABLED);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }
}
