// GuiRenderer.java - Updated
package io.github.luckymcdev.groovyengine.lens.client.rendering.renderer;

import io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.core.BaseRenderer;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.*;
import java.util.function.Consumer;

public class GuiRenderer extends BaseRenderer {
    private static final List<Class<? extends Event>> GUI_EVENTS = List.of(
            RenderGuiEvent.Pre.class,
            RenderGuiEvent.Post.class,
            RenderGuiLayerEvent.Pre.class,
            RenderGuiLayerEvent.Post.class,
            RenderTooltipEvent.Pre.class,
            RenderTooltipEvent.Color.class,
            RenderTooltipEvent.GatherComponents.class
    );

    public enum RenderGuiEventType {
        RENDER_GUI_PRE,
        RENDER_GUI_POST,
        RENDER_GUI_LAYER_PRE,
        RENDER_GUI_LAYER_POST,
        RENDER_TOOLTIP_PRE,
        RENDER_TOOLTIP_COLOR,
        RENDER_TOOLTIP_GATHER_COMPONENTS
    }

    private final Map<RenderGuiEventType, List<Consumer<?>>> eventCallbacks = new EnumMap<>(RenderGuiEventType.class);

    public GuiRenderer() {
        for (RenderGuiEventType event : RenderGuiEventType.values()) {
            eventCallbacks.put(event, new ArrayList<>());
        }
    }

    @Override
    protected List<Class<? extends Event>> getEvents() {
        return GUI_EVENTS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void execute(Enum<?> eventType, T event) {
        if (eventType instanceof RenderGuiEventType guiEvent) {
            List<Consumer<?>> callbacks = eventCallbacks.get(guiEvent);
            for (Consumer<?> callback : callbacks) {
                ((Consumer<T>) callback).accept(event);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void registerCallback(Enum<?> eventType, Consumer<T> callback) {
        if (eventType instanceof RenderGuiEventType guiEvent) {
            eventCallbacks.get(guiEvent).add(callback);
        }
    }

    // Convenience methods
    public void onRenderGuiPre(Consumer<RenderGuiEvent.Pre> callback) {
        registerCallback(RenderGuiEventType.RENDER_GUI_PRE, callback);
    }

    public void onRenderGuiPost(Consumer<RenderGuiEvent.Post> callback) {
        registerCallback(RenderGuiEventType.RENDER_GUI_POST, callback);
    }

    public void onRenderGuiLayerPre(Consumer<RenderGuiLayerEvent.Pre> callback) {
        registerCallback(RenderGuiEventType.RENDER_GUI_LAYER_PRE, callback);
    }

    public void onRenderGuiLayerPost(Consumer<RenderGuiLayerEvent.Post> callback) {
        registerCallback(RenderGuiEventType.RENDER_GUI_LAYER_POST, callback);
    }

    public void onRenderTooltipPre(Consumer<RenderTooltipEvent.Pre> callback) {
        registerCallback(RenderGuiEventType.RENDER_TOOLTIP_PRE, callback);
    }

    public void onRenderTooltipColor(Consumer<RenderTooltipEvent.Color> callback) {
        registerCallback(RenderGuiEventType.RENDER_TOOLTIP_COLOR, callback);
    }

    public void onRenderTooltipGatherComponents(Consumer<RenderTooltipEvent.GatherComponents> callback) {
        registerCallback(RenderGuiEventType.RENDER_TOOLTIP_GATHER_COMPONENTS, callback);
    }

    public static RenderGuiEventType getEventType(RenderGuiEvent.Pre event) {
        return RenderGuiEventType.RENDER_GUI_PRE;
    }

    public static RenderGuiEventType getEventType(RenderGuiEvent.Post event) {
        return RenderGuiEventType.RENDER_GUI_POST;
    }

    public static RenderGuiEventType getEventType(RenderGuiLayerEvent.Pre event) {
        return RenderGuiEventType.RENDER_GUI_LAYER_PRE;
    }

    public static RenderGuiEventType getEventType(RenderGuiLayerEvent.Post event) {
        return RenderGuiEventType.RENDER_GUI_LAYER_POST;
    }

    public static RenderGuiEventType getEventType(RenderTooltipEvent.Pre event) {
        return RenderGuiEventType.RENDER_TOOLTIP_PRE;
    }

    public static RenderGuiEventType getEventType(RenderTooltipEvent.Color event) {
        return RenderGuiEventType.RENDER_TOOLTIP_COLOR;
    }

    public static RenderGuiEventType getEventType(RenderTooltipEvent.GatherComponents event) {
        return RenderGuiEventType.RENDER_TOOLTIP_GATHER_COMPONENTS;
    }
}