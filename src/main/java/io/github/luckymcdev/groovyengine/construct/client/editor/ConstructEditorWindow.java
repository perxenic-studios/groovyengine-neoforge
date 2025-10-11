package io.github.luckymcdev.groovyengine.construct.client.editor;

import imgui.ImGuiIO;
import imgui.type.ImInt;
import imgui.type.ImString;
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

@OnlyIn(Dist.CLIENT)
public class ConstructEditorWindow extends EditorWindow {

    private static final Minecraft mc = Minecraft.getInstance();
    private final Selection selectionManager = new Selection();
    private final AsyncBlockPlacer blockPlacer = AsyncBlockPlacer.getInstance();

    private final ImString blockIdInput = new ImString("minecraft:stone", 256);
    private final ImInt blocksPerTick = new ImInt(blockPlacer.getBlocksPerTick());
    private final ImInt updatesPerTick = new ImInt(blockPlacer.getUpdatesPerTick());
    private final ImString pos1Display = new ImString("Not set", 50);
    private final ImString pos2Display = new ImString("Not set", 50);

    public ConstructEditorWindow() {
        super(ImIcons.WRENCH.get() + " Construct Editor");
    }

    @Override
    public void render(ImGuiIO io) {
        ImGe.window(title, () -> {
            renderSelectionSection();
            renderPatternSection();
            renderOperationsSection();
            renderStatsSection();
        });
    }

    private void renderSelectionSection() {
        ImGe.title(ImIcons.SELECT_ALL.get() + " Selection");

        ImGe.text("Position 1: " + pos1Display.get());
        ImGe.text("Position 2: " + pos2Display.get());

        if (ImGe.button("Set Pos1")) setPositionFromLookingAt(true);
        ImGe.sameLine();
        if (ImGe.button("Set Pos2")) setPositionFromLookingAt(false);

        if (ImGe.button("Clear Selection")) {
            selectionManager.clearSelection();
            updatePositionDisplays();
        }

        if (selectionManager.hasValidSelection()) {
            ImGe.text("Selection Size: " + selectionManager.getSelectionSize() + " blocks");
        }
    }

    private void renderPatternSection() {
        ImGe.title("Patterns");
        ImGe.inputText("Block ID", blockIdInput);
        ImGe.helpMarker("Enter block ID (e.g., minecraft:stone, minecraft:oak_planks)");
    }

    private void renderOperationsSection() {
        ImGe.title("Operations");

        if (ImGe.button("Fill Selection")) fillSelection();
        ImGe.sameLine();
        if (ImGe.button("Fill Selection With Air")) clearSelection();

        ImGe.separator();

        if (ImGe.sliderInt("Blocks/Tick", blocksPerTick.getData(), 1, 100_000)) {
            blockPlacer.setBlocksPerTick(blocksPerTick.get());
        }
        if (ImGe.sliderInt("Updates/Tick", updatesPerTick.getData(), 0, 50_000)) {
            blockPlacer.setUpdatesPerTick(updatesPerTick.get());
        }
    }

    private void renderStatsSection() {
        ImGe.title("Statistics");
        ImGe.text("Queued Placements: " + blockPlacer.getQueuedPlacements());
        ImGe.text("Queued Updates: " + blockPlacer.getQueuedUpdates());
        ImGe.separator();
        ImGe.text("Blocks/Tick: " + blockPlacer.getBlocksPerTick());
        ImGe.text("Updates/Tick: " + blockPlacer.getUpdatesPerTick());

        if (ImGe.button("Clear Queue")) blockPlacer.clearQueues();
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

    private void fillSelection() {
        if (!selectionManager.hasValidSelection()) {
            System.out.println("No valid selection!");
            return;
        }

        Block block = parseBlock(blockIdInput.get());
        if (block == null) return;

        BlockPattern pattern = new BlockPattern.SingleBlockPattern(block);
        blockPlacer.fillSelection(selectionManager, pattern);

        System.out.println("Started filling " + selectionManager.getSelectionSize() + " blocks with " + blockIdInput.get());
    }

    private void clearSelection() {
        if (!selectionManager.hasValidSelection()) return;

        BlockPattern pattern = new BlockPattern.SingleBlockPattern(Blocks.AIR);
        blockPlacer.fillSelection(selectionManager, pattern);

        System.out.println("Started clearing " + selectionManager.getSelectionSize() + " blocks");
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
