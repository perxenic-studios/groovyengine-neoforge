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

package io.github.luckymcdev.groovyengine.core.client.editor.core;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class KeybindManager {

    /**
     * Keybind for toggling ImGui rendering.
     * Default to F6, can be changed by user.
     */
    public static final KeyMapping TOGGLE_IMGUI = new KeyMapping(
            "key.groovyengine.toggle_imgui",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F6, // Default to F6, can be changed by user
            "key.categories.groovyengine"
    );

    /**
     * Keybind for opening the Construct Editor screen.
     * Default to G, can be changed by user.
     */
    public static final KeyMapping OPEN_EDITOR_SCREEN = new KeyMapping(
            "key.groovyengine.open_editor_screen",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G, // Default to G, can be changed by user
            "key.categories.groovyengine"
    );

    /**
     * Registers the keybinds for the Construct Editor.
     * @param event The RegisterKeyMappingsEvent to register the keybinds to.
     */
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_IMGUI);
        event.register(OPEN_EDITOR_SCREEN);
    }

    /**
     * Handles key input events for the Construct Editor.
     * @param event The InputEvent.Key to handle.
     */
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null && !(mc.screen instanceof EditorScreen)) {
            return; // Don't handle keybinds when other screens are open
        }

        if (TOGGLE_IMGUI.consumeClick()) {
            toggleImGui();
        }

        if (OPEN_EDITOR_SCREEN.consumeClick()) {
            openEditorScreen();
            enableImGui();
        }
    }

    /**
     * Toggles the ImGui rendering state.
     * If the current state is {@link EditorState#ENABLED}, it will be set to {@link EditorState#DISABLED}.
     * If the current state is {@link EditorState#DISABLED}, it will be set to {@link EditorState#ENABLED}.
     */
    private static void toggleImGui() {
        EditorState currentState = GroovyEngineEditor.getEditorState();
        if (currentState == EditorState.ENABLED) {
            GroovyEngineEditor.setEditorState(EditorState.DISABLED);
        } else {
            GroovyEngineEditor.setEditorState(EditorState.ENABLED);
        }
    }

    /**
     * Enables ImGui rendering for the Construct Editor.
     * Sets the current state to {@link EditorState#ENABLED}.
     */
    private static void enableImGui() {
        GroovyEngineEditor.setEditorState(EditorState.ENABLED);
    }

    /**
     * Opens the Construct Editor screen.
     * This method sets the current screen to an instance of {@link EditorScreen}.
     */
    private static void openEditorScreen() {
        Minecraft.getInstance().setScreen(new EditorScreen());
    }
}