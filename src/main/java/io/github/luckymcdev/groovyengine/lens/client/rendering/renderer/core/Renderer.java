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

    public static Renderer getInstance() {
        return INSTANCE;
    }

    private <T extends BaseRenderer> void registerRenderer(Class<T> rendererClass, T renderer) {
        renderers.put(rendererClass, renderer);
    }

    public <T extends BaseRenderer> T getRenderer(Class<T> rendererClass) {
        return (T) renderers.get(rendererClass);
    }

    public EntityRenderer getEntityRenderer() {
        return getRenderer(EntityRenderer.class);
    }

    public GuiRenderer getGuiRenderer() {
        return getRenderer(GuiRenderer.class);
    }

    public PlayerRenderer getPlayerRenderer() {
        return getRenderer(PlayerRenderer.class);
    }

    public WorldRenderer getWorldRenderer() {
        return getRenderer(WorldRenderer.class);
    }

    public Collection<BaseRenderer> getAllRenderers() {
        return renderers.values();
    }
}