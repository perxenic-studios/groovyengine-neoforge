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

package io.github.luckymcdev.groovyengine.core.client.imgui.styles;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.extension.imnodes.ImNodes;
import imgui.flag.ImGuiCol;
import io.github.luckymcdev.groovyengine.core.client.imgui.core.ImNumberType;
import io.github.luckymcdev.groovyengine.core.client.imgui.styles.theme.ImThemes;
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
 * A class to make working with ImGui easier.
 *
 * @author LatvianModder but changes by LuckyMcDev
 */
@OnlyIn(Dist.CLIENT)
public class ImGraphics {
    public static ImGraphics INSTANCE = new ImGraphics(Minecraft.getInstance());
    public final Minecraft mc;
    public final boolean inGame;
    private VarStackStack stack;

    public ImGraphics(Minecraft mc) {
        this.mc = mc;
        this.inGame = mc.player != null && mc.level != null;
    }

    /**
     * Sets the given ImGuiStyle to the full default style, which includes
     * setting the window padding to (8F, 8F), frame padding to (4F, 3F),
     * popup border size to 0F, item spacing to (8F, 8F), and item inner spacing
     * to (8F, 6F).
     *
     * @param style The ImGuiStyle to set to the full default style.
     */
    public static void setFullDefaultStyle(ImGuiStyle style) {
        setDefaultStyle(style);
        style.setWindowPadding(8F, 8F);
        style.setFramePadding(4F, 3F);
        style.setPopupBorderSize(0F);
        style.setItemSpacing(8F, 8F);
        style.setItemInnerSpacing(8F, 6F);
    }

    /**
     * Sets the given ImGuiStyle to the default style, which is a set of
     * values that are used as the default for ImGui windows and widgets.
     * This includes setting the window rounding to 4F, frame rounding to 3F,
     * child rounding to 3F, popup rounding to 3F, scrollbar rounding to 9F,
     * grab rounding to 3F, indent spacing to 25F, scrollbar size to 15F,
     * grab minimum size to 5F, window border size to 0F, selectable text align
     * to (0F, 0.5F), and alpha to 1F. Additionally, this sets the colors
     * for the window background, popup background, frame background, title background,
     * title background active, menu bar background, title background collapsed, and
     * button, button hovered, and button active to the default colors.
     *
     * @param style The ImGuiStyle to set to the default style.
     */
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

    /**
     * Sets the color of the given ImGuiStyle at the given key to the given color.
     *
     * @param style The ImGuiStyle to set the color of.
     * @param key   The key to set the color of.
     * @param color The color to set, in ARGB format.
     */
    public static void setColor(ImGuiStyle style, int key, int color) {
        style.setColor(key, ARGB.toABGR(color));
    }

    /**
     * Returns the OpenGL texture ID for the given resource location.
     *
     * @param identifier The resource location of the texture.
     * @return The OpenGL texture ID of the texture.
     */
    public static int getTextureId(ResourceLocation identifier) {
        return Minecraft.getInstance().getTextureManager().getTexture(identifier).getId();
    }

    /**
     * Renders a texture with the given resource location, width, and height.
     *
     * @param id     The resource location of the texture to render.
     * @param width  The width of the texture to render.
     * @param height The height of the texture to render.
     */
    public static void texture(ResourceLocation id, float width, float height) {
        ImGui.image(getTextureId(id), width, height);
    }

    /**
     * Renders a texture with the given resource location, width, height, and texture coordinates.
     *
     * @param id     The resource location of the texture to render.
     * @param width  The width of the texture to render.
     * @param height The height of the texture to render.
     * @param u0     The x-coordinate of the top-left corner of the texture in the texture coordinates.
     * @param v0     The y-coordinate of the top-left corner of the texture in the texture coordinates.
     */
    public static void texture(ResourceLocation id, float width, float height, float u0, float v0) {
        ImGui.image(getTextureId(id), width, height, u0, v0);
    }

    /**
     * Renders a texture with the given resource location, width, height, and texture coordinates.
     *
     * @param id     The resource location of the texture to render.
     * @param width  The width of the texture to render.
     * @param height The height of the texture to render.
     * @param u0     The x-coordinate of the top-left corner of the texture in the texture coordinates.
     * @param v0     The y-coordinate of the top-left corner of the texture in the texture coordinates.
     * @param u1     The x-coordinate of the bottom-right corner of the texture in the texture coordinates.
     * @param v1     The y-coordinate of the bottom-right corner of the texture in the texture coordinates.
     */
    public static void texture(ResourceLocation id, float width, float height, float u0, float v0, float u1, float v1) {
        ImGui.image(getTextureId(id), width, height, u0, v0, u1, v1);
    }

    /**
     * Pushes a new VarStackStack onto the stack, copying the current number type and number range.
     * This method is used to isolate changes to the VarStackStack made by a function or code block.
     * After calling this method, any changes made to the VarStackStack will not affect the original stack.
     */
    public void pushStack() {
        VarStackStack newStack = new VarStackStack();
        newStack.parent = stack;

        if (stack != null) {
            newStack.numberType = stack.numberType;
            newStack.numberRange = stack.numberRange;
        }

        stack = newStack;
    }

    /**
     * Pushes a new root VarStackStack onto the stack, copying the current number type and number range.
     * This method is used to isolate changes to the VarStackStack made by a function or code block.
     * After calling this method, any changes made to the VarStackStack will not affect the original stack.
     * Additionally, this method sets the default ImGui style to Bess Dark, the number type to double, and the number range to null.
     */
    public void pushRootStack() {
        pushStack();
        //setDefaultStyle(ImGui.getStyle());
        ImThemes.applyBessDark();
        setNumberType(ImNumberType.DOUBLE);
        setNumberRange(null);
    }

    /**
     * Pops the current VarStackStack off the stack, undoing all of the changes made to the VarStackStack since the last call to pushStack().
     * This method is used to restore the original VarStackStack after a function or code block has finished making changes to the VarStackStack.
     * After calling this method, any changes made to the VarStackStack will affect the original stack.
     * If there is no matching pushStack() call, this method will throw a RuntimeException.
     */
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

    /**
     * Sets the style variable of the given key to the given value.
     * This method modifies the VarStackStack by incrementing the pushedStyle count.
     *
     * @param key   The key of the style variable to set.
     * @param value The value to set the style variable to.
     */
    public void setStyleVar(int key, float value) {
        ImGui.pushStyleVar(key, value);
        stack.pushedStyle++;
    }

    /**
     * Sets the style variable of the given key to the given value.
     * This method modifies the VarStackStack by incrementing the pushedStyle count.
     *
     * @param key The key of the style variable to set.
     * @param x   The x-coordinate of the value to set the style variable to.
     * @param y   The y-coordinate of the value to set the style variable to.
     */
    public void setStyleVar(int key, float x, float y) {
        ImGui.pushStyleVar(key, x, y);
        stack.pushedStyle++;
    }

    /**
     * Sets the style color of the given key to the given value.
     * This method modifies the VarStackStack by incrementing the pushedColors count.
     *
     * @param key The key of the style color to set.
     * @param r   The red component of the color to set the style color to.
     * @param g   The green component of the color to set the style color to.
     * @param b   The blue component of the color to set the style color to.
     * @param a   The alpha component of the color to set the style color to.
     */
    public void setStyleCol(int key, float r, float g, float b, float a) {
        ImGui.pushStyleColor(key, r, g, b, a);
        stack.pushedColors++;
    }

    /**
     * Sets the style color of the given key to the given value.
     * This method modifies the VarStackStack by incrementing the pushedColors count.
     *
     * @param key The key of the style color to set.
     * @param r   The red component of the color to set the style color to.
     * @param g   The green component of the color to set the style color to.
     * @param b   The blue component of the color to set the style color to.
     * @param a   The alpha component of the color to set the style color to.
     */
    public void setStyleCol(int key, int r, int g, int b, int a) {
        ImGui.pushStyleColor(key, r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
        stack.pushedColors++;
    }

    /**
     * Sets the style color of the given key to the given value.
     * This method modifies the VarStackStack by incrementing the pushedColors count.
     *
     * @param key  The key of the style color to set.
     * @param argb The color to set the style color to, in ARGB format.
     */
    public void setStyleCol(int key, int argb) {
        setStyleCol(key, (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >> 24) & 0xFF);
    }

    /**
     * Sets the style color of the given key to the given value.
     * This method modifies the VarStackStack by incrementing the pushedColors count.
     *
     * @param key   The key of the style color to set.
     * @param value The color to set the style color to.
     */
    public void setStyleCol(int key, Color value) {
        setStyleCol(key, value.r, value.g, value.b, value.a);
    }

    /**
     * Sets the style variable of the given key to the given value for nodes.
     * This method modifies the VarStackStack by incrementing the pushedNodesStyle count.
     *
     * @param key   The key of the style variable to set.
     * @param value The value to set the style variable to.
     */
    public void setNodesStyleVar(int key, float value) {
        ImNodes.pushStyleVar(key, value);
        stack.pushedNodesStyle++;
    }

    /**
     * Sets the style variable of the given key to the given value for nodes.
     * This method modifies the VarStackStack by incrementing the pushedNodesStyle count.
     *
     * @param key The key of the style variable to set.
     * @param x   The x-coordinate of the value to set the style variable to.
     * @param y   The y-coordinate of the value to set the style variable to.
     */
    public void setNodesStyleVar(int key, float x, float y) {
        ImNodes.pushStyleVar(key, x, y);
        stack.pushedNodesStyle++;
    }

    /**
     * Sets the style color of the given key to the given value for nodes.
     * This method modifies the VarStackStack by incrementing the pushedNodesColors count.
     *
     * @param key The key of the style color to set.
     * @param r   The red component of the color to set the style color to, in the range [0, 255].
     * @param g   The green component of the color to set the style color to, in the range [0, 255].
     * @param b   The blue component of the color to set the style color to, in the range [0, 255].
     * @param a   The alpha component of the color to set the style color to, in the range [0, 255].
     */
    public void setNodesStyleCol(int key, int r, int g, int b, int a) {
        ImNodes.pushColorStyle(key, (a << 24) | (b << 16) | (g << 8) | r);
        stack.pushedNodesColors++;
    }

    /**
     * Sets the style color of the given key to the given value for nodes.
     * This method modifies the VarStackStack by incrementing the pushedNodesColors count.
     *
     * @param key The key of the style color to set.
     * @param r   The red component of the color to set the style color to, in the range [0, 1].
     * @param g   The green component of the color to set the style color to, in the range [0, 1].
     * @param b   The blue component of the color to set the style color to, in the range [0, 1].
     * @param a   The alpha component of the color to set the style color to, in the range [0, 1].
     */
    public void setNodesStyleCol(int key, float r, float g, float b, float a) {
        setNodesStyleCol(key, (int) (r * 255F), (int) (g * 255F), (int) (b * 255F), (int) (a * 255F));
    }

    /**
     * Sets the style color of the given key to the given value for nodes.
     * This method modifies the VarStackStack by incrementing the pushedNodesColors count.
     *
     * @param key  The key of the style color to set.
     * @param argb The color to set the style color to, in ARGB format.
     */
    public void setNodesStyleCol(int key, int argb) {
        setNodesStyleCol(key, (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >> 24) & 0xFF);
    }

    /**
     * Sets the style color of the given key to the given value for nodes.
     * This method modifies the VarStackStack by incrementing the pushedNodesColors count.
     *
     * @param key   The key of the style color to set.
     * @param value The color to set the style color to.
     */
    public void setNodesStyleCol(int key, Color value) {
        setNodesStyleCol(key, value.r, value.g, value.b, value.a);
    }

    /**
     * Sets the item flag of the given key to the given value.
     * This method modifies the VarStackStack by incrementing the pushedItemFlags count.
     *
     * @param key  The key of the item flag to set.
     * @param flag The value to set the item flag to.
     */
    public void setItemFlag(int key, boolean flag) {
        imgui.internal.ImGui.pushItemFlag(key, flag);
        stack.pushedItemFlags++;
    }

    /**
     * Sets the font scale for the ImGui context.
     * This method modifies the VarStackStack by incrementing the pushedFontScales count.
     * It also modifies the current font scale of the VarStackStack.
     *
     * @param scale The scale to set the font to, in the range [0, Infinity].
     */
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

    /**
     * Returns the current number type of the VarStackStack.
     * This value is used to determine how numbers are formatted and parsed when using ImGui functions.
     *
     * @return The current number type of the VarStackStack.
     */
    public ImNumberType getNumberType() {
        return stack.numberType;
    }

    /**
     * Sets the number type of the VarStackStack.
     * This value is used to determine how numbers are formatted and parsed when using ImGui functions.
     *
     * @param type The number type to set the VarStackStack to.
     */
    public void setNumberType(ImNumberType type) {
        stack.numberType = type;
    }

    /**
     * Returns the current number range of the VarStackStack.
     * This value is used to clamp and validate numbers when using ImGui functions.
     *
     * @return The current number range of the VarStackStack, or null if no range is set.
     */
    @Nullable
    public NumberRange getNumberRange() {
        return stack.numberRange;
    }

    /**
     * Sets the number range of the VarStackStack.
     * This value is used to clamp and validate numbers when using ImGui functions.
     * If the range is null, no clamping or validation will be performed.
     *
     * @param range The number range to set the VarStackStack to, or null if no range is desired.
     */
    public void setNumberRange(@Nullable NumberRange range) {
        stack.numberRange = range;
    }

    /**
     * Sets the text color to the given variant.
     * This method sets the style color for ImGuiCol.Text to the given variant's text color.
     *
     * @param variant The ImColorVariant to set the text color to.
     */
    public void setText(ImColorVariant variant) {
        setStyleCol(ImGuiCol.Text, variant.textColor);
    }

    /**
     * Sets the text color to yellow, indicating a warning.
     */
    public void setWarningText() {
        setText(ImColorVariant.YELLOW);
    }

    /**
     * Sets the text color to red, indicating an error.
     */
    public void setErrorText() {
        setText(ImColorVariant.RED);
    }

    /**
     * Sets the text color to green, indicating success.
     */
    public void setSuccessText() {
        setText(ImColorVariant.GREEN);
    }

    /**
     * Sets the text color to blue, indicating information.
     */
    public void setInfoText() {
        setText(ImColorVariant.BLUE);
    }

    /**
     * Sets the text color to the given style.
     * If the style's color is not null, this method sets the style color for ImGuiCol.Text to the given style's text color.
     *
     * @param style The style to set the text color to.
     */
    public void setStyle(Style style) {
        if (style.getColor() != null) {
            setStyleCol(ImGuiCol.Text, 0xFF000000 | style.getColor().getValue());
        }
    }

    /**
     * A stack used to keep track of the current state of the ImGui variables.
     * This stack is used to isolate changes to the ImGui variables made by a function or code block.
     * Each VarStackStack contains a reference to its parent, the number of pushed style variables,
     * the number of pushed color variables, the number of pushed item flags, the number of pushed node style variables,
     * the number of pushed node color variables, the current font scale, and an array of the pushed font scales.
     * Additionally, each VarStackStack contains the current number type and number range.
     */
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
}