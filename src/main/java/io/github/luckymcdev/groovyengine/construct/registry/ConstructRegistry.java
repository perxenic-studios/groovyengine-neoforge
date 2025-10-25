package io.github.luckymcdev.groovyengine.construct.registry;

import io.github.luckymcdev.groovyengine.GE;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConstructRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GE.MODID);

    public static final DeferredItem<Item> BRUSH = ITEMS.registerSimpleItem("brush");

    /**
     * Registers the Construct registry items to the given event bus.
     *
     * @param modEventBus the event bus to register to
     */
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
