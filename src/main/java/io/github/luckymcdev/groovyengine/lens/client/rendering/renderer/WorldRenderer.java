// WorldRenderer.java - Updated
package io.github.luckymcdev.groovyengine.lens.client.rendering.renderer;

import io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.core.BaseRenderer;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.*;
import java.util.function.Consumer;

public class WorldRenderer extends BaseRenderer {
    private static final List<Class<? extends Event>> WORLD_EVENTS = List.of(
            RenderLevelStageEvent.class,
            RenderFrameEvent.Pre.class,
            RenderFrameEvent.Post.class,
            RenderHighlightEvent.Block.class,
            RenderHighlightEvent.Entity.class,
            RenderBlockScreenEffectEvent.class
    );

    public enum RenderWorldEvent {
        RENDER_LEVEL_STAGE,
        RENDER_FRAME_PRE,
        RENDER_FRAME_POST,
        RENDER_HIGHLIGHT_BLOCK,
        RENDER_HIGHLIGHT_ENTITY,
        RENDER_BLOCK_SCREEN_EFFECT
    }

    private final Map<RenderWorldEvent, List<Consumer<?>>> eventCallbacks = new EnumMap<>(RenderWorldEvent.class);

    public WorldRenderer() {
        for (RenderWorldEvent event : RenderWorldEvent.values()) {
            eventCallbacks.put(event, new ArrayList<>());
        }
    }

    @Override
    protected List<Class<? extends Event>> getEvents() {
        return WORLD_EVENTS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void execute(Enum<?> eventType, T event) {
        if (eventType instanceof RenderWorldEvent worldEvent) {
            List<Consumer<?>> callbacks = eventCallbacks.get(worldEvent);
            for (Consumer<?> callback : callbacks) {
                ((Consumer<T>) callback).accept(event);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void registerCallback(Enum<?> eventType, Consumer<T> callback) {
        if (eventType instanceof RenderWorldEvent worldEvent) {
            eventCallbacks.get(worldEvent).add(callback);
        }
    }

    // Convenience methods
    public void onRenderLevelStage(Consumer<RenderLevelStageEvent> callback) {
        registerCallback(RenderWorldEvent.RENDER_LEVEL_STAGE, callback);
    }

    public void onRenderFramePre(Consumer<RenderFrameEvent.Pre> callback) {
        registerCallback(RenderWorldEvent.RENDER_FRAME_PRE, callback);
    }

    public void onRenderFramePost(Consumer<RenderFrameEvent.Post> callback) {
        registerCallback(RenderWorldEvent.RENDER_FRAME_POST, callback);
    }

    public void onRenderHighlightBlock(Consumer<RenderHighlightEvent.Block> callback) {
        registerCallback(RenderWorldEvent.RENDER_HIGHLIGHT_BLOCK, callback);
    }

    public void onRenderHighlightEntity(Consumer<RenderHighlightEvent.Entity> callback) {
        registerCallback(RenderWorldEvent.RENDER_HIGHLIGHT_ENTITY, callback);
    }

    public void onRenderBlockScreenEffect(Consumer<RenderBlockScreenEffectEvent> callback) {
        registerCallback(RenderWorldEvent.RENDER_BLOCK_SCREEN_EFFECT, callback);
    }

    public static RenderWorldEvent getEventType(RenderLevelStageEvent event) {
        return RenderWorldEvent.RENDER_LEVEL_STAGE;
    }

    public static RenderWorldEvent getEventType(RenderFrameEvent.Pre event) {
        return RenderWorldEvent.RENDER_FRAME_PRE;
    }

    public static RenderWorldEvent getEventType(RenderFrameEvent.Post event) {
        return RenderWorldEvent.RENDER_FRAME_POST;
    }

    public static RenderWorldEvent getEventType(RenderHighlightEvent.Block event) {
        return RenderWorldEvent.RENDER_HIGHLIGHT_BLOCK;
    }

    public static RenderWorldEvent getEventType(RenderHighlightEvent.Entity event) {
        return RenderWorldEvent.RENDER_HIGHLIGHT_ENTITY;
    }

    public static RenderWorldEvent getEventType(RenderBlockScreenEffectEvent event) {
        return RenderWorldEvent.RENDER_BLOCK_SCREEN_EFFECT;
    }
}