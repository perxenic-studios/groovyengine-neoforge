package io.github.luckymcdev.groovyengine.threads.api.attachments.item;

import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelAccessor;

import java.util.Collections;
import java.util.List;

public abstract class ItemAttachment implements BaseAttachment {

    public final List<Item> targetItems;

    /**
     * Constructor for single item attachment
     */
    public ItemAttachment(Item item) {
        this.targetItems = List.of(item);
    }

    /**
     * Constructor for multi-item attachment
     */
    public ItemAttachment(List<Item> items) {
        this.targetItems = List.copyOf(items);
    }

    /**
     * Constructor for multi-item attachment (varargs)
     */
    public ItemAttachment(Item... items) {
        this.targetItems = List.of(items);
    }

    @Override
    public Item getTarget() {
        return targetItems.isEmpty() ? null : targetItems.get(0);
    }

    @Override
    public List<Object> getTargets() {
        return Collections.singletonList(targetItems);
    }

    // Item Usage Events
    public InteractionResultHolder<ItemStack> onUse(LevelAccessor level, Player player, InteractionHand hand, ItemStack stack) {
        return InteractionResultHolder.pass(stack);
    }

    public InteractionResult onUseOn(UseOnContext context, ItemStack stack) {
        return InteractionResult.PASS;
    }

    // Item State Events
    public void onInventoryTick(ItemStack stack, LevelAccessor level, Entity entity, int slotId, boolean isSelected) {
    }

    public void onCraftedBy(ItemStack stack, LevelAccessor level, Player player) {
    }

    // Combat Events
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return false;
    }
}