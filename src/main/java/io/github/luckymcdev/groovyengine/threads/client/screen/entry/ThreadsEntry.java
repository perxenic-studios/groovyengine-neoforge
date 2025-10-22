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

    @Override
    public Component getNarration() {
        return Component.literal("Error in script " + error.scriptName());
    }
}