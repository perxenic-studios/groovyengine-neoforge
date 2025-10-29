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

package io.github.luckymcdev.groovyengine.core.client.editor.core.window;

import imgui.ImGuiIO;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


/**
 * Base class for all editor windows.
 * Subclasses should override the {@link #render(ImGuiIO)} method to render the window content.
 */
@OnlyIn(Dist.CLIENT)
public abstract class EditorWindow {
    protected final String title;
    protected final String id;
    protected boolean enabled = false;

    public EditorWindow(String title, String id) {
        this.title = title;
        this.id = id;
    }

    public EditorWindow(String title) {
        this(title, title.toLowerCase().replace(' ', '_'));
    }

    /**
     * Called every frame to render the window content
     * @param io the ImGuiIo
     */
    public abstract void render(ImGuiIO io);

    /**
     * Called when the window is first opened
     */
    public void onOpen() {
    }

    /**
     * Called when the window is closed
     */
    public void onClose() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            if (enabled) {
                onOpen();
            } else {
                onClose();
            }
            this.enabled = enabled;
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }
}