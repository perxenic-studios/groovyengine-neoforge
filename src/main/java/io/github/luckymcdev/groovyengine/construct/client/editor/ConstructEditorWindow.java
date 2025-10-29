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

import imgui.ImGuiIO;
import imgui.type.ImInt;
import imgui.type.ImString;
import io.github.luckymcdev.groovyengine.construct.client.rendering.SelectionRenderer;
import io.github.luckymcdev.groovyengine.construct.core.history.HistoryManager;
import io.github.luckymcdev.groovyengine.construct.core.pattern.BlockPattern;
import io.github.luckymcdev.groovyengine.construct.core.placement.AsyncBlockPlacer;
import io.github.luckymcdev.groovyengine.construct.core.selection.Selection;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.ImGe;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;

@OnlyIn(Dist.CLIENT)
public class ConstructEditorWindow extends EditorWindow {

    private static final Minecraft mc = Minecraft.getInstance();
    private static final String[] PATTERN_TYPES = {
            "Single Block", "Random Mix", "Checkerboard", "Layers", "Weighted Random", "Gradient"
    };
    private static final String[] SHAPE_TYPES = {
            "Fill", "Hollow", "Walls", "Outline", "Sphere", "Cylinder"
    };
    private final Selection selectionManager = new Selection();
    private final AsyncBlockPlacer blockPlacer = AsyncBlockPlacer.getInstance();
    private final HistoryManager historyManager = new HistoryManager();
    // UI State
    private final ImString blockIdInput = new ImString("minecraft:stone", 256);
    private final ImInt blocksPerTick = new ImInt(blockPlacer.getBlocksPerTick());
    private final ImInt updatesPerTick = new ImInt(blockPlacer.getUpdatesPerTick());
    private final ImString pos1Display = new ImString("Not set", 50);
    private final ImString pos2Display = new ImString("Not set", 50);
    // Pattern controls
    private final ImInt patternType = new ImInt(0);
    private final ImString secondaryBlockInput = new ImString("minecraft:oak_planks", 256);
    private final ImInt checkerboardScale = new ImInt(2);
    private final ImInt layerThickness = new ImInt(1);
    private final ImString weightedBlocks = new ImString("minecraft:stone,50,minecraft:cobblestone,30,minecraft:andesite,20", 512);
    // Shape controls
    private final ImInt shapeType = new ImInt(0);
    private final ImInt hollowThickness = new ImInt(1);
    private final ImInt sphereRadius = new ImInt(5);

    public ConstructEditorWindow() {
        super(ImIcons.WRENCH.get() + " Construct Editor");
        NeoForge.EVENT_BUS.register(new SelectionRenderer(selectionManager));
    }

    /**
     * Renders the Construct Editor window.
     * This method is called every frame to update the UI and handle user interactions.
     * It renders a window with multiple sections including selection information, pattern configuration,
     * shape options, operation controls, history management, and statistics.
     *
     * @param io the ImGui IO context for handling input/output operations
     * @see ImGuiIO
     */
    @Override
    public void render(ImGuiIO io) {
        ImGe.window(title, () -> {
            renderSelectionSection();
            renderPatternSection();
            renderShapeSection();
            renderOperationsSection();
            renderHistorySection();
            renderStatsSection();
        });
    }

    /**
     * Renders the selection section in the Construct Editor window.
     * This section displays information about the current selection and provides buttons to set the position 1 and 2, clear the selection, and expand the selection.
     */
    private void renderSelectionSection() {
        // Selection header
        ImGe.collapsingHeader(ImIcons.SELECT_ALL.get() + " Selection", () -> {
            // Display positions
            ImGe.text("Position 1: " + pos1Display.get());
            ImGe.text("Position 2: " + pos2Display.get());

            // Set position buttons
            if (ImGe.button("Set Pos1")) setPositionFromLookingAt(true);
            ImGe.sameLine();
            if (ImGe.button("Set Pos2")) setPositionFromLookingAt(false);

            // Clear selection button
            if (ImGe.button("Clear Selection")) {
                selectionManager.clearSelection();
                updatePositionDisplays();
            }

            // Expand selection button
            ImGe.sameLine();
            if (ImGe.button("Expand Selection")) expandSelection();

            // Display selection size and dimensions if valid
            if (selectionManager.hasValidSelection()) {
                ImGe.text("Selection Size: " + selectionManager.getSelectionSize() + " blocks");
                ImGe.text("Dimensions: " + selectionManager.getDimensions());
            }
        });
    }

    /**
     * Renders the Pattern Configuration section in the Construct Editor window.
     * This section allows the user to select the block placement pattern and configure relevant inputs.
     * The available patterns are Single Block, Random Mix, Checkerboard, Layers, Weighted Random, Sphere, and Cylinder.
     * Each pattern has its own set of relevant inputs and buttons to perform the operation.
     */
    private void renderPatternSection() {
        if (ImGe.collapsingHeader("Pattern Configuration", true)) {
            ImGe.combo("Pattern Type", patternType, PATTERN_TYPES);
            ImGe.helpMarker("Select the block placement pattern");

            ImGe.inputText("Primary Block", blockIdInput);

            int currentPattern = patternType.get();

            // Show relevant inputs based on pattern type
            switch (currentPattern) {
                case 1: // Random Mix
                case 5: // Gradient
                    ImGe.inputText("Secondary Block", secondaryBlockInput);
                    break;

                case 2: // Checkerboard
                    ImGe.inputText("Secondary Block", secondaryBlockInput);
                    ImGe.sliderInt("Scale", checkerboardScale.getData(), 1, 10);
                    break;

                case 3: // Layers
                    ImGe.inputText("Secondary Block", secondaryBlockInput);
                    ImGe.sliderInt("Layer Thickness", layerThickness.getData(), 1, 10);
                    break;

                case 4: // Weighted Random
                    ImGe.inputText("Weighted Blocks", weightedBlocks);
                    ImGe.helpMarker("Format: block1,weight1,block2,weight2,block3,weight3\nExample: minecraft:stone,50,minecraft:cobblestone,30,minecraft:andesite,20");
                    break;
            }
        }
    }

    /**
     * Renders the Shape Options section of the Construct Editor window.
     * This section includes controls for selecting the shape operation to perform on the current selection.
     * The available shape operations are Fill, Hollow, Walls, Outline, Sphere, and Cylinder.
     * Each shape operation has its own set of relevant inputs and buttons to perform the operation.
     */
    private void renderShapeSection() {
        if (ImGe.collapsingHeader("Shape Options", true)) {
            ImGe.combo("Shape", shapeType, SHAPE_TYPES);

            int currentShape = shapeType.get();

            switch (currentShape) {
                case 1: // Hollow
                case 2: // Walls
                    ImGe.sliderInt("Thickness", hollowThickness.getData(), 1, 5);
                    break;

                case 4: // Sphere
                    ImGe.sliderInt("Radius", sphereRadius.getData(), 1, 50);
                    if (ImGe.button("Create Sphere at Pos1")) createSphere();
                    break;

                case 5: // Cylinder
                    ImGe.sliderInt("Radius", sphereRadius.getData(), 1, 50);
                    if (ImGe.button("Create Cylinder")) createCylinder();
                    ImGe.helpMarker("Creates a vertical cylinder between Pos1 and Pos2");
                    break;
            }
        }
    }

    /**
     * Renders the Operations section of the Construct Editor window.
     * This section includes buttons to execute a queued operation, clear the current selection, and replace blocks in the selection.
     */
    private void renderOperationsSection() {
        if (ImGe.collapsingHeader("Operations", true)) {
            if (ImGe.button("Execute Operation")) executeOperation();
            ImGe.sameLine();
            if (ImGe.button("Clear Selection")) clearSelection();

            ImGe.separator();

            if (ImGe.button("Replace Blocks")) replaceBlocks();
            ImGe.helpMarker("Replace primary block with secondary block in selection");

            /*
            ImGe.separator();

            if (ImGe.sliderInt("Blocks/Tick", blocksPerTick.getData(), 1, 100_000)) {
                blockPlacer.setBlocksPerTick(blocksPerTick.get());
            }
            if (ImGe.sliderInt("Updates/Tick", updatesPerTick.getData(), 0, 50_000)) {
                blockPlacer.setUpdatesPerTick(updatesPerTick.get());
            }
             */
        }
    }

    /**
     * Renders the History section of the Construct Editor window.
     * This section includes buttons to undo and redo the last operation, as well as a button to clear the entire history.
     * It also displays the current history size and whether or not undo and redo are available.
     */
    private void renderHistorySection() {
        if (ImGe.collapsingHeader("History", true)) {
            boolean canUndo = historyManager.canUndo();
            boolean canRedo = historyManager.canRedo();

            if (!canUndo) ImGe.beginDisabled();
            if (ImGe.button("Undo")) undo();
            if (!canUndo) ImGe.endDisabled();

            ImGe.sameLine();

            if (!canRedo) ImGe.beginDisabled();
            if (ImGe.button("Redo")) redo();
            if (!canRedo) ImGe.endDisabled();

            ImGe.sameLine();
            if (ImGe.button("Clear History")) historyManager.clearHistory();

            ImGe.text("History Size: " + historyManager.getHistorySize());
            ImGe.text("Can Undo: " + (canUndo ? "Yes" : "No"));
            ImGe.text("Can Redo: " + (canRedo ? "Yes" : "No"));
        }
    }

    /**
     * Renders the Statistics section of the Construct Editor window.
     * Displays the current queue sizes, blocks per tick, updates per tick, and current FPS.
     * Also includes a button to clear the queues.
     */
    private void renderStatsSection() {
        if (ImGe.collapsingHeader("Statistics", true)) {
            ImGe.text("Queued Placements: " + blockPlacer.getQueuedPlacements());
            ImGe.text("Queued Updates: " + blockPlacer.getQueuedUpdates());
            ImGe.separator();
            ImGe.text("Blocks/Tick: " + blockPlacer.getBlocksPerTick());
            ImGe.text("Updates/Tick: " + blockPlacer.getUpdatesPerTick());
            ImGe.text("FPS: " + Minecraft.getInstance().getFps());

            if (ImGe.button("Clear Queue")) blockPlacer.clearQueues();
        }
    }

    /**
     * Sets the current position of the player to either Pos1 or Pos2, depending on the value of pos1.
     * The position is determined by the block that the player is currently looking at.
     * If the player is not looking at a block, or if the level is null, does nothing.
     * Updates the position displays after setting the position.
     *
     * @param pos1 If true, sets the position to Pos1. If false, sets the position to Pos2.
     */
    private void setPositionFromLookingAt(boolean pos1) {
        if (mc.hitResult == null || mc.level == null) return;

        BlockPos pos = BlockPos.containing(mc.hitResult.getLocation());
        if (pos1) selectionManager.setPos1(pos);
        else selectionManager.setPos2(pos);

        updatePositionDisplays();
    }

    /**
     * Updates the display strings of the current position 1 and position 2.
     * If either position is not set, sets the display string to "Not set".
     */
    private void updatePositionDisplays() {
        BlockPos pos1 = selectionManager.getPos1();
        BlockPos pos2 = selectionManager.getPos2();

        pos1Display.set(pos1 != null ? pos1.toShortString() : "Not set");
        pos2Display.set(pos2 != null ? pos2.toShortString() : "Not set");
    }

    /**
     * Executes the current shape operation on the selection area.
     * If the selection area is not valid, prints an error message and returns.
     * If the pattern cannot be created, returns without performing the operation.
     * Saves the current state of blocks before performing the operation.
     * Applies the shape transformation to the selection area.
     */
    private void executeOperation() {
        if (!selectionManager.hasValidSelection()) {
            System.out.println("No valid selection!");
            return;
        }

        BlockPattern pattern = createPattern();
        if (pattern == null) return;

        // Save current state for undo
        historyManager.saveState(selectionManager);

        // Apply shape transformation
        applyShapeOperation(pattern);
    }

    /**
     * Creates a BlockPattern based on the current values of the blockIdInput, patternType, and other relevant inputs.
     * The created pattern is then used to perform the shape operation.
     * If the pattern cannot be created for any reason, null is returned.
     *
     * @return A BlockPattern representing the created pattern, or null if the pattern cannot be created.
     */
    private BlockPattern createPattern() {
        Block primaryBlock = parseBlock(blockIdInput.get());
        if (primaryBlock == null) return null;

        int currentPattern = patternType.get();

        try {
            switch (currentPattern) {
                case 0: // Single Block
                    return new BlockPattern.SingleBlockPattern(primaryBlock);

                case 1: // Random Mix
                    Block secondaryBlock = parseBlock(secondaryBlockInput.get());
                    if (secondaryBlock == null) return null;
                    return new BlockPattern.RandomPattern(primaryBlock, secondaryBlock);

                case 2: // Checkerboard
                    secondaryBlock = parseBlock(secondaryBlockInput.get());
                    if (secondaryBlock == null) return null;
                    return new BlockPattern.CheckerboardPattern(
                            primaryBlock.defaultBlockState(),
                            secondaryBlock.defaultBlockState(),
                            checkerboardScale.get()
                    );

                case 3: // Layers
                    secondaryBlock = parseBlock(secondaryBlockInput.get());
                    if (secondaryBlock == null) return null;
                    return new BlockPattern.LayeredPattern(
                            primaryBlock.defaultBlockState(),
                            secondaryBlock.defaultBlockState(),
                            layerThickness.get()
                    );

                case 4: // Weighted Random
                    return parseWeightedPattern();

                case 5: // Gradient
                    secondaryBlock = parseBlock(secondaryBlockInput.get());
                    if (secondaryBlock == null) return null;
                    return new BlockPattern.GradientPattern(
                            primaryBlock.defaultBlockState(),
                            secondaryBlock.defaultBlockState()
                    );

                default:
                    return new BlockPattern.SingleBlockPattern(primaryBlock);
            }
        } catch (Exception e) {
            System.out.println("Error creating pattern: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parses a weighted pattern from the given string.
     * The string should be in the format of "block1,weight1,block2,weight2,...".
     * Each block should be a valid block ID, and each weight should be a valid float value.
     * If the string is invalid, returns null.
     *
     * @return A BlockPattern representing the parsed weighted pattern, or null if the string is invalid.
     */
    private BlockPattern parseWeightedPattern() {
        String[] parts = weightedBlocks.get().split(",");
        if (parts.length < 2 || parts.length % 2 != 0) {
            System.out.println("Invalid weighted pattern format");
            return null;
        }

        Object[] blocks = new Object[parts.length];
        for (int i = 0; i < parts.length; i += 2) {
            Block block = parseBlock(parts[i].trim());
            if (block == null) return null;

            try {
                float weight = Float.parseFloat(parts[i + 1].trim());
                blocks[i] = block;
                blocks[i + 1] = weight;
            } catch (NumberFormatException e) {
                System.out.println("Invalid weight value: " + parts[i + 1]);
                return null;
            }
        }

        return BlockPattern.WeightedPattern.fromBlocks(blocks);
    }

    /**
     * Applies a shape operation on the current selection using the given pattern.
     * The type of operation is determined by the current value of shapeType.
     * Saves the current state before performing the operation.
     *
     * @param pattern The BlockPattern to use when performing the operation
     */
    private void applyShapeOperation(BlockPattern pattern) {
        int currentShape = shapeType.get();

        switch (currentShape) {
            case 0: // Fill
                blockPlacer.fillSelection(selectionManager, pattern);
                break;

            case 1: // Hollow
                blockPlacer.fillHollow(selectionManager, pattern, hollowThickness.get());
                break;

            case 2: // Walls
                blockPlacer.fillWalls(selectionManager, pattern);
                break;

            case 3: // Outline
                blockPlacer.fillOutline(selectionManager, pattern);
                break;

            default:
                blockPlacer.fillSelection(selectionManager, pattern);
                break;
        }

        System.out.println("Started operation on " + selectionManager.getSelectionSize() + " blocks");
    }

    /**
     * Clears the current selection by replacing all blocks with air.
     * Saves the current state before performing the operation.
     */
    private void clearSelection() {
        if (!selectionManager.hasValidSelection()) return;

        historyManager.saveState(selectionManager);

        BlockPattern pattern = new BlockPattern.SingleBlockPattern(Blocks.AIR);
        blockPlacer.fillSelection(selectionManager, pattern);

        System.out.println("Started clearing " + selectionManager.getSelectionSize() + " blocks");
    }

    /**
     * Replaces all blocks of one type with another in a selection.
     * Saves the current state before performing the operation.
     */
    private void replaceBlocks() {
        if (!selectionManager.hasValidSelection()) return;

        Block targetBlock = parseBlock(blockIdInput.get());
        Block replacementBlock = parseBlock(secondaryBlockInput.get());

        if (targetBlock == null || replacementBlock == null) return;

        historyManager.saveState(selectionManager);

        blockPlacer.replaceBlocks(selectionManager, targetBlock, replacementBlock);

        System.out.println("Replacing " + blockIdInput.get() + " with " + secondaryBlockInput.get());
    }

    /**
     * Expands the current selection by 1 block in all directions.
     * Useful for quickly expanding a selection to encompass a larger area.
     */
    private void expandSelection() {
        if (!selectionManager.hasValidSelection()) return;

        selectionManager.expand(1);
        updatePositionDisplays();

        System.out.println("Expanded selection to " + selectionManager.getSelectionSize() + " blocks");
    }

    /**
     * Creates a sphere at the specified center with the given radius.
     * The sphere is filled with blocks from a specified pattern.
     *
     * @throws NullPointerException if Pos1 is not set
     */
    private void createSphere() {
        BlockPos center = selectionManager.getPos1();
        if (center == null) {
            System.out.println("Set Pos1 first!");
            return;
        }

        BlockPattern pattern = createPattern();
        if (pattern == null) return;

        historyManager.saveState(selectionManager);

        blockPlacer.createSphere(center, sphereRadius.get(), pattern);

        System.out.println("Creating sphere with radius " + sphereRadius.get());
    }

    /**
     * Creates a cylinder between the two positions in the selection area.
     * The cylinder's radius is determined by the sphereRadius input.
     * The cylinder is filled with blocks from the pattern created by the createPattern method.
     */
    private void createCylinder() {
        if (!selectionManager.hasValidSelection()) {
            System.out.println("Set both positions first!");
            return;
        }

        BlockPattern pattern = createPattern();
        if (pattern == null) return;

        historyManager.saveState(selectionManager);

        blockPlacer.createCylinder(selectionManager.getPos1(), selectionManager.getPos2(),
                sphereRadius.get(), pattern);

        System.out.println("Creating cylinder with radius " + sphereRadius.get());
    }

    /**
     * Undoes the last operation.
     * <p>
     * This will revert the last operation, restoring the previous state.
     * If there are no operations to undo, this will do nothing.
     */
    private void undo() {
        if (!historyManager.canUndo()) return;

        historyManager.undo(blockPlacer);
        System.out.println("Undo completed");
    }

    /**
     * Redoes the last operation.
     *
     * @see #undo()
     */
    private void redo() {
        if (!historyManager.canRedo()) return;

        historyManager.redo(blockPlacer);
        System.out.println("Redo completed");
    }

    /**
     * Parses a block ID and returns the corresponding block.
     *
     * @param blockId The block ID to parse. Can be in the format of "namespace:blockname" or "namespace:blockname[state]".
     * @return The parsed block, or null if the block ID is invalid.
     */
    private Block parseBlock(String blockId) {
        try {
            var loc = ResourceLocation.tryParse(blockId);
            if (loc == null) throw new IllegalArgumentException("Invalid block ID");

            var block = BuiltInRegistries.BLOCK.get(loc);
            if (block == null || (block == Blocks.AIR && !blockId.equals("minecraft:air"))) {
                System.out.println("Block not found: " + blockId);
                return null;
            }
            return block;
        } catch (Exception e) {
            System.out.println("Error parsing block ID: " + e.getMessage());
            return null;
        }
    }
}