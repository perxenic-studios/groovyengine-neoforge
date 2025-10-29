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

package io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.core;

import io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.EntityRenderer;
import io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.GuiRenderer;
import io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.PlayerRenderer;
import io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.WorldRenderer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Renderer {
    private static final Renderer INSTANCE = new Renderer();
    private final Map<Class<? extends BaseRenderer>, BaseRenderer> renderers = new HashMap<>();

    private Renderer() {
        // Register all sub-renderers
        registerRenderer(EntityRenderer.class, new EntityRenderer());
        registerRenderer(GuiRenderer.class, new GuiRenderer());
        registerRenderer(PlayerRenderer.class, new PlayerRenderer());
        registerRenderer(WorldRenderer.class, new WorldRenderer());
    }

    /**
     * Returns the singleton instance of the renderer.
     *
     * @return the renderer instance
     */
    public static Renderer getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a sub-renderer with the given class.
     *
     * @param rendererClass the class of the sub-renderer to register
     * @param renderer      the instance of the sub-renderer to register
     */
    private <T extends BaseRenderer> void registerRenderer(Class<T> rendererClass, T renderer) {
        renderers.put(rendererClass, renderer);
    }

    /**
     * Returns the sub-renderer instance associated with the given class.
     *
     * @param rendererClass the class of the sub-renderer to retrieve
     * @return the sub-renderer instance associated with the given class, or null if no such instance exists
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseRenderer> T getRenderer(Class<T> rendererClass) {
        return (T) renderers.get(rendererClass);
    }

    /**
     * Returns the entity renderer.
     *
     * @return the entity renderer
     */
    public EntityRenderer getEntityRenderer() {
        return getRenderer(EntityRenderer.class);
    }

    /**
     * Returns the GUI renderer instance.
     *
     * @return the GUI renderer instance
     */
    public GuiRenderer getGuiRenderer() {
        return getRenderer(GuiRenderer.class);
    }

    /**
     * Returns the player renderer.
     *
     * @return the player renderer
     */
    public PlayerRenderer getPlayerRenderer() {
        return getRenderer(PlayerRenderer.class);
    }

    /**
     * Returns the world renderer instance.
     *
     * @return the world renderer instance
     */
    public WorldRenderer getWorldRenderer() {
        return getRenderer(WorldRenderer.class);
    }

    /**
     * Returns a collection of all registered sub-renderers.
     *
     * @return a collection of all registered sub-renderers
     */
    public Collection<BaseRenderer> getAllRenderers() {
        return renderers.values();
    }
}