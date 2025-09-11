package dev.lucky.groovyengine.api.components.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockComponent implements ScriptComponent<Block> {

    public void onPlace(Level level, BlockPos pos, BlockState state) {}
    public void onBreak(Level level, BlockPos pos, BlockState state) {}
    public void onNeighborUpdate(Level level, BlockPos pos, BlockState state, BlockPos neighborPos) {}
}