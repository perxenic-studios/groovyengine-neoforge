package io.github.luckymcdev.groovyengine.core.client.editor.windows;

import imgui.ImGui;
import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.construct.client.MovementController;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.core.registry.ModAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class MovementDebuggingWindow extends EditorWindow {
    private LocalPlayer player = Minecraft.getInstance().player;

    private int[] flySpeedInt = new int[]{(int)(player.getData(ModAttachmentTypes.FLY_SPEED) * 100)};

    public MovementDebuggingWindow() {
        super("Movement Debugging", "movement_debug");
    }

    @Override
    public void render(ImGuiIO io) {
        if (ImGui.begin(title)) {

            if(ImGui.button("Toggle FlyingChanges")) {
                MovementController.toggleFlyingChanges();
            }

            ImGui.sameLine();
            ImGui.text(""+MovementController.flyingChangesEnabled());

            if (ImGui.sliderInt("Fly Speed", flySpeedInt, 0, 100)) {
                player.setData(ModAttachmentTypes.FLY_SPEED, ( flySpeedInt[0] / 100.0f ) );
            }

            ImGui.separator();

            if(ImGui.button("Toggle NoClip")) {
                MovementController.toggleNoClip();
            }

        }
        ImGui.end();
    }
}