// ItemComponent.java - Enhanced with more events
package io.github.luckymcdev.groovyengine.api.components.types;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionHand;

public abstract class ItemComponent<T extends Item> implements BaseComponent<T> {

    public void onUse(Level level, Player player, InteractionHand hand, ItemStack stack) {}
    public void onInventoryTick(Level level, Player player, ItemStack stack, int slot) {}
    public void onCrafted(Level level, Player player, ItemStack stack) {}
    public void onDestroyed(Level level, Player player, ItemStack stack) {}
    public void onDropped(Level level, Player player, ItemStack stack) {}
}