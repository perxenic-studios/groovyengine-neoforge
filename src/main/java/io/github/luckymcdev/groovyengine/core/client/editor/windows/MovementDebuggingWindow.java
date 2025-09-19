package io.github.luckymcdev.groovyengine.core.client.editor.windows;

import imgui.ImGui;
import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.construct.client.FlightController;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;

public class MovementDebuggingWindow extends EditorWindow {
    private int[] flySpeedInt = new int[]{(int)(FlightController.CUSTOM_FLY_SPEED * 100)};

    public MovementDebuggingWindow() {
        super("Movement Debugging", "movement_debug");
    }

    @Override
    public void render(ImGuiIO io) {
        if (ImGui.begin(title)) {
            if (ImGui.sliderInt("Fly Speed", flySpeedInt, 0, 100)) {
                FlightController.CUSTOM_FLY_SPEED = flySpeedInt[0] / 100.0f;
            }
        }
        ImGui.end();
    }
}