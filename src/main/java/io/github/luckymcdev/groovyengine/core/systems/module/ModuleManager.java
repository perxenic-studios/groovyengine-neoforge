package io.github.luckymcdev.groovyengine.core.systems.module;

import net.neoforged.bus.api.IEventBus;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static final ModuleManager instance = new ModuleManager();
    private static final List<Module> modules = new ArrayList<>();

    //private constructor to prevet instancing
    private ModuleManager() {

    }

    /**
     * Registers a module with the module system.
     * This method is typically used to register modules manually.
     * @param module The module to register.
     */
    public static void registerModule(Module module) {
        modules.add(module);
    }

    /**
     * Registers a list of modules with the module system.
     * This method is typically used to register a list of modules in bulk.
     * @param moduleList The list of modules to register.
     */
    public static void registerModules(List<Module> moduleList) {
        modules.addAll(moduleList);
    }

    /**
     * Returns the single instance of the ModuleManager class.
     * This method is used to get a reference to the ModuleManager, which is responsible for managing the modules in the Groovy Engine.
     * @return The single instance of the ModuleManager class.
     */
    public static ModuleManager getInstance() {
        return instance;
    }

    /**
     * Initializes all modules registered with the module system.
     * This method is responsible for calling the {@link Module#init(IEventBus)} method on each registered module.
     * @param eventBus The event bus to pass to the modules' {@link Module#init(IEventBus)} method.
     */
    public void runInit(IEventBus eventBus) {
        modules.forEach(module -> module.init(eventBus));
    }

    /**
     * Calls the {@link Module#onServerStarting()} method on each registered module.
     * This method is responsible for calling the {@link Module#onServerStarting()} method on each registered module in the order they were registered.
     * The {@link Module#onServerStarting()} method is called on the server-side after the module has been initialized.
     */
    public void runOnServerStarting() {
        modules.forEach(Module::onServerStarting);
    }

    /**
     * Calls the {@link Module#onClientSetup()} method on each registered module.
     * This method is responsible for calling the {@link Module#onClientSetup()} method on each registered module in the order they were registered.
     * The {@link Module#onClientSetup()} method is called on the client-side after the module has been initialized.
     */
    public void runOnClientSetup() {
        modules.forEach(Module::onClientSetup);
    }

    /**
     * Calls the {@link Module#onCommonSetup()} method on each registered module.
     * This method is responsible for calling the {@link Module#onCommonSetup()} method on each registered module in the order they were registered.
     * The {@link Module#onCommonSetup()} method is called on both the server-side and client-side after the module has been initialized.
     */
    public void runOnCommonSetup() {
        modules.forEach(Module::onCommonSetup);
    }

    /**
     * Calls the {@link Module#registerWindows()} method on each registered module.
     * This method is responsible for calling the {@link Module#registerWindows()} method on each registered module in the order they were registered.
     * The {@link Module#registerWindows()} method is responsible for registering all windows associated with the module with the window manager.
     */
    public void runRegisterWindows() {
        modules.forEach(Module::registerWindows);
    }

}
