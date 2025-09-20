package io.github.luckymcdev.groovyengine.scribe.client.editor;

import imgui.ImGui;
import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.util.ImUtil;

public class ScribeWindow extends EditorWindow {

    public ScribeWindow() {
        super("Scribe Window");
    }

    @Override
    public void render(ImGuiIO io) {
        ImUtil.window("Scribe Window", () -> {

            ImGui.text("The custom Ui Editor for Chest UIs");

        });
    }
}
