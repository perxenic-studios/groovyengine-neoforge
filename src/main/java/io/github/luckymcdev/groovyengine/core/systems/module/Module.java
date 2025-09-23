package io.github.luckymcdev.groovyengine.core.systems.module;

import net.neoforged.bus.api.IEventBus;

public interface Module {
    void init(IEventBus modEventBus);
    default void onServerStarting() {}
    default void onCommonSetup() {}
    void registerWindows();
}