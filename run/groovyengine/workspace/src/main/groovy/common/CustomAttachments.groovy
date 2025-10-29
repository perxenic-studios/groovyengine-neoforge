//priority=1000
package common

import io.github.luckymcdev.groovyengine.threads.api.attachments.AttachmentManager
import io.github.luckymcdev.groovyengine.threads.api.attachments.global.*
import io.github.luckymcdev.groovyengine.threads.api.attachments.local.*
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

    @Override
    void onScriptLoad() {
        SCRIPT_LOG.info("ScriptAttachment.onScriptLoad()")
    }

    @Override
    void onScriptError(Exception error) {
        SCRIPT_LOG.info("ScriptAttachment.onScriptError(): ${error.message}")
    }

    @Override
    void onInit() {
        SCRIPT_LOG.info("ScriptAttachment.onInit()")
    }

    @Override
    void onDestroy() {
        SCRIPT_LOG.info("ScriptAttachment.onDestroy()")
    }
}

// Server Attachment
class TestServerAttachment extends ServerAttachment {
    @Override
    void onServerStart() {
        SCRIPT_LOG.info("ServerAttachment.onServerStart()")
    }

    @Override
    void onServerStop() {
        SCRIPT_LOG.info("ServerAttachment.onServerStop()")
    }

    @Override
    void onServerTick() {
        SCRIPT_LOG.info("ServerAttachment.onServerTick()")
    }

    @Override
    void onPlayerJoin(String name) {
        SCRIPT_LOG.info("ServerAttachment.onPlayerJoin(): ${name}")
    }

    @Override
    void onPlayerLeave(String name) {
        SCRIPT_LOG.info("ServerAttachment.onPlayerLeave(): ${name}")
    }

    @Override
    void onWorldLoad(String worldName) {
        SCRIPT_LOG.info("ServerAttachment.onWorldLoad(): ${worldName}")
    }

    @Override
    void onWorldUnload(String worldName) {
        SCRIPT_LOG.info("ServerAttachment.onWorldUnload(): ${worldName}")
    }

    @Override
    void onInit() {
        SCRIPT_LOG.info("ServerAttachment.onInit()")
    }

    @Override
    void onDestroy() {
        SCRIPT_LOG.info("ServerAttachment.onDestroy()")
    }
}

// Item Attachment
class TestItemAttachment extends ItemAttachment {
    TestItemAttachment() {
        super(Items.STICK, Items.DIAMOND_SWORD, Items.APPLE)
    }

    @Override
    void onInit() {
        SCRIPT_LOG.info("ItemAttachment.onInit()")
    }

    @Override
    void onDestroy() {
        SCRIPT_LOG.info("ItemAttachment.onDestroy()")
    }

    @Override
    InteractionResultHolder<ItemStack> onUse(LevelAccessor level, Player player, InteractionHand hand, ItemStack stack) {
        SCRIPT_LOG.info("ItemAttachment.onUse(): ${stack.item.descriptionId} by ${player.name.string}")
        return InteractionResultHolder.pass(stack)
    }

    @Override
    InteractionResult onUseOn(UseOnContext ctx, ItemStack stack) {
        SCRIPT_LOG.info("ItemAttachment.onUseOn(): ${stack.item.descriptionId}")
        return InteractionResult.PASS
    }

    @Override
    void onCraftedBy(ItemStack stack, LevelAccessor level, Player player) {
        SCRIPT_LOG.info("ItemAttachment.onCraftedBy(): ${stack.item.descriptionId} by ${player.name.string}")
    }

    @Override
    boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        SCRIPT_LOG.info("ItemAttachment.onLeftClickEntity(): ${stack.item.descriptionId} on ${entity.name.string}")
        return false
    }

    @Override
    void onInventoryTick(ItemStack stack, LevelAccessor level, Entity entity, int slotId, boolean isSelected) {
        if (isSelected) {
            SCRIPT_LOG.info("ItemAttachment.onInventoryTick(): ${stack.item.descriptionId} in slot ${slotId} (selected)")
        }
    }
}

// Block Attachment
class TestBlockAttachment extends BlockAttachment {
    TestBlockAttachment() {
        super(Blocks.STONE, Blocks.DIRT, Blocks.OAK_PLANKS)
    }

    @Override
    void onInit() {
        SCRIPT_LOG.info("BlockAttachment.onInit()")
    }

    @Override
    void onDestroy() {
        SCRIPT_LOG.info("BlockAttachment.onDestroy()")
    }

    @Override
    void onPlace(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        SCRIPT_LOG.info("BlockAttachment.onPlace(): ${state.block.descriptionId} at ${pos}")
    }

    @Override
    void onBreak(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        SCRIPT_LOG.info("BlockAttachment.onBreak(): ${state.block.descriptionId} at ${pos}")
    }

    @Override
    InteractionResult onUse(BlockState state, LevelAccessor level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        SCRIPT_LOG.info("BlockAttachment.onUse(): ${state.block.descriptionId}")
        return InteractionResult.PASS
    }

    @Override
    void onAttack(BlockState state, LevelAccessor level, BlockPos pos, Player player) {
        SCRIPT_LOG.info("BlockAttachment.onAttack(): ${state.block.descriptionId}")
    }

    @Override
    void onNeighborChanged(BlockState state, LevelAccessor level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moving) {
        SCRIPT_LOG.info("BlockAttachment.onNeighborChanged(): ${state.block.descriptionId} neighbor: ${neighbor.descriptionId}")
    }
}

// Entity Attachment
class TestEntityAttachment extends EntityAttachment {
    TestEntityAttachment() {
        super(EntityType.PLAYER, EntityType.ZOMBIE, EntityType.COW)
    }

    @Override
    void onInit() {
        SCRIPT_LOG.info("EntityAttachment.onInit()")
    }

    @Override
    void onDestroy() {
        SCRIPT_LOG.info("EntityAttachment.onDestroy()")
    }

    @Override
    void onSpawn(Entity entity, LevelAccessor level) {
        SCRIPT_LOG.info("EntityAttachment.onSpawn(): ${entity.name.string}")
    }

    @Override
    void onRemove(Entity entity, Entity.RemovalReason reason) {
        SCRIPT_LOG.info("EntityAttachment.onRemove(): ${entity.name.string} reason: ${reason}")
    }

    @Override
    void onTick(Entity entity) {
        // Only log occasionally to avoid spam
        if (entity.tickCount % 100 == 0) {
            SCRIPT_LOG.info("EntityAttachment.onTick(): ${entity.name.string}")
        }
    }

    @Override
    InteractionResult onInteract(Entity entity, Player player, InteractionHand hand) {
        SCRIPT_LOG.info("EntityAttachment.onInteract(): ${entity.name.string} by ${player.name.string}")
        return InteractionResult.PASS
    }

    @Override
    void onHurt(LivingEntity entity, DamageSource src, float amt) {
        SCRIPT_LOG.info("EntityAttachment.onHurt(): ${entity.name.string} by ${src.getMsgId()} for ${amt} damage")
    }

    @Override
    void onDeath(LivingEntity entity, DamageSource src) {
        SCRIPT_LOG.info("EntityAttachment.onDeath(): ${entity.name.string} by ${src.getMsgId()}")
    }
}

// Registry Attachment
class TestRegistryAttachment extends RegistryAttachment {
    @Override
    void onRegister(IEventBus bus) {
        SCRIPT_LOG.info("RegistryAttachment.onRegister()")
    }

    @Override
    void onInit() {
        SCRIPT_LOG.info("RegistryAttachment.onInit()")
    }

    @Override
    void onDestroy() {
        SCRIPT_LOG.info("RegistryAttachment.onDestroy()")
    }
}

// Recipe Attachment
class TestRecipeAttachment extends RecipeAttachment {
    @Override
    void onGenerate(net.minecraft.data.recipes.RecipeOutput recipeOutput) {
        SCRIPT_LOG.info("RecipeAttachment.onGenerate()")
    }

    @Override
    void onInit() {
        SCRIPT_LOG.info("RecipeAttachment.onInit()")
    }

    @Override
    void onDestroy() {
        SCRIPT_LOG.info("RecipeAttachment.onDestroy()")
    }
}

// Client Attachment
class TestClientAttachment extends ClientAttachment {
    @Override
    void onClientStart() {
        SCRIPT_LOG.info("ClientAttachment.onClientStart()")
    }

    @Override
    void onClientTick() {
        // Only log occasionally to avoid spam
        if (System.currentTimeMillis() % 2000 < 50) {
            SCRIPT_LOG.info("ClientAttachment.onClientTick()")
        }
    }

    @Override
    void onKeyPress(int key, int action, int mods) {
        SCRIPT_LOG.info("ClientAttachment.onKeyPress(): key=${key}, action=${action}")
    }

    @Override
    void onMouseClick(int btn, int action, int mods) {
        SCRIPT_LOG.info("ClientAttachment.onMouseClick(): button=${btn}, action=${action}")
    }

    @Override
    void onMouseScroll(double h, double v) {
        SCRIPT_LOG.info("ClientAttachment.onMouseScroll(): h=${h}, v=${v}")
    }

    @Override
    void onInit() {
        SCRIPT_LOG.info("ClientAttachment.onInit()")
    }

    @Override
    void onDestroy() {
        SCRIPT_LOG.info("ClientAttachment.onDestroy()")
    }
}

// Main test execution
SCRIPT_LOG.info("Starting Attachment Tests...")
AttachmentManager manager = AttachmentManager.getInstance()
def toggles = [
        script  : false,
        server  : false,
        item    : true,
        block   : true,
        entity  : false,
        registry: true,
        recipe  : true,
        client  : false
]

// Function to safely register based on toggle
def tryRegister = { name, attachment, action ->
    if (!toggles[name]) {
        SCRIPT_LOG.info("Skipping ${name} attachment (disabled)")
        return
    }
    try {
        action(attachment)
        SCRIPT_LOG.info("Enabled ${name} attachment")
    } catch (Exception e) {
        SCRIPT_LOG.error("Failed to register ${name}: ${e.message}")
    }
}

// Use the toggles during registration
tryRegister('script',   new TestScriptAttachment(),   manager.&registerScript)
tryRegister('server',   new TestServerAttachment(),   manager.&registerServer)
tryRegister('item',     new TestItemAttachment(),     manager.&registerItem)
tryRegister('block',    new TestBlockAttachment(),    manager.&registerBlock)
tryRegister('entity',   new TestEntityAttachment(),   manager.&registerEntity)
tryRegister('registry', new TestRegistryAttachment(), manager.&registerRegistry)
tryRegister('recipe',   new TestRecipeAttachment(),   manager.&registerRecipe)
tryRegister('client',   new TestClientAttachment(),   manager.&registerClient)

SCRIPT_LOG.info("Attachment test registration completed!")