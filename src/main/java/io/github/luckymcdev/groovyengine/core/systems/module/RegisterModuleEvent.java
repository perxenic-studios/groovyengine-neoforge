package io.github.luckymcdev.groovyengine.core.systems.module;

import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Fire this event to register modules with the {@link ModuleManager}.
 * This event is fired on the NeoForge event bus.
 */
public class RegisterModuleEvent extends Event {

    private final List<Module> modules = new ArrayList<>();

    /**
     * Register a new module.
     *
     * @param module The module to register.
     */
    public void register(Module module) {
        modules.add(module);
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
