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

package io.github.luckymcdev.groovyengine.core.client.editor.core.window;

import imgui.ImGuiIO;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.*;

/**
 * Manages the Construct windows and their respective categories.
 * This class is responsible for registering the Construct windows with the Window Manager.
 * It provides methods to register a window with the manager and to register a window in a specific category.
 */
@OnlyIn(Dist.CLIENT)
public class WindowManager {
    private static final Map<String, EditorWindow> windows = new LinkedHashMap<>();
    private static final Map<String, String> categories = new HashMap<>();

    private WindowManager() {}

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