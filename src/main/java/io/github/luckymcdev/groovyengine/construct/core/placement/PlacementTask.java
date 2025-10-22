package io.github.luckymcdev.groovyengine.construct.core.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Represents a single block placement operation.
 *
 * @param position    The target block position
 * @param blockState  The block state to place
 * @param sendUpdates Whether to send block updates after placement
 */
public record PlacementTask(BlockPos position, BlockState blockState, boolean sendUpdates) {
}