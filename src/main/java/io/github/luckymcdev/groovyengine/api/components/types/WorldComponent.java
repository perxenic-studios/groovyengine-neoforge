// WorldComponent.java - Enhanced with more events
package io.github.luckymcdev.groovyengine.api.components.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class WorldComponent implements BaseComponent<Level> {
    public void onWorldLoad(Level level) {}
    public void onWorldUnload(Level level) {}
    public void onChunkLoad(Level level, BlockPos chunkPos) {}
    public void onChunkUnload(Level level, BlockPos chunkPos) {}
    public void onBlockUpdate(Level level, BlockPos pos, BlockState state) {}
    public void onEntitySpawn(Level level, Entity entity) {}
    public void onEntityDespawn(Level level, Entity entity) {}
    public void onWeatherChange(Level level, boolean raining) {}
    public void onTimeChange(Level level, long time) {}
    public void onExplosion(Level level, double x, double y, double z, float power) {}
}