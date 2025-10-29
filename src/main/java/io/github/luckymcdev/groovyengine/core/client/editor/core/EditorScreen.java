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

package io.github.luckymcdev.groovyengine.core.client.editor.core;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class EditorScreen extends Screen {

    public EditorScreen() {
        super(Component.literal("GroovyEngine Editor"));
    }

    /**
     * Initializes the screen.
     * <p>
     * Does not automatically enable ImGui when opening the screen.
     * Instead, lets the user control it via keybind or buttons.
     */
    @Override
    protected void init() {
        super.init();
    }

    /**
     * Renders the screen.
     * <p>
     * This method does not call the super class's render method to avoid rendering the default background.
     * Instead, it makes the screen transparent so you can see the game behind it.
     *
     * @param guiGraphics the graphics handler for the screen
     * @param mouseX      the x-coordinate of the mouse in screen coordinates
     * @param mouseY      the y-coordinate of the mouse in screen coordinates
     * @param partialTick the partial tick value for the render operation
     */
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    /**
     * Closes the screen without disabling ImGui.
     * <p>
     * This method does not automatically disable ImGui when closing the screen.
     * Instead, it lets the user control it via keybind or buttons.
     */
    @Override
    public void onClose() {
        super.onClose();
    }

    /**
     * Override to not render the background of the screen.
     * This keeps the game visible behind the screen.
     *
     * @param guiGraphics the graphics handler for the screen
     * @param mouseX      the x-coordinate of the mouse in screen coordinates
     * @param mouseY      the y-coordinate of the mouse in screen coordinates
     * @param partialTick the partial tick value for the render operation
     */
    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    /**
     * Determines whether the game should pause when this screen is open.
     * <p>
     * If this method returns true, the game will pause when this screen is open.
     * If this method returns false, the game will not pause when this screen is open.
     *
     * @return true if the game should pause, false if not
     */
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * Returns whether the screen should close when the Escape key is pressed.
     * <p>
     * If this method returns true, the screen will close when the Escape key is pressed.
     * If this method returns false, the screen will not close when the Escape key is pressed.
     *
     * @return true if the screen should close on Escape, false if not
     */
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}