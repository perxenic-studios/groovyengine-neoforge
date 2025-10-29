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

    /**
     * A callback method that is called when the game's window is resized.
     * It is used to notify the post-processing manager to update its internal state.
     * @param width The new width of the window
     * @param height The new height of the window
     * @param ci The callback information object
     */
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
