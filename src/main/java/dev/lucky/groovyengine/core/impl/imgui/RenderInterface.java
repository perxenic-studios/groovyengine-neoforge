package dev.lucky.groovyengine.core.impl.imgui;

import imgui.ImGuiIO;

@FunctionalInterface
public interface RenderInterface {

    void render(final ImGuiIO io);

}
