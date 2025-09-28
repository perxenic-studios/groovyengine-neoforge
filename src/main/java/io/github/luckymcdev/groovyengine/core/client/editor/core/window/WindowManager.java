package io.github.luckymcdev.groovyengine.core.client.editor.core.window;

import imgui.ImGuiIO;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class WindowManager {
    private static final Map<String, EditorWindow> windows = new LinkedHashMap<>();
    private static final Map<String, String> categories = new HashMap<>();

    /**
     * Register a window with the manager
     */
    public static void registerWindow(EditorWindow window) {
        registerWindow(window, "General");
    }

    /**
     * Register a window with the manager in a specific category
     */
    public static void registerWindow(EditorWindow window, String category) {
        windows.put(window.getId(), window);
        categories.put(window.getId(), category);
    }

    /**
     * Get a window by ID
     */
    public static EditorWindow getWindow(String id) {
        return windows.get(id);
    }

    /**
     * Get all windows
     */
    public static Collection<EditorWindow> getAllWindows() {
        return windows.values();
    }

    /**
     * Get windows by category
     */
    public static List<EditorWindow> getWindowsByCategory(String category) {
        return windows.values().stream()
                .filter(window -> category.equals(categories.get(window.getId())))
                .toList();
    }

    /**
     * Get all categories
     */
    public static Set<String> getCategories() {
        return new HashSet<>(categories.values());
    }

    /**
     * Get the category for a window
     */
    public static String getCategory(String windowId) {
        return categories.getOrDefault(windowId, "General");
    }

    /**
     * Toggle a window by ID
     */
    public static void toggleWindow(String id) {
        EditorWindow window = windows.get(id);
        if (window != null) {
            window.toggle();
        }
    }

    /**
     * Enable a window by ID
     */
    public static void enableWindow(String id) {
        EditorWindow window = windows.get(id);
        if (window != null) {
            window.setEnabled(true);
        }
    }

    /**
     * Disable a window by ID
     */
    public static void disableWindow(String id) {
        EditorWindow window = windows.get(id);
        if (window != null) {
            window.setEnabled(false);
        }
    }

    /**
     * Render all enabled windows
     */
    public static void renderAllWindows(ImGuiIO io) {
        for (EditorWindow window : windows.values()) {
            if (window.isEnabled()) {
                window.render(io);
            }
        }
    }

    /**
     * Close all windows
     */
    public static void closeAllWindows() {
        for (EditorWindow window : windows.values()) {
            window.setEnabled(false);
        }
    }
}