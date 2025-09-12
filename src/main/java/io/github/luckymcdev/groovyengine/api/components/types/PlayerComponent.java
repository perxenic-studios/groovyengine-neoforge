// PlayerComponent.java - Enhanced with more events
package io.github.luckymcdev.groovyengine.api.components.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.InteractionHand;

public abstract class PlayerComponent implements BaseComponent<Player> {
    public void onPlayerJoin(Level level, Player player) {}
    public void onPlayerLeave(Level level, Player player) {}
    public void onPlayerRespawn(Level level, Player player) {}
    public void onPlayerAttack(Level level, Player player, Entity target) {}
    public void onPlayerInteractEntity(Level level, Player player, Entity entity, InteractionHand hand) {}
    public void onPlayerPickupItem(Level level, Player player, ItemStack stack) {}
    public void onPlayerBreakBlock(Level level, Player player, BlockPos pos, BlockState state) {}
    public void onPlayerPlaceBlock(Level level, Player player, BlockPos pos, BlockState state) {}
    public void onPlayerRaycast(Level level, Player player, HitResult hitResult) {}
    public void onPlayerDamage(Level level, Player player, DamageSource source) {}
    public void onPlayerDeath(Level level, Player player, DamageSource source) {}
    public void onPlayerSleep(Level level, Player player, BlockPos pos) {}
    public void onPlayerWake(Level level, Player player) {}
}