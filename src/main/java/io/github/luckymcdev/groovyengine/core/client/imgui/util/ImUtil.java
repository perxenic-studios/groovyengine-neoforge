package io.github.luckymcdev.groovyengine.core.client.imgui.util;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.type.ImBoolean;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ImUtil {

    public static float[] toFloatArray(float value) {
        return new float[]{value};
    }

    public static float[] toFloatArray(float... values) {
        return values;
    }

    public static int[] toIntArray(int value) {
        return new int[]{value};
    }

    //Windows

    public static void window(String title, Runnable body) {
        if (ImGui.begin(title)) {
            body.run();
        }
        ImGui.end();
    }

    public static void window(String title, ImBoolean pOpen, Runnable body) {
        if (ImGui.begin(title, pOpen)) {
            body.run();
        }
        ImGui.end();
    }

    public static void window(final String title, final int imGuiWindowFlags, Runnable body) {
        if (ImGui.begin(title, imGuiWindowFlags)) {
            body.run();
        }
        ImGui.end();
    }

    public static void window(final String title, final ImBoolean pOpen, final int imGuiWindowFlags, Runnable body) {
        if (ImGui.begin(title, pOpen, imGuiWindowFlags)) {
            body.run();
        }
        ImGui.end();
    }

    // Buttons

    public static void button(String title, Runnable body) {
        if(ImGui.button(title)) {
            body.run();
        }
    }

    public static void button(String title, ImVec2 size, Runnable body) {
        if(ImGui.button(title, size)) {
            body.run();
        }
    }

    public static void button(String title, float sizeX, float sizeY, Runnable body) {
        if(ImGui.button(title, sizeX, sizeY)) {
            body.run();
        }
    }
}