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

package io.github.luckymcdev.groovyengine.threads;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.WindowManager;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import io.github.luckymcdev.groovyengine.core.systems.module.Module;
import io.github.luckymcdev.groovyengine.threads.api.attachments.AttachmentManager;
import io.github.luckymcdev.groovyengine.threads.client.editor.ThreadsWindow;
import io.github.luckymcdev.groovyengine.threads.core.scripting.core.ScriptManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;

public class ThreadsModule implements Module {

    /**
     * Initializes the Threads module. This method is called when the module is initialized.
     * It will initialize the script manager and fire the on register event for the attachment event listener.
     *
     * @param modEventBus the event bus for the module
     */
    @Override
    public void init(IEventBus modEventBus) {

        GE.THREADS_LOG.info("Initializing Threads module");

        ScriptManager.initialize();

        AttachmentManager.getInstance().getRegistryAttachments().forEach(att -> att.onRegister(modEventBus));

    }

    /**
     * Reloads all scripts when the server is starting.
     * This method is called when the server is starting, and will
     * reload all scripts in the script manager.
     */
    @Override
    public void onServerStarting() {
        ScriptManager.reloadScripts();
    }

    /**
     * Registers the Threads window with the Window Manager.
     * This method is called on the client side to register the Threads window with the Window Manager.
     * It registers the Threads window with the display name "Threads".
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerWindows() {
        WindowManager.registerWindow(new ThreadsWindow(), ImIcons.CODE.get() + " Threads");
    }
}