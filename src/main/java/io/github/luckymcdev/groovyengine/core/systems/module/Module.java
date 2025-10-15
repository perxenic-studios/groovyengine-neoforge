package io.github.luckymcdev.groovyengine.core.systems.module;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;

public interface Module {
    void init(IEventBus modEventBus);
    default void onServerStarting() {}
    default void onCommonSetup() {}
    @OnlyIn(Dist.CLIENT)
    default void onClientSetup() {}
    @OnlyIn(Dist.CLIENT)
    void registerWindows();
}