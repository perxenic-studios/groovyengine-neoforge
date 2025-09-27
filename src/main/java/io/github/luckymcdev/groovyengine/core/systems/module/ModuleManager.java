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

    public static void registerModule(Module module) {
        modules.add(module);
    }

    public static void registerModules(List<Module> moduleList) {
        moduleList.forEach(module -> modules.add(module));
    }

    public void runInit(IEventBus eventBus) {
        modules.forEach(module -> module.init(eventBus));
    }

    public void runOnServerStarting() {
        modules.forEach(Module::onServerStarting);
    }

    public void runOnCommonSetup() {
        modules.forEach(Module::onCommonSetup);
    }

    public void runRegisterWindows() {
        modules.forEach(Module::registerWindows);
    }

    public static ModuleManager getInstance() {
        return instance;
    }

}
