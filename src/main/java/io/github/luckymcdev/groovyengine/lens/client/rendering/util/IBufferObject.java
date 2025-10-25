package io.github.luckymcdev.groovyengine.lens.client.rendering.util;

import io.github.luckymcdev.groovyengine.lens.client.rendering.core.LensRenderSystem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Utility interface for registering buffer objects to be destroyed when the game closes.
 */
@OnlyIn(Dist.CLIENT)
public interface IBufferObject {
    /**
     * Registers this buffer object to be destroyed when the game closes.
     * <p>
     * This method is a no-op by default, but can be overridden if needed.
     */
    default void registerBufferObject() {
        LensRenderSystem.registerBufferObject(this);
    }

    /**
     * Destroys this buffer object.
     * <p>
     * This method is called when the game closes, and is responsible for cleaning up any resources allocated by the buffer object.
     */
    void destroy();
}