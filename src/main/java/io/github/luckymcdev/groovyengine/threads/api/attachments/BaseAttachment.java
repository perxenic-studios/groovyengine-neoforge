package io.github.luckymcdev.groovyengine.threads.api.attachments;

import java.util.List;

public interface BaseAttachment {
    /**
     * Get the primary target this attachment is for
     */
    default Object getTarget() {
        return null;
    }

    /**
     * Get all targets this attachment applies to (for multi-target attachments)
     */
    default List<Object> getTargets() {
        return getTarget() != null ? List.of(getTarget()) : List.of();
    }

    /**
     * Check if this attachment applies to the given target
     */
    default boolean appliesTo(Object target) {
        return getTargets().contains(target);
    }

    /**
     * Called when the attachment manager is initialized, allowing attachments to initialize themselves if needed.
     */
    default void onInit() {
    }

    /**
     * Called when the attachment manager is being shut down, allowing attachments to clean up after themselves if needed.
     */
    default void onDestroy() {
    }
}