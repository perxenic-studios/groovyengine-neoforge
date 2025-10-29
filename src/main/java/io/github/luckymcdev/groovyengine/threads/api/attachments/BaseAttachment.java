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

package io.github.luckymcdev.groovyengine.threads.api.attachments;

/**
 * Base interface for all attachments.
 * Attachments provide a way to add custom behavior to game objects without modifying their classes.
 *
 * @param <T> The type of object this attachment targets
 */
public interface BaseAttachment<T> {

    /**
     * Called when this attachment is registered with the attachment manager.
     * Use this to perform any initialization logic.
     */
    default void onInit() {
    }

    /**
     * Called when this attachment is unregistered from the attachment manager.
     * Use this to perform any cleanup logic.
     */
    default void onDestroy() {
    }

    /**
     * Determines if this attachment should handle events for the given target.
     *
     * @param target The object to check
     * @return true if this attachment applies to the target
     */
    boolean appliesTo(T target);

    /**
     * Get the priority of this attachment. Higher priority attachments are executed first.
     * Default priority is 0.
     *
     * @return The priority value
     */
    default int getPriority() {
        return 0;
    }
}