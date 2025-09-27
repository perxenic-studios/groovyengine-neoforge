package io.github.luckymcdev.groovyengine.threads.api.attachments.block;

import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public abstract class BlockAttachment implements BaseAttachment<Block> {

    public final List<Block> targetBlocks;

    /**
     * Constructor for single block attachment
     */
    public BlockAttachment(Block block) {
        this.targetBlocks = List.of(block);
    }

    /**
     * Constructor for multi-block attachment
     */
    public BlockAttachment(List<Block> blocks) {
        this.targetBlocks = List.copyOf(blocks);
    }

    /**
     * Constructor for multi-block attachment (varargs)
     */
    public BlockAttachment(Block... blocks) {
        this.targetBlocks = List.of(blocks);
    }

    @Override
    public Block getTarget() {
        return targetBlocks.isEmpty() ? null : targetBlocks.get(0);
    }

    @Override
    public List<Block> getTargets() {
        return targetBlocks;
    }


    public void onPlace(LevelAccessor level, BlockPos pos, BlockState state, Player player) {}
    public void onBreak(LevelAccessor level, BlockPos pos, BlockState state, Player player) {}
    public InteractionResult onUse(BlockState state, LevelAccessor level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {return InteractionResult.PASS; }

    public void onAttack(BlockState state, LevelAccessor level, BlockPos pos, Player player) {}
    public void onNeighborChanged(BlockState state, LevelAccessor level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {}


}