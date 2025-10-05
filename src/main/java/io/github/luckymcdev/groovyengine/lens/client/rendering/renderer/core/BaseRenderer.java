package io.github.luckymcdev.groovyengine.lens.client.rendering.renderer.core;

import net.neoforged.bus.api.Event;

import java.util.List;
import java.util.function.Consumer;

public abstract class BaseRenderer {
    protected abstract List<Class<? extends Event>> getEvents();

    public abstract <T extends Event> void execute(Enum<?> eventType, T event);

    public abstract <T extends Event> void registerCallback(Enum<?> eventType, Consumer<T> callback);
}