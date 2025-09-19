package io.github.luckymcdev.groovyengine.threads.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import io.github.luckymcdev.groovyengine.threads.core.scripting.error.ScriptErrors;

public class ThreadsErrorScreen extends Screen {

    private int scrollOffset = 0; // current scroll
    private final int lineHeight = 12; // spacing between lines

    public ThreadsErrorScreen() {
        super(Component.literal("Script Errors Detected"));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int startY = 20; // top margin
        int screenHeight = this.height - 40; // leave some margin at bottom

        int y = startY - scrollOffset;

        for (ScriptErrors.ErrorEntry error : ScriptErrors.getErrors()) {
            guiGraphics.drawString(this.font, "Script: " + error.scriptName, 10, y, 0xFFFF5555, false);
            y += lineHeight;
            guiGraphics.drawString(this.font, "Message: " + error.message, 10, y, 0xFFFFFFFF, false);
            y += lineHeight;
            guiGraphics.drawString(this.font, "Fix: " + error.description, 10, y, 0xFFAAAAAA, false);
            y += lineHeight + 8; // extra spacing between entries
        }

        // Optional: show scroll hint if needed
        if (ScriptErrors.getErrors().size() * (lineHeight * 3 + 8) > screenHeight) {
            guiGraphics.drawString(this.font, "Use mouse wheel to scroll", 10, this.height - 20, 0xFFFFFF55, false);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int totalHeight = ScriptErrors.getErrors().size() * (lineHeight * 3 + 8);
        scrollOffset -= (int)(scrollY * 15); // scroll speed
        scrollOffset = Math.max(0, Math.min(scrollOffset, Math.max(0, totalHeight - (height - 40))));
        return true;
    }
}
