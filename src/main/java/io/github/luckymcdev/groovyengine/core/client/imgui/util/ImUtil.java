package io.github.luckymcdev.groovyengine.core.client.imgui.util;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.type.ImBoolean;
import io.github.luckymcdev.groovyengine.core.client.imgui.styles.ImGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Consumer;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
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

    // Text

    public static void title(String text) {
        // Use a separator and bold styling for title effect
        ImGui.separator();

        // Make text stand out with different color or styling
        ImGui.pushStyleColor(ImGuiCol.Text, 0xFFFFFFFF); // Bright white
        ImGui.text(text);
        ImGui.popStyleColor();

        ImGui.separator();
        ImGui.spacing();
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
        if (ImGui.button(title)) {
            body.run();
        }
    }

    public static void button(String title, ImVec2 size, Runnable body) {
        if (ImGui.button(title, size)) {
            body.run();
        }
    }

    public static void button(String title, float sizeX, float sizeY, Runnable body) {
        if (ImGui.button(title, sizeX, sizeY)) {
            body.run();
        }
    }

    // checkboxes

    public static void checkbox(String label, ImBoolean imBoolean, Runnable body) {
        if (ImGui.checkbox(label, imBoolean)) {
            imBoolean.set(imBoolean.get());
            body.run();
        }
    }

    // CollapsingHeader



    public static void collapsingHeader(final String label, Runnable body) {
        if(ImGui.collapsingHeader(label)) {
            body.run();
        }
    }

    public static void pushIconScale() {
        ImGraphics.INSTANCE.pushStack();
        ImGraphics.INSTANCE.setFontScale(1.75f);
    }

    public static void pushIconScale(float multiplier) {
        ImGraphics.INSTANCE.pushStack();
        ImGraphics.INSTANCE.setFontScale(1.75f * multiplier);
    }

    public static void popIconScale() {
        ImGraphics.INSTANCE.popStack();
    }


    // Utils

    public static void helpMarker(String description) {
        ImGui.sameLine();
        ImGui.textDisabled("(?)");
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.pushTextWrapPos(ImGui.getFontSize() * 35.0f);
            ImGui.textUnformatted(description);
            ImGui.popTextWrapPos();
            ImGui.endTooltip();
        }
    }
}