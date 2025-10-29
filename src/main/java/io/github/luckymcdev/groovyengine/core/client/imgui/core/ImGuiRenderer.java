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

package io.github.luckymcdev.groovyengine.core.client.imgui.core;

import com.mojang.blaze3d.platform.Window;
import imgui.ImGui;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import io.github.luckymcdev.groovyengine.core.client.editor.core.EditorScreen;
import io.github.luckymcdev.groovyengine.core.client.editor.core.EditorState;
import io.github.luckymcdev.groovyengine.core.client.editor.core.GroovyEngineEditor;
import io.github.luckymcdev.groovyengine.core.client.editor.core.KeybindManager;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.WindowManager;
import io.github.luckymcdev.groovyengine.core.client.editor.windows.DemoWindows;
import io.github.luckymcdev.groovyengine.core.client.editor.windows.EditorControlWindow;
import io.github.luckymcdev.groovyengine.core.client.editor.windows.MetricsWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ImGuiRenderer {

    private static boolean initialized = false;

    /**
     * Initializes the windows managed by the Window Manager.
     * This method registers the EditorControlWindow and MetricsWindow with the Window Manager.
     * It also registers the demo windows with the Window Manager.
     * It is called once when the ImGui is initialized.
     */
    private static void initializeWindows() {
        if (initialized) return;
        WindowManager.registerWindow(new EditorControlWindow(), ImIcons.SETTINGS.get() + " Debug");
        WindowManager.registerWindow(new MetricsWindow(), ImIcons.SETTINGS.get() + " Debug");

        // Register demo windows
        WindowManager.registerWindow(new DemoWindows.AboutWindow(), ImIcons.STACKS.get() + " ImGui");
        WindowManager.registerWindow(new DemoWindows.DemoWindow(), ImIcons.STACKS.get() + " ImGui");
        WindowManager.registerWindow(new DemoWindows.ImGuiMetricsWindow(), ImIcons.STACKS.get() + " ImGui");
        WindowManager.registerWindow(new DemoWindows.IconsWindow(), ImIcons.STACKS.get() + " ImGui");

        initialized = true;
    }

    /**
     * Renders the ImGui UI for the Groovy Engine.
     * This method is called once per frame and is responsible for rendering the ImGui UI.
     * It initializes the windows managed by the Window Manager if necessary, and then renders the ImGui UI using the ImGuiImpl.draw method.
     * The {@link ImGuiImpl#draw} method is passed a render interface, which is called once per frame.
     * The render interface is responsible for setting up the docking and main window, rendering the main menu bar, and rendering all enabled windows.
     * @param event The RenderGuiEvent.Post event which triggered this method call.
     */
    public static void onRender(RenderGuiEvent.Post event) {
        initializeWindows();

        ImGuiImpl.draw(io -> {
            boolean isImGuiEnabled = GroovyEngineEditor.getEditorState() == EditorState.ENABLED;

            if (isImGuiEnabled) {
                setupDockingAndMainWindow();
                renderMainMenuBar();

                WindowManager.renderAllWindows(io);
            }
        });
    }

    /**
     * Sets up docking and creates the main window.
     * This method is called once per frame and is responsible for setting up the docking and main window for the ImGui UI.
     * It sets up docking by creating a docking space over the main viewport and setting local flags to disable docking in the central node.
     * It then creates the main window by calling ImGui.begin with the docking space ID and various flags to disable movement, navigation, resizing, and other features.
     */
    private static void setupDockingAndMainWindow() {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();

        // Setup docking
        ImGui.setNextWindowBgAlpha(0);
        int mainDock = ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), ImGuiDockNodeFlags.NoDockingInCentralNode);
        imgui.internal.ImGui.dockBuilderGetCentralNode(mainDock).addLocalFlags(imgui.internal.flag.ImGuiDockNodeFlags.NoTabBar);

        // Create main window
        ImGui.setNextWindowDockID(mainDock);
        if (ImGui.begin("Main", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoNavInputs | ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoSavedSettings)) {
            ImGui.end();
        }
    }

    /**
     * Renders the main menu bar for the Groovy Engine's ImGui UI.
     * This method is called once per frame and is responsible for rendering the main menu bar.
     * It begins the main menu bar and then renders all categories in the desired order.
     * For each category, it renders a menu item for each window in that category.
     * If the window is enabled, clicking the menu item will toggle the window.
     * It also renders additional menu items (View, Help) on the right side of the menu bar.
     * The View menu allows the user to close all windows and open the Editor Screen.
     * The Help menu displays keybind information for toggling the ImGui UI and opening the Editor Screen.
     */
    private static void renderMainMenuBar() {
        if (ImGui.beginMainMenuBar()) {
            // Define the exact order you want
            List<String> desiredOrder = Arrays.asList(
                    ImIcons.STACKS.get() + " ImGui",        // Leftmost
                    ImIcons.WRENCH.get() + " Construct",    //
                    ImIcons.CAMERA.get() + " Lens",         //
                    ImIcons.CODE.get() + " Threads",      //
                    ImIcons.EDIT.get() + " Scribe",       //
                    ImIcons.SETTINGS.get() + " Debug"         // Rightmost before View/Help
            );

            // Get all categories and sort according to desired order
            List<String> sortedCategories = getSortedCategoriesFor(desiredOrder);

            // Render categories in the desired order
            for (String category : sortedCategories) {
                if (ImGui.beginMenu(category)) {
                    for (var window : WindowManager.getWindowsByCategory(category)) {
                        boolean enabled = window.isEnabled();
                        if (ImGui.menuItem(window.getTitle(), null, enabled)) {
                            window.toggle();
                        }
                    }
                    ImGui.endMenu();
                }
            }

            // Additional menu items (View, Help) - these will appear on the right
            if (ImGui.beginMenu(ImIcons.VISIBLE.get() + " View")) {
                if (ImGui.menuItem("Close All Windows")) {
                    WindowManager.closeAllWindows();
                }
                ImGui.separator();
                if (ImGui.menuItem("Open Editor Screen")) {
                    Minecraft.getInstance().setScreen(new EditorScreen());
                }
                ImGui.endMenu();
            }

            // Help menu with keybind info
            if (ImGui.beginMenu(ImIcons.QUESTION.get() + " Help")) {
                ImGui.text("Keybinds:");
                ImGui.text(KeybindManager.TOGGLE_IMGUI.getTranslatedKeyMessage().getString() + " - Toggle ImGui");
                ImGui.text(KeybindManager.OPEN_EDITOR_SCREEN.getTranslatedKeyMessage().getString() + " - Open Editor Screen");
                ImGui.separator();
                ImGui.text("Change keybinds in Options > Controls");
                ImGui.endMenu();
            }

            ImGui.endMainMenuBar();
        }
    }

/* <<<<<<<<<<<<<<  ✨ Windsurf Command ⭐ >>>>>>>>>>>>>>>> */
    /**
     * Returns a list of categories sorted according to the given desired order.
     * If a category is not in the desired order list, it is sorted alphabetically.
     * If a category is in the desired order list, it is sorted by its position in that list.
     * If two categories are in the desired order list, the one with the lower index comes first.
     * If one category is in the desired order list and the other is not, the one in the desired order list comes first.
     *
     * @param desiredOrder The list of categories in the desired order
     * @return A list of categories sorted according to the given desired order
     */
/* <<<<<<<<<<  eb41226b-af41-4337-9a38-d89a5cedfae2  >>>>>>>>>>> */
    private static @NotNull List<String> getSortedCategoriesFor(List<String> desiredOrder) {
        List<String> sortedCategories = new ArrayList<>(WindowManager.getCategories());
        sortedCategories.sort((a, b) -> {
            int indexA = desiredOrder.indexOf(a);
            int indexB = desiredOrder.indexOf(b);

            // If both are in the desired order list, sort by that order
            if (indexA != -1 && indexB != -1) {
                return Integer.compare(indexA, indexB);
            }
            // If only one is in the desired order, that one comes first
            else if (indexA != -1) {
                return -1;
            } else if (indexB != -1) {
                return 1;
            }
            // If neither are in the desired order, sort alphabetically
            else {
                return a.compareTo(b);
            }
        });
        return sortedCategories;
    }
}