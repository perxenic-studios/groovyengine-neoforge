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
