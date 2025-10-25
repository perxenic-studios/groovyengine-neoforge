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