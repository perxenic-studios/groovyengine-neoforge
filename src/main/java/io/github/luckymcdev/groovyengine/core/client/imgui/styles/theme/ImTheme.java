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

package io.github.luckymcdev.groovyengine.core.client.imgui.styles.theme;

import imgui.ImGuiStyle;

public interface ImTheme {
    /**
     * Returns the name of this theme.
     *
     * @return the name of this theme
     */
    String getName();

    /**
     * Applies the theme to the given ImGui style.
     * @param style the ImGui style to apply the theme to
     */
    void applyTheme(ImGuiStyle style);
}
