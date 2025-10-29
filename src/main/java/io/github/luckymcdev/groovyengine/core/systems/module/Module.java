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

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;

/**
 * Represents a module in the module system.
 * A module is a self-contained unit of code that performs a specific function or set of functions.
 * Modules are typically used to organize the mod's functionality into smaller, more manageable pieces.
 * <p>
 * Modules are registered with the module system using the {@link ModuleManager} class.
 */
public interface Module {
    /**
     * Initializes the module with the given event bus.
     * This method is typically used to register events, initialize systems, and perform other setup tasks.
     *
     * @param modEventBus The event bus to use for registering events.
     */
    void init(IEventBus modEventBus);

    /**
     * Called on the server-side after the module has been initialized.
     * This is the first point at which the module can safely interact with the game's core systems on the server-side.
     * This method is only called on the server-side, and is ignored on the client-side.
     */
    default void onServerStarting() {
    }

    /**
     * Called on both the server-side and client-side after the module has been initialized.
     * This is the first point at which the module can safely interact with the game's core systems.
     * This method is called on both the server-side and the client-side, and is ignored if the module is not present on the respective side.
     */
    default void onCommonSetup() {
    }

    /**
     * Called on the client-side after the module has been initialized.
     * This is the first point at which the module can safely interact with the client-side of the game.
     * This method is only called on the client-side, and is ignored on the server-side.
     */
    @OnlyIn(Dist.CLIENT)
    default void onClientSetup() {
    }

    /**
     * Registers all windows associated with this module with the window manager.
     * This method should only be called on the client-side, and is ignored on the server-side.
     * <p>
     * This method is typically used to register windows that need to be rendered on the client-side.
     * <p>
     * This method is called automatically by the module system when the module is initialized on the client-side.
     * <p>
     * Modules should not call this method manually, as it may interfere with the module system's internal workings.
     */
    @OnlyIn(Dist.CLIENT)
    default void registerWindows() {
    }
}