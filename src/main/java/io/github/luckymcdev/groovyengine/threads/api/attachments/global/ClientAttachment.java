
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

package io.github.luckymcdev.groovyengine.threads.api.attachments.global;

/**
 * Attachment for client-side only events.
 * Use this for UI, rendering, input handling, etc.
 */
public abstract class ClientAttachment implements GlobalAttachment<Void> {

    /**
     * Called when the client starts up
     */
    public void onClientStart() {
    }

    /**
     * Called every client tick
     */
    public void onClientTick() {
    }

    /**
     * Called when a key is pressed, released, or held
     */
    public void onKeyPress(int key, int action, int modifiers) {
    }

    /**
     * Called when a mouse button is clicked
     */
    public void onMouseClick(int button, int action, int modifiers) {
    }

    /**
     * Called when the mouse wheel is scrolled
     */
    public void onMouseScroll(double horizontal, double vertical) {
    }
}