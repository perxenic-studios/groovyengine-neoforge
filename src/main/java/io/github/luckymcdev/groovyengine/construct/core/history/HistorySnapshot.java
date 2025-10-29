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
