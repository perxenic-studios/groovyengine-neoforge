package io.github.luckymcdev.groovyengine.threads.api.attachments.registry;

import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;
import net.neoforged.bus.api.IEventBus;

import java.util.List;

public abstract class RegistryAttachment implements BaseAttachment {

    @Override
    public Object getTarget() {
        return "registry";
    }

    @Override
    public List<Object> getTargets() {
        return List.of("registry");
    }

    // Global Registry Events
    public void onRegister(IEventBus modEventBus) {
    }
}