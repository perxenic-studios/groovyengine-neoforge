package io.github.luckymcdev.groovyengine.core.client.imgui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.type.ImBoolean;
import io.github.luckymcdev.groovyengine.core.client.imgui.styles.ImGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ImGe extends ImGui {

    // Float / Int utilities
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
        separator();
        pushStyleColor(ImGuiCol.Text, 0xFFFFFFFF);
        text(text);
        popStyleColor();
        separator();
        spacing();
    }

    // Windows
    public static void window(String title, Runnable body) {
        if (begin(title)) body.run();
        end();
    }

    public static void window(String title, ImBoolean pOpen, Runnable body) {
        if (begin(title, pOpen)) body.run();
        end();
    }

    public static void window(String title, int flags, Runnable body) {
        if (begin(title, flags)) body.run();
        end();
    }

    public static void window(String title, ImBoolean pOpen, int flags, Runnable body) {
        if (begin(title, pOpen, flags)) body.run();
        end();
    }

    // Buttons
    public static void button(String title, Runnable body) {
        if (button(title)) body.run();
    }

    public static void button(String title, ImVec2 size, Runnable body) {
        if (button(title, size)) body.run();
    }

    public static void button(String title, float sizeX, float sizeY, Runnable body) {
        if (button(title, sizeX, sizeY)) body.run();
    }

    // Checkbox
    public static void checkbox(String label, ImBoolean imBoolean, Runnable body) {
        if (checkbox(label, imBoolean)) {
            imBoolean.set(imBoolean.get());
            body.run();
        }
    }

    // Collapsing header
    public static void collapsingHeader(String label, Runnable body) {
        if (collapsingHeader(label)) body.run();
    }

    // Icon scale
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
        sameLine();
        textDisabled("(?)");
        if (isItemHovered()) {
            beginTooltip();
            pushTextWrapPos(getFontSize() * 35.0f);
            textUnformatted(description);
            popTextWrapPos();
            endTooltip();
        }
    }
}
