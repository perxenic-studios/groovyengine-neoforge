package io.github.luckymcdev.groovyengine.core.core.registry;

import net.neoforged.bus.api.IEventBus;

public class ModRegistry {
    public static void register(IEventBus modEventBus) {
        ModAttachmentTypes.register(modEventBus);
    }
}
