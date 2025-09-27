package io.github.luckymcdev.groovyengine.threads.client.screen;

import io.github.luckymcdev.groovyengine.threads.core.scripting.error.ScriptErrors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

public class ThreadsEntry extends ObjectSelectionList.Entry<ThreadsEntry> {
    private final ScriptErrors.ErrorEntry error;

    public ThreadsEntry(ScriptErrors.ErrorEntry error) {
        this.error = error;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int index, int y, int x, int width, int height,
                       int mouseX, int mouseY, boolean hovered, float partialTick) {
        Font font = Minecraft.getInstance().font;

        guiGraphics.drawString(font, "Script: " + error.scriptName, x + 5, y, 0xFFFF5555, false);
        guiGraphics.drawString(font, "Message: " + error.message, x + 5, y + 12, 0xFFFFFFFF, false);
        guiGraphics.drawString(font, "Fix: " + error.description, x + 5, y + 24, 0xFFAAAAAA, false);
    }

    @Override
    public Component getNarration() {
        return Component.literal("Error in script " + error.scriptName);
    }
}