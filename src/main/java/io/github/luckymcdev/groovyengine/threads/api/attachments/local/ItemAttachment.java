package io.github.luckymcdev.groovyengine.threads.api.attachments.local;

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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Attachment for adding custom behavior to items.
 * Supports targeting specific items or matching items based on a predicate.
 */
public abstract class ItemAttachment implements BaseAttachment {

    public final Set<Item> targetItems;
    private final Predicate<Item> itemMatcher;

    /**
     * Constructor for single item attachment
     */
    public ItemAttachment(Item item) {
        this.targetItems = Set.of(item);
        this.itemMatcher = null;
    }

    /**
     * Constructor for multiple specific items
     */
    public ItemAttachment(Item... items) {
        this.targetItems = new HashSet<>(Arrays.asList(items));
        this.itemMatcher = null;
    }

    /**
     * Constructor for predicate-based matching (e.g., all tools, all food items)
     */
    public ItemAttachment(Predicate<Item> itemMatcher) {
        this.targetItems = Set.of();
        this.itemMatcher = itemMatcher;
    }

    @Override
    public final boolean appliesTo(Object target) {
        if (target instanceof Item item) {
            if (itemMatcher != null) {
                return itemMatcher.test(item);
            }
            return targetItems.contains(item);
        }
        return false;
    }

    // === Item Usage Events ===

    /**
     * Called when a player uses (right-clicks) the item
     *
     * @return The interaction result holder. Return PASS to allow other attachments to process.
     */
    public InteractionResultHolder<ItemStack> onUse(LevelAccessor level, Player player, InteractionHand hand, ItemStack stack) {
        return InteractionResultHolder.pass(stack);
    }

    /**
     * Called when a player uses the item on a block (right-click on block)
     *
     * @return The interaction result. Return PASS to allow other attachments to process.
     */
    public InteractionResult onUseOn(UseOnContext context, ItemStack stack) {
        return InteractionResult.PASS;
    }

    /**
     * Called every tick while the item is in a player's inventory
     */
    public void onInventoryTick(ItemStack stack, LevelAccessor level, Entity entity, int slotId, boolean isSelected) {
    }

    /**
     * Called when the item is crafted by a player
     */
    public void onCraftedBy(ItemStack stack, LevelAccessor level, Player player) {
    }

    /**
     * Called when a player left-clicks an entity while holding the item
     *
     * @return true to cancel the default attack behavior
     */
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return false;
    }
}