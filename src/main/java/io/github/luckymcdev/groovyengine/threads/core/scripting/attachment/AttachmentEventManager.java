package io.github.luckymcdev.groovyengine.threads.core.scripting.attachment;

import dev.perxenic.acidapi.api.datagen.*;
import io.github.luckymcdev.groovyengine.threads.api.attachments.AttachmentManager;
import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;
import io.github.luckymcdev.groovyengine.threads.api.attachments.block.BlockAttachment;
import io.github.luckymcdev.groovyengine.threads.api.attachments.client.ClientAttachment;
import io.github.luckymcdev.groovyengine.threads.api.attachments.entity.EntityAttachment;
import io.github.luckymcdev.groovyengine.threads.api.attachments.item.ItemAttachment;
import io.github.luckymcdev.groovyengine.threads.api.attachments.registry.RegistryAttachment;
import io.github.luckymcdev.groovyengine.threads.api.attachments.script.ScriptAttachment;
import io.github.luckymcdev.groovyengine.threads.api.attachments.server.ServerAttachment;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.IEventBus;

import java.util.List;

public class AttachmentEventManager {

    private static final AttachmentEventManager INSTANCE = new AttachmentEventManager();
    private final AttachmentManager attachmentManager = AttachmentManager.getInstance();

    public static AttachmentEventManager getInstance() {
        return INSTANCE;
    }

    // ============= BLOCK EVENTS =============

    public void fireBlockPlace(Block block, LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        List<BaseAttachment> attachments = attachmentManager.getBlockAttachments(block);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof BlockAttachment ba) {
                ba.onPlace(level, pos, state, player);
            }
        }
    }

    public void fireBlockBreak(Block block, LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        List<BaseAttachment> attachments = attachmentManager.getBlockAttachments(block);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof BlockAttachment ba) {
                ba.onBreak(level, pos, state, player);
            }
        }
    }

    public InteractionResult fireBlockUse(Block block, BlockState state, LevelAccessor level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        List<BaseAttachment> attachments = attachmentManager.getBlockAttachments(block);
        InteractionResult finalResult = InteractionResult.PASS;
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof BlockAttachment ba) {
                InteractionResult result = ba.onUse(state, level, pos, player, hand, hit);
                if (result != InteractionResult.PASS) {
                    finalResult = result;
                }
            }
        }
        return finalResult;
    }

    public void fireBlockAttack(Block block, BlockState state, LevelAccessor level, BlockPos pos, Player player) {
        List<BaseAttachment> attachments = attachmentManager.getBlockAttachments(block);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof BlockAttachment ba) {
                ba.onAttack(state, level, pos, player);
            }
        }
    }

    public void fireBlockNeighborChanged(Block block, BlockState state, LevelAccessor level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        List<BaseAttachment> attachments = attachmentManager.getBlockAttachments(block);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof BlockAttachment ba) {
                ba.onNeighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
            }
        }
    }

    // ============= ITEM EVENTS =============

    public InteractionResultHolder<ItemStack> fireItemUse(Item item, LevelAccessor level, Player player, InteractionHand hand, ItemStack stack) {
        List<BaseAttachment> attachments = attachmentManager.getItemAttachments(item);
        for (BaseAttachment attachment : attachments) {
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
        List<BaseAttachment> attachments = attachmentManager.getItemAttachments(item);
        for (BaseAttachment attachment : attachments) {
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
        List<BaseAttachment> attachments = attachmentManager.getItemAttachments(item);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ItemAttachment ia) {
                ia.onInventoryTick(stack, level, entity, slotId, isSelected);
            }
        }
    }

    public void fireItemCraftedBy(Item item, ItemStack stack, LevelAccessor level, Player player) {
        List<BaseAttachment> attachments = attachmentManager.getItemAttachments(item);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ItemAttachment ia) {
                ia.onCraftedBy(stack, level, player);
            }
        }
    }

    public boolean fireItemOnLeftClickEntity(Item item, ItemStack stack, Player player, Entity entity) {
        List<BaseAttachment> attachments = attachmentManager.getItemAttachments(item);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ItemAttachment ia) {
                if (ia.onLeftClickEntity(stack, player, entity)) {
                    return true;
                }
            }
        }
        return false;
    }

    // ============= ENTITY EVENTS =============

    public void fireEntitySpawn(EntityType<?> entityType, Entity entity, LevelAccessor level) {
        List<BaseAttachment> attachments = attachmentManager.getEntityAttachments(entityType);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof EntityAttachment ea) {
                ea.onSpawn(entity, level);
            }
        }
    }

    public void fireEntityRemove(EntityType<?> entityType, Entity entity, Entity.RemovalReason reason) {
        List<BaseAttachment> attachments = attachmentManager.getEntityAttachments(entityType);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof EntityAttachment ea) {
                ea.onRemove(entity, reason);
            }
        }
    }

    public void fireEntityTick(EntityType<?> entityType, Entity entity) {
        List<BaseAttachment> attachments = attachmentManager.getEntityAttachments(entityType);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof EntityAttachment ea) {
                ea.onTick(entity);
            }
        }
    }

    public InteractionResult fireEntityInteract(EntityType<?> entityType, Entity entity, Player player, InteractionHand hand) {
        List<BaseAttachment> attachments = attachmentManager.getEntityAttachments(entityType);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof EntityAttachment ea) {
                InteractionResult result = ea.onInteract(entity, player, hand);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public void fireEntityHurt(EntityType<?> entityType, LivingEntity entity, DamageSource source, float amount) {
        List<BaseAttachment> attachments = attachmentManager.getEntityAttachments(entityType);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof EntityAttachment ea) {
                ea.onHurt(entity, source, amount);
            }
        }
    }

    public void fireEntityDeath(EntityType<?> entityType, LivingEntity entity, DamageSource source) {
        List<BaseAttachment> attachments = attachmentManager.getEntityAttachments(entityType);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof EntityAttachment ea) {
                ea.onDeath(entity, source);
            }
        }
    }

    // ============= SCRIPT EVENTS =============

    public void fireScriptLoad(String scriptId) {
        List<BaseAttachment> attachments = attachmentManager.getScriptAttachments(scriptId);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ScriptAttachment sa) {
                sa.onScriptLoad();
            }
        }
    }

    public void fireScriptReload(String scriptId) {
        List<BaseAttachment> attachments = attachmentManager.getScriptAttachments(scriptId);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ScriptAttachment sa) {
                sa.onScriptReload();
            }
        }
    }

    public void fireScriptError(String scriptId, Exception error) {
        List<BaseAttachment> attachments = attachmentManager.getScriptAttachments(scriptId);
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ScriptAttachment sa) {
                sa.onScriptError(error);
            }
        }
    }

    // ============= CLIENT EVENTS =============

    public void fireClientStart() {
        List<BaseAttachment> attachments = attachmentManager.getClientAttachments();
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ClientAttachment ca) {
                ca.onClientStart();
            }
        }
    }

    public void fireClientTick() {
        List<BaseAttachment> attachments = attachmentManager.getClientAttachments();
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ClientAttachment ca) {
                ca.onClientTick();
            }
        }
    }

    public void fireKeyPress(int key, int action, int modifiers) {
        List<BaseAttachment> attachments = attachmentManager.getClientAttachments();
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ClientAttachment ca) {
                ca.onKeyPress(key, action, modifiers);
            }
        }
    }

    public void fireMouseClick(int button, int action, int modifiers) {
        List<BaseAttachment> attachments = attachmentManager.getClientAttachments();
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ClientAttachment ca) {
                ca.onMouseClick(button, action, modifiers);
            }
        }
    }

    public void fireMouseScroll(double horizontal, double vertical) {
        List<BaseAttachment> attachments = attachmentManager.getClientAttachments();
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ClientAttachment ca) {
                ca.onMouseScroll(horizontal, vertical);
            }
        }
    }

    // ============= SERVER EVENTS =============

    public void fireServerStart() {
        List<BaseAttachment> attachments = attachmentManager.getServerAttachments();
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ServerAttachment sa) {
                sa.onServerStart();
            }
        }
    }

    public void fireServerStop() {
        List<BaseAttachment> attachments = attachmentManager.getServerAttachments();
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ServerAttachment sa) {
                sa.onServerStop();
            }
        }
    }

    public void fireServerTick() {
        List<BaseAttachment> attachments = attachmentManager.getServerAttachments();
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ServerAttachment sa) {
                sa.onServerTick();
            }
        }
    }

    public void fireWorldLoad(String worldName) {
        List<BaseAttachment> attachments = attachmentManager.getServerAttachments();
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ServerAttachment sa) {
                sa.onWorldLoad(worldName);
            }
        }
    }

    public void fireWorldUnload(String worldName) {
        List<BaseAttachment> attachments = attachmentManager.getServerAttachments();
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ServerAttachment sa) {
                sa.onWorldUnload(worldName);
            }
        }
    }

    public void firePlayerJoin(String playerName) {
        List<BaseAttachment> attachments = attachmentManager.getServerAttachments();
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ServerAttachment sa) {
                sa.onPlayerJoin(playerName);
            }
        }
    }

    public void firePlayerLeave(String playerName) {
        List<BaseAttachment> attachments = attachmentManager.getServerAttachments();
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof ServerAttachment sa) {
                sa.onPlayerLeave(playerName);
            }
        }
    }

    // ============= REGISTRY EVENTS =============

    public void fireOnRegister(IEventBus modEventBus) {
        List<BaseAttachment> attachments = attachmentManager.getRegistryAttachments();
        for (BaseAttachment attachment : attachments) {
            if (attachment instanceof RegistryAttachment sa) {
                sa.onRegister(modEventBus);
            }
        }
    }

}