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

package io.github.luckymcdev.groovyengine.core;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.construct.client.editor.ConstructEditorWindow;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.WindowManager;
import io.github.luckymcdev.groovyengine.core.systems.module.Module;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;

public class CoreModule implements Module {
    /**
     * Initializes the Core module.
     * This method is called when the module is initialized and is responsible for initializing
     * all Core-related systems.
     * @param modEventBus the event bus for the module
     */
    @Override
    public void init(IEventBus modEventBus) {
        GE.CORE_LOG.info("Core Initialized");
    }
}
