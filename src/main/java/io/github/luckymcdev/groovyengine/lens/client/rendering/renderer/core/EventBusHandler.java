// EventBusHandler.java - New file
package io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.core;

import io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.*;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EventBusHandler {
    private static final Map<Class<? extends Event>, Method> EVENT_MAPPINGS = new HashMap<>();

    static {
        // Initialize event to enum mapping methods
        try {
            // Entity RegisterRenderable mappings
            EVENT_MAPPINGS.put(RenderLivingEvent.Pre.class,
                    EntityRenderer.class.getMethod("getEventType", RenderLivingEvent.Pre.class));
            EVENT_MAPPINGS.put(RenderLivingEvent.Post.class,
                    EntityRenderer.class.getMethod("getEventType", RenderLivingEvent.Post.class));
            EVENT_MAPPINGS.put(RenderNameTagEvent.class,
                    EntityRenderer.class.getMethod("getEventType", RenderNameTagEvent.class));
            EVENT_MAPPINGS.put(RenderItemInFrameEvent.class,
                    EntityRenderer.class.getMethod("getEventType", RenderItemInFrameEvent.class));

            // GUI RegisterRenderable mappings
            EVENT_MAPPINGS.put(RenderGuiEvent.Pre.class,
                    GuiRenderer.class.getMethod("getEventType", RenderGuiEvent.Pre.class));
            EVENT_MAPPINGS.put(RenderGuiEvent.Post.class,
                    GuiRenderer.class.getMethod("getEventType", RenderGuiEvent.Post.class));
            EVENT_MAPPINGS.put(RenderGuiLayerEvent.Pre.class,
                    GuiRenderer.class.getMethod("getEventType", RenderGuiLayerEvent.Pre.class));
            EVENT_MAPPINGS.put(RenderGuiLayerEvent.Post.class,
                    GuiRenderer.class.getMethod("getEventType", RenderGuiLayerEvent.Post.class));
            EVENT_MAPPINGS.put(RenderTooltipEvent.Pre.class,
                    GuiRenderer.class.getMethod("getEventType", RenderTooltipEvent.Pre.class));
            EVENT_MAPPINGS.put(RenderTooltipEvent.Color.class,
                    GuiRenderer.class.getMethod("getEventType", RenderTooltipEvent.Color.class));
            EVENT_MAPPINGS.put(RenderTooltipEvent.GatherComponents.class,
                    GuiRenderer.class.getMethod("getEventType", RenderTooltipEvent.GatherComponents.class));

            // Player RegisterRenderable mappings
            EVENT_MAPPINGS.put(RenderPlayerEvent.Pre.class,
                    PlayerRenderer.class.getMethod("getEventType", RenderPlayerEvent.Pre.class));
            EVENT_MAPPINGS.put(RenderPlayerEvent.Post.class,
                    PlayerRenderer.class.getMethod("getEventType", RenderPlayerEvent.Post.class));
            EVENT_MAPPINGS.put(RenderArmEvent.class,
                    PlayerRenderer.class.getMethod("getEventType", RenderArmEvent.class));
            EVENT_MAPPINGS.put(RenderHandEvent.class,
                    PlayerRenderer.class.getMethod("getEventType", RenderHandEvent.class));

            // World RegisterRenderable mappings
            EVENT_MAPPINGS.put(RenderLevelStageEvent.class,
                    WorldRenderer.class.getMethod("getEventType", RenderLevelStageEvent.class));
            EVENT_MAPPINGS.put(RenderFrameEvent.Pre.class,
                    WorldRenderer.class.getMethod("getEventType", RenderFrameEvent.Pre.class));
            EVENT_MAPPINGS.put(RenderFrameEvent.Post.class,
                    WorldRenderer.class.getMethod("getEventType", RenderFrameEvent.Post.class));
            EVENT_MAPPINGS.put(RenderHighlightEvent.Block.class,
                    WorldRenderer.class.getMethod("getEventType", RenderHighlightEvent.Block.class));
            EVENT_MAPPINGS.put(RenderHighlightEvent.Entity.class,
                    WorldRenderer.class.getMethod("getEventType", RenderHighlightEvent.Entity.class));
            EVENT_MAPPINGS.put(RenderBlockScreenEffectEvent.class,
                    WorldRenderer.class.getMethod("getEventType", RenderBlockScreenEffectEvent.class));

        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to initialize event mappings", e);
        }
    }

    public static void register(Renderer renderer) {
        IEventBus eventBus = NeoForge.EVENT_BUS;

        for (BaseRenderer subRenderer : renderer.getAllRenderers()) {
            for (Class<? extends Event> eventClass : subRenderer.getEvents()) {
                eventBus.addListener(EventPriority.NORMAL, false, eventClass, event -> {
                    handleEvent(subRenderer, eventClass, event);
                });
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Event> void handleEvent(BaseRenderer renderer, Class<? extends Event> eventClass, T event) {
        try {
            Method mappingMethod = EVENT_MAPPINGS.get(eventClass);
            if (mappingMethod != null) {
                Enum<?> eventType = (Enum<?>) mappingMethod.invoke(null, event);
                renderer.execute(eventType, event);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to handle event: " + eventClass.getSimpleName(), e);
        }
    }
}