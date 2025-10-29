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

package io.github.luckymcdev.groovyengine.core.client.imgui.core;

import imgui.*;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.core.client.imgui.styles.ImGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class ImGuiImpl {
    private final static ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    private final static ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();

    private static ImFont defaultFont;

    /**
    * Initialize ImGui, ImPlot and ImNodes context.
    * Set configuration flags for docking and viewports.
    * Initialize ImGuiImplGlfw and ImGuiImplGl3.
    *
    * @param handle the window handle
    */
    public static void create(final long handle) {
        ImGui.createContext();
        ImPlot.createContext();
        ImNodes.createContext();

        final ImGuiIO data = ImGui.getIO();
        data.setIniFilename("groovyengine.ini");
        data.setFontGlobalScale(1f);

        ImGraphics.INSTANCE.pushRootStack();

        data.setConfigFlags(ImGuiConfigFlags.DockingEnable | ImGuiConfigFlags.ViewportsEnable);

        imGuiImplGlfw.init(handle, true);
        imGuiImplGl3.init();
    }

    /**
    * Load ImGui fonts.
    * Set configuration flags for docking and viewports.
    * Initialize ImGuiImplGlfw and ImGuiImplGl3.
    *
    * @param resourceManager the resource manager
    */
    public static void loadFonts(net.minecraft.server.packs.resources.ResourceManager resourceManager) {
        GE.CORE_LOG.debug("Loading ImGui fonts...");
        final ImGuiIO io = ImGui.getIO();
        final ImFontAtlas fonts = io.getFonts();

        ImFontGlyphRangesBuilder builder = new ImFontGlyphRangesBuilder();
        builder.addRanges(fonts.getGlyphRangesDefault());    // Basic + Extended Latin
        builder.addRanges(fonts.getGlyphRangesCyrillic());   // Cyrillic characters
        short[] combinedGlyphRanges = builder.buildRanges();

        try {
            // Load JetBrains Mono
            ResourceLocation jetBrainsLocation = GE.id("fonts/jetbrains_mono_regular.ttf");
            var jetBrainsRes = resourceManager.getResource(jetBrainsLocation);
            if (jetBrainsRes.isPresent()) {
                byte[] fontData;
                try (var input = jetBrainsRes.get().open()) {
                    fontData = input.readAllBytes();
                }

                if (fontData.length > 0) {
                    ImFontConfig config = new ImFontConfig();
                    config.setName("JetBrains Mono");
                    config.setGlyphRanges(combinedGlyphRanges);

                    defaultFont = fonts.addFontFromMemoryTTF(fontData, 16f, config);
                    config.destroy();
                    GE.CORE_LOG.debug("Loaded JetBrains Mono font ({} bytes)", fontData.length);
                } else {
                    defaultFont = fonts.addFontDefault();
                    GE.CORE_LOG.debug("JetBrains font is empty, using default");
                }
            } else {
                defaultFont = fonts.addFontDefault();
                GE.CORE_LOG.debug("JetBrains font not found, using default");
            }

            // Merge Material Icons safely
            ResourceLocation materialLocation = GE.id("fonts/material_icons_round_regular.ttf");
            var materialRes = resourceManager.getResource(materialLocation);
            if (materialRes.isPresent()) {
                byte[] iconData;
                try (var input = materialRes.get().open()) {
                    iconData = input.readAllBytes();
                }

                if (iconData.length > 0) {
                    ImFontConfig iconConfig = new ImFontConfig();
                    iconConfig.setMergeMode(true);
                    iconConfig.setPixelSnapH(true);
                    iconConfig.setName("Material Icons");

                    short[] iconRanges = new short[]{(short) 0xE000, (short) 0xF8FF, 0};
                    fonts.addFontFromMemoryTTF(iconData, 16f, iconConfig, iconRanges);
                    iconConfig.destroy();
                    GE.CORE_LOG.debug("Merged Material Icons font ({} bytes)", iconData.length);
                } else {
                    GE.CORE_LOG.debug("Material Icons font is empty, skipping merge");
                }
            } else {
                GE.CORE_LOG.debug("Material Icons font not found, skipping merge");
            }

            fonts.build(); // build once after all fonts added
        } catch (Exception e) {
            GE.CORE_LOG.error("Failed to load fonts: {}", e.getMessage());
            defaultFont = fonts.addFontDefault();
            fonts.build();
        }
    }


    /**
     * Gets the default font for the ImGui context.
     * <p>This is the font that was loaded from the JetBrains Mono font resource.
     * If the font could not be loaded, it will be the default ImGui font.
     *
     * @return the default font
     */
    public static ImFont getDefaultFont() {
        return defaultFont;
    }

    /**
     * Draws the ImGui UI.
     * <p>This method is the entry point for drawing the ImGui UI.
     * It takes a render interface as a parameter, which is called once per frame.
     * The render interface is responsible for rendering the UI of the ImGui context.
     * It is passed the ImGui IO context, which can be used to access the ImGui state and handle input/output operations.
     * @param runnable the render interface to call once per frame
     */
    public static void draw(final RenderInterface runnable) {
        // start frame
        imGuiImplGl3.newFrame();
        imGuiImplGlfw.newFrame(); // Handle keyboard and mouse interactions
        ImGui.newFrame();

        // do rendering logic
        runnable.render(ImGui.getIO());

        // end frame
        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long pointer = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();

            GLFW.glfwMakeContextCurrent(pointer);
        }
    }

    /**
     * Disposes of the ImGui context.
     * <p>This method is called when the ImGui context is no longer needed.
     * It disposes of the ImGui context, ImPlot context, and ImNodes context.
     * It also disposes of the ImGuiImplGl3 and ImGuiImplGlfw contexts.
     */
    public static void dispose() {
        imGuiImplGl3.shutdown();


        ImNodes.destroyContext();
        ImPlot.destroyContext();
        ImGui.destroyContext();
    }

    // Can be used to load buffered images in ImGui
    //    public static int fromBufferedImage(BufferedImage image) {
    //        final int[] pixels = new int[image.getWidth() * image.getHeight()];
    //        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
    //
    //        final ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
    //
    //        for (int y = 0; y < image.getHeight(); y++) {
    //            for (int x = 0; x < image.getWidth(); x++) {
    //                final int pixel = pixels[y * image.getWidth() + x];
    //
    //                buffer.put((byte) ((pixel >> 16) & 0xFF));
    //                buffer.put((byte) ((pixel >> 8) & 0xFF));
    //                buffer.put((byte) (pixel & 0xFF));
    //                buffer.put((byte) ((pixel >> 24) & 0xFF));
    //            }
    //        }
    //
    //        buffer.flip();
    //
    //        final int texture = GlStateManager._genTexture();
    //        GlStateManager._bindTexture(texture);
    //
    //        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    //        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    //
    //        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
    //
    //        return texture;
    //    }

    @FunctionalInterface
    public interface RenderInterface {
        void render(final ImGuiIO io);
    }
}