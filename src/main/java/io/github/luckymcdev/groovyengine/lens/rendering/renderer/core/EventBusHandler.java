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

// EventBusHandler.java - New file
package io.github.luckymcdev.groovyengine.lens.rendering.renderer.core;

import io.github.luckymcdev.groovyengine.lens.rendering.renderer.EntityRenderer;
import io.github.luckymcdev.groovyengine.lens.rendering.renderer.GuiRenderer;
import io.github.luckymcdev.groovyengine.lens.rendering.renderer.PlayerRenderer;
import io.github.luckymcdev.groovyengine.lens.rendering.renderer.WorldRenderer;
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

    /**
     * Registers all renderers and their respective event mappings to the event bus.
     *
     * @param renderer the renderer to register
     */
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

    /**
     * Handles the given event on the given renderer.
     *
     * @param renderer   the renderer that is handling the event
     * @param eventClass the class of the event being handled
     * @param event      the event being handled
     * @throws RuntimeException if any exception occurs while handling the event
     */
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