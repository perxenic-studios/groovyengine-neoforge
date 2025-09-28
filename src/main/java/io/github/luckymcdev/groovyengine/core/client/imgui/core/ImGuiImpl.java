package io.github.luckymcdev.groovyengine.core.client.imgui.core;

import imgui.*;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import io.github.luckymcdev.groovyengine.core.client.imgui.styles.ImGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class ImGuiImpl {
    private final static ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    private final static ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();

    public static void create(final long handle) {
        ImGui.createContext();
        ImPlot.createContext();

        final ImGuiIO data = ImGui.getIO();
        data.setIniFilename("groovyengine.ini");
        data.setFontGlobalScale(1F);

        // The "generatedFonts" list now contains an ImFont for each scale from 5 to 50, you should save the font scales you want as global fields here to use them later:
        // For example:
        // defaultFont = generatedFonts.get(30); // Font scale is 30
        // How you can apply the font then, you can see in ExampleMixin

        ImGraphics.INSTANCE.pushRootStack();

        data.setConfigFlags(ImGuiConfigFlags.DockingEnable | ImGuiConfigFlags.ViewportsEnable);

        // In case you want to enable Viewports on Windows, you have to do this instead of the above line:
        // data.setConfigFlags(ImGuiConfigFlags.DockingEnable | ImGuiConfigFlags.ViewportsEnable);

        imGuiImplGlfw.init(handle, true);
        imGuiImplGl3.init();
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

        ImGui.destroyContext();
        ImPlot.destroyContext();
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
    public static interface RenderInterface {

        void render(final ImGuiIO io);

    }
}