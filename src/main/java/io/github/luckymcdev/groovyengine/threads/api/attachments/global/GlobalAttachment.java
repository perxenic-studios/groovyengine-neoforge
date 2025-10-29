package io.github.luckymcdev.groovyengine.threads.api.attachments.global;

import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;

/**
 * Marker interface for global attachments that apply to all instances of their target type.
 * These attachments don't target specific objects but respond to global events.
 */
public interface GlobalAttachment<T> extends BaseAttachment<T> {

    @Override
    default boolean appliesTo(T target) {
        return true; // Global attachments apply to everything
    }
}