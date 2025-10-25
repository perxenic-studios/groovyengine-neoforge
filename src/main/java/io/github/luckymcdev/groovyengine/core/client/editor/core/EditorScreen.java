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
     *
     * Does not automatically enable ImGui when opening the screen.
     * Instead, lets the user control it via keybind or buttons.
     */
    @Override
    protected void init() {
        super.init();
    }

    /**
     * Renders the screen.
     *
     * This method does not call the super class's render method to avoid rendering the default background.
     * Instead, it makes the screen transparent so you can see the game behind it.
     *
     * @param guiGraphics the graphics handler for the screen
     * @param mouseX the x-coordinate of the mouse in screen coordinates
     * @param mouseY the y-coordinate of the mouse in screen coordinates
     * @param partialTick the partial tick value for the render operation
     */
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    /**
     * Closes the screen without disabling ImGui.
     *
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
     * @param mouseX the x-coordinate of the mouse in screen coordinates
     * @param mouseY the y-coordinate of the mouse in screen coordinates
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
     *
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