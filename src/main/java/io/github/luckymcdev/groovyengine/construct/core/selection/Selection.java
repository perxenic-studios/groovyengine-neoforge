// File: Selection.java
package io.github.luckymcdev.groovyengine.construct.core.selection;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a 3D block selection between two points in the world.
 * This class is client-side only and manages the selection area defined by two corner positions.
 */
@OnlyIn(Dist.CLIENT)
public class Selection {
    private BlockPos pos1 = null;
    private BlockPos pos2 = null;
    private final Set<BlockPos> selectedBlocks = new HashSet<>();

    /**
     * Sets the first corner position of the selection.
     * @param pos The block position for the first corner
     */
    public void setPos1(BlockPos pos) {
        this.pos1 = pos;
        recalculateSelection();
    }

    /**
     * Sets the second corner position of the selection.
     * @param pos The block position for the second corner
     */
    public void setPos2(BlockPos pos) {
        this.pos2 = pos;
        recalculateSelection();
    }

    public BlockPos getPos1() { return pos1; }
    public BlockPos getPos2() { return pos2; }

    /**
     * Gets a copy of all selected block positions.
     * @return A set containing all block positions within the selection
     */
    public Set<BlockPos> getSelectedBlocks() {
        return new HashSet<>(selectedBlocks);
    }

    /**
     * Checks if the selection has both corner positions set.
     * @return true if both pos1 and pos2 are not null
     */
    public boolean hasValidSelection() {
        return pos1 != null && pos2 != null;
    }

    /**
     * Clears the current selection by resetting both corner positions and clearing selected blocks.
     */
    public void clearSelection() {
        pos1 = null;
        pos2 = null;
        selectedBlocks.clear();
    }

    /**
     * Gets the number of blocks in the current selection.
     * @return The count of selected blocks
     */
    public int getSelectionSize() {
        return selectedBlocks.size();
    }

    /**
     * Recalculates all block positions within the current selection bounds.
     * This method is called automatically when either corner position changes.
     */
    private void recalculateSelection() {
        selectedBlocks.clear();
        if (!hasValidSelection()) return;

        // Calculate the bounding box
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        // Generate all positions within the bounding box
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    selectedBlocks.add(new BlockPos(x, y, z));
                }
            }
        }
    }
}