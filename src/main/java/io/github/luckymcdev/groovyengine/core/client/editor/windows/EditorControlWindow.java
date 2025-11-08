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

package io.github.luckymcdev.groovyengine.core.client.editor.windows;

import imgui.ImGui;
import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.core.client.editor.core.EditorScreen;
import io.github.luckymcdev.groovyengine.core.client.editor.core.EditorState;
import io.github.luckymcdev.groovyengine.core.client.editor.core.GroovyEngineEditor;
import io.github.luckymcdev.groovyengine.core.client.editor.core.KeybindManager;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditorControlWindow extends EditorWindow {

    public EditorControlWindow() {
        super(ImIcons.SETTINGS.get() + " Editor Control", "editor_control");
    }

    /**
     * Renders the window.
     * <p>
     * This method renders the window and its contents.
     * It is called automatically by the ImGui library.
     *
     * @param io the ImGuiIO object that provides information about the ImGui library
     */
    @Override
    public void render(ImGuiIO io) {
        if (ImGui.begin(title)) {
            // Current state info
            ImGui.text("Current State: " + GroovyEngineEditor.getEditorState().toString());
            ImGui.separator();

            // Keybind information
            if (ImGui.collapsingHeader("Keybinds")) {
                ImGui.text("Toggle ImGui: " + KeybindManager.TOGGLE_IMGUI.getTranslatedKeyMessage().getString());
                ImGui.text("Open Editor Screen: " + KeybindManager.OPEN_EDITOR_SCREEN.getTranslatedKeyMessage().getString());
                ImGui.text("(Keybinds can be changed in Controls settings)");
            }

            ImGui.separator();

            // Manual controls
            if (ImGui.collapsingHeader("Manual Controls")) {
                if (ImGui.button("Toggle ImGui")) {
                    EditorState currentState = GroovyEngineEditor.getEditorState();
                    GroovyEngineEditor.setEditorState(
                            currentState == EditorState.ENABLED ? EditorState.DISABLED : EditorState.ENABLED
                    );
                }

                if (ImGui.button("Enable ImGui")) {
                    GroovyEngineEditor.setEditorState(EditorState.ENABLED);
                }

                if (ImGui.button("Disable ImGui")) {
                    GroovyEngineEditor.setEditorState(EditorState.DISABLED);
                }

                ImGui.separator();

                if (ImGui.button("Open Editor Screen (Non-Pausing)")) {
                    Minecraft.getInstance().setScreen(new EditorScreen());
                }

                if (ImGui.button("Close Current Screen") && Minecraft.getInstance().screen != null) {
                    Minecraft.getInstance().setScreen(null);
                }
            }
        }
        ImGui.end();
    }
}