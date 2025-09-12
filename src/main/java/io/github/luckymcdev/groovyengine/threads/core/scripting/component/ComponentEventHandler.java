// ComponentEventHandler.java - Updated with more event subscriptions
package io.github.luckymcdev.groovyengine.threads.core.scripting.component;

import io.github.luckymcdev.groovyengine.api.components.ComponentManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.minecraft.world.InteractionHand;

@EventBusSubscriber
public class ComponentEventHandler {
    static ComponentManager manager = ComponentManager.getInstance();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        // Tick all item components in inventory
        for (int slot = 0; slot < player.getInventory().items.size(); slot++) {
            ItemStack stack = player.getInventory().items.get(slot);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                if (manager.hasItemComponents(item)) {
                    manager.onItemInventoryTick(item, stack, player, slot, level);
                }
            }
        }

        // Tick player components
        if (manager.hasPlayerComponents(player)) {
            // Player tick is handled through onGlobalTick for generic components
        }
    }

    @SubscribeEvent
    public static void onPlayerUseItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        Item item = stack.getItem();
        Level level = player.level();
        InteractionHand hand = event.getHand();

        if (manager.hasItemComponents(item)) {
            manager.onItemUse(item, stack, player, level, hand);
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (manager.hasBlockComponents(block)) {
            manager.onBlockPlace(block, level, pos, state);
        }

        if (manager.hasPlayerComponents(player)) {
            manager.onPlayerPlaceBlock(level, player, pos, state);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = player.level();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (manager.hasBlockComponents(block)) {
            manager.onBlockBreak(block, level, pos, state);
        }

        if (manager.hasPlayerComponents(player)) {
            manager.onPlayerBreakBlock(level, player, pos, state);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        Entity entity = event.getTarget();
        Level level = player.level();
        InteractionHand hand = event.getHand();

        if (manager.hasEntityComponents(entity)) {
            manager.onEntityInteract(entity);
        }

        if (manager.hasPlayerComponents(player)) {
            manager.onPlayerInteractEntity(level, player, entity, hand);
        }
    }

    @SubscribeEvent
    public static void onEntityDamage(LivingDamageEvent.Post event) {
        Entity entity = event.getEntity();
        DamageSource source = event.getEntity().getLastDamageSource();
        Level level = entity.level();

        if (manager.hasEntityComponents(entity)) {
            manager.onEntityDamage(entity, source);
        }

        if (entity instanceof Player player && manager.hasPlayerComponents(player)) {
            manager.onPlayerDamage(level, player, source);
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        DamageSource source = event.getSource();
        Level level = entity.level();

        if (manager.hasEntityComponents(entity)) {
            manager.onEntityDeath(entity, source);
        }

        if (entity instanceof Player player && manager.hasPlayerComponents(player)) {
            manager.onPlayerDeath(level, player, source);
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        Level level = event.getLevel();

        if (manager.hasEntityComponents(entity)) {
            manager.onEntitySpawn(entity);
        }

        manager.onEntitySpawn(level, entity);
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        Level level = event.getLevel();

        manager.onEntityDespawn(level, entity);
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (manager.hasPlayerComponents(player)) {
            manager.onPlayerJoin(level, player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (manager.hasPlayerComponents(player)) {
            manager.onPlayerLeave(level, player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (manager.hasPlayerComponents(player)) {
            manager.onPlayerRespawn(level, player);
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof Level level) {
            manager.onWorldLoad(level);
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            manager.onWorldUnload(level);
        }
    }

    // Global tick for generic components
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        manager.onGlobalTick();
    }
}