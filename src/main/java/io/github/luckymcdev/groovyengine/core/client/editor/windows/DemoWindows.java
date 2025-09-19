package io.github.luckymcdev.groovyengine.core.client.editor.windows;

import imgui.ImGui;
import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;

public class DemoWindows {

    public static class AboutWindow extends EditorWindow {
        public AboutWindow() {
            super("About", "about");
        }

        @Override
        public void render(ImGuiIO io) {
            ImGui.showAboutWindow();
        }
    }

    public static class DemoWindow extends EditorWindow {
        public DemoWindow() {
            super("Demo", "demo");
        }

        @Override
        public void render(ImGuiIO io) {
            ImGui.showDemoWindow();
        }
    }

    public static class MetricsWindow extends EditorWindow {
        public MetricsWindow() {
            super("Metrics", "metrics");
        }

        @Override
        public void render(ImGuiIO io) {
            ImGui.showMetricsWindow();
        }
    }
}