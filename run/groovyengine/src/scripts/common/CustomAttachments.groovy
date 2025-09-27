package common

import io.github.luckymcdev.groovyengine.threads.api.attachments.AttachmentManager
import io.github.luckymcdev.groovyengine.threads.api.attachments.item.ItemAttachment
import io.github.luckymcdev.groovyengine.threads.api.attachments.block.BlockAttachment
import io.github.luckymcdev.groovyengine.threads.api.attachments.entity.EntityAttachment
import io.github.luckymcdev.groovyengine.threads.api.attachments.script.ScriptAttachment

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Block
import net.minecraft.world.entity.EntityType

// Script Attachment - Global events
class TestScriptAttachment extends ScriptAttachment {
    TestScriptAttachment() {
        super("test_script")
    }

    @Override
    void onScriptLoad() {
        println("Script loaded: ${scriptId}")
    }

    @Override
    void onScriptReload() {
        println("Script reloaded: ${scriptId}")
    }

    @Override
    void onScriptError(Exception error) {
        println("Script error: ${error.message}")
    }

    @Override
    void onServerStart() {
        println("Server started - script attachment")
    }

    @Override
    void onServerStop() {
        println("Server stopping - script attachment")
    }

    @Override
    void onInit() {
        println("Script Attachment initialized!")
    }

    @Override
    void onDestroy() {
        println("Script Attachment destroyed!")
    }
}

// Item Attachment - Testing all item events
class TestItemAttachment extends ItemAttachment {

    TestItemAttachment() {
        super(Items.STICK, Items.DIAMOND_SWORD, Items.APPLE)
    }

    @Override
    void onInit() {
        println("Item Attachment initialized for: ${targetItems*.descriptionId}")
    }

    @Override
    void onDestroy() {
        println("Item Attachment destroyed!")
    }

    @Override
    InteractionResultHolder<ItemStack> onUse(LevelAccessor level, Player player, InteractionHand hand, ItemStack stack) {
        println("Item used: ${stack.item.descriptionId} by ${player.name.string}")
        return InteractionResultHolder.success(stack)
    }

    @Override
    InteractionResult onUseOn(UseOnContext context, ItemStack stack) {
        println("Item used on block: ${stack.item.descriptionId} at ${context.clickLocation}")
        return InteractionResult.SUCCESS
    }

    @Override
    void onInventoryTick(ItemStack stack, LevelAccessor level, Entity entity, int slotId, boolean isSelected) {
        if (entity.tickCount % 100 == 0) { // Log every 5 seconds (100 ticks)
            println("Item inventory tick: ${stack.item.descriptionId} in slot ${slotId}, selected: ${isSelected}")
        }
    }

    @Override
    void onCraftedBy(ItemStack stack, LevelAccessor level, Player player) {
        println("Item crafted: ${stack.item.descriptionId} by ${player.name.string}")
    }

    @Override
    boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        println("Item left-click entity: ${stack.item.descriptionId} on ${entity.name.string}")
        return false // Return true to cancel the attack
    }
}

// Block Attachment - Testing all block events
class TestBlockAttachment extends BlockAttachment {
    TestBlockAttachment() {
        super(Blocks.STONE, Blocks.DIRT, Blocks.OAK_PLANKS)
    }

    @Override
    void onInit() {
        println("Block Attachment initialized for: ${targetBlocks*.descriptionId}")
    }

    @Override
    void onDestroy() {
        println("Block Attachment destroyed!")
    }

    @Override
    void onPlace(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        println("Block placed: ${state.block.descriptionId} at ${pos} by ${player.name.string}")
    }

    @Override
    void onBreak(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        println("Block broken: ${state.block.descriptionId} at ${pos} by ${player.name.string}")
    }

    @Override
    InteractionResult onUse(BlockState state, LevelAccessor level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        println("Block used: ${state.block.descriptionId} at ${pos} by ${player.name.string}")
        return InteractionResult.SUCCESS
    }

    @Override
    void onAttack(BlockState state, LevelAccessor level, BlockPos pos, Player player) {
        println("Block attacked: ${state.block.descriptionId} at ${pos} by ${player.name.string}")
    }

    @Override
    void onNeighborChanged(BlockState state, LevelAccessor level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        println("Block neighbor changed: ${state.block.descriptionId} at ${pos}, neighbor: ${neighborBlock.descriptionId}")
    }
}

// Entity Attachment - Testing all entity events
class TestEntityAttachment extends EntityAttachment {
    TestEntityAttachment() {
        super(EntityType.PLAYER, EntityType.ZOMBIE, EntityType.COW)
    }

    @Override
    void onInit() {
        //println("Entity Attachment initialized for: ${targetEntityTypes*.descriptionId}")
    }

    @Override
    void onDestroy() {
        //println("Entity Attachment destroyed!")
    }

    @Override
    void onSpawn(Entity entity, LevelAccessor level) {
        //println("Entity spawned: ${entity.name.string} (${entity.type.descriptionId})")
    }

    @Override
    void onRemove(Entity entity, Entity.RemovalReason reason) {
        //println("Entity removed: ${entity.name.string} (${entity.type.descriptionId}) - Reason: ${reason}")
    }

    @Override
    void onTick(Entity entity) {
        if (entity.tickCount % 100 == 0) { // Log every 5 seconds (100 ticks)
            //println("Entity tick: ${entity.name.string} (${entity.type.descriptionId})")
        }
    }

    @Override
    InteractionResult onInteract(Entity entity, Player player, InteractionHand hand) {
        //println("Entity interacted: ${entity.name.string} by ${player.name.string}")
        return InteractionResult.SUCCESS
    }

    @Override
    void onHurt(LivingEntity entity, DamageSource source, float amount) {
        //println("Entity hurt: ${entity.name.string} by ${source.type().msgId()} with amount ${amount}")
    }

    @Override
    void onDeath(LivingEntity entity, DamageSource source) {
        //println("Entity died: ${entity.name.string} by ${source.type().msgId()}")
    }
}

// Register all attachments
AttachmentManager attachmentManager = AttachmentManager.getInstance()

// Script attachment
TestScriptAttachment scriptAttachment = new TestScriptAttachment()
attachmentManager.register(scriptAttachment)

// Item attachment
TestItemAttachment itemAttachment = new TestItemAttachment()
attachmentManager.register(itemAttachment)

// Block attachment
TestBlockAttachment blockAttachment = new TestBlockAttachment()
attachmentManager.register(blockAttachment)

// Entity attachment
TestEntityAttachment entityAttachment = new TestEntityAttachment()
attachmentManager.register(entityAttachment)

println("All test attachments registered successfully!")
println("=== Available Events to Test ===")
println(" Script Events: onScriptLoad, onScriptReload, onScriptError, onServerStart, onServerStop")
println(" Item Events: onUse, onUseOn, onInventoryTick, onCraftedBy, onLeftClickEntity")
println(" Block Events: onPlace, onBreak, onUse, onAttack, onNeighborChanged")
println(" Entity Events: onSpawn, onRemove, onTick, onInteract, onHurt, onDeath")

println("\n Test the attachments by:")
println("1. Using sticks, diamond swords, or apples in the game")
println("2. Placing/breaking stone, dirt, or oak planks")
println("3. Interacting with players, zombies, or cows")
println("4. Watch the server console for event logs!")