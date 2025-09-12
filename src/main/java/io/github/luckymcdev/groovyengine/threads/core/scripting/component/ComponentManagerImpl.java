// ComponentManagerImpl.java - Full implementation with all methods
package io.github.luckymcdev.groovyengine.threads.core.scripting.component;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.api.components.ComponentManager;
import io.github.luckymcdev.groovyengine.api.components.types.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.InteractionHand;

import java.util.*;

public class ComponentManagerImpl implements ComponentManager {

    public static ComponentManager INSTANCE = new ComponentManagerImpl();

    private final Map<Item, List<BaseComponent<Item>>> itemComponents = new HashMap<>();
    private final Map<Entity, List<BaseComponent<Entity>>> entityComponents = new HashMap<>();
    private final Map<Block, List<BaseComponent<Block>>> blockComponents = new HashMap<>();
    private final Map<Level, List<BaseComponent<Level>>> worldComponents = new HashMap<>();
    private final Map<Player, List<BaseComponent<Player>>> playerComponents = new HashMap<>();
    private final Map<Class<?>, List<BaseComponent<?>>> genericComponents = new HashMap<>();

    @Override
    public void registerComponent(BaseComponent<?> component) {
        Object target = component.getTarget();

        if (target instanceof Item item) {
            itemComponents.computeIfAbsent(item, k -> new ArrayList<>()).add((BaseComponent<Item>) component);
            GE.LOG.info("Registered item component for: {}", BuiltInRegistries.ITEM.getKey(item));
        } else if (target instanceof Block block) {
            blockComponents.computeIfAbsent(block, k -> new ArrayList<>()).add((BaseComponent<Block>) component);
            GE.LOG.info("Registered block component for: {}", BuiltInRegistries.BLOCK.getKey(block));
        } else if (target instanceof Entity entity) {
            entityComponents.computeIfAbsent(entity, k -> new ArrayList<>()).add((BaseComponent<Entity>) component);
            GE.LOG.info("Registered entity component for: {}", entity.getType());
        } else if (target instanceof Level level) {
            worldComponents.computeIfAbsent(level, k -> new ArrayList<>()).add((BaseComponent<Level>) component);
            GE.LOG.info("Registered world component for level: {}", level.dimension().location());
        } else if (target instanceof Player player) {
            playerComponents.computeIfAbsent(player, k -> new ArrayList<>()).add((BaseComponent<Player>) component);
            GE.LOG.info("Registered player component for: {}", player.getName().getString());
        } else {
            genericComponents.computeIfAbsent(component.getClass(), k -> new ArrayList<>()).add(component);
            GE.LOG.info("Registered generic component: {}", component.getClass().getSimpleName());
        }

        component.onInit();
    }

    // Item component methods
    @Override
    public void onItemUse(Item item, ItemStack stack, Player player, Level level, InteractionHand hand) {
        List<BaseComponent<Item>> components = itemComponents.get(item);
        if (components != null) {
            for (BaseComponent<Item> component : components) {
                if (component instanceof ItemComponent<?> ic) {
                    ic.onUse(level, player, hand, stack);
                }
            }
        }
    }

    @Override
    public void onItemInventoryTick(Item item, ItemStack stack, Player player, int slot, Level level) {
        List<BaseComponent<Item>> components = itemComponents.get(item);
        if (components != null) {
            for (BaseComponent<Item> component : components) {
                if (component instanceof ItemComponent<?> ic) {
                    ic.onInventoryTick(level, player, stack, slot);
                }
            }
        }
    }

    @Override
    public void onItemCrafted(Item item, ItemStack stack, Player player, Level level) {
        List<BaseComponent<Item>> components = itemComponents.get(item);
        if (components != null) {
            for (BaseComponent<Item> component : components) {
                if (component instanceof ItemComponent<?> ic) {
                    ic.onCrafted(level, player, stack);
                }
            }
        }
    }

    @Override
    public void onItemDestroyed(Item item, ItemStack stack, Player player, Level level) {
        List<BaseComponent<Item>> components = itemComponents.get(item);
        if (components != null) {
            for (BaseComponent<Item> component : components) {
                if (component instanceof ItemComponent<?> ic) {
                    ic.onDestroyed(level, player, stack);
                }
            }
        }
    }

    @Override
    public void onItemDropped(Item item, ItemStack stack, Player player, Level level) {
        List<BaseComponent<Item>> components = itemComponents.get(item);
        if (components != null) {
            for (BaseComponent<Item> component : components) {
                if (component instanceof ItemComponent<?> ic) {
                    ic.onDropped(level, player, stack);
                }
            }
        }
    }

    // Block component methods
    @Override
    public void onBlockPlace(Block block, Level level, BlockPos pos, BlockState state) {
        List<BaseComponent<Block>> components = blockComponents.get(block);
        if (components != null) {
            for (BaseComponent<Block> component : components) {
                if (component instanceof BlockComponent bc) {
                    bc.onPlace(level, pos, state);
                }
            }
        }
    }

    @Override
    public void onBlockBreak(Block block, Level level, BlockPos pos, BlockState state) {
        List<BaseComponent<Block>> components = blockComponents.get(block);
        if (components != null) {
            for (BaseComponent<Block> component : components) {
                if (component instanceof BlockComponent bc) {
                    bc.onBreak(level, pos, state);
                }
            }
        }
    }

    @Override
    public void onBlockNeighborUpdate(Block block, Level level, BlockPos pos, BlockState state, BlockPos neighborPos) {
        List<BaseComponent<Block>> components = blockComponents.get(block);
        if (components != null) {
            for (BaseComponent<Block> component : components) {
                if (component instanceof BlockComponent bc) {
                    bc.onNeighborUpdate(level, pos, state, neighborPos);
                }
            }
        }
    }

    @Override
    public void onBlockEntityWalk(Block block, Level level, BlockPos pos, BlockState state, Entity entity) {
        List<BaseComponent<Block>> components = blockComponents.get(block);
        if (components != null) {
            for (BaseComponent<Block> component : components) {
                if (component instanceof BlockComponent bc) {
                    bc.onEntityWalk(level, pos, state, entity);
                }
            }
        }
    }

    @Override
    public void onBlockRandomTick(Block block, Level level, BlockPos pos, BlockState state) {
        List<BaseComponent<Block>> components = blockComponents.get(block);
        if (components != null) {
            for (BaseComponent<Block> component : components) {
                if (component instanceof BlockComponent bc) {
                    bc.onRandomTick(level, pos, state);
                }
            }
        }
    }

    @Override
    public void onBlockFallOn(Block block, Level level, BlockPos pos, BlockState state, Entity entity, float fallDistance) {
        List<BaseComponent<Block>> components = blockComponents.get(block);
        if (components != null) {
            for (BaseComponent<Block> component : components) {
                if (component instanceof BlockComponent bc) {
                    bc.onFallOn(level, pos, state, entity, fallDistance);
                }
            }
        }
    }

    // Entity component methods
    @Override
    public void onEntityTick(Entity entity) {
        List<BaseComponent<Entity>> components = entityComponents.get(entity);
        if (components != null) {
            for (BaseComponent<Entity> component : components) {
                if (component instanceof EntityComponent ec) {
                    ec.onTick(entity);
                }
            }
        }
    }

    @Override
    public void onEntityInteract(Entity entity) {
        List<BaseComponent<Entity>> components = entityComponents.get(entity);
        if (components != null) {
            for (BaseComponent<Entity> component : components) {
                if (component instanceof EntityComponent ec) {
                    ec.onInteract(entity);
                }
            }
        }
    }

    @Override
    public void onEntityDamage(Entity entity, DamageSource source) {
        List<BaseComponent<Entity>> components = entityComponents.get(entity);
        if (components != null) {
            for (BaseComponent<Entity> component : components) {
                if (component instanceof EntityComponent ec) {
                    ec.onDamage(entity, source);
                }
            }
        }
    }

    @Override
    public void onEntityDeath(Entity entity, DamageSource source) {
        List<BaseComponent<Entity>> components = entityComponents.get(entity);
        if (components != null) {
            for (BaseComponent<Entity> component : components) {
                if (component instanceof EntityComponent ec) {
                    ec.onDeath(entity, source);
                }
            }
        }
    }

    @Override
    public void onEntitySpawn(Entity entity) {
        List<BaseComponent<Entity>> components = entityComponents.get(entity);
        if (components != null) {
            for (BaseComponent<Entity> component : components) {
                if (component instanceof EntityComponent ec) {
                    ec.onSpawn(entity);
                }
            }
        }
    }

    @Override
    public void onEntityCollide(Entity entity, Entity other) {
        List<BaseComponent<Entity>> components = entityComponents.get(entity);
        if (components != null) {
            for (BaseComponent<Entity> component : components) {
                if (component instanceof EntityComponent ec) {
                    ec.onCollide(entity, other);
                }
            }
        }
    }

    @Override
    public void onEntityMount(Entity entity, Entity passenger) {
        List<BaseComponent<Entity>> components = entityComponents.get(entity);
        if (components != null) {
            for (BaseComponent<Entity> component : components) {
                if (component instanceof EntityComponent ec) {
                    ec.onMount(entity, passenger);
                }
            }
        }
    }

    @Override
    public void onEntityDismount(Entity entity, Entity passenger) {
        List<BaseComponent<Entity>> components = entityComponents.get(entity);
        if (components != null) {
            for (BaseComponent<Entity> component : components) {
                if (component instanceof EntityComponent ec) {
                    ec.onDismount(entity, passenger);
                }
            }
        }
    }

    // World component methods
    @Override
    public void onWorldLoad(Level level) {
        List<BaseComponent<Level>> components = worldComponents.get(level);
        if (components != null) {
            for (BaseComponent<Level> component : components) {
                if (component instanceof WorldComponent wc) {
                    wc.onWorldLoad(level);
                }
            }
        }
    }

    @Override
    public void onWorldUnload(Level level) {
        List<BaseComponent<Level>> components = worldComponents.get(level);
        if (components != null) {
            for (BaseComponent<Level> component : components) {
                if (component instanceof WorldComponent wc) {
                    wc.onWorldUnload(level);
                }
            }
        }
    }

    @Override
    public void onChunkLoad(Level level, BlockPos chunkPos) {
        List<BaseComponent<Level>> components = worldComponents.get(level);
        if (components != null) {
            for (BaseComponent<Level> component : components) {
                if (component instanceof WorldComponent wc) {
                    wc.onChunkLoad(level, chunkPos);
                }
            }
        }
    }

    @Override
    public void onChunkUnload(Level level, BlockPos chunkPos) {
        List<BaseComponent<Level>> components = worldComponents.get(level);
        if (components != null) {
            for (BaseComponent<Level> component : components) {
                if (component instanceof WorldComponent wc) {
                    wc.onChunkUnload(level, chunkPos);
                }
            }
        }
    }

    @Override
    public void onBlockUpdate(Level level, BlockPos pos, BlockState state) {
        List<BaseComponent<Level>> components = worldComponents.get(level);
        if (components != null) {
            for (BaseComponent<Level> component : components) {
                if (component instanceof WorldComponent wc) {
                    wc.onBlockUpdate(level, pos, state);
                }
            }
        }
    }

    @Override
    public void onEntitySpawn(Level level, Entity entity) {
        List<BaseComponent<Level>> components = worldComponents.get(level);
        if (components != null) {
            for (BaseComponent<Level> component : components) {
                if (component instanceof WorldComponent wc) {
                    wc.onEntitySpawn(level, entity);
                }
            }
        }
    }

    @Override
    public void onEntityDespawn(Level level, Entity entity) {
        List<BaseComponent<Level>> components = worldComponents.get(level);
        if (components != null) {
            for (BaseComponent<Level> component : components) {
                if (component instanceof WorldComponent wc) {
                    wc.onEntityDespawn(level, entity);
                }
            }
        }
    }

    @Override
    public void onWeatherChange(Level level, boolean raining) {
        List<BaseComponent<Level>> components = worldComponents.get(level);
        if (components != null) {
            for (BaseComponent<Level> component : components) {
                if (component instanceof WorldComponent wc) {
                    wc.onWeatherChange(level, raining);
                }
            }
        }
    }

    @Override
    public void onTimeChange(Level level, long time) {
        List<BaseComponent<Level>> components = worldComponents.get(level);
        if (components != null) {
            for (BaseComponent<Level> component : components) {
                if (component instanceof WorldComponent wc) {
                    wc.onTimeChange(level, time);
                }
            }
        }
    }

    @Override
    public void onExplosion(Level level, double x, double y, double z, float power) {
        List<BaseComponent<Level>> components = worldComponents.get(level);
        if (components != null) {
            for (BaseComponent<Level> component : components) {
                if (component instanceof WorldComponent wc) {
                    wc.onExplosion(level, x, y, z, power);
                }
            }
        }
    }

    // Player component methods
    @Override
    public void onPlayerJoin(Level level, Player player) {
        List<BaseComponent<Player>> components = playerComponents.get(player);
        if (components != null) {
            for (BaseComponent<Player> component : components) {
                if (component instanceof PlayerComponent pc) {
                    pc.onPlayerJoin(level, player);
                }
            }
        }
    }

    @Override
    public void onPlayerLeave(Level level, Player player) {
        List<BaseComponent<Player>> components = playerComponents.get(player);
        if (components != null) {
            for (BaseComponent<Player> component : components) {
                if (component instanceof PlayerComponent pc) {
                    pc.onPlayerLeave(level, player);
                }
            }
        }
    }

    @Override
    public void onPlayerRespawn(Level level, Player player) {
        List<BaseComponent<Player>> components = playerComponents.get(player);
        if (components != null) {
            for (BaseComponent<Player> component : components) {
                if (component instanceof PlayerComponent pc) {
                    pc.onPlayerRespawn(level, player);
                }
            }
        }
    }

    @Override
    public void onPlayerAttack(Level level, Player player, Entity target) {
        List<BaseComponent<Player>> components = playerComponents.get(player);
        if (components != null) {
            for (BaseComponent<Player> component : components) {
                if (component instanceof PlayerComponent pc) {
                    pc.onPlayerAttack(level, player, target);
                }
            }
        }
    }

    @Override
    public void onPlayerInteractEntity(Level level, Player player, Entity entity, InteractionHand hand) {
        List<BaseComponent<Player>> components = playerComponents.get(player);
        if (components != null) {
            for (BaseComponent<Player> component : components) {
                if (component instanceof PlayerComponent pc) {
                    pc.onPlayerInteractEntity(level, player, entity, hand);
                }
            }
        }
    }

    @Override
    public void onPlayerPickupItem(Level level, Player player, ItemStack stack) {
        List<BaseComponent<Player>> components = playerComponents.get(player);
        if (components != null) {
            for (BaseComponent<Player> component : components) {
                if (component instanceof PlayerComponent pc) {
                    pc.onPlayerPickupItem(level, player, stack);
                }
            }
        }
    }

    @Override
    public void onPlayerBreakBlock(Level level, Player player, BlockPos pos, BlockState state) {
        List<BaseComponent<Player>> components = playerComponents.get(player);
        if (components != null) {
            for (BaseComponent<Player> component : components) {
                if (component instanceof PlayerComponent pc) {
                    pc.onPlayerBreakBlock(level, player, pos, state);
                }
            }
        }
    }

    @Override
    public void onPlayerPlaceBlock(Level level, Player player, BlockPos pos, BlockState state) {
        List<BaseComponent<Player>> components = playerComponents.get(player);
        if (components != null) {
            for (BaseComponent<Player> component : components) {
                if (component instanceof PlayerComponent pc) {
                    pc.onPlayerPlaceBlock(level, player, pos, state);
                }
            }
        }
    }

    @Override
    public void onPlayerRaycast(Level level, Player player, HitResult hitResult) {
        List<BaseComponent<Player>> components = playerComponents.get(player);
        if (components != null) {
            for (BaseComponent<Player> component : components) {
                if (component instanceof PlayerComponent pc) {
                    pc.onPlayerRaycast(level, player, hitResult);
                }
            }
        }
    }

    @Override
    public void onPlayerDamage(Level level, Player player, DamageSource source) {
        List<BaseComponent<Player>> components = playerComponents.get(player);
        if (components != null) {
            for (BaseComponent<Player> component : components) {
                if (component instanceof PlayerComponent pc) {
                    pc.onPlayerDamage(level, player, source);
                }
            }
        }
    }

    @Override
    public void onPlayerDeath(Level level, Player player, DamageSource source) {
        List<BaseComponent<Player>> components = playerComponents.get(player);
        if (components != null) {
            for (BaseComponent<Player> component : components) {
                if (component instanceof PlayerComponent pc) {
                    pc.onPlayerDeath(level, player, source);
                }
            }
        }
    }

    @Override
    public void onPlayerSleep(Level level, Player player, BlockPos pos) {
        List<BaseComponent<Player>> components = playerComponents.get(player);
        if (components != null) {
            for (BaseComponent<Player> component : components) {
                if (component instanceof PlayerComponent pc) {
                    pc.onPlayerSleep(level, player, pos);
                }
            }
        }
    }

    @Override
    public void onPlayerWake(Level level, Player player) {
        List<BaseComponent<Player>> components = playerComponents.get(player);
        if (components != null) {
            for (BaseComponent<Player> component : components) {
                if (component instanceof PlayerComponent pc) {
                    pc.onPlayerWake(level, player);
                }
            }
        }
    }

    // Global tick
    @Override
    public void onGlobalTick() {
        for (List<BaseComponent<?>> components : genericComponents.values()) {
            for (BaseComponent<?> component : components) {
                component.onUpdate();
            }
        }

        // Also tick entity components
        for (Map.Entry<Entity, List<BaseComponent<Entity>>> entry : entityComponents.entrySet()) {
            for (BaseComponent<Entity> component : entry.getValue()) {
                if (component instanceof EntityComponent ec) {
                    ec.onTick(entry.getKey());
                }
            }
        }
    }

    // Check methods
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

    @Override
    public boolean hasWorldComponents(Level level) {
        return worldComponents.containsKey(level);
    }

    @Override
    public boolean hasPlayerComponents(Player player) {
        return playerComponents.containsKey(player);
    }

    public static ComponentManager getInstance() {
        return INSTANCE;
    }
}