import io.github.luckymcdev.groovyengine.api.components.types.ItemComponent
import io.github.luckymcdev.groovyengine.api.components.ComponentManager
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

class CustomStickComponent extends ItemComponent {
    @Override
    def getTarget() {
        return Items.STICK
    }

    @Override
    void onUse(Level level, Player player, InteractionHand hand, ItemStack stack) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Script stick used!"))
    }
}

// Register the component
ComponentManager.getInstance().registerComponent(new CustomStickComponent());
