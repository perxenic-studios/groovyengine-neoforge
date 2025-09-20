package io.github.luckymcdev.groovyengine.core.client.imgui.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ImUtil {

    // Convert a single float to a float[] for ImGui sliders
    public static float[] toFloatArray(float value) {
        return new float[]{value};
    }

    // Convert multiple floats to a float[] for ImGui sliders
    public static float[] toFloatArray(float... values) {
        return values;
    }

    public static int[] toIntArray(int value) {
        return new int[]{value};
    }
}