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
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class AttachmentEventManagerImpl {
    private static final AttachmentEventManager instance = AttachmentEventManager.getInstance();

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