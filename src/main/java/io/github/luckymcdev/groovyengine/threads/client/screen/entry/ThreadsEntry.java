package io.github.luckymcdev.groovyengine.threads.client.screen.entry;

import io.github.luckymcdev.groovyengine.threads.core.scripting.error.ScriptErrors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThreadsEntry extends ObjectSelectionList.Entry<ThreadsEntry> {
    private final ScriptErrors.ErrorEntry error;

    public ThreadsEntry(ScriptErrors.ErrorEntry error) {
        this.error = error;
    }

    /**
     * Renders the given entry in the ObjectSelectionList.
     * This method is responsible for rendering the script name, error message, and fix description
     * of the given script error.
     *
     * @param guiGraphics the GuiGraphics object to render to
     * @param index the index of the entry in the list
     * @param y the y position of the entry
     * @param x the x position of the entry
     * @param width the width of the entry
     * @param height the height of the entry
     * @param mouseX the x position of the mouse
     * @param mouseY the y position of the mouse
     * @param hovered whether the entry is currently hovered
     * @param partialTick the partial tick of the render
     */
    @Override
    public void render(GuiGraphics guiGraphics, int index, int y, int x, int width, int height,
                       int mouseX, int mouseY, boolean hovered, float partialTick) {
        Font font = Minecraft.getInstance().font;
        int textWidth = width - 10;
        int lineHeight = 12;
        int currentY = y;

        String scriptText = "Script: " + error.scriptName();
        if (font.width(scriptText) > textWidth) {
            scriptText = font.plainSubstrByWidth(scriptText, textWidth - font.width("...")) + "...";
        }
        guiGraphics.drawString(font, scriptText, x + 5, currentY, 0xFFFF5555, false);
        currentY += lineHeight;

        String messageText = "Message: " + error.message();
        var messageLines = font.split(Component.literal(messageText), textWidth);
        for (var line : messageLines) {
            if (currentY + lineHeight > y + height) break; // Don't go beyond entry bounds
            guiGraphics.drawString(font, line, x + 5, currentY, 0xFFFFFFFF, false);
            currentY += lineHeight;
        }

        String descText = "Fix: " + error.description();
        var descLines = font.split(Component.literal(descText), textWidth);
        for (var line : descLines) {
            if (currentY + lineHeight > y + height) break; // Don't go beyond entry bounds
            guiGraphics.drawString(font, line, x + 5, currentY, 0xFFAAAAAA, false);
            currentY += lineHeight;
        }
    }

    /**
     * Gets the narration for the entry, which is used to describe the entry to screen readers.
     * <p>
     * The narration is a short description of the entry that is read out to screen readers.
     * <p>
     * In this case, the narration is "Error in script {scriptName}", where {scriptName} is the name of the script
     * that the error occurred in.
     *
     * @return the narration for the entry
     */
    @Override
    public Component getNarration() {
        return Component.literal("Error in script " + error.scriptName());
    }
}