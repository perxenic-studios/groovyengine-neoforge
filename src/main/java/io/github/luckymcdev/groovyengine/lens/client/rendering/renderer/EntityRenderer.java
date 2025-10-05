// EntityRenderer.java - Updated
package io.github.luckymcdev.groovyengine.lens.client.rendering.renderer;

import io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.core.BaseRenderer;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.client.event.RenderItemInFrameEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;

import java.util.*;
import java.util.function.Consumer;

public class EntityRenderer extends BaseRenderer {
    private static final List<Class<? extends Event>> ENTITY_EVENTS = List.of(
            RenderLivingEvent.Pre.class,
            RenderLivingEvent.Post.class,
            RenderNameTagEvent.class,
            RenderItemInFrameEvent.class
    );

    public enum RenderEntityEvent {
        RENDER_LIVING_PRE,
        RENDER_LIVING_POST,
        RENDER_NAME_TAG,
        RENDER_ITEM_IN_FRAME
    }

    private final Map<RenderEntityEvent, List<Consumer<?>>> eventCallbacks = new EnumMap<>(RenderEntityEvent.class);

    public EntityRenderer() {
        for (RenderEntityEvent event : RenderEntityEvent.values()) {
            eventCallbacks.put(event, new ArrayList<>());
        }
    }

    @Override
    protected List<Class<? extends Event>> getEvents() {
        return ENTITY_EVENTS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void execute(Enum<?> eventType, T event) {
        if (eventType instanceof RenderEntityEvent entityEvent) {
            List<Consumer<?>> callbacks = eventCallbacks.get(entityEvent);
            for (Consumer<?> callback : callbacks) {
                ((Consumer<T>) callback).accept(event);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void registerCallback(Enum<?> eventType, Consumer<T> callback) {
        if (eventType instanceof RenderEntityEvent entityEvent) {
            eventCallbacks.get(entityEvent).add(callback);
        }
    }

    // Convenience methods
    public void onRenderLivingPre(Consumer<RenderLivingEvent.Pre> callback) {
        registerCallback(RenderEntityEvent.RENDER_LIVING_PRE, callback);
    }

    public void onRenderLivingPost(Consumer<RenderLivingEvent.Post> callback) {
        registerCallback(RenderEntityEvent.RENDER_LIVING_POST, callback);
    }

    public void onRenderNameTag(Consumer<RenderNameTagEvent> callback) {
        registerCallback(RenderEntityEvent.RENDER_NAME_TAG, callback);
    }

    public void onRenderItemInFrame(Consumer<RenderItemInFrameEvent> callback) {
        registerCallback(RenderEntityEvent.RENDER_ITEM_IN_FRAME, callback);
    }

    public static RenderEntityEvent getEventType(RenderLivingEvent.Pre event) {
        return RenderEntityEvent.RENDER_LIVING_PRE;
    }

    public static RenderEntityEvent getEventType(RenderLivingEvent.Post event) {
        return RenderEntityEvent.RENDER_LIVING_POST;
    }

    public static RenderEntityEvent getEventType(RenderNameTagEvent event) {
        return RenderEntityEvent.RENDER_NAME_TAG;
    }

    public static RenderEntityEvent getEventType(RenderItemInFrameEvent event) {
        return RenderEntityEvent.RENDER_ITEM_IN_FRAME;
    }
}