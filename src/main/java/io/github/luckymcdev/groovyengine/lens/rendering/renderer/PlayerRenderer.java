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

// PlayerRenderer.java - Updated
package io.github.luckymcdev.groovyengine.lens.rendering.renderer;

import io.github.luckymcdev.groovyengine.lens.rendering.renderer.core.BaseRenderer;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PlayerRenderer extends BaseRenderer {
    private static final List<Class<? extends Event>> PLAYER_EVENTS = List.of(
            RenderPlayerEvent.Pre.class,
            RenderPlayerEvent.Post.class,
            RenderArmEvent.class,
            RenderHandEvent.class
    );
    private final Map<RenderPlayerEventType, List<Consumer<?>>> eventCallbacks = new EnumMap<>(RenderPlayerEventType.class);

    public PlayerRenderer() {
        for (RenderPlayerEventType event : RenderPlayerEventType.values()) {
            eventCallbacks.put(event, new ArrayList<>());
        }
    }

    public static RenderPlayerEventType getEventType(RenderPlayerEvent.Pre event) {
        return RenderPlayerEventType.RENDER_PLAYER_PRE;
    }

    public static RenderPlayerEventType getEventType(RenderPlayerEvent.Post event) {
        return RenderPlayerEventType.RENDER_PLAYER_POST;
    }

    public static RenderPlayerEventType getEventType(RenderArmEvent event) {
        return RenderPlayerEventType.RENDER_ARM;
    }

    public static RenderPlayerEventType getEventType(RenderHandEvent event) {
        return RenderPlayerEventType.RENDER_HAND;
    }

    @Override
    protected List<Class<? extends Event>> getEvents() {
        return PLAYER_EVENTS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void execute(Enum<?> eventType, T event) {
        if (eventType instanceof RenderPlayerEventType playerEvent) {
            List<Consumer<?>> callbacks = eventCallbacks.get(playerEvent);
            for (Consumer<?> callback : callbacks) {
                ((Consumer<T>) callback).accept(event);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void registerCallback(Enum<?> eventType, Consumer<T> callback) {
        if (eventType instanceof RenderPlayerEventType playerEvent) {
            eventCallbacks.get(playerEvent).add(callback);
        }
    }

    // Convenience methods
    public void onRenderPlayerPre(Consumer<RenderPlayerEvent.Pre> callback) {
        registerCallback(RenderPlayerEventType.RENDER_PLAYER_PRE, callback);
    }

    public void onRenderPlayerPost(Consumer<RenderPlayerEvent.Post> callback) {
        registerCallback(RenderPlayerEventType.RENDER_PLAYER_POST, callback);
    }

    public void onRenderArm(Consumer<RenderArmEvent> callback) {
        registerCallback(RenderPlayerEventType.RENDER_ARM, callback);
    }

    public void onRenderHand(Consumer<RenderHandEvent> callback) {
        registerCallback(RenderPlayerEventType.RENDER_HAND, callback);
    }

    public enum RenderPlayerEventType {
        RENDER_PLAYER_PRE,
        RENDER_PLAYER_POST,
        RENDER_ARM,
        RENDER_HAND
    }
}