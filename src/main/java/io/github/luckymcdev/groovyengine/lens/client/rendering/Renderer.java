package io.github.luckymcdev.groovyengine.lens.client.rendering;

import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class Renderer {
    private static final Renderer INSTANCE = new Renderer();

    public static Renderer getInstance() {
        return INSTANCE;
    }

    private final Map<Class<?>, List<Consumer<?>>> renderCallbacks = new HashMap<>();

    private Renderer() {
        // Initialize all supported events
        List<Class<?>> events = List.of(
                RenderLevelStageEvent.class,
                RenderFrameEvent.class,
                RenderGuiEvent.class,
                RenderGuiLayerEvent.class,
                RenderTooltipEvent.class,
                RenderNameTagEvent.class,
                RenderHighlightEvent.class,
                RenderLivingEvent.class,
                RenderPlayerEvent.class,
                RenderArmEvent.class,
                RenderHandEvent.class,
                RenderItemInFrameEvent.class,
                RenderBlockScreenEffectEvent.class
        );

        for (Class<?> eventClass : events) {
            renderCallbacks.put(eventClass, new ArrayList<>());
        }

        // Register with NeoForge event bus
        // NeoForge.EVENT_BUS.register(this);
    }
}
