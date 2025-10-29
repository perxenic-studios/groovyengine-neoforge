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

package io.github.luckymcdev.groovyengine.threads.api.attachments.local;

import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Attachment for adding custom behavior to blocks.
 * Supports targeting specific blocks or matching blocks based on a predicate.
 */
public abstract class BlockAttachment implements BaseAttachment<Block> {

    private final Set<Block> targetBlocks;
    private final Predicate<Block> blockMatcher;

    /**
     * Constructor for single block attachment
     */
    protected BlockAttachment(Block block) {
        this.targetBlocks = Set.of(block);
        this.blockMatcher = null;
    }

    /**
     * Constructor for multiple specific blocks
     */
    protected BlockAttachment(Block... blocks) {
        this.targetBlocks = new HashSet<>(Arrays.asList(blocks));
        this.blockMatcher = null;
    }

    /**
     * Constructor for predicate-based matching (e.g., all blocks with certain properties)
     */
    protected BlockAttachment(Predicate<Block> blockMatcher) {
        this.targetBlocks = Set.of();
        this.blockMatcher = blockMatcher;
    }

    @Override
    public final boolean appliesTo(Block block) {
        if (blockMatcher != null) {
            return blockMatcher.test(block);
        }
        return targetBlocks.contains(block);
    }

    // === Block Lifecycle Events ===

    /**
     * Called when a block is placed in the world
     */
    public void onPlace(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
    }

    /**
     * Called when a block is broken by a player
     */
    public void onBreak(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
    }

    /**
     * Called when a player right-clicks on the block
     *
     * @return The interaction result. Return PASS to allow other attachments to process.
     */
    public InteractionResult onUse(BlockState state, LevelAccessor level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    /**
     * Called when a player left-clicks (attacks) the block
     */
    public void onAttack(BlockState state, LevelAccessor level, BlockPos pos, Player player) {
    }

    /**
     * Called when a neighboring block changes
     */
    public void onNeighborChanged(BlockState state, LevelAccessor level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
    }
}