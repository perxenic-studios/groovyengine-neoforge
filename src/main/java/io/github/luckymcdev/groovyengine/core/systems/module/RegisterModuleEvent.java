/*
 * Copyright 2025 LuckyMcDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
