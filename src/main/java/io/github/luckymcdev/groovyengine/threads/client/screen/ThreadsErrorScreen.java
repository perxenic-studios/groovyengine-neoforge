package io.github.luckymcdev.groovyengine.threads.client.screen;

import io.github.luckymcdev.groovyengine.threads.client.screen.entry.ThreadsEntryList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.network.chat.Component;

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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
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

}

