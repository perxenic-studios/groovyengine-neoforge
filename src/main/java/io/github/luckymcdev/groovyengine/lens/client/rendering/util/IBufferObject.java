package io.github.luckymcdev.groovyengine.lens.client.rendering.util;

import io.github.luckymcdev.groovyengine.lens.client.rendering.core.LensRenderSystem;

/**
 * Utility interface for registering buffer objects to be destroyed when the game closes.
 */
public interface IBufferObject {
    default void registerBufferObject() {
        LensRenderSystem.registerBufferObject(this);
    }
    void destroy();
}