// ComponentManager.java - Updated with new method signatures
package io.github.luckymcdev.groovyengine.api.components;

import io.github.luckymcdev.groovyengine.api.components.types.BaseComponent;
import io.github.luckymcdev.groovyengine.threads.core.scripting.component.ComponentManagerImpl;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.InteractionHand;

public interface ComponentManager {

    static ComponentManager getInstance() {
        return ComponentManagerImpl.INSTANCE;
    }

    void registerComponent(BaseComponent<?> component);

    void onItemUse(Item item, ItemStack stack, Player player, Level level, InteractionHand hand);
    void onItemInventoryTick(Item item, ItemStack stack, Player player, int slot, Level level);
    void onItemCrafted(Item item, ItemStack stack, Player player, Level level);
    void onItemDestroyed(Item item, ItemStack stack, Player player, Level level);
    void onItemDropped(Item item, ItemStack stack, Player player, Level level);

    void onBlockPlace(Block block, Level level, BlockPos pos, BlockState state);
    void onBlockBreak(Block block, Level level, BlockPos pos, BlockState state);
    void onBlockNeighborUpdate(Block block, Level level, BlockPos pos, BlockState state, BlockPos neighborPos);
    void onBlockEntityWalk(Block block, Level level, BlockPos pos, BlockState state, Entity entity);
    void onBlockRandomTick(Block block, Level level, BlockPos pos, BlockState state);
    void onBlockFallOn(Block block, Level level, BlockPos pos, BlockState state, Entity entity, float fallDistance);

    void onEntityTick(Entity entity);
    void onEntityInteract(Entity entity);
    void onEntityDamage(Entity entity, DamageSource source);
    void onEntityDeath(Entity entity, DamageSource source);
    void onEntitySpawn(Entity entity);
    void onEntityCollide(Entity entity, Entity other);
    void onEntityMount(Entity entity, Entity passenger);
    void onEntityDismount(Entity entity, Entity passenger);

    void onGlobalTick();

    boolean hasItemComponents(Item item);
    boolean hasBlockComponents(Block block);
    boolean hasEntityComponents(Entity entity);
    boolean hasWorldComponents(Level level);
    boolean hasPlayerComponents(Player player);

    void onWorldLoad(Level level);
    void onWorldUnload(Level level);
    void onChunkLoad(Level level, BlockPos chunkPos);
    void onChunkUnload(Level level, BlockPos chunkPos);
    void onBlockUpdate(Level level, BlockPos pos, BlockState state);
    void onEntitySpawn(Level level, Entity entity);
    void onEntityDespawn(Level level, Entity entity);
    void onWeatherChange(Level level, boolean raining);
    void onTimeChange(Level level, long time);
    void onExplosion(Level level, double x, double y, double z, float power);

    void onPlayerJoin(Level level, Player player);
    void onPlayerLeave(Level level, Player player);
    void onPlayerRespawn(Level level, Player player);
    void onPlayerAttack(Level level, Player player, Entity target);
    void onPlayerInteractEntity(Level level, Player player, Entity entity, InteractionHand hand);
    void onPlayerPickupItem(Level level, Player player, ItemStack stack);
    void onPlayerBreakBlock(Level level, Player player, BlockPos pos, BlockState state);
    void onPlayerPlaceBlock(Level level, Player player, BlockPos pos, BlockState state);
    void onPlayerRaycast(Level level, Player player, HitResult hitResult);
    void onPlayerDamage(Level level, Player player, DamageSource source);
    void onPlayerDeath(Level level, Player player, DamageSource source);
    void onPlayerSleep(Level level, Player player, BlockPos pos);
    void onPlayerWake(Level level, Player player);
}