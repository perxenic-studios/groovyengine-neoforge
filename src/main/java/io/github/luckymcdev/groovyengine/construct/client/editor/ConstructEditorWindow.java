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

    private void renderSelectionSection() {
        ImGe.collapsingHeader(ImIcons.SELECT_ALL.get() + " Selection", () -> {
            ImGe.text("Position 1: " + pos1Display.get());
            ImGe.text("Position 2: " + pos2Display.get());

            if (ImGe.button("Set Pos1")) setPositionFromLookingAt(true);
            ImGe.sameLine();
            if (ImGe.button("Set Pos2")) setPositionFromLookingAt(false);

            if (ImGe.button("Clear Selection")) {
                selectionManager.clearSelection();
                updatePositionDisplays();
            }
            ImGe.sameLine();
            if (ImGe.button("Expand Selection")) expandSelection();

            if (selectionManager.hasValidSelection()) {
                ImGe.text("Selection Size: " + selectionManager.getSelectionSize() + " blocks");
                ImGe.text("Dimensions: " + selectionManager.getDimensions());
            }
        });
    }

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

    private void setPositionFromLookingAt(boolean pos1) {
        if (mc.hitResult == null || mc.level == null) return;

        BlockPos pos = BlockPos.containing(mc.hitResult.getLocation());
        if (pos1) selectionManager.setPos1(pos);
        else selectionManager.setPos2(pos);

        updatePositionDisplays();
    }

    private void updatePositionDisplays() {
        BlockPos pos1 = selectionManager.getPos1();
        BlockPos pos2 = selectionManager.getPos2();

        pos1Display.set(pos1 != null ? pos1.toShortString() : "Not set");
        pos2Display.set(pos2 != null ? pos2.toShortString() : "Not set");
    }

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

    private void clearSelection() {
        if (!selectionManager.hasValidSelection()) return;

        historyManager.saveState(selectionManager);

        BlockPattern pattern = new BlockPattern.SingleBlockPattern(Blocks.AIR);
        blockPlacer.fillSelection(selectionManager, pattern);

        System.out.println("Started clearing " + selectionManager.getSelectionSize() + " blocks");
    }

    private void replaceBlocks() {
        if (!selectionManager.hasValidSelection()) return;

        Block targetBlock = parseBlock(blockIdInput.get());
        Block replacementBlock = parseBlock(secondaryBlockInput.get());

        if (targetBlock == null || replacementBlock == null) return;

        historyManager.saveState(selectionManager);

        blockPlacer.replaceBlocks(selectionManager, targetBlock, replacementBlock);

        System.out.println("Replacing " + blockIdInput.get() + " with " + secondaryBlockInput.get());
    }

    private void expandSelection() {
        if (!selectionManager.hasValidSelection()) return;

        selectionManager.expand(1);
        updatePositionDisplays();

        System.out.println("Expanded selection to " + selectionManager.getSelectionSize() + " blocks");
    }

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

    private void undo() {
        if (!historyManager.canUndo()) return;

        historyManager.undo(blockPlacer);
        System.out.println("Undo completed");
    }

    private void redo() {
        if (!historyManager.canRedo()) return;

        historyManager.redo(blockPlacer);
        System.out.println("Redo completed");
    }

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