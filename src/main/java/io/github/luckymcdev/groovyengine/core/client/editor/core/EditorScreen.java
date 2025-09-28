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

    @Override
    protected void init() {
        super.init();
        // Don't automatically enable ImGui when opening screen
        // Let the user control it via keybind or buttons
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Don't call super.render() to avoid rendering the default background
        // This makes the screen transparent so you can see the game behind it
    }

    @Override
    public void onClose() {
        super.onClose();
        // Don't automatically disable ImGui when closing screen
        // Let it stay enabled if the user wants it
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Override to not render background - keeps game visible
    }

    @Override
    public boolean isPauseScreen() {
        return false; // This prevents the game from pausing
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true; // Allow closing with Escape key
    }
}