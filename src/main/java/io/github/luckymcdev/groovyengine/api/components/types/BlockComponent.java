// BlockComponent.java - Enhanced with more events
package io.github.luckymcdev.groovyengine.api.components.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockComponent implements BaseComponent<Block> {

    public void onPlace(Level level, BlockPos pos, BlockState state) {}
    public void onBreak(Level level, BlockPos pos, BlockState state) {}
    public void onNeighborUpdate(Level level, BlockPos pos, BlockState state, BlockPos neighborPos) {}
    public void onEntityWalk(Level level, BlockPos pos, BlockState state, Entity entity) {}
    public void onRandomTick(Level level, BlockPos pos, BlockState state) {}
    public void onFallOn(Level level, BlockPos pos, BlockState state, Entity entity, float fallDistance) {}
}