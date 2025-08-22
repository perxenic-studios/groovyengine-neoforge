package dev.lucky.groovyengine.impl.imgui;

import imgui.ImGuiIO;

@FunctionalInterface
public interface RenderInterface {

    void render(final ImGuiIO io);

}
