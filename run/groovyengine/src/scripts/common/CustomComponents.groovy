import dev.lucky.groovyengine.api.components.types.ItemComponent
import dev.lucky.groovyengine.api.components.ComponentManager
import net.minecraft.world.item.Items
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

class CustomStickComponent extends ItemComponent {
    @Override
    def getTarget() {
        return Items.STICK
    }

    @Override
    void onUse(Level level, Player player, ItemStack stack) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Script stick used!"))
    }
}

// Register the component
ComponentManager manager = ComponentManager.getInstance();
manager.registerComponent(new CustomStickComponent());
