package io.github.luckymcdev.groovyengine.core.client.imgui.core;

import com.mojang.blaze3d.platform.Window;
import io.github.luckymcdev.groovyengine.core.client.editor.core.EditorScreen;
import io.github.luckymcdev.groovyengine.core.client.editor.core.EditorState;
import io.github.luckymcdev.groovyengine.core.client.editor.core.GroovyEngineEditor;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.WindowManager;
import io.github.luckymcdev.groovyengine.core.client.editor.windows.*;
import imgui.ImGui;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber
public class ImGuiRenderer {

    private static boolean initialized = false;

    private static void initializeWindows() {
        if (initialized) return;

        // Register debugging windows
        WindowManager.registerWindow(new MovementDebuggingWindow(), "Debug");
        WindowManager.registerWindow(new EditorControlWindow(), "Debug");
        WindowManager.registerWindow(new RenderingDebuggingWindow(), "Debug");

        // Register demo windows
        WindowManager.registerWindow(new DemoWindows.AboutWindow(), "ImGui");
        WindowManager.registerWindow(new DemoWindows.DemoWindow(), "ImGui");
        WindowManager.registerWindow(new DemoWindows.MetricsWindow(), "ImGui");

        initialized = true;
    }

    @SubscribeEvent
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
            // Windows menu organized by category
            for (String category : WindowManager.getCategories()) {
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

            // Additional menu items
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
                ImGui.text("F1 - Toggle ImGui (default)");
                ImGui.text("F2 - Open Editor Screen (default)");
                ImGui.separator();
                ImGui.text("Change keybinds in Options > Controls");
                ImGui.endMenu();
            }

            ImGui.endMainMenuBar();
        }
    }
}