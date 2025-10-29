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

package io.github.luckymcdev.groovyengine.core.client.imgui.styles.theme.defaults;

import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import io.github.luckymcdev.groovyengine.core.client.imgui.styles.theme.ImTheme;

public class DarkImTheme implements ImTheme {
    @Override
    public String getName() {
        return "Dark";
    }

    /**
     * Applies the Dark theme to the given ImGuiStyle.
     * @param style The ImGuiStyle to apply the theme to.
     */
    @Override
    public void applyTheme(ImGuiStyle style) {
        style.setColor(ImGuiCol.WindowBg, 0.1f, 0.105f, 0.11f, 1.0f);

        // Headers
        style.setColor(ImGuiCol.Header, 0.2f, 0.205f, 0.21f, 1.0f);
        style.setColor(ImGuiCol.HeaderHovered, 0.3f, 0.305f, 0.31f, 1.0f);
        style.setColor(ImGuiCol.HeaderActive, 0.15f, 0.1505f, 0.151f, 1.0f);

        // Buttons
        style.setColor(ImGuiCol.Button, 0.2f, 0.205f, 0.21f, 1.0f);
        style.setColor(ImGuiCol.ButtonHovered, 0.3f, 0.305f, 0.31f, 1.0f);
        style.setColor(ImGuiCol.ButtonActive, 0.15f, 0.1505f, 0.151f, 1.0f);

        // Frame BG
        style.setColor(ImGuiCol.FrameBg, 0.2f, 0.205f, 0.21f, 1.0f);
        style.setColor(ImGuiCol.FrameBgHovered, 0.3f, 0.305f, 0.31f, 1.0f);
        style.setColor(ImGuiCol.FrameBgActive, 0.15f, 0.1505f, 0.151f, 1.0f);

        // Tabs
        style.setColor(ImGuiCol.Tab, 0.15f, 0.1505f, 0.151f, 1.0f);
        style.setColor(ImGuiCol.TabHovered, 0.38f, 0.3805f, 0.381f, 1.0f);
        style.setColor(ImGuiCol.TabActive, 0.28f, 0.2805f, 0.281f, 1.0f);
        style.setColor(ImGuiCol.TabUnfocused, 0.15f, 0.1505f, 0.151f, 1.0f);
        style.setColor(ImGuiCol.TabUnfocusedActive, 0.2f, 0.205f, 0.21f, 1.0f);

        // Title
        style.setColor(ImGuiCol.TitleBg, 0.15f, 0.1505f, 0.151f, 1.0f);
        style.setColor(ImGuiCol.TitleBgActive, 0.15f, 0.1505f, 0.151f, 1.0f);
        style.setColor(ImGuiCol.TitleBgCollapsed, 0.15f, 0.1505f, 0.151f, 1.0f);
    }
}
