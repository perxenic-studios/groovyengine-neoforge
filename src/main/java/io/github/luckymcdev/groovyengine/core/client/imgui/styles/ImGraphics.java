package io.github.luckymcdev.groovyengine.core.client.imgui.styles;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.extension.imnodes.ImNodes;
import imgui.flag.ImGuiCol;
import io.github.luckymcdev.groovyengine.core.client.imgui.core.ImNumberType;
import io.github.luckymcdev.groovyengine.util.color.ARGB;
import io.github.luckymcdev.groovyengine.util.color.Color;
import io.github.luckymcdev.groovyengine.util.math.NumberRange;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * @author LatvianModder but changes by LuckyMcDev
 */
@OnlyIn(Dist.CLIENT)
public class ImGraphics {
    public static ImGraphics INSTANCE = new ImGraphics(Minecraft.getInstance());

    private static class VarStackStack {
        private VarStackStack parent;
        private int pushedStyle = 0;
        private int pushedColors = 0;
        private int pushedItemFlags = 0;
        private int pushedNodesStyle = 0;
        private int pushedNodesColors = 0;
        private float currentFontScale = 1F;
        private float[] pushedFontScales = null;
        private ImNumberType numberType = null;
        private NumberRange numberRange = null;
    }

    public final Minecraft mc;
    public final boolean inGame;
    private VarStackStack stack;

    public ImGraphics(Minecraft mc) {
        this.mc = mc;
        this.inGame = mc.player != null && mc.level != null;
    }

    public void pushStack() {
        VarStackStack newStack = new VarStackStack();
        newStack.parent = stack;

        if (stack != null) {
            newStack.numberType = stack.numberType;
            newStack.numberRange = stack.numberRange;
        }

        stack = newStack;
    }

    public void pushRootStack() {
        pushStack();
        setDefaultStyle(ImGui.getStyle());
        setNumberType(ImNumberType.DOUBLE);
        setNumberRange(null);
    }

    public void popStack() {
        if (stack == null) {
            throw new RuntimeException("popStack() called without a matching pushStack()");
        }

        if (stack.pushedStyle > 0) {
            ImGui.popStyleVar(stack.pushedStyle);
        }

        if (stack.pushedColors > 0) {
            ImGui.popStyleColor(stack.pushedColors);
        }

        for (int i = 0; i < stack.pushedNodesStyle; i++) {
            ImNodes.popStyleVar();
        }

        for (int i = 0; i < stack.pushedNodesColors; i++) {
            ImNodes.popColorStyle();
        }

        for (int i = 0; i < stack.pushedItemFlags; i++) {
            imgui.internal.ImGui.popItemFlag();
        }

        if (stack.pushedFontScales != null) {
            var font = ImGui.getFont();

            for (int i = stack.pushedFontScales.length - 1; i >= 0; i--) {
                font.setScale(stack.pushedFontScales[i]);
                ImGui.popFont();
            }
        }

        stack = stack.parent;
    }

    public void setStyleVar(int key, float value) {
        ImGui.pushStyleVar(key, value);
        stack.pushedStyle++;
    }

    public void setStyleVar(int key, float x, float y) {
        ImGui.pushStyleVar(key, x, y);
        stack.pushedStyle++;
    }

    public void setStyleCol(int key, float r, float g, float b, float a) {
        ImGui.pushStyleColor(key, r, g, b, a);
        stack.pushedColors++;
    }

    public void setStyleCol(int key, int r, int g, int b, int a) {
        ImGui.pushStyleColor(key, r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
        stack.pushedColors++;
    }

    public void setStyleCol(int key, int argb) {
        setStyleCol(key, (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >> 24) & 0xFF);
    }

    public void setStyleCol(int key, Color value) {
        setStyleCol(key, value.r, value.g, value.b, value.a);
    }

    public void setNodesStyleVar(int key, float value) {
        ImNodes.pushStyleVar(key, value);
        stack.pushedNodesStyle++;
    }

    public void setNodesStyleVar(int key, float x, float y) {
        ImNodes.pushStyleVar(key, x, y);
        stack.pushedNodesStyle++;
    }

    public void setNodesStyleCol(int key, int r, int g, int b, int a) {
        ImNodes.pushColorStyle(key, (a << 24) | (b << 16) | (g << 8) | r);
        stack.pushedNodesColors++;
    }

    public void setNodesStyleCol(int key, float r, float g, float b, float a) {
        setNodesStyleCol(key, (int) (r * 255F), (int) (g * 255F), (int) (b * 255F), (int) (a * 255F));
    }

    public void setNodesStyleCol(int key, int argb) {
        setNodesStyleCol(key, (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >> 24) & 0xFF);
    }

    public void setNodesStyleCol(int key, Color value) {
        setNodesStyleCol(key, value.r, value.g, value.b, value.a);
    }

    public void setItemFlag(int key, boolean flag) {
        imgui.internal.ImGui.pushItemFlag(key, flag);
        stack.pushedItemFlags++;
    }

    public void setFontScale(float scale) {
        var font = ImGui.getFont();
        font.setScale(scale);
        ImGui.pushFont(font);

        if (stack.pushedFontScales == null) {
            stack.pushedFontScales = new float[1];
        } else {
            stack.pushedFontScales = Arrays.copyOf(stack.pushedFontScales, stack.pushedFontScales.length + 1);
        }

        stack.pushedFontScales[stack.pushedFontScales.length - 1] = stack.currentFontScale;
        stack.currentFontScale = scale;
    }

    public void setNumberType(ImNumberType type) {
        stack.numberType = type;
    }

    public ImNumberType getNumberType() {
        return stack.numberType;
    }

    public void setNumberRange(@Nullable NumberRange range) {
        stack.numberRange = range;
    }

    @Nullable
    public NumberRange getNumberRange() {
        return stack.numberRange;
    }

    public static void setFullDefaultStyle(ImGuiStyle style) {
        setDefaultStyle(style);
        style.setWindowPadding(8F, 8F);
        style.setFramePadding(4F, 3F);
        style.setPopupBorderSize(0F);
        style.setItemSpacing(8F, 8F);
        style.setItemInnerSpacing(8F, 6F);
    }

    public static void setDefaultStyle(ImGuiStyle style) {
        style.setWindowRounding(4F);
        style.setFrameRounding(3F);
        style.setChildRounding(3F);
        style.setPopupRounding(3F);
        style.setScrollbarRounding(9F);
        style.setGrabRounding(3F);

        style.setIndentSpacing(25F);
        style.setScrollbarSize(15F);
        style.setGrabMinSize(5F);
        style.setWindowBorderSize(0F);
        style.setSelectableTextAlign(0F, 0.5F);
        style.setAlpha(1F);

        setColor(style, ImGuiCol.WindowBg, 0xFF222228);
        setColor(style, ImGuiCol.PopupBg, 0xE30D0D11);
        setColor(style, ImGuiCol.FrameBg, 0xFF15151C);
        setColor(style, ImGuiCol.TitleBg, 0xFF010101);
        setColor(style, ImGuiCol.TitleBgActive, 0xFF010101);
        setColor(style, ImGuiCol.MenuBarBg, 0xFF17171C);
        setColor(style, ImGuiCol.TitleBgCollapsed, 0xFF010101);

        setColor(style, ImGuiCol.Button, ImColorVariant.DEFAULT.color.argb());
        setColor(style, ImGuiCol.ButtonHovered, ImColorVariant.DEFAULT.hoverColor.argb());
        setColor(style, ImGuiCol.ButtonActive, ImColorVariant.DEFAULT.activeColor.argb());
    }

    public static void setColor(ImGuiStyle style, int key, int color) {
        style.setColor(key, ARGB.toABGR(color));
    }

    public void setText(ImColorVariant variant) {
        setStyleCol(ImGuiCol.Text, variant.textColor);
    }

    public void setWarningText() {
        setText(ImColorVariant.YELLOW);
    }

    public void setErrorText() {
        setText(ImColorVariant.RED);
    }

    public void setSuccessText() {
        setText(ImColorVariant.GREEN);
    }

    public void setInfoText() {
        setText(ImColorVariant.BLUE);
    }

    public void setStyle(Style style) {
        if (style.getColor() != null) {
            setStyleCol(ImGuiCol.Text, 0xFF000000 | style.getColor().getValue());
        }
    }

    public static int getTextureId(ResourceLocation identifier) {
        return Minecraft.getInstance().getTextureManager().getTexture(identifier).getId();
    }

    public static void texture(ResourceLocation id, float width, float height) {
        ImGui.image(getTextureId(id), width, height);
    }

    public static void texture(ResourceLocation id, float width, float height, float u0, float v0) {
        ImGui.image(getTextureId(id), width, height, u0, v0);
    }

    public static void texture(ResourceLocation id, float width, float height, float u0, float v0, float u1, float v1) {
        ImGui.image(getTextureId(id), width, height, u0, v0, u1, v1);
    }
}