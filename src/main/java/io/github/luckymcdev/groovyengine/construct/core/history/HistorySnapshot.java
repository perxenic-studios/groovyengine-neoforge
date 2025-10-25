/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package io.github.luckymcdev.groovyengine.construct.core.history;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a snapshot of block states at a point in time.
 *
 */
public class HistorySnapshot {
    private final Map<BlockPos, BlockState> blockStates;
    private final long timestamp;

    /**
     * Creates a new snapshot of block states at the current time
     *
     * @param blockStates The block states to include in the snapshot
     */
    public HistorySnapshot(Map<BlockPos, BlockState> blockStates) {
        this.blockStates = new HashMap<>(blockStates);
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Gets the block states stored in this snapshot
     *
     * @return A map of block positions to their respective block states
     */
    public Map<BlockPos, BlockState> getBlockStates() {
        return blockStates;
    }

    /**
     * Gets the timestamp at which this snapshot was created
     *
     * @return The timestamp at which this snapshot was created
     */
    public long getTimestamp() {
        return timestamp;
    }
}
