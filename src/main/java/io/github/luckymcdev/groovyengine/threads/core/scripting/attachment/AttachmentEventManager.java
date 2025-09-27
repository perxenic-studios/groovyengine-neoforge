package io.github.luckymcdev.groovyengine.threads.core.scripting.attachment;

import io.github.luckymcdev.groovyengine.api.attachments.AttachmentManager;
import io.github.luckymcdev.groovyengine.api.attachments.BaseAttachment;
import io.github.luckymcdev.groovyengine.api.attachments.block.BlockAttachment;
import io.github.luckymcdev.groovyengine.api.attachments.entity.EntityAttachment;
import io.github.luckymcdev.groovyengine.api.attachments.item.ItemAttachment;
import io.github.luckymcdev.groovyengine.api.attachments.script.ScriptAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;

public class AttachmentEventManager {

    private static final AttachmentEventManager INSTANCE = new AttachmentEventManager();
    private final AttachmentManager attachmentManager = AttachmentManager.getInstance();

    public static AttachmentEventManager getInstance() {
        return INSTANCE;
    }

    // ============= BLOCK EVENTS =============

    public void fireBlockPlace(Block block, LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        List<BaseAttachment<Block>> attachments = attachmentManager.getBlockAttachments(block);
        for (BaseAttachment<Block> attachment : attachments) {
            if (attachment instanceof BlockAttachment ba) {
                ba.onPlace(level, pos, state, player);
            }
        }
    }

    public void fireBlockBreak(Block block, LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        List<BaseAttachment<Block>> attachments = attachmentManager.getBlockAttachments(block);
        for (BaseAttachment<Block> attachment : attachments) {
            if (attachment instanceof BlockAttachment ba) {
                ba.onBreak(level, pos, state, player);
            }
        }
    }

    public InteractionResult fireBlockUse(Block block, BlockState state, LevelAccessor level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        List<BaseAttachment<Block>> attachments = attachmentManager.getBlockAttachments(block);
        for (BaseAttachment<Block> attachment : attachments) {
            if (attachment instanceof BlockAttachment ba) {
                InteractionResult result = ba.onUse(state, level, pos, player, hand, hit);
                if (result != InteractionResult.PASS) {
                    return result; // Return first non-PASS result
                }
            }
        }
        return InteractionResult.PASS;
    }

    public void fireBlockAttack(Block block, BlockState state, LevelAccessor level, BlockPos pos, Player player) {
        List<BaseAttachment<Block>> attachments = attachmentManager.getBlockAttachments(block);
        for (BaseAttachment<Block> attachment : attachments) {
            if (attachment instanceof BlockAttachment ba) {
                ba.onAttack(state, level, pos, player);
            }
        }
    }

    public void fireBlockNeighborChanged(Block block, BlockState state, LevelAccessor level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        List<BaseAttachment<Block>> attachments = attachmentManager.getBlockAttachments(block);
        for (BaseAttachment<Block> attachment : attachments) {
            if (attachment instanceof BlockAttachment ba) {
                ba.onNeighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
            }
        }
    }

    public InteractionResultHolder<ItemStack> fireItemUse(Item item, LevelAccessor level, Player player, InteractionHand hand, ItemStack stack) {
        List<BaseAttachment<Item>> attachments = attachmentManager.getItemAttachments(item);
        for (BaseAttachment<Item> attachment : attachments) {
            if (attachment instanceof ItemAttachment ia) {
                InteractionResultHolder<ItemStack> result = ia.onUse(level, player, hand, stack);
                if (result.getResult() != InteractionResult.PASS) {
                    return result; // Return first non-PASS result
                }
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    public InteractionResult fireItemUseOn(Item item, UseOnContext context, ItemStack stack) {
        List<BaseAttachment<Item>> attachments = attachmentManager.getItemAttachments(item);
        for (BaseAttachment<Item> attachment : attachments) {
            if (attachment instanceof ItemAttachment ia) {
                InteractionResult result = ia.onUseOn(context, stack);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public void fireItemInventoryTick(Item item, ItemStack stack, LevelAccessor level, Entity entity, int slotId, boolean isSelected) {
        List<BaseAttachment<Item>> attachments = attachmentManager.getItemAttachments(item);
        for (BaseAttachment<Item> attachment : attachments) {
            if (attachment instanceof ItemAttachment ia) {
                ia.onInventoryTick(stack, level, entity, slotId, isSelected);
            }
        }
    }

    public void fireItemCraftedBy(Item item, ItemStack stack, LevelAccessor level, Player player) {
        List<BaseAttachment<Item>> attachments = attachmentManager.getItemAttachments(item);
        for (BaseAttachment<Item> attachment : attachments) {
            if (attachment instanceof ItemAttachment ia) {
                ia.onCraftedBy(stack, level, player);
            }
        }
    }

    public boolean fireItemOnLeftClickEntity(Item item, ItemStack stack, Player player, Entity entity) {
        List<BaseAttachment<Item>> attachments = attachmentManager.getItemAttachments(item);
        for (BaseAttachment<Item> attachment : attachments) {
            if (attachment instanceof ItemAttachment ia) {
                if (ia.onLeftClickEntity(stack, player, entity)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void fireEntitySpawn(EntityType<?> entityType, Entity entity, LevelAccessor level) {
        List<BaseAttachment<EntityType<?>>> attachments = attachmentManager.getEntityAttachments(entityType);
        for (BaseAttachment<EntityType<?>> attachment : attachments) {
            if (attachment instanceof EntityAttachment ea) {
                ea.onSpawn(entity, level);
            }
        }
    }

    public void fireEntityRemove(EntityType<?> entityType, Entity entity, Entity.RemovalReason reason) {
        List<BaseAttachment<EntityType<?>>> attachments = attachmentManager.getEntityAttachments(entityType);
        for (BaseAttachment<EntityType<?>> attachment : attachments) {
            if (attachment instanceof EntityAttachment ea) {
                ea.onRemove(entity, reason);
            }
        }
    }

    public void fireEntityTick(EntityType<?> entityType, Entity entity) {
        List<BaseAttachment<EntityType<?>>> attachments = attachmentManager.getEntityAttachments(entityType);
        for (BaseAttachment<EntityType<?>> attachment : attachments) {
            if (attachment instanceof EntityAttachment ea) {
                ea.onTick(entity);
            }
        }
    }

    public InteractionResult fireEntityInteract(EntityType<?> entityType, Entity entity, Player player, InteractionHand hand) {
        List<BaseAttachment<EntityType<?>>> attachments = attachmentManager.getEntityAttachments(entityType);
        for (BaseAttachment<EntityType<?>> attachment : attachments) {
            if (attachment instanceof EntityAttachment ea) {
                InteractionResult result = ea.onInteract(entity, player, hand);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public void fireEntityHurt(EntityType<?> entityType, LivingEntity entity, DamageSource source, float ammount) {
        List<BaseAttachment<EntityType<?>>> attachments = attachmentManager.getEntityAttachments(entityType);
        for (BaseAttachment<EntityType<?>> attachment : attachments) {
            if (attachment instanceof EntityAttachment ea) {
                ea.onHurt(entity, source, ammount);
            }
        }
    }

    public void fireEntityDeath(EntityType<?> entityType, LivingEntity entity, DamageSource source) {
        List<BaseAttachment<EntityType<?>>> attachments = attachmentManager.getEntityAttachments(entityType);
        for (BaseAttachment<EntityType<?>> attachment : attachments) {
            if (attachment instanceof EntityAttachment ea) {
                ea.onDeath(entity, source);
            }
        }
    }

    // ============= SCRIPT EVENTS =============

    public void fireScriptLoad(String scriptId) {
        List<BaseAttachment<Object>> attachments = attachmentManager.getScriptAttachments(scriptId);
        for (BaseAttachment<Object> attachment : attachments) {
            if (attachment instanceof ScriptAttachment sa) {
                sa.onScriptLoad();
            }
        }
    }

    public void fireScriptReload(String scriptId) {
        List<BaseAttachment<Object>> attachments = attachmentManager.getScriptAttachments(scriptId);
        for (BaseAttachment<Object> attachment : attachments) {
            if (attachment instanceof ScriptAttachment sa) {
                sa.onScriptReload();
            }
        }
    }

    public void fireScriptError(String scriptId, Exception error) {
        List<BaseAttachment<Object>> attachments = attachmentManager.getScriptAttachments(scriptId);
        for (BaseAttachment<Object> attachment : attachments) {
            if (attachment instanceof ScriptAttachment sa) {
                sa.onScriptError(error);
            }
        }
    }

    // ============= UTILITY METHODS =============

    public void fireServerStart() {
        // Fire server start for all script attachments
        for (BaseAttachment<?> attachment : attachmentManager.getAttachments("server", String.class)) {
            if (attachment instanceof ScriptAttachment sa) {
                sa.onServerStart();
            }
        }
    }

    public void fireServerStop() {
        // Fire server stop for all script attachments
        for (BaseAttachment<?> attachment : attachmentManager.getAttachments("server", String.class)) {
            if (attachment instanceof ScriptAttachment sa) {
                sa.onServerStop();
            }
        }
    }
}