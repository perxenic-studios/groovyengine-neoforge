package io.github.luckymcdev.groovyengine.core.client.imgui.core;

import com.mojang.blaze3d.platform.Window;
import io.github.luckymcdev.groovyengine.construct.client.editor.ConstructEditorWindow;
import io.github.luckymcdev.groovyengine.core.client.editor.core.EditorScreen;
import io.github.luckymcdev.groovyengine.core.client.editor.core.EditorState;
import io.github.luckymcdev.groovyengine.core.client.editor.core.GroovyEngineEditor;
import io.github.luckymcdev.groovyengine.core.client.editor.core.KeybindManager;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.WindowManager;
import io.github.luckymcdev.groovyengine.core.client.editor.windows.*;
import imgui.ImGui;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import io.github.luckymcdev.groovyengine.lens.client.editor.RenderingDebuggingWindow;
import io.github.luckymcdev.groovyengine.scribe.client.editor.ScribeWindow;
import io.github.luckymcdev.groovyengine.threads.client.editor.ThreadsWindow;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ImGuiRenderer {

    private static boolean initialized = false;

    private static void initializeWindows() {
        if (initialized) return;
        WindowManager.registerWindow(new EditorControlWindow(), "Debug");

        // Register demo windows
        WindowManager.registerWindow(new DemoWindows.AboutWindow(), "ImGui");
        WindowManager.registerWindow(new DemoWindows.DemoWindow(), "ImGui");
        WindowManager.registerWindow(new DemoWindows.MetricsWindow(), "ImGui");
        WindowManager.registerWindow(new DemoWindows.IconsWindow(), "ImGui");

        initialized = true;
    }

    public static void onRender(RenderGuiEvent.Post event) {
        initializeWindows();

        ImGuiImpl.draw(io -> {
            boolean isImGuiEnabled = GroovyEngineEditor.getEditorState() == EditorState.ENABLED;
            boolean isOnEditorScreen = Minecraft.getInstance().screen instanceof EditorScreen;

            // Always show editor control if ImGui is enabled OR if we're on the editor screen
            // This is the only window that should be accessible when ImGui is disabled
            if (isImGuiEnabled || isOnEditorScreen) {
                WindowManager.getWindow("editor_control").setEnabled(true);
            } else {
                WindowManager.getWindow("editor_control").setEnabled(false);
            }

            // Only show docking, main menu bar, and other windows when ImGui is fully enabled
            if (isImGuiEnabled) {
                setupDockingAndMainWindow();
                renderMainMenuBar();

                // Render all enabled windows (including editor control)
                WindowManager.renderAllWindows(io);
            } else {
                // When ImGui is disabled, only render the editor control window if it's enabled
                EditorWindow editorControl = WindowManager.getWindow("editor_control");
                if (editorControl != null && editorControl.isEnabled()) {
                    editorControl.render(io);
                }
            }
        });
    }

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

    private static void renderMainMenuBar() {
        if (ImGui.beginMainMenuBar()) {
            // Define the exact order you want
            List<String> desiredOrder = Arrays.asList(
                    "ImGui",        // Leftmost
                    "Construct",    //
                    "Lens",         //
                    "Threads",      //
                    "Scribe",       //
                    "Debug"         // Rightmost before View/Help
            );

            // Get all categories and sort according to desired order
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
                }
                else if (indexB != -1) {
                    return 1;
                }
                // If neither are in the desired order, sort alphabetically
                else {
                    return a.compareTo(b);
                }
            });

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
            if (ImGui.beginMenu("View")) {
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
            if (ImGui.beginMenu("Help")) {
                ImGui.text("Keybinds:");
                ImGui.text(KeybindManager.TOGGLE_IMGUI.getTranslatedKeyMessage().getString()+" - Toggle ImGui");
                ImGui.text(KeybindManager.OPEN_EDITOR_SCREEN.getTranslatedKeyMessage().getString()+" - Open Editor Screen");
                ImGui.separator();
                ImGui.text("Change keybinds in Options > Controls");
                ImGui.endMenu();
            }

            ImGui.endMainMenuBar();
        }
    }
}