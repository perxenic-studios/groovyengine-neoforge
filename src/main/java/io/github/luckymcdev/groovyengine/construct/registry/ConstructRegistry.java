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

package io.github.luckymcdev.groovyengine.construct.registry;

import io.github.luckymcdev.groovyengine.GE;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConstructRegistry {

    private ConstructRegistry() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GE.MODID);

    public static final DeferredItem<Item> BRUSH = ITEMS.registerSimpleItem("brush");

    /**
     * Registers the Construct registry items to the given event bus.
     *
     * @param modEventBus the event bus to register to
     */
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
