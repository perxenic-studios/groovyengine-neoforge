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

import net.neoforged.bus.api.Event;

import java.util.List;
import java.util.function.Consumer;

public abstract class BaseRenderer {
    /**
     * Gets the list of event classes that this renderer is listening to.
     *
     * @return the list of event classes
     */
    protected abstract List<Class<? extends Event>> getEvents();

    /**
     * Executes the given event based on the event type.
     *
     * @param eventType the type of the event
     * @param event the event to execute
     */
    public abstract <T extends Event> void execute(Enum<?> eventType, T event);

    /**
     * Registers a callback for the given event type.
     *
     * @param eventType the type of the event
     * @param callback the callback to register
     */
    public abstract <T extends Event> void registerCallback(Enum<?> eventType, Consumer<T> callback);
}