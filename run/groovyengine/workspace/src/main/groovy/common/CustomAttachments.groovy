//disabled
package common

import dev.perxenic.acidapi.api.datagen.*
import io.github.luckymcdev.groovyengine.threads.api.attachments.*
import io.github.luckymcdev.groovyengine.threads.api.attachments.client.ClientAttachment
import io.github.luckymcdev.groovyengine.threads.api.attachments.item.ItemAttachment
import io.github.luckymcdev.groovyengine.threads.api.attachments.block.BlockAttachment
import io.github.luckymcdev.groovyengine.threads.api.attachments.entity.EntityAttachment
import io.github.luckymcdev.groovyengine.threads.api.attachments.registry.RegistryAttachment
import io.github.luckymcdev.groovyengine.threads.api.attachments.script.ScriptAttachment
import io.github.luckymcdev.groovyengine.threads.api.attachments.server.ServerAttachment
import io.github.luckymcdev.groovyengine.GE
import net.minecraft.world.*
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.*
import net.minecraft.world.item.*
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.*
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.*

import static io.github.luckymcdev.groovyengine.GE.SCRIPT_LOG

// Script Attachment
class TestScriptAttachment extends ScriptAttachment {
    TestScriptAttachment() { super("test_script_attachment") }
    @Override void onScriptLoad()   { SCRIPT_LOG.info("ScriptAttachment.onScriptLoad()") }
    @Override void onScriptReload() { SCRIPT_LOG.info("ScriptAttachment.onScriptReload()") }
    @Override void onScriptError(Exception error) { SCRIPT_LOG.info("ScriptAttachment.onScriptError(): ${error.message}") }
    @Override void onInit()         { SCRIPT_LOG.info("ScriptAttachment.onInit()") }
    @Override void onDestroy()      { SCRIPT_LOG.info("ScriptAttachment.onDestroy()") }
}

// Server Attachment
class TestServerAttachment extends ServerAttachment {
    @Override void onServerStart()  { SCRIPT_LOG.info("ServerAttachment.onServerStart()") }
    @Override void onServerStop()   { SCRIPT_LOG.info("ServerAttachment.onServerStop()") }
    @Override void onPlayerJoin(String name)  { SCRIPT_LOG.info("ServerAttachment.onPlayerJoin(): ${name}") }
    @Override void onPlayerLeave(String name) { SCRIPT_LOG.info("ServerAttachment.onPlayerLeave(): ${name}") }
    @Override void onInit()         { SCRIPT_LOG.info("ServerAttachment.onInit()") }
    @Override void onDestroy()      { SCRIPT_LOG.info("ServerAttachment.onDestroy()") }
}

// Item Attachment
class TestItemAttachment extends ItemAttachment {
    TestItemAttachment() { super(Items.STICK, Items.DIAMOND_SWORD, Items.APPLE) }
    @Override void onInit()    { SCRIPT_LOG.info("ItemAttachment.onInit()") }
    @Override void onDestroy() { SCRIPT_LOG.info("ItemAttachment.onDestroy()") }
    @Override InteractionResultHolder<ItemStack> onUse(LevelAccessor level, Player player, InteractionHand hand, ItemStack stack) {
        SCRIPT_LOG.info("ItemAttachment.onUse(): ${stack.item.descriptionId} by ${player.name.string}")
        return InteractionResultHolder.pass(stack)
    }
    @Override InteractionResult onUseOn(UseOnContext ctx, ItemStack stack) {
        SCRIPT_LOG.info("ItemAttachment.onUseOn(): ${stack.item.descriptionId}")
        return InteractionResult.PASS
    }
    @Override void onCraftedBy(ItemStack stack, LevelAccessor level, Player player) {
        SCRIPT_LOG.info("ItemAttachment.onCraftedBy(): ${stack.item.descriptionId} by ${player.name.string}")
    }
    @Override boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        SCRIPT_LOG.info("ItemAttachment.onLeftClickEntity(): ${stack.item.descriptionId} on ${entity.name.string}")
        return false
    }
}

// Block Attachment
class TestBlockAttachment extends BlockAttachment {
    TestBlockAttachment() { super(Blocks.STONE, Blocks.DIRT, Blocks.OAK_PLANKS) }
    @Override void onInit()    { SCRIPT_LOG.info("BlockAttachment.onInit()") }
    @Override void onDestroy() { SCRIPT_LOG.info("BlockAttachment.onDestroy()") }
    @Override void onPlace(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        SCRIPT_LOG.info("BlockAttachment.onPlace(): ${state.block.descriptionId} at ${pos}")
    }
    @Override void onBreak(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        SCRIPT_LOG.info("BlockAttachment.onBreak(): ${state.block.descriptionId} at ${pos}")
    }
    @Override InteractionResult onUse(BlockState state, LevelAccessor level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        SCRIPT_LOG.info("BlockAttachment.onUse(): ${state.block.descriptionId}")
        return InteractionResult.PASS
    }
    @Override void onAttack(BlockState state, LevelAccessor level, BlockPos pos, Player player) {
        SCRIPT_LOG.info("BlockAttachment.onAttack(): ${state.block.descriptionId}")
    }
    @Override void onNeighborChanged(BlockState state, LevelAccessor level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moving) {
        SCRIPT_LOG.info("BlockAttachment.onNeighborChanged(): ${state.block.descriptionId}")
    }
}

// Entity Attachment
class TestEntityAttachment extends EntityAttachment {
    TestEntityAttachment() { super(EntityType.PLAYER, EntityType.ZOMBIE, EntityType.COW) }
    @Override void onInit()    { SCRIPT_LOG.info("EntityAttachment.onInit()") }
    @Override void onDestroy() { SCRIPT_LOG.info("EntityAttachment.onDestroy()") }
    @Override void onSpawn(Entity entity, LevelAccessor level) { SCRIPT_LOG.info("EntityAttachment.onSpawn(): ${entity.name.string}") }
    @Override void onRemove(Entity entity, Entity.RemovalReason reason) { SCRIPT_LOG.info("EntityAttachment.onRemove(): ${entity.name.string}") }
    @Override InteractionResult onInteract(Entity entity, Player player, InteractionHand hand) {
        SCRIPT_LOG.info("EntityAttachment.onInteract(): ${entity.name.string} by ${player.name.string}")
        return InteractionResult.PASS
    }
    @Override void onHurt(LivingEntity entity, DamageSource src, float amt) { SCRIPT_LOG.info("EntityAttachment.onHurt(): ${entity.name.string}") }
    @Override void onDeath(LivingEntity entity, DamageSource src) { SCRIPT_LOG.info("EntityAttachment.onDeath(): ${entity.name.string}") }
}

// Registry Attachment
class TestRegistryAttachment extends RegistryAttachment {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("testmod")
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("testmod")
    public static final DeferredItem<Item> GROOVE = ITEMS.registerSimpleItem("groove")
    public static final DeferredBlock<Block> GROOVE_BLOCK = BLOCKS.registerSimpleBlock("groovy_block")
    @Override void onRegister(IEventBus bus) { ITEMS.register(bus); SCRIPT_LOG.info("RegistryAttachment.onRegister()") }
    @Override void onInit()    { SCRIPT_LOG.info("RegistryAttachment.onInit()") }
    @Override void onDestroy() { SCRIPT_LOG.info("RegistryAttachment.onDestroy()") }
}

// Client Attachment
class TestClientAttachment extends ClientAttachment {
    @Override void onClientStart() { SCRIPT_LOG.info("ClientAttachment.onClientStart()") }
    @Override void onKeyPress(int key, int action, int mods) { SCRIPT_LOG.info("ClientAttachment.onKeyPress(): key=${key}, action=${action}") }
    @Override void onMouseClick(int btn, int action, int mods) { SCRIPT_LOG.info("ClientAttachment.onMouseClick(): button=${btn}, action=${action}") }
    @Override void onMouseScroll(double h, double v) { SCRIPT_LOG.info("ClientAttachment.onMouseScroll(): h=${h}, v=${v}") }
    @Override void onInit()    { SCRIPT_LOG.info("ClientAttachment.onInit()") }
    @Override void onDestroy() { SCRIPT_LOG.info("ClientAttachment.onDestroy()") }
}

// Main test execution
SCRIPT_LOG.info("Starting Attachment Tests...")
AttachmentManager manager = AttachmentManager.getInstance()
[
        new TestScriptAttachment(),
        new TestServerAttachment(),
        new TestItemAttachment(),
        new TestBlockAttachment(),
        new TestEntityAttachment(),
        new TestRegistryAttachment(),
        new TestClientAttachment()
].each { a ->
    try {
        manager.register(a)
        SCRIPT_LOG.info("Registered: ${a.class.simpleName}")
    } catch (Exception e) {
        SCRIPT_LOG.error("Failed to register ${a.class.simpleName}: ${e.message}")
    }
}
