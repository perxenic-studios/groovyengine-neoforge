package io.github.luckymcdev.groovyengine.api.components;

import io.github.luckymcdev.groovyengine.api.components.types.ScriptComponent;
import io.github.luckymcdev.groovyengine.threads.scripting.component.ComponentManagerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface ComponentManager {

    static ComponentManager getInstance() {
        return ComponentManagerImpl.INSTANCE;
    }

    void registerComponent(ScriptComponent<?> component);

    void onItemUse(Item item, ItemStack stack, Player player, Level level);

    void onItemInventoryTick(Item item, ItemStack stack, Player player, int slot, Level level);

    void onBlockPlace(Block block, Level level, BlockPos pos, BlockState state);

    void onBlockBreak(Block block, Level level, BlockPos pos, BlockState state);

    void onEntityTick(Entity entity);

    void onGlobalTick();

    boolean hasItemComponents(Item item);

    boolean hasBlockComponents(Block block);

    boolean hasEntityComponents(Entity entity);
}
