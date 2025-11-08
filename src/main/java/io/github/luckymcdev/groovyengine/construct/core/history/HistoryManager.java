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

package io.github.luckymcdev.groovyengine.construct.core.history;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.construct.core.placement.AsyncBlockPlacer;
import io.github.luckymcdev.groovyengine.construct.core.selection.Selection;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.*;

/**
 * Manages undo/redo history for block operations.
 * Stores snapshots of block states before operations to enable rollback.
 */
@OnlyIn(Dist.CLIENT)
public class HistoryManager {
    private static final int MAX_HISTORY_SIZE = 50;
    private static final int MAX_SNAPSHOT_SIZE = 1_000_000; // Maximum blocks per snapshot

    private final Deque<HistorySnapshot> undoStack = new ArrayDeque<>();
    private final Deque<HistorySnapshot> redoStack = new ArrayDeque<>();

    /**
     * Saves the current state of blocks in a selection before an operation.
     *
     * @param selection The selection area to save
     */
    public void saveState(Selection selection) {
        if (!selection.hasValidSelection()) return;

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Set<BlockPos> positions = selection.getSelectedBlocks();

        // Limit snapshot size to prevent memory issues
        if (positions.size() > MAX_SNAPSHOT_SIZE) {
            GE.CONSTRUCT_LOG.info("Warning: Selection too large for undo (" + positions.size() + " blocks). Undo disabled for this operation.");
            return;
        }

        Map<BlockPos, BlockState> blockStates = new HashMap<>();
        for (BlockPos pos : positions) {
            blockStates.put(pos.immutable(), level.getBlockState(pos));
        }

        HistorySnapshot snapshot = new HistorySnapshot(blockStates);
        undoStack.push(snapshot);

        // Clear redo stack when new action is performed
        redoStack.clear();

        // Limit history size
        if (undoStack.size() > MAX_HISTORY_SIZE) {
            undoStack.removeLast();
        }

        GE.CONSTRUCT_LOG.info("Saved state: " + blockStates.size() + " blocks");
    }

    /**
     * Undoes the last operation.
     *
     * @param blockPlacer The block placer to use for restoration
     */
    public void undo(AsyncBlockPlacer blockPlacer) {
        if (!canUndo()) return;

        HistorySnapshot snapshot = undoStack.pop();

        // Save current state for redo
        Map<BlockPos, BlockState> currentState = captureCurrentState(snapshot.getBlockStates().keySet());
        redoStack.push(new HistorySnapshot(currentState));

        // Restore previous state
        restoreSnapshot(snapshot, blockPlacer);

        GE.CONSTRUCT_LOG.info("Undo: Restored " + snapshot.getBlockStates().size() + " blocks");
    }

    /**
     * Redoes the last undone operation.
     *
     * @param blockPlacer The block placer to use for restoration
     */
    public void redo(AsyncBlockPlacer blockPlacer) {
        if (!canRedo()) return;

        HistorySnapshot snapshot = redoStack.pop();

        // Save current state for undo
        Map<BlockPos, BlockState> currentState = captureCurrentState(snapshot.getBlockStates().keySet());
        undoStack.push(new HistorySnapshot(currentState));

        // Restore redo state
        restoreSnapshot(snapshot, blockPlacer);

        GE.CONSTRUCT_LOG.info("Redo: Restored " + snapshot.getBlockStates().size() + " blocks");
    }

    /**
     * Captures the current state of blocks at given positions.
     */
    private Map<BlockPos, BlockState> captureCurrentState(Set<BlockPos> positions) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return new HashMap<>();

        Map<BlockPos, BlockState> currentState = new HashMap<>();
        for (BlockPos pos : positions) {
            currentState.put(pos.immutable(), level.getBlockState(pos));
        }
        return currentState;
    }

    /**
     * Restores blocks from a snapshot.
     */
    private void restoreSnapshot(HistorySnapshot snapshot, AsyncBlockPlacer blockPlacer) {
        for (Map.Entry<BlockPos, BlockState> entry : snapshot.getBlockStates().entrySet()) {
            blockPlacer.setBlock(entry.getKey(), entry.getValue(), false);
        }
    }

    /**
     * Checks if undo is available.
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Checks if redo is available.
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Gets the number of operations that can be undone.
     */
    public int getHistorySize() {
        return undoStack.size();
    }

    /**
     * Clears all history.
     */
    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
        GE.CONSTRUCT_LOG.info("History cleared");
    }


}