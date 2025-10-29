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

package io.github.luckymcdev.groovyengine.construct.core.placement;

import io.github.luckymcdev.groovyengine.construct.core.flags.BlockPlacementFlags;
import io.github.luckymcdev.groovyengine.construct.core.pattern.BlockPattern;
import io.github.luckymcdev.groovyengine.construct.core.selection.Selection;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashSet;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Asynchronous block placement thingiemajig
 */
@EventBusSubscriber(value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class AsyncBlockPlacer {
    private static final AsyncBlockPlacer INSTANCE = new AsyncBlockPlacer();
    private final Queue<PlacementTask> immediatePlacementQueue = new ConcurrentLinkedQueue<>();
    private final Queue<PlacementTask> delayedUpdateQueue = new ConcurrentLinkedQueue<>();
    private final Random random = new Random();
    private final int maxBlocksPerTick = 100000;
    private final int fpsThreshold = 45;
    private final int adjustmentInterval = 5;
    private final int blocksPerTickIncrement = 250;
    private int blocksPerTick = 1000;
    private int updatesPerTick = blocksPerTick / 2;
    private int tickCounter = 0;

    private AsyncBlockPlacer() {
    }

    /**
     * Retrieves the singleton instance of the asynchronous block placer.
     *
     * @return the singleton instance of the asynchronous block placer
     */
    public static AsyncBlockPlacer getInstance() {
        return INSTANCE;
    }

    /**
     * Handles server tick events to process queued block operations.
     * @param event the Tick event.
     */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        Level playerLevel = player.level();
        Level level = event.getServer().getLevel(playerLevel.dimension());
        if (level == null) return;

        if (INSTANCE.immediatePlacementQueue.isEmpty() && INSTANCE.delayedUpdateQueue.isEmpty()) return;

        INSTANCE.tickCounter++;
        if (INSTANCE.immediatePlacementQueue.size() > 10000 && INSTANCE.tickCounter % INSTANCE.adjustmentInterval == 0) {
            INSTANCE.adjustPerformanceParameters();
        }

        INSTANCE.processQueues(level);
    }

    /**
     * Retrieves the current number of blocks that are processed per tick.
     * This value can be modified by calling {@link #setBlocksPerTick(int)}.
     * The value is adjusted automatically to maintain a target frame rate
     * if the number of blocks per tick is greater than 10000.
     *
     * @return the current number of blocks per tick
     */
    public int getBlocksPerTick() {
        return blocksPerTick;
    }

    /**
     * Sets the number of blocks to process per tick.
     * The value is clamped to a minimum of 1 to prevent division by zero.
     *
     * @param blocksPerTick the number of blocks to process per tick
     */
    public void setBlocksPerTick(int blocksPerTick) {
        this.blocksPerTick = Math.max(1, blocksPerTick);
    }

    /**
     * Retrieves the current number of block updates that are processed per tick.
     * This value can be modified by calling {@link #setUpdatesPerTick(int)}.
     * The value is adjusted automatically to maintain a target frame rate
     * if the number of blocks per tick is greater than 10000.
     *
     * @return the current number of block updates per tick
     */
    public int getUpdatesPerTick() {
        return updatesPerTick;
    }

    /**
     * Sets the number of block updates to process per tick.
     * The value is clamped to a minimum of 1 to prevent division by zero.
     *
     * @param updatesPerTick the number of block updates to process per tick
     */
    public void setUpdatesPerTick(int updatesPerTick) {
        this.updatesPerTick = Math.max(1, updatesPerTick);
    }

    /**
     * Fills a selection with blocks from a specified pattern.
     * @param selection the selection to fill
     * @param pattern the pattern to fill the selection with.
     */
    public void fillSelection(Selection selection, BlockPattern pattern) {
        if (!selection.hasValidSelection()) return;

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Set<BlockPos> positions = selection.getSelectedBlocks();
        BlockPos origin = selection.getPos1();

        queueBlockPlacements(positions, pattern, origin);
    }

    /**
     * Fills only the hollow shell of a selection.
     */
    public void fillHollow(Selection selection, BlockPattern pattern, int thickness) {
        if (!selection.hasValidSelection()) return;

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Set<BlockPos> positions = selection.getHollowBlocks(thickness);
        BlockPos origin = selection.getPos1();

        queueBlockPlacements(positions, pattern, origin);
    }

    /**
     * Fills only the walls (vertical faces) of a selection.
     */
    public void fillWalls(Selection selection, BlockPattern pattern) {
        if (!selection.hasValidSelection()) return;

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Set<BlockPos> positions = selection.getWallBlocks();
        BlockPos origin = selection.getPos1();

        queueBlockPlacements(positions, pattern, origin);
    }

    /**
     * Fills only the outline (edges) of a selection.
     */
    public void fillOutline(Selection selection, BlockPattern pattern) {
        if (!selection.hasValidSelection()) return;

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Set<BlockPos> positions = selection.getEdgeBlocks();
        BlockPos origin = selection.getPos1();

        queueBlockPlacements(positions, pattern, origin);
    }

    /**
     * Creates a sphere at the specified center with the given radius.
     */
    public void createSphere(BlockPos center, int radius, BlockPattern pattern) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Set<BlockPos> positions = new HashSet<>();
        int radiusSquared = radius * radius;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radiusSquared) {
                        positions.add(center.offset(x, y, z));
                    }
                }
            }
        }

        queueBlockPlacements(positions, pattern, center);
    }

    /**
     * Creates a hollow sphere at the specified center.
     */
    public void createHollowSphere(BlockPos center, int radius, int thickness, BlockPattern pattern) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Set<BlockPos> positions = new HashSet<>();
        int outerRadiusSquared = radius * radius;
        int innerRadius = Math.max(0, radius - thickness);
        int innerRadiusSquared = innerRadius * innerRadius;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    int distSquared = x * x + y * y + z * z;
                    if (distSquared <= outerRadiusSquared && distSquared >= innerRadiusSquared) {
                        positions.add(center.offset(x, y, z));
                    }
                }
            }
        }

        queueBlockPlacements(positions, pattern, center);
    }

    /**
     * Creates a cylinder between two points.
     */
    public void createCylinder(BlockPos pos1, BlockPos pos2, int radius, BlockPattern pattern) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Set<BlockPos> positions = new HashSet<>();
        int minY = Math.min(pos1.getY(), pos2.getY());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int centerX = (pos1.getX() + pos2.getX()) / 2;
        int centerZ = (pos1.getZ() + pos2.getZ()) / 2;
        int radiusSquared = radius * radius;

        for (int y = minY; y <= maxY; y++) {
            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    int dx = x - centerX;
                    int dz = z - centerZ;
                    if (dx * dx + dz * dz <= radiusSquared) {
                        positions.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        queueBlockPlacements(positions, pattern, pos1);
    }

    /**
     * Creates a hollow cylinder between two points.
     */
    public void createHollowCylinder(BlockPos pos1, BlockPos pos2, int radius, int thickness, BlockPattern pattern) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Set<BlockPos> positions = new HashSet<>();
        int minY = Math.min(pos1.getY(), pos2.getY());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int centerX = (pos1.getX() + pos2.getX()) / 2;
        int centerZ = (pos1.getZ() + pos2.getZ()) / 2;
        int outerRadiusSquared = radius * radius;
        int innerRadius = Math.max(0, radius - thickness);
        int innerRadiusSquared = innerRadius * innerRadius;

        for (int y = minY; y <= maxY; y++) {
            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    int dx = x - centerX;
                    int dz = z - centerZ;
                    int distSquared = dx * dx + dz * dz;
                    if (distSquared <= outerRadiusSquared && distSquared >= innerRadiusSquared) {
                        positions.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        queueBlockPlacements(positions, pattern, pos1);
    }

    /**
     * Creates a pyramid at the specified base position.
     */
    public void createPyramid(BlockPos base, int baseSize, int height, BlockPattern pattern) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Set<BlockPos> positions = new HashSet<>();

        for (int y = 0; y < height; y++) {
            float progress = (float) y / height;
            int currentSize = (int) (baseSize * (1 - progress));

            for (int x = -currentSize; x <= currentSize; x++) {
                for (int z = -currentSize; z <= currentSize; z++) {
                    positions.add(base.offset(x, y, z));
                }
            }
        }

        queueBlockPlacements(positions, pattern, base);
    }

    /**
     * Replaces all blocks of one type with another in a selection.
     */
    public void replaceBlocks(Selection selection, Block targetBlock, Block replacementBlock) {
        if (!selection.hasValidSelection()) return;

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Set<BlockPos> positions = selection.getSelectedBlocks();
        BlockState targetState = targetBlock.defaultBlockState();
        BlockState replacementState = replacementBlock.defaultBlockState();

        for (BlockPos pos : positions) {
            BlockState currentState = level.getBlockState(pos);
            if (currentState.getBlock() == targetBlock) {
                immediatePlacementQueue.offer(new PlacementTask(pos, replacementState, false));
            }
        }

        if (shouldSendUpdates()) {
            for (BlockPos pos : positions) {
                BlockState currentState = level.getBlockState(pos);
                if (currentState.getBlock() == targetBlock) {
                    delayedUpdateQueue.offer(new PlacementTask(pos, null, true));
                }
            }
        }
    }

    /**
     * Helper method to queue block placements for a set of positions.
     */
    private void queueBlockPlacements(Set<BlockPos> positions, BlockPattern pattern, BlockPos origin) {
        for (BlockPos pos : positions) {
            BlockState state = pattern.getBlockState(pos, origin, random);
            immediatePlacementQueue.offer(new PlacementTask(pos, state, false));
        }

        if (shouldSendUpdates()) {
            for (BlockPos pos : positions) {
                delayedUpdateQueue.offer(new PlacementTask(pos, null, true));
            }
        }
    }

    /**
     * Queues a single block placement operation.
     */
    public void setBlock(BlockPos pos, BlockState state, boolean sendUpdates) {
        if (sendUpdates) {
            immediatePlacementQueue.offer(new PlacementTask(pos, state, false));
            delayedUpdateQueue.offer(new PlacementTask(pos, null, true));
        } else {
            immediatePlacementQueue.offer(new PlacementTask(pos, state, false));
        }
    }

    /**
     * Adjusts performance parameters based on current FPS.
     */
    private void adjustPerformanceParameters() {
        int currentFps = Minecraft.getInstance().getFps();
        if (currentFps >= fpsThreshold) {
            blocksPerTick = Math.min(blocksPerTick + blocksPerTickIncrement, maxBlocksPerTick);
        } else {
            blocksPerTick = Math.max(blocksPerTick - blocksPerTickIncrement, 1000);
        }
        updatesPerTick = Math.max(1, blocksPerTick / 2);
    }

    /**
     * Processes both placement and update queues.
     */
    private void processQueues(Level level) {
        processImmediatePlacements(level);
        processDelayedUpdates(level);
    }

    /**
     * Processes the immediate block placement queue.
     */
    private void processImmediatePlacements(Level level) {
        if (level == null) return;

        int processed = 0;
        while (processed < blocksPerTick && !immediatePlacementQueue.isEmpty()) {
            PlacementTask task = immediatePlacementQueue.poll();
            if (task != null && isValidPosition(task.position(), level)) {
                level.setBlock(task.position(), task.blockState(), BlockPlacementFlags.UPDATE_CLIENTS);
                processed++;
            }
        }
    }

    /**
     * Processes the delayed block update queue.
     */
    private void processDelayedUpdates(Level level) {
        if (level == null) return;

        int processed = 0;
        while (processed < updatesPerTick && !delayedUpdateQueue.isEmpty()) {
            PlacementTask task = delayedUpdateQueue.poll();
            if (task != null && isValidPosition(task.position(), level)) {
                BlockState currentState = level.getBlockState(task.position());
                level.sendBlockUpdated(task.position(), currentState, currentState, BlockPlacementFlags.UPDATE_ALL);
                processed++;
            }
        }
    }

    /**
     * Validates if a position is within world bounds.
     */
    private boolean isValidPosition(BlockPos pos, Level level) {
        return level.isInWorldBounds(pos);
    }

    /**
     * Determines whether block updates should be sent.
     */
    private boolean shouldSendUpdates() {
        return true;
    }

    /**
     * Retrieves the current number of block placement operations that are queued.
     * This value does not include block updates that are queued.
     *
     * @return the current number of block placement operations that are queued
     */
    public int getQueuedPlacements() {
        return immediatePlacementQueue.size();
    }

    /**
     * Retrieves the current number of block updates that are queued.
     * This value does not include block placement operations that are queued.
     *
     * @return the current number of block updates that are queued
     */
    public int getQueuedUpdates() {
        return delayedUpdateQueue.size();
    }

    /**
     * Clears both the immediate block placement queue and the delayed block update queue.
     * This method can be used to clear any queued block operations and updates.
     */
    public void clearQueues() {
        immediatePlacementQueue.clear();
        delayedUpdateQueue.clear();
    }
}