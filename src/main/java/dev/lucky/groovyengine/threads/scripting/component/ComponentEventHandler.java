package dev.lucky.groovyengine.threads.scripting.component;

import dev.lucky.groovyengine.api.components.ComponentManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

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
    }

    // Called when a player uses an item
    @SubscribeEvent
    public static void onPlayerUseItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        Item item = stack.getItem();
        Level level = player.level();

        if (manager.hasItemComponents(item)) {
            manager.onItemUse(item, stack, player, level);
        }
    }

    // Example: block place
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (manager.hasBlockComponents(block)) {
            manager.onBlockPlace(block, level, pos, state);
        }
    }

    // Example: block break
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Level level = event.getPlayer().level();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (manager.hasBlockComponents(block)) {
            manager.onBlockBreak(block, level, pos, state);
        }
    }

    // Global tick for generic components
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        manager.onGlobalTick();
    }

}
