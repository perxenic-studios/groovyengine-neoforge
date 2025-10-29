# Attachments

The Attachment system is a powerful, event-driven API that allows your scripts to hook into a wide variety of game events. Instead of manually registering event listeners, you can create simple classes that "attach" to game objects, such as items, blocks, or entities, and respond to their specific events.

## Core Concepts

- **Attachment:** A class that extends one of the `BaseAttachment` types (e.g., `ItemAttachment`, `BlockAttachment`). It contains methods that are automatically called when specific game events occur.
- **Target:** The object an attachment is associated with. This can be a specific `Item`, `Block`, `EntityType`, or a global target like `client` or `server`.
- **AttachmentManager:** A global singleton used to register and manage all your attachments.

## Using Attachments

The process is straightforward:

1.  **Create a class** that extends the appropriate attachment type (e.g., `ItemAttachment`).
2.  **Override the methods** for the events you want to listen to (e.g., `onUse`).
3.  **Instantiate and register** your attachment with the `AttachmentManager`.

### Example: Creating a Magic Wand

Let's create a simple attachment that makes the Blaze Rod produce a particle effect when used.

**File: `client/MagicWand.groovy`**
```groovy
//priority=10
package client

import io.github.luckymcdev.groovyengine.threads.api.attachments.local.ItemAttachment
import net.minecraft.world.item.Items
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.item.ItemStack
import net.minecraft.core.particles.ParticleTypes

class MagicWandAttachment extends ItemAttachment {
    MagicWandAttachment() {
        // Target the Blaze Rod
        super(Items.BLAZE_ROD)
    }

    @Override
    InteractionResultHolder<ItemStack> onUse(LevelAccessor level, Player player, InteractionHand hand, ItemStack stack) {
        // Only run on the client side
        if (level.isClientSide()) {
            // Spawn some particles
            for (int i = 0; i < 10; i++) {
                level.addParticle(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1.5, player.getZ(), (Math.random() - 0.5) * 0.5, (Math.random() - 0.5) * 0.5, (Math.random() - 0.5) * 0.5)
            }
        }
        return InteractionResultHolder.success(stack)
    }
}
```

**File: `client/_RegisterAttachments.groovy`**
```groovy
//priority=0
package client

import io.github.luckymcdev.groovyengine.threads.api.attachments.AttachmentManager

// Register the attachment
AttachmentManager.getInstance().register(new MagicWandAttachment())
```

Now, whenever a player right-clicks with a Blaze Rod, it will emit enchantment particles.

## Attachment Types

Here is a summary of the available attachment types and their purposes.

### `ItemAttachment`
- **Target:** One or more `net.minecraft.world.item.Item`.
- **Events:** `onUse`, `onUseOn`, `onInventoryTick`, `onCraftedBy`, `onLeftClickEntity`.
- **Use Case:** Creating custom item behaviors, tools, or weapons.

### `BlockAttachment`
- **Target:** One or more `net.minecraft.world.level.block.Block`.
- **Events:** `onPlace`, `onBreak`, `onUse`, `onAttack`, `onNeighborChanged`.
- **Use Case:** Adding custom logic to blocks when they are interacted with or changed.

### `EntityAttachment`
- **Target:** One or more `net.minecraft.world.entity.EntityType`.
- **Events:** `onSpawn`, `onRemove`, `onTick`, `onInteract`, `onHurt`, `onDeath`.
- **Use Case:** Modifying entity behavior, adding custom AI, or tracking entity stats.

### `ClientAttachment`
- **Target:** The global `client`.
- **Events:** `onClientTick`, `onKeyPress`, `onMouseClick`, `onMouseScroll`, `onClientStart`.
- **Use Case:** Creating client-side systems, custom HUDs, or handling user input.

### `ServerAttachment`
- **Target:** The global `server`.
- **Events:** `onServerStart`, `onServerStop`, `onServerTick`, `onWorldLoad`, `onWorldUnload`, `onPlayerJoin`, `onPlayerLeave`.
- **Use Case:** Implementing server-wide game logic, player management, or world-level systems.

### `ScriptAttachment`
- **Target:** A specific script file name (e.g., `"MyScript.groovy"`).
- **Events:** `onScriptLoad`, `onScriptReload`, `onScriptError`.
- **Use Case:** Managing a script's own lifecycle, state, or dependencies.

### `RegistryAttachment`
- **Target:** The global `registry` process.
- **Events:** This attachment type currently has no specific events but is used to hook into the game's registration phase for advanced use cases.
- **Use Case:** Dynamically registering custom content (items, blocks, etc.) from scripts.
