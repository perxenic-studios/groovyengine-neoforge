package io.github.luckymcdev.groovyengine.core.client.imgui.styles.theme;

import imgui.ImGuiStyle;

public interface ImTheme {
    String getName();
    void applyTheme(ImGuiStyle style);
}
