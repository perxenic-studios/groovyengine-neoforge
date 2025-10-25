package io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.event;

import io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.core.Renderer;
import net.neoforged.bus.api.Event;

/**
 * Simple event that provides access to the renderer
 */
public class RenderEvent extends Event {
    private final Renderer renderer;

    public RenderEvent() {
        this.renderer = Renderer.getInstance();
    }

    /**
     * Returns the renderer instance.
     *
     * @return the renderer instance
     */
    public Renderer getRenderer() {
        return renderer;
    }
}