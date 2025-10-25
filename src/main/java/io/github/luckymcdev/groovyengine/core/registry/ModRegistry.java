package io.github.luckymcdev.groovyengine.core.registry;

import net.neoforged.bus.api.IEventBus;

public class ModRegistry {
    /**
     * Registers the mod's attachment types with the event bus.
     *
     * This method is called when the mod is initialized and is responsible for registering the mod's attachment types.
     *
     * @param modEventBus the event bus for the mod
     */
    public static void register(IEventBus modEventBus) {
        ModAttachmentTypes.register(modEventBus);
    }
}
