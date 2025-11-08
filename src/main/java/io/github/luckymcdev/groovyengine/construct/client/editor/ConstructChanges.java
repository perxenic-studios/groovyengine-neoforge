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

package io.github.luckymcdev.groovyengine.construct.client.editor;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.type.ImBoolean;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.construct.client.movement.MovementController;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.ImGe;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import io.github.luckymcdev.groovyengine.core.registry.ModAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * A configuration window for Construct mod features and client-side modifications.
 * This window provides real-time control over various gameplay enhancements including
 * building optimizations and movement modifications.
 *
 * <p><b>Features Include:</b></p>
 * <ul>
 *   <li>Block placement optimizations</li>
 *   <li>Movement system enhancements</li>
 *   <li>Flight control adjustments</li>
 *   <li>Reach distance modifications</li>
 * </ul>
 *
 * <p><b>Usage:</b> This window is typically accessed through the main editor interface
 * and provides immediate feedback for all configuration changes.</p>
 */
@OnlyIn(Dist.CLIENT)
public class ConstructChanges extends EditorWindow {

    /**
     * Reference to the local player entity for applying real-time modifications.
     */
    private final LocalPlayer player = Minecraft.getInstance().player;

    /**
     * Integer array wrapper for fly speed slider control.
     * Stores the current fly speed as a percentage (0-100).
     * Synchronized with {@link ModAttachmentTypes#FLY_SPEED} attachment data.
     */
    private final int[] flySpeedInt = new int[]{(int) (player.getData(ModAttachmentTypes.FLY_SPEED) * 100)};

    // ===== CONFIGURATION TOGGLES =====

    /**
     * Toggles block updates when placing blocks.
     * When disabled, prevents the game from sending block updates, which can
     * improve performance during large-scale building operations.
     */
    private final ImBoolean blockUpdatesEnabled = new ImBoolean(true);

    /**
     * Toggles instant block placement without cooldown.
     * When enabled, allows placing multiple blocks simultaneously without delay.
     */
    private final ImBoolean instantPlacement = new ImBoolean(false);

    /**
     * Toggles extended reach distance for block interaction.
     * When enabled, increases block interaction range from 3 blocks to 10 blocks.
     */
    private final ImBoolean extendedReach = new ImBoolean(false);

    /**
     * Toggles flying movement modifications.
     * When enabled, provides more responsive flight controls with immediate stopping.
     */
    private final ImBoolean flyingChanges = new ImBoolean(true);

    /**
     * Constructs a new ConstructChanges configuration window.
     * Initializes the window with a settings icon and title.
     */
    public ConstructChanges() {
        super(ImIcons.SETTINGS.get() + " Construct Changes");
    }

    /**
     * Renders the Construct Changes configuration interface.
     * This method is called every frame to update the UI and handle user interactions.
     *
     * @param io the ImGui IO context for handling input/output operations
     * @see ImGuiIO
     */
    @Override
    public void render(ImGuiIO io) {
        ImGe.window(title, () -> {
            renderBuildingSettingsSection();
            renderMovementSettingsSection();
        });
    }

    /**
     * Renders the Building Settings section of the configuration interface.
     * Contains controls for block placement behavior and building optimizations.
     */
    private void renderBuildingSettingsSection() {
        ImGe.title(ImIcons.WIDGETS.get() + " Building Settings");

        // Block Updates Toggle
        ImGe.checkbox(ImIcons.BLOCK.get() + " Block Updates", blockUpdatesEnabled, () ->
            GE.CONSTRUCT_LOG.info("Block updates: " + blockUpdatesEnabled.get())
        );
        ImGe.helpMarker("Disable block updates when placing blocks. This can significantly improve performance during large-scale building operations but may cause rendering issues with blocks that depend on updates (like redstone).");

        // Instant Placement Toggle
        ImGe.checkbox(ImIcons.ADD.get() + " Instant Placement", instantPlacement, () ->
                GE.CONSTRUCT_LOG.info("Instant placement: " + instantPlacement.get())
        );
        ImGe.helpMarker("Allows placing multiple blocks simultaneously without delay. Useful for fast construction but may feel less vanilla.");

        // Extended Reach Toggle
        ImGe.checkbox(ImIcons.ARROW_RIGHT.get() + " Extended Reach", extendedReach, () ->
                GE.CONSTRUCT_LOG.info("Extended reach: " + extendedReach.get())
        );
        ImGe.helpMarker("Extends block interaction range from 3 blocks to 10 blocks. Useful for building tall structures or making precise placements from a distance. May affect PvP balance if enabled in multiplayer.");
    }

    /**
     * Renders the Movement Settings section of the configuration interface.
     * Contains controls for flight behavior and movement modifications.
     */
    private void renderMovementSettingsSection() {
        ImGe.title(ImIcons.SPEED.get() + " Movement Settings");

        // Flying Changes Toggle
        ImGe.checkbox(ImIcons.ROCKET.get() + " Flying Changes", flyingChanges, MovementController::toggleFlyingChanges);
        ImGe.helpMarker("Improves flight responsiveness by eliminating momentum-based sliding. When enabled, flying stops immediately when movement keys are released. Provides more precise control for building and navigation.");

        // Fly Speed Slider
        if (ImGui.sliderInt(ImIcons.SPEED.get() + " Fly Speed", flySpeedInt, 0, 100)) {
            // Convert percentage to decimal and update player data
            float flySpeedDecimal = flySpeedInt[0] / 100.0f;
            player.setData(ModAttachmentTypes.FLY_SPEED, flySpeedDecimal);

            // Optional: Add visual feedback or logging
            GE.CONSTRUCT_LOG.info("Fly speed updated to: " + flySpeedInt[0] + "%");
        }
        ImGe.helpMarker("Controls flight movement speed as a percentage of maximum. 0% = No movement, 100% = Maximum flight speed. Adjust for precise building control or fast travel.");
    }

    // ===== PUBLIC ACCESSOR METHODS =====

    /**
     * Returns whether block updates are currently enabled.
     *
     * @return true if block updates are enabled, false if disabled
     */
    public boolean isBlockUpdatesEnabled() {
        return blockUpdatesEnabled.get();
    }

    /**
     * Returns whether instant block placement is currently enabled.
     *
     * @return true if instant placement is enabled, false if using vanilla cooldown
     */
    public boolean isInstantPlacementEnabled() {
        return instantPlacement.get();
    }

    /**
     * Returns whether extended reach is currently enabled.
     *
     * @return true if extended reach (10 blocks) is enabled, false for vanilla (3 blocks)
     */
    public boolean isExtendedReachEnabled() {
        return extendedReach.get();
    }

    /**
     * Returns whether flying movement modifications are currently enabled.
     *
     * @return true if enhanced flight controls are enabled, false for vanilla flight
     */
    public boolean isFlyingChangesEnabled() {
        return flyingChanges.get();
    }

    /**
     * Returns the current fly speed as a percentage value.
     *
     * @return fly speed percentage (0-100)
     */
    public int getFlySpeedPercentage() {
        return flySpeedInt[0];
    }

    /**
     * Returns the current fly speed as a decimal multiplier.
     *
     * @return fly speed multiplier (0.0 - 1.0)
     */
    public float getFlySpeedMultiplier() {
        return flySpeedInt[0] / 100.0f;
    }
}