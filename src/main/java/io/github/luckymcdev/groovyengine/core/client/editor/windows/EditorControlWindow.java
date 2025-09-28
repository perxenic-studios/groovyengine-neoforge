package io.github.luckymcdev.groovyengine.core.client.editor.windows;

import imgui.ImGui;
import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.core.client.editor.core.EditorScreen;
import io.github.luckymcdev.groovyengine.core.client.editor.core.EditorState;
import io.github.luckymcdev.groovyengine.core.client.editor.core.GroovyEngineEditor;
import io.github.luckymcdev.groovyengine.core.client.editor.core.KeybindManager;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditorControlWindow extends EditorWindow {

    public EditorControlWindow() {
        super("Editor Control", "editor_control");
    }

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

                if (ImGui.button("Close Current Screen")) {
                    if (Minecraft.getInstance().screen != null) {
                        Minecraft.getInstance().setScreen(null);
                    }
                }
            }
        }
        ImGui.end();
    }
}