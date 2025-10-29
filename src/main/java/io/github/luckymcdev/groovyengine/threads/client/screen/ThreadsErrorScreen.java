/*
 * Copyright 2025 LuckyMcDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.luckymcdev.groovyengine.threads.client.screen;

import io.github.luckymcdev.groovyengine.threads.client.screen.entry.ThreadsEntryList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThreadsErrorScreen extends ErrorScreen {
    private ThreadsEntryList entryList;

    public ThreadsErrorScreen() {
        super(
                Component.literal("Script Errors Detected"),
                Component.literal("Details are shown below")
        );
    }

    /**
     * Initializes the screen components.
     * Clears all widgets, creates a new {@link ThreadsEntryList} and adds it to the screen,
     * sets the focus to the entry list, and adds a "Close" button to the screen.
     */
    @Override
    protected void init() {
        this.clearWidgets();

        this.entryList = new ThreadsEntryList(this);
        this.addWidget(entryList);
        this.setFocused(entryList);

        this.addRenderableWidget(
                Button.builder(Component.literal("Close"), b -> this.minecraft.close())
                        .bounds(this.width / 2 - 50, this.height - 24, 100, 20)
                        .build()
        );
    }

    /**
     * Handles a key press event.
     *
     * @param keyCode   the key code of the key that was pressed
     * @param scanCode  the scan code of the key that was pressed
     * @param modifiers the modifier keys that were pressed
     * @return whether the key press event was handled
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    /**
     * Renders the screen components.
     *
     * @param guiGraphics the graphics object to render with
     * @param mouseX      the x position of the mouse
     * @param mouseY      the y position of the mouse
     * @param partialTick the partial tick to render with
     */
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

