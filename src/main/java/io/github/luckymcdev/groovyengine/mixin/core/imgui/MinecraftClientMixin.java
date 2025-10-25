package io.github.luckymcdev.groovyengine.mixin.core.imgui;

import com.mojang.blaze3d.platform.Window;
import io.github.luckymcdev.groovyengine.core.client.imgui.core.ImGuiImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
@OnlyIn(Dist.CLIENT)
public class MinecraftClientMixin {

    @Shadow
    @Final
    private Window window;

    /**
     * Initializes ImGuiImpl when the Minecraft client is initialized.
     * This method is only called on the client side, and is used to initialize ImGuiImpl.
     * It creates an instance of ImGuiImpl and loads the fonts from the resource manager.
     *
     * @param gameConfig the game configuration
     * @param ci the callback information for the initialization method
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    public void initImGui(GameConfig gameConfig, CallbackInfo ci) {
        ImGuiImpl.create(window.getWindow());
        ImGuiImpl.loadFonts(Minecraft.getInstance().getResourceManager());
    }

    /**
     * Called when the Minecraft client is closed.
     * This method is only called on the client side, and is used to dispose of ImGuiImpl.
     * It disposes of ImGuiImpl, freeing up resources and preventing memory leaks.
     *
     * @param ci the callback information for the close method
     */
    @Inject(method = "close", at = @At("RETURN"))
    public void closeImGui(CallbackInfo ci) {
        ImGuiImpl.dispose();
    }

}
