package io.github.luckymcdev.groovyengine.construct.client.editor;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.type.ImBoolean;
import io.github.luckymcdev.groovyengine.construct.client.movement.MovementController;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.util.ImUtil;
import io.github.luckymcdev.groovyengine.core.registry.ModAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConstructChanges extends EditorWindow {
    private final LocalPlayer player = Minecraft.getInstance().player;
    private final int[] flySpeedInt = new int[]{(int)(player.getData(ModAttachmentTypes.FLY_SPEED) * 100)};

    private final ImBoolean blockUpdatesEnabled = new ImBoolean(true);
    private final ImBoolean instantPlacement = new ImBoolean(false);
    private final ImBoolean extendedReach = new ImBoolean(false);
    private final ImBoolean flyingChanges = new ImBoolean(true);

    public ConstructChanges() {
        super("Construct Changes");
    }

    @Override
    public void render(ImGuiIO io) {
        ImUtil.window("Construct Controls", () -> {
            // Building Settings Section
            ImUtil.title("Building Settings:");

            ImUtil.checkbox("Block Updates", blockUpdatesEnabled, () -> {
                System.out.println("Block updates: " + blockUpdatesEnabled.get());
            });
            ImUtil.helpMarker("Makes the game not send Block Updates when placing blocks");

            ImUtil.checkbox("Instant Placement", instantPlacement, () -> {
                System.out.println("Instant placement: " + instantPlacement.get());
            });
            ImUtil.helpMarker("Makes the game instantly place blocks, no cooldown.");

            ImUtil.checkbox("Extended Reach", extendedReach, () -> {
                System.out.println("Extended reach: " + extendedReach.get());
            });
            ImUtil.helpMarker("Enables extended reach, from 3 blocks to 10");

            // Movement Settings Section
            ImUtil.title("Movement Settings:");

            ImUtil.checkbox("Flying Changes", flyingChanges, MovementController::toggleFlyingChanges);
            ImUtil.helpMarker("Makes Flying stop abruptly instead of gliding weirdly like ice.");


            if (ImGui.sliderInt("Fly Speed", flySpeedInt, 0, 100)) {
                player.setData(ModAttachmentTypes.FLY_SPEED, (flySpeedInt[0] / 100.0f));
            }
            ImUtil.helpMarker("The actual speed of flight, an int between 0 and 100, 0 meaning not moving.");
        });
    }

    public boolean isBlockUpdatesEnabled() { return blockUpdatesEnabled.get(); }
    public boolean isInstantPlacementEnabled() { return instantPlacement.get(); }
    public boolean isExtendedReachEnabled() { return extendedReach.get(); }
}