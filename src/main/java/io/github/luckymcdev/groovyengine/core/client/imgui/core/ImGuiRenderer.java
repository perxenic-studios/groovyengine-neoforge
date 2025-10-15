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
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import io.github.luckymcdev.groovyengine.lens.client.editor.RenderingDebuggingWindow;
import io.github.luckymcdev.groovyengine.threads.client.editor.ThreadsWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ImGuiRenderer {

    private static boolean initialized = false;

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
                    ImIcons.STACKS.get() + " ImGui",        // Leftmost
                    ImIcons.WRENCH.get()+" Construct",    //
                    ImIcons.CAMERA.get() + " Lens",         //
                    ImIcons.CODE.get()+" Threads",      //
                    ImIcons.EDIT.get()+" Scribe",       //
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
                ImGui.text(KeybindManager.TOGGLE_IMGUI.getTranslatedKeyMessage().getString()+" - Toggle ImGui");
                ImGui.text(KeybindManager.OPEN_EDITOR_SCREEN.getTranslatedKeyMessage().getString()+" - Open Editor Screen");
                ImGui.separator();
                ImGui.text("Change keybinds in Options > Controls");
                ImGui.endMenu();
            }

            ImGui.endMainMenuBar();
        }
    }

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
            }
            else if (indexB != -1) {
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