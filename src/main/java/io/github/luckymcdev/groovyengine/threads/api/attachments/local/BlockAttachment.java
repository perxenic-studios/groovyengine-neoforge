/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
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