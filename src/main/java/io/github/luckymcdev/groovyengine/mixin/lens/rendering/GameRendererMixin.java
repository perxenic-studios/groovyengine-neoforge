package io.github.luckymcdev.groovyengine.mixin.lens.rendering;

import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.PostProcessManager;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
@OnlyIn(Dist.CLIENT)
public class GameRendererMixin {
    @Shadow
    private float renderDistance;

    @Inject(method = "resize", at = @At(value = "HEAD"))
    public void groovyengine$injectionResizeListener(int width, int height, CallbackInfo ci) {
        PostProcessManager.resize(width, height);
    }

    /**
     * @author LuckyMcDev
     * @reason To increase draw distance for rendering
     */
    @Overwrite()
    public float getDepthFar() {
        return this.renderDistance * 1000.0F;
    }
}
