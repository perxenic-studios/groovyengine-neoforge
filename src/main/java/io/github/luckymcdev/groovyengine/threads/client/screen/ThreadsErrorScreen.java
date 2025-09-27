package io.github.luckymcdev.groovyengine.threads.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import io.github.luckymcdev.groovyengine.threads.core.scripting.error.ScriptErrors;

public class ThreadsErrorScreen extends ErrorScreen {
    private ThreadsEntryList entryList;

    public ThreadsErrorScreen() {
        super(
                Component.literal("Script Errors Detected"),
                Component.literal("Details are shown below")
        );
    }

    @Override
    protected void init() {
        this.clearWidgets();

        this.entryList = new ThreadsEntryList(this);
        this.addWidget(entryList);
        this.setFocused(entryList);

        this.addRenderableWidget(
                Button.builder(Component.literal("Close"), b -> this.minecraft.setScreen(null))
                        .bounds(this.width / 2 - 50, this.height - 24, 100, 20)
                        .build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFF5555);

        // render custom entries
        this.entryList.render(guiGraphics, mouseX, mouseY, partialTick);

        // render buttons
        for (var widget : this.renderables) {
            widget.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }


    // Custom entry list like LoadingErrorScreen.LoadingEntryList
    public static class ThreadsEntryList extends ObjectSelectionList<ThreadsEntry> {
        public ThreadsEntryList(ThreadsErrorScreen parent) {
            super(parent.minecraft, parent.width, parent.height - 40, 35, 20);

            for (ScriptErrors.ErrorEntry error : ScriptErrors.getErrors()) {
                this.addEntry(new ThreadsEntry(error));
            }
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getRight() - 6;
        }

        @Override
        public int getRowWidth() {
            return this.width - 15;
        }
    }

    public static class ThreadsEntry extends ObjectSelectionList.Entry<ThreadsEntry> {
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
}

