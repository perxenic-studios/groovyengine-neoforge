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

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initImGui(GameConfig gameConfig, CallbackInfo ci) {
        ImGuiImpl.create(window.getWindow());
        ImGuiImpl.loadFonts(Minecraft.getInstance().getResourceManager());
    }

    @Inject(method = "close", at = @At("RETURN"))
    public void closeImGui(CallbackInfo ci) {
        ImGuiImpl.dispose();
    }

}
