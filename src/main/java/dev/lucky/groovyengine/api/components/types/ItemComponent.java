package dev.lucky.groovyengine.api.components.types;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class ItemComponent<T extends Item> implements ScriptComponent<T> {

    public void onUse(Level level, Player player, ItemStack stack) {}

    public void onInventoryTick(Level level, Player player, ItemStack stack, int slot) {}
}