package io.github.luckymcdev.groovyengine.core.systems.module;

import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Fire this event to register modules with the {@link ModuleManager}.
 * This event is fired on the NeoForge event bus.
 */
public class RegisterModuleEvent extends Event {
    public static final RegisterModuleEvent INSTANCE = new RegisterModuleEvent();

    private final List<Module> modules = new ArrayList<>();

    /**
     * Register a new module.
     *
     * @param module The module to register.
     */
    public void register(Module module) {
        modules.add(module);
        ModuleManager.registerModule(module);
    }

    /**
     * Get the list of modules that have been registered.
     *
     * @return The list of registered modules.
     */
    public List<Module> getModules() {
        return modules;
    }
}
