package io.github.luckymcdev.groovyengine.lens.client.rendering.util;

import io.github.luckymcdev.groovyengine.lens.client.rendering.core.LensRenderSystem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Utility interface for registering buffer objects to be destroyed when the game closes.
 */
@OnlyIn(Dist.CLIENT)
public interface IBufferObject {
    default void registerBufferObject() {
        LensRenderSystem.registerBufferObject(this);
    }
    void destroy();
}