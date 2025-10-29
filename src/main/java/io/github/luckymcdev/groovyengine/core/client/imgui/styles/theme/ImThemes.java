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

import imgui.ImGui;
import imgui.ImGuiStyle;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.core.client.imgui.styles.theme.defaults.*;

public class ImThemes {
    private static final ImGuiStyle STYLE = ImGui.getStyle();

    private static final BessDarkImTheme BESS_DARK_IM_THEME = new BessDarkImTheme();
    private static final CatpuccinMochaImTheme CATPUCCIN_MOCHA_IM_THEME = new CatpuccinMochaImTheme();
    private static final ModernDarkImTheme MODERN_DARK_IM_THEME = new ModernDarkImTheme();
    private static final DarkImTheme DARK_IM_THEME = new DarkImTheme();
    private static final CherryImTheme CHERRY_IM_THEME = new CherryImTheme();


    /**
     * Applies the Bess Dark ImGui theme.
     */
    public static void applyBessDark() {
        BESS_DARK_IM_THEME.applyTheme(STYLE);
        GE.CORE_LOG.debug("Applied ImGui theme: " + BESS_DARK_IM_THEME.getName());
    }

    /**
     * Applies the Cherry ImGui theme.
     */
    public static void applyCherry() {
        CHERRY_IM_THEME.applyTheme(STYLE);
        GE.CORE_LOG.debug("Applied ImGui theme: " + CHERRY_IM_THEME.getName());
    }

    /**
     * Applies the Catppuccin Mocha ImGui theme.
     */
    public static void applyCatpuccinMocha() {
        CATPUCCIN_MOCHA_IM_THEME.applyTheme(STYLE);
        GE.CORE_LOG.debug("Applied ImGui theme: " + CATPUCCIN_MOCHA_IM_THEME.getName());
    }

    /**
     * Applies the Modern Dark ImGui theme.
     */
    public static void applyModernDark() {
        MODERN_DARK_IM_THEME.applyTheme(STYLE);
        GE.CORE_LOG.debug("Applied ImGui theme: " + MODERN_DARK_IM_THEME.getName());
    }

    /**
     * Applies the Dark ImGui theme.
     */
    public static void applyDark() {
        DARK_IM_THEME.applyTheme(STYLE);
        GE.CORE_LOG.debug("Applied ImGui theme: " + DARK_IM_THEME.getName());
    }

}
