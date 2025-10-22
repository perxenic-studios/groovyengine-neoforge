package io.github.luckymcdev.groovyengine.threads.core.scripting.attachment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber
public class AttachmentEventManagerImpl {
    private static final AttachmentEventManager instance = AttachmentEventManager.getInstance();

    // ============= CLIENT EVENTS =============


    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        instance.fireClientStart();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        instance.fireClientTick();
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        instance.fireKeyPress(event.getKey(), event.getAction(), event.getModifiers());
    }

    @SubscribeEvent
    public static void onMouseClick(InputEvent.MouseButton.Post event) {
        instance.fireMouseClick(event.getButton(), event.getAction(), event.getModifiers());
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        instance.fireMouseScroll(event.getScrollDeltaX(), event.getScrollDeltaY());
    }

    @SubscribeEvent
    public static void onRenderTick(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            // Fire render events if needed
        }
    }

    // ============= SERVER EVENTS =============

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        instance.fireServerStart();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        instance.fireServerStop();
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        instance.fireServerTick();
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (!event.getLevel().isClientSide()) {
            String worldName = event.getLevel().toString(); // You might want to get a better identifier
            instance.fireWorldLoad(worldName);
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide()) {
            String worldName = event.getLevel().toString();
            instance.fireWorldUnload(worldName);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        instance.firePlayerJoin(event.getEntity().getName().getString());
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        instance.firePlayerLeave(event.getEntity().getName().getString());
    }

    // ============= EXISTING BLOCK/ITEM/ENTITY EVENTS =============

    @SubscribeEvent
    public static void onBlockAttack(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null) {
            instance.fireBlockAttack(event.getState().getBlock(), event.getState(), event.getLevel(),
                    event.getPos(), event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null) {
            instance.fireBlockBreak(event.getState().getBlock(), event.getLevel(), event.getPos(),
                    event.getState(), event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player) {
            instance.fireBlockPlace(event.getPlacedBlock().getBlock(), event.getLevel(), event.getPos(),
                    event.getState(), player);
        }
    }

    @SubscribeEvent
    public static void onBlockUse(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().isClientSide()) {
            InteractionResult result = instance.fireBlockUse(
                    event.getLevel().getBlockState(event.getPos()).getBlock(),
                    event.getLevel().getBlockState(event.getPos()),
                    event.getLevel(),
                    event.getPos(),
                    event.getEntity(),
                    event.getHand(),
                    event.getHitVec()
            );
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        BlockPos mainPos = event.getPos();
        BlockState mainState = event.getState();
        LevelAccessor levelAccessor = event.getLevel();

        if (!(levelAccessor instanceof Level level)) {
            return;
        }

        for (Direction direction : event.getNotifiedSides()) {
            BlockPos neighborPos = mainPos.relative(direction);
            BlockState neighborState = levelAccessor.getBlockState(neighborPos);
            Block neighborBlock = neighborState.getBlock();

            instance.fireBlockNeighborChanged(
                    mainState.getBlock(),
                    mainState,
                    levelAccessor,
                    mainPos,
                    neighborBlock,
                    neighborPos,
                    false
            );
        }
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
        if (!event.getLevel().isClientSide()) {
            ItemStack stack = event.getItemStack();
            InteractionResultHolder<ItemStack> result = instance.fireItemUse(
                    stack.getItem(),
                    event.getLevel(),
                    event.getEntity(),
                    event.getHand(),
                    stack
            );
            if (result.getResult() != InteractionResult.PASS) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onItemUseOn(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().isClientSide()) {
            ItemStack stack = event.getItemStack();
            UseOnContext context = new UseOnContext(
                    event.getEntity(),
                    event.getHand(),
                    event.getHitVec()
            );
            InteractionResult result = instance.fireItemUseOn(stack.getItem(), context, stack);
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        instance.fireItemCraftedBy(
                event.getCrafting().getItem(),
                event.getCrafting(),
                event.getEntity().level(),
                event.getEntity()
        );
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.getLevel().isClientSide()) {
            InteractionResult result = instance.fireEntityInteract(
                    event.getTarget().getType(),
                    event.getTarget(),
                    event.getEntity(),
                    event.getHand()
            );
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingDamageEvent.Post event) {
        if (!event.getEntity().level().isClientSide()) {
            instance.fireEntityHurt(
                    event.getEntity().getType(),
                    event.getEntity(),
                    event.getEntity().getLastDamageSource(),
                    event.getNewDamage()
            );
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            instance.fireEntityDeath(
                    event.getEntity().getType(),
                    event.getEntity(),
                    event.getSource()
            );
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            instance.fireEntitySpawn(
                    event.getEntity().getType(),
                    event.getEntity(),
                    event.getLevel()
            );
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            instance.fireEntityRemove(
                    event.getEntity().getType(),
                    event.getEntity(),
                    event.getEntity().getRemovalReason()
            );
        }
    }

    @SubscribeEvent
    public static void onLevelTick(EntityTickEvent.Post event) {
        instance.fireEntityTick(event.getEntity().getType(), event.getEntity());
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            Player player = event.getEntity();
            ItemStack stack = player.getItemInHand(player.getUsedItemHand());

            boolean handled = instance.fireItemOnLeftClickEntity(
                    stack.getItem(),
                    stack,
                    player,
                    event.getTarget()
            );

            if (handled) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide()) {
            Player player = event.getEntity();
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (!stack.isEmpty()) {
                    instance.fireItemInventoryTick(
                            stack.getItem(),
                            stack,
                            player.level(),
                            player,
                            i,
                            i == player.getInventory().selected
                    );
                }
            }
        }
    }
}