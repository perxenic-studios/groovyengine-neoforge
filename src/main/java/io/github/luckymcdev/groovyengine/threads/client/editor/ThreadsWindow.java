package io.github.luckymcdev.groovyengine.threads.client.editor;

import imgui.ImGui;
import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.util.ImUtil;

public class ThreadsWindow extends EditorWindow {

    public ThreadsWindow() {
        super("Threads Window");
    }

    @Override
    public void render(ImGuiIO io) {

        ImUtil.window("Threads Window", () -> {
            // widgets go here
            ImGui.text("Hello World");

            ImUtil.button("title", () -> {
                ImGui.text("hello");
            });

        });



    }
}
