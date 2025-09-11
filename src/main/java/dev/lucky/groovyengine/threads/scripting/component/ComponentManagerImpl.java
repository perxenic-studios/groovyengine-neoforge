package dev.lucky.groovyengine.threads.scripting.component;

import dev.lucky.groovyengine.api.components.*;
import dev.lucky.groovyengine.GE;
import dev.lucky.groovyengine.api.components.types.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class ComponentManagerImpl implements ComponentManager {

    public static ComponentManager INSTANCE = new ComponentManagerImpl();

    private final Map<Item, List<ScriptComponent<Item>>> itemComponents = new HashMap<>();
    private final Map<Entity, List<ScriptComponent<Entity>>> entityComponents = new HashMap<>();
    private final Map<Block, List<ScriptComponent<Block>>> blockComponents = new HashMap<>();
    private final Map<Class<?>, List<ScriptComponent<?>>> genericComponents = new HashMap<>();

    @Override
    public void registerComponent(ScriptComponent<?> component) {
        Object target = component.getTarget();

        if (target instanceof Item item) {
            itemComponents.computeIfAbsent(item, k -> new ArrayList<>()).add((ScriptComponent<Item>) component);
            GE.LOG.info("Registered item component for: {}", BuiltInRegistries.ITEM.getKey(item));
        } else if (target instanceof Block block) {
            blockComponents.computeIfAbsent(block, k -> new ArrayList<>()).add((ScriptComponent<Block>) component);
            GE.LOG.info("Registered block component for: {}", BuiltInRegistries.BLOCK.getKey(block));
        } else if (target instanceof Entity entity) {
            entityComponents.computeIfAbsent(entity, k -> new ArrayList<>()).add((ScriptComponent<Entity>) component);
            GE.LOG.info("Registered entity component for: {}", entity.getType());
        } else {
            genericComponents.computeIfAbsent(component.getClass(), k -> new ArrayList<>()).add(component);
            GE.LOG.info("Registered generic component: {}", component.getClass().getSimpleName());
        }

        component.onInit();
    }

    @Override
    public void onItemUse(Item item, ItemStack stack, Player player, Level level) {
        List<ScriptComponent<Item>> components = itemComponents.get(item);
        if (components != null) {
            for (ScriptComponent<Item> component : components) {
                if (component instanceof ItemComponent<?> ic) {
                    ic.onUse(level, player, stack);
                }
            }
        }
    }

    @Override
    public void onItemInventoryTick(Item item, ItemStack stack, Player player, int slot, Level level) {
        List<ScriptComponent<Item>> components = itemComponents.get(item);
        if (components != null) {
            for (ScriptComponent<Item> component : components) {
                if (component instanceof ItemComponent<?> ic) {
                    ic.onInventoryTick(level, player, stack, slot);
                }
            }
        }
    }

    @Override
    public void onBlockPlace(Block block, Level level, BlockPos pos, BlockState state) {
        List<ScriptComponent<Block>> components = blockComponents.get(block);
        if (components != null) {
            for (ScriptComponent<Block> component : components) {
                if (component instanceof BlockComponent bc) {
                    bc.onPlace(level, pos, state);
                }
            }
        }
    }

    @Override
    public void onBlockBreak(Block block, Level level, BlockPos pos, BlockState state) {
        List<ScriptComponent<Block>> components = blockComponents.get(block);
        if (components != null) {
            for (ScriptComponent<Block> component : components) {
                if (component instanceof BlockComponent bc) {
                    bc.onBreak(level, pos, state);
                }
            }
        }
    }

    @Override
    public void onEntityTick(Entity entity) {
        List<ScriptComponent<Entity>> components = entityComponents.get(entity);
        if (components != null) {
            for (ScriptComponent<Entity> component : components) {
                if (component instanceof EntityComponent ec) {
                    ec.onTick(entity);
                }
            }
        }
    }

    @Override
    public void onGlobalTick() {
        for (List<ScriptComponent<?>> components : genericComponents.values()) {
            for (ScriptComponent<?> component : components) {
                component.onUpdate();
            }
        }
    }

    @Override
    public boolean hasItemComponents(Item item) {
        return itemComponents.containsKey(item);
    }

    @Override
    public boolean hasBlockComponents(Block block) {
        return blockComponents.containsKey(block);
    }

    @Override
    public boolean hasEntityComponents(Entity entity) {
        return entityComponents.containsKey(entity);
    }

    public static ComponentManager getInstance() {
        return INSTANCE;
    }
}
