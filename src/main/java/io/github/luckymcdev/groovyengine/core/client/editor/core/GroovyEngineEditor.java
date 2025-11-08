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

package io.github.luckymcdev.groovyengine.core.client.editor.core;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * The main class for the Construct Editor client-side mod.
 * This class is responsible for rendering the editor user interface and handling
 * user input events.
 */
@OnlyIn(Dist.CLIENT)
public class GroovyEngineEditor {
    private GroovyEngineEditor() {}

    private static EditorState editorState = EditorState.DISABLED;

    /**
     * Renders the Construct Editor user interface.
     * This method is responsible for rendering all editor windows and handling
     * user input events.
     */
    public static void render() {

    }

    /**
     * Gets the current state of the Construct Editor.
     *
     * @return The current state of the Construct Editor.
     */
    public static EditorState getEditorState() {
        return editorState;
    }


    /**
     * Sets the current state of the Construct Editor.
     *
     * @param editorState The new state of the Construct Editor.
     */
    public static void setEditorState(EditorState editorState) {
        GroovyEngineEditor.editorState = editorState;
    }
}
