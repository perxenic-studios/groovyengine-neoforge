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

package io.github.luckymcdev.groovyengine.lens;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.WindowManager;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import io.github.luckymcdev.groovyengine.core.systems.module.Module;
import io.github.luckymcdev.groovyengine.lens.client.editor.AnimationWindow;
import io.github.luckymcdev.groovyengine.lens.client.editor.RenderingDebuggingWindow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;

public class LensModule implements Module {
    private static final String LENS_CATEGORY = ImIcons.CAMERA.get() + " Lens";

    /**
     * Called when the module is initialized.
     * This method is only called on the client side, and is used to initialize the Lens module.
     *
     * @param modEventBus the event bus for the module.
     */
    @Override
    public void init(IEventBus modEventBus) {
        GE.LENS_LOG.info("Lens Initialization");
    }

    /**
     * Registers the windows for this module.
     * This method is only called on the client side, and is used to register the windows
     * for the Lens module.
     *
     * @implSpec
     * @implNote This method is only called on the client side, and is used to register the windows
     * for the Lens module. It registers the {@link RenderingDebuggingWindow} and the
     * {@link AnimationWindow} to the window manager.
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerWindows() {
        WindowManager.registerWindow(new RenderingDebuggingWindow(), LENS_CATEGORY);
        WindowManager.registerWindow(new AnimationWindow(), LENS_CATEGORY);
    }
}