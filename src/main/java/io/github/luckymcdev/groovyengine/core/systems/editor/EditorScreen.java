package io.github.luckymcdev.groovyengine.core.systems.editor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class EditorScreen extends Screen {

    public EditorScreen() {
        super(Component.literal("EditorScreen"));
    }

    @Override
    protected void init() {
        super.init();

        //set imgui enable on open
        GroovyEngineEditor.setEditorState(EditorState.ENABLED);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

    }

    @Override
    public void onClose() {
        super.onClose();

        //set imgui disable on close
        GroovyEngineEditor.setEditorState(EditorState.DISABLED);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }
}
