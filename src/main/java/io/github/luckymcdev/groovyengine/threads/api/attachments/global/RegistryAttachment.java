package io.github.luckymcdev.groovyengine.threads.api.attachments.global;

import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;
import net.neoforged.bus.api.IEventBus;

import java.util.List;

/**
 * Attachment for registry events during mod initialization.
 * Use this to register custom content.
 */
public abstract class RegistryAttachment implements GlobalAttachment<Void> {

    /**
     * Called during the registration phase
     */
    public void onRegister(net.neoforged.bus.api.IEventBus modEventBus) {
    }
}