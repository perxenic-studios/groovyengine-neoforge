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


    public static ImFont getDefaultFont() {
        return defaultFont;
    }

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