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

package io.github.luckymcdev.groovyengine.construct.core.selection;

import io.github.luckymcdev.groovyengine.GE;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a 3D block selection between two points in the world.
 * Enhanced with expansion, contraction, and dimension calculation features.
 */
@OnlyIn(Dist.CLIENT)
public class Selection {
    private final Set<BlockPos> selectedBlocks = new HashSet<>();
    private BlockPos pos1 = null;
    private BlockPos pos2 = null;

    public BlockPos getPos1() {
        return pos1;
    }

    /**
     * Sets the first corner position of the selection.
     */
    public void setPos1(BlockPos pos) {
        this.pos1 = pos;
        recalculateSelection();
    }

    public BlockPos getPos2() {
        return pos2;
    }

    /**
     * Sets the second corner position of the selection.
     */
    public void setPos2(BlockPos pos) {
        this.pos2 = pos;
        recalculateSelection();
    }

    /**
     * Gets a copy of all selected block positions.
     */
    public Set<BlockPos> getSelectedBlocks() {
        return new HashSet<>(selectedBlocks);
    }

    /**
     * Checks if the selection has both corner positions set.
     */
    public boolean hasValidSelection() {
        return pos1 != null && pos2 != null;
    }

    /**
     * Clears the current selection.
     */
    public void clearSelection() {
        pos1 = null;
        pos2 = null;
        selectedBlocks.clear();
    }

    /**
     * Gets the number of blocks in the current selection.
     */
    public int getSelectionSize() {
        return selectedBlocks.size();
    }

    /**
     * Gets the dimensions of the selection as a formatted string.
     *
     * @return String in format "XxYxZ"
     */
    public String getDimensions() {
        if (!hasValidSelection()) return "N/A";

        int width = Math.abs(pos2.getX() - pos1.getX()) + 1;
        int height = Math.abs(pos2.getY() - pos1.getY()) + 1;
        int depth = Math.abs(pos2.getZ() - pos1.getZ()) + 1;

        return width + "x" + height + "x" + depth;
    }

    /**
     * Gets the minimum corner position of the selection.
     */
    public BlockPos getMinPos() {
        if (!hasValidSelection()) return null;

        return new BlockPos(
                Math.min(pos1.getX(), pos2.getX()),
                Math.min(pos1.getY(), pos2.getY()),
                Math.min(pos1.getZ(), pos2.getZ())
        );
    }

    /**
     * Gets the maximum corner position of the selection.
     */
    public BlockPos getMaxPos() {
        if (!hasValidSelection()) return null;

        return new BlockPos(
                Math.max(pos1.getX(), pos2.getX()),
                Math.max(pos1.getY(), pos2.getY()),
                Math.max(pos1.getZ(), pos2.getZ())
        );
    }

    /**
     * Expands the selection by a specified amount in all directions.
     *
     * @param amount The number of blocks to expand by
     */
    public void expand(int amount) {
        if (!hasValidSelection()) return;

        BlockPos min = getMinPos();
        BlockPos max = getMaxPos();

        pos1 = new BlockPos(
                min.getX() - amount,
                min.getY() - amount,
                min.getZ() - amount
        );

        pos2 = new BlockPos(
                max.getX() + amount,
                max.getY() + amount,
                max.getZ() + amount
        );

        recalculateSelection();
    }

    /**
     * Contracts the selection by a specified amount in all directions.
     *
     * @param amount The number of blocks to contract by
     */
    public void contract(int amount) {
        if (!hasValidSelection()) return;

        BlockPos min = getMinPos();
        BlockPos max = getMaxPos();

        int newMinX = min.getX() + amount;
        int newMinY = min.getY() + amount;
        int newMinZ = min.getZ() + amount;
        int newMaxX = max.getX() - amount;
        int newMaxY = max.getY() - amount;
        int newMaxZ = max.getZ() - amount;

        // Ensure selection doesn't invert
        if (newMinX > newMaxX || newMinY > newMaxY || newMinZ > newMaxZ) {
            GE.CONSTRUCT_LOG.info("Cannot contract selection: would become invalid");
            return;
        }

        pos1 = new BlockPos(newMinX, newMinY, newMinZ);
        pos2 = new BlockPos(newMaxX, newMaxY, newMaxZ);

        recalculateSelection();
    }

    /**
     * Shifts the entire selection by a specified offset.
     *
     * @param offset The offset to shift by
     */
    public void shift(BlockPos offset) {
        if (!hasValidSelection()) return;

        pos1 = pos1.offset(offset);
        pos2 = pos2.offset(offset);

        recalculateSelection();
    }

    /**
     * Gets the center position of the selection.
     */
    public BlockPos getCenter() {
        if (!hasValidSelection()) return null;

        BlockPos min = getMinPos();
        BlockPos max = getMaxPos();

        return new BlockPos(
                (min.getX() + max.getX()) / 2,
                (min.getY() + max.getY()) / 2,
                (min.getZ() + max.getZ()) / 2
        );
    }

    /**
     * Checks if a position is within the selection bounds.
     */
    public boolean contains(BlockPos pos) {
        if (!hasValidSelection()) return false;

        BlockPos min = getMinPos();
        BlockPos max = getMaxPos();

        return pos.getX() >= min.getX() && pos.getX() <= max.getX() &&
                pos.getY() >= min.getY() && pos.getY() <= max.getY() &&
                pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ();
    }

    /**
     * Gets only the surface blocks of the selection (faces).
     */
    public Set<BlockPos> getSurfaceBlocks() {
        if (!hasValidSelection()) return new HashSet<>();

        Set<BlockPos> surface = new HashSet<>();
        BlockPos min = getMinPos();
        BlockPos max = getMaxPos();

        // Add all blocks on the six faces
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    if (x == min.getX() || x == max.getX() ||
                            y == min.getY() || y == max.getY() ||
                            z == min.getZ() || z == max.getZ()) {
                        surface.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        return surface;
    }

    /**
     * Gets only the edge blocks of the selection (12 edges of the cuboid).
     */
    public Set<BlockPos> getEdgeBlocks() {
        if (!hasValidSelection()) return new HashSet<>();

        Set<BlockPos> edges = new HashSet<>();
        BlockPos min = getMinPos();
        BlockPos max = getMaxPos();

        // X-aligned edges
        for (int x = min.getX(); x <= max.getX(); x++) {
            edges.add(new BlockPos(x, min.getY(), min.getZ()));
            edges.add(new BlockPos(x, min.getY(), max.getZ()));
            edges.add(new BlockPos(x, max.getY(), min.getZ()));
            edges.add(new BlockPos(x, max.getY(), max.getZ()));
        }

        // Y-aligned edges
        for (int y = min.getY(); y <= max.getY(); y++) {
            edges.add(new BlockPos(min.getX(), y, min.getZ()));
            edges.add(new BlockPos(min.getX(), y, max.getZ()));
            edges.add(new BlockPos(max.getX(), y, min.getZ()));
            edges.add(new BlockPos(max.getX(), y, max.getZ()));
        }

        // Z-aligned edges
        for (int z = min.getZ(); z <= max.getZ(); z++) {
            edges.add(new BlockPos(min.getX(), min.getY(), z));
            edges.add(new BlockPos(min.getX(), max.getY(), z));
            edges.add(new BlockPos(max.getX(), min.getY(), z));
            edges.add(new BlockPos(max.getX(), max.getY(), z));
        }

        return edges;
    }

    /**
     * Gets the hollow version of the selection (surface minus interior).
     */
    public Set<BlockPos> getHollowBlocks(int thickness) {
        if (!hasValidSelection()) return new HashSet<>();

        Set<BlockPos> hollow = new HashSet<>();
        BlockPos min = getMinPos();
        BlockPos max = getMaxPos();

        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    // Calculate distance to nearest edge
                    int distX = Math.min(x - min.getX(), max.getX() - x);
                    int distY = Math.min(y - min.getY(), max.getY() - y);
                    int distZ = Math.min(z - min.getZ(), max.getZ() - z);
                    int minDist = Math.min(Math.min(distX, distY), distZ);

                    // Include if within thickness of edge
                    if (minDist < thickness) {
                        hollow.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        return hollow;
    }

    /**
     * Gets only the wall blocks (vertical faces) of the selection.
     */
    public Set<BlockPos> getWallBlocks() {
        if (!hasValidSelection()) return new HashSet<>();

        Set<BlockPos> walls = new HashSet<>();
        BlockPos min = getMinPos();
        BlockPos max = getMaxPos();

        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    if (x == min.getX() || x == max.getX() ||
                            z == min.getZ() || z == max.getZ()) {
                        walls.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        return walls;
    }

    /**
     * Recalculates all block positions within the current selection bounds.
     */
    private void recalculateSelection() {
        selectedBlocks.clear();
        if (!hasValidSelection()) return;

        BlockPos min = getMinPos();
        BlockPos max = getMaxPos();

        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    selectedBlocks.add(new BlockPos(x, y, z));
                }
            }
        }
    }
}