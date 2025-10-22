package io.github.luckymcdev.groovyengine.core.client.editor.windows;

import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.*;
import imgui.flag.ImGuiCol;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.ImGe;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

@OnlyIn(Dist.CLIENT)
public class MetricsWindow extends EditorWindow {
    private static final Minecraft mc = Minecraft.getInstance();

    // Circular buffers for graphs
    private static final int HISTORY_SIZE = 120;
    private final double[] fpsHistory = new double[HISTORY_SIZE];
    private final double[] memoryHistory = new double[HISTORY_SIZE];
    private final double[] frameTimeHistory = new double[HISTORY_SIZE];
    private final double[] entityHistory = new double[HISTORY_SIZE];
    private int historyIndex = 0;
    private int sampleCount = 0;

    // Graph settings
    private final boolean autoScaleFPS = false;
    private final boolean autoScaleMemory = false;
    private final boolean showCombinedGraph = true;

    // Frame time tracking
    private long lastFrameTime = System.nanoTime();
    private double avgFps = 60.0;
    private double avgFrameTime = 16.67;
    private double maxMemory = 0;

    public MetricsWindow() {
        super(ImIcons.GRAPH.get() + " Minecraft Metrics");
    }

    @Override
    public void render(ImGuiIO io) {
        if (mc.player == null || mc.level == null) {
            ImGe.window(title, () -> {
                ImGe.textColored(0xFFFF8800, ImIcons.WARNING.get() + " Waiting for world to load...");
            });
            return;
        }

        updateHistory();

        ImGe.window(title, () -> {
            // Tab bar for organized sections
            if (ImGe.beginTabBar("MetricsTabBar")) {
                if (ImGe.beginTabItem(ImIcons.SPEED.get() + " Performance")) {
                    renderPerformanceTab();
                    ImGe.endTabItem();
                }

                if (ImGe.beginTabItem(ImIcons.PERSON.get() + " Player")) {
                    renderPlayerTab();
                    ImGe.endTabItem();
                }

                if (ImGe.beginTabItem(ImIcons.WORLD.get() + " World")) {
                    renderWorldTab();
                    ImGe.endTabItem();
                }

                if (ImGe.beginTabItem(ImIcons.POLYLINE.get() + " Graphs")) {
                    renderGraphsTab();
                    ImGe.endTabItem();
                }

                ImGe.endTabBar();
            }
        });
    }

    private void updateHistory() {
        // Update FPS history
        int currentFps = mc.getFps();
        fpsHistory[historyIndex] = currentFps;

        // Calculate moving average for FPS
        avgFps = 0;
        for (double fps : fpsHistory) avgFps += fps;
        avgFps /= HISTORY_SIZE;

        // Update memory usage (MB)
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        memoryHistory[historyIndex] = usedMemory;
        maxMemory = Math.max(maxMemory, usedMemory);

        // Update frame time
        long currentTime = System.nanoTime();
        double frameTimeMs = (currentTime - lastFrameTime) / 1_000_000.0;
        frameTimeHistory[historyIndex] = frameTimeMs;
        lastFrameTime = currentTime;

        // Calculate moving average for frame time
        avgFrameTime = 0;
        for (double ft : frameTimeHistory) avgFrameTime += ft;
        avgFrameTime /= HISTORY_SIZE;

        // Update entity count
        if (mc.level != null) {
            entityHistory[historyIndex] = mc.level.getEntityCount();
        }

        historyIndex = (historyIndex + 1) % HISTORY_SIZE;
        sampleCount = Math.min(sampleCount + 1, HISTORY_SIZE);
    }

    private void renderPerformanceTab() {
        ImGe.spacing();

        // FPS Section
        ImGe.pushStyleColor(ImGuiCol.Text, 0xFF00FF00);
        ImGe.text(ImIcons.SPEED.get() + " Frame Rate");
        ImGe.popStyleColor();
        ImGe.indent();

        ImGe.text(String.format("Current FPS: %d", mc.getFps()));
        ImGe.text(String.format("Average FPS: %.1f", avgFps));
        ImGe.text(String.format("Frame Time: %.2f ms", frameTimeHistory[(historyIndex - 1 + HISTORY_SIZE) % HISTORY_SIZE]));
        ImGe.text(String.format("Avg Frame Time: %.2f ms", avgFrameTime));

        ImGe.unindent();
        ImGe.spacing();
        ImGe.separator();
        ImGe.spacing();

        // Memory Section
        Runtime runtime = Runtime.getRuntime();
        long maxMem = runtime.maxMemory() / (1024 * 1024);
        long totalMem = runtime.totalMemory() / (1024 * 1024);
        long freeMem = runtime.freeMemory() / (1024 * 1024);
        long usedMem = totalMem - freeMem;
        float memPercent = (float) usedMem / maxMem;

        ImGe.pushStyleColor(ImGuiCol.Text, 0xFF00AAFF);
        ImGe.text(ImIcons.MEMORY.get() + " Memory");
        ImGe.popStyleColor();
        ImGe.indent();

        ImGe.text(String.format("Used: %d MB", usedMem));
        ImGe.text(String.format("Allocated: %d MB", totalMem));
        ImGe.text(String.format("Maximum: %d MB", maxMem));

        // Memory bar
        ImGe.progressBar(memPercent, 300, 20, String.format("%.1f%%", memPercent * 100));

        ImGe.unindent();
        ImGe.spacing();
        ImGe.separator();
        ImGe.spacing();

        // Render Info
        ImGe.pushStyleColor(ImGuiCol.Text, 0xFFFFAA00);
        ImGe.text(ImIcons.CAMERA.get() + " Rendering");
        ImGe.popStyleColor();
        ImGe.indent();

        ImGe.text("Render Distance: " + mc.options.getEffectiveRenderDistance() + " chunks");

        if (mc.level != null) {
            ImGe.text("Entities: " + mc.level.getEntityCount());
            ImGe.text("Loaded Chunks: " + mc.level.getChunkSource().getLoadedChunksCount());
        }

        ImGe.unindent();

        ImGe.spacing();
        ImGe.textColored(0xFF00FFAA, ImIcons.CAMERA.get() + " GPU Information");
        ImGe.spacing();

        ImGe.indent();

        String vendor = GL11.glGetString(GL11.GL_VENDOR);
        String renderer = GL11.glGetString(GL11.GL_RENDERER);
        String version = GL11.glGetString(GL11.GL_VERSION);
        String shadingLang = GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);

        ImGe.text("Vendor: " + (vendor != null ? vendor : "Unknown"));
        ImGe.text("Renderer: " + (renderer != null ? renderer : "Unknown"));
        ImGe.text("OpenGL Version: " + (version != null ? version : "Unknown"));
        ImGe.text("GLSL Version: " + (shadingLang != null ? shadingLang : "Unknown"));
        ImGe.spacing();

        // Optional: memory info (approximate if available)
        if (GL.getCapabilities().GL_NVX_gpu_memory_info) {
            int totalMemKB = GL11.glGetInteger(0x9048); // GL_GPU_MEMORY_INFO_TOTAL_AVAILABLE_MEMORY_NVX
            int freeMemKB = GL11.glGetInteger(0x9049);  // GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX
            ImGe.text(String.format("VRAM: %.1f / %.1f MB", (totalMemKB - freeMemKB) / 1024f, totalMemKB / 1024f));
        } else if (GL.getCapabilities().GL_ATI_meminfo) {
            int[] vboStats = new int[4];
            GL11.glGetIntegerv(0x87FB, vboStats); // GL_VBO_FREE_MEMORY_ATI
            ImGe.text("VRAM Free (ATI): " + (vboStats[0] / 1024) + " MB");
        }

        ImGe.unindent();
        ImGe.spacing();
    }

    private void renderPlayerTab() {
        ImGe.spacing();

        // Position Section
        ImGe.pushStyleColor(ImGuiCol.Text, 0xFF00FFFF);
        ImGe.text(ImIcons.LOCATION.get() + " Position");
        ImGe.popStyleColor();
        ImGe.indent();

        ImGe.text(String.format("X: %.3f", mc.player.getX()));
        ImGe.text(String.format("Y: %.3f", mc.player.getY()));
        ImGe.text(String.format("Z: %.3f", mc.player.getZ()));

        BlockPos blockPos = mc.player.blockPosition();
        ImGe.text(String.format("Block: %d, %d, %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));

        ImGe.unindent();
        ImGe.spacing();
        ImGe.separator();
        ImGe.spacing();

        // Rotation Section
        ImGe.pushStyleColor(ImGuiCol.Text, 0xFFFF00FF);
        ImGe.text(ImIcons.ROTATE.get() + " Rotation");
        ImGe.popStyleColor();
        ImGe.indent();

        ImGe.text(String.format("Yaw: %.1f°", mc.player.getYRot()));
        ImGe.text(String.format("Pitch: %.1f°", mc.player.getXRot()));

        Direction facing = mc.player.getDirection();
        ImGe.text("Facing: " + facing.getName().toUpperCase() + " (" + facing.getAxis() + ")");

        ImGe.unindent();
        ImGe.spacing();
        ImGe.separator();
        ImGe.spacing();

        // Motion Section
        ImGe.pushStyleColor(ImGuiCol.Text, 0xFFFFFF00);
        ImGe.text(ImIcons.ARROW_RIGHT.get() + " Motion");
        ImGe.popStyleColor();
        ImGe.indent();

        ImGe.text(String.format("Velocity X: %.4f", mc.player.getDeltaMovement().x));
        ImGe.text(String.format("Velocity Y: %.4f", mc.player.getDeltaMovement().y));
        ImGe.text(String.format("Velocity Z: %.4f", mc.player.getDeltaMovement().z));

        double speed = mc.player.getDeltaMovement().horizontalDistance();
        ImGe.text(String.format("Horizontal Speed: %.4f m/s", speed * 20)); // Convert to m/s

        ImGe.text("On Ground: " + (mc.player.onGround() ? "Yes" : "No"));
        ImGe.text("In Water: " + (mc.player.isInWater() ? "Yes" : "No"));
        ImGe.text("In Lava: " + (mc.player.isInLava() ? "Yes" : "No"));

        ImGe.unindent();
        ImGe.spacing();
    }

    private void renderWorldTab() {
        ImGe.spacing();

        Level level = mc.level;

        // Dimension Section
        ImGe.pushStyleColor(ImGuiCol.Text, 0xFFAA00FF);
        ImGe.text(ImIcons.WORLD.get() + " Dimension");
        ImGe.popStyleColor();
        ImGe.indent();

        ImGe.text("Dimension: " + level.dimension().location());

        ImGe.unindent();
        ImGe.spacing();
        ImGe.separator();
        ImGe.spacing();

        // Time Section
        ImGe.pushStyleColor(ImGuiCol.Text, 0xFFFFAA00);
        ImGe.text(ImIcons.TIMELAPSE.get() + " Time");
        ImGe.popStyleColor();
        ImGe.indent();

        long gameTime = level.getGameTime();
        long dayTime = level.getDayTime() % 24000;
        int day = (int) (level.getDayTime() / 24000);

        ImGe.text(String.format("Game Time: %d ticks", gameTime));
        ImGe.text(String.format("Day: %d", day));
        ImGe.text(String.format("Time of Day: %d (%02d:%02d)", dayTime, (dayTime / 1000 + 6) % 24, (dayTime % 1000) * 60 / 1000));

        ImGe.unindent();
        ImGe.spacing();
        ImGe.separator();
        ImGe.spacing();

        // Chunk Section
        BlockPos playerPos = mc.player.blockPosition();
        ChunkPos chunkPos = new ChunkPos(playerPos);

        ImGe.pushStyleColor(ImGuiCol.Text, 0xFF00FFAA);
        ImGe.text(ImIcons.SQUARE.get() + " Chunks");
        ImGe.popStyleColor();
        ImGe.indent();

        ImGe.text(String.format("Chunk: %d, %d", chunkPos.x, chunkPos.z));
        ImGe.text(String.format("Section: %d", playerPos.getY() >> 4));
        ImGe.text(String.format("In Chunk: %d, %d, %d", playerPos.getX() & 15, playerPos.getY() & 15, playerPos.getZ() & 15));
        ImGe.text("Loaded Chunks: " + level.getChunkSource().getLoadedChunksCount());

        ImGe.unindent();
        ImGe.spacing();
        ImGe.separator();
        ImGe.spacing();

        // Environment Section
        ImGe.pushStyleColor(ImGuiCol.Text, 0xFF88FF88);
        ImGe.text(ImIcons.WEATHER.get() + " Environment");
        ImGe.popStyleColor();
        ImGe.indent();

        String biomeName = level.getBiome(playerPos).unwrap().map(
                key -> key.location().toString(),
                biome -> "unknown"
        );
        ImGe.text("Biome: " + biomeName);
        ImGe.text("Light Level: " + level.getMaxLocalRawBrightness(playerPos));
        ImGe.text("Sky Light: " + level.getBrightness(net.minecraft.world.level.LightLayer.SKY, playerPos));
        ImGe.text("Block Light: " + level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, playerPos));
        ImGe.text("Rain: " + (level.isRaining() ? "Yes" : "No"));
        ImGe.text("Thunder: " + (level.isThundering() ? "Yes" : "No"));

        ImGe.unindent();
        ImGe.spacing();
    }

    private void renderGraphsTab() {
        ImGe.spacing();

        // Graph options
        ImGe.checkbox("Auto-scale FPS", autoScaleFPS);
        ImGe.sameLine();
        ImGe.checkbox("Auto-scale Memory", autoScaleMemory);

        ImGe.spacing();
        ImGe.separator();
        ImGe.spacing();

        // Combined Performance Graph
        if (ImPlot.beginPlot("##CombinedPerformance", -1, 250, ImPlotFlags.None)) {
            ImPlot.setupAxis(ImPlotAxis.X1, "Time (samples)", ImPlotAxisFlags.None);
            ImPlot.setupAxis(ImPlotAxis.Y1, "", ImPlotAxisFlags.None);

            ImPlot.setupAxisLimits(ImPlotAxis.X1, 0, HISTORY_SIZE, ImPlotCond.Always);
            if (!autoScaleFPS) {
                ImPlot.setupAxisLimits(ImPlotAxis.Y1, 0, 120, ImPlotCond.Always);
            }

            // FPS Line (Green)
            ImPlot.pushStyleColor(ImPlotCol.Line, ImGe.getColorU32(0, 255, 0, 255));
            ImPlot.plotLine("FPS", fpsHistory, sampleCount, 1, 0, historyIndex);
            ImPlot.popStyleColor();

            ImPlot.endPlot();
        }

        ImGe.spacing();

        // Frame Time Graph
        if (ImPlot.beginPlot("##FrameTime", -1, 200, ImPlotFlags.None)) {
            ImPlot.setupAxis(ImPlotAxis.X1, "Time (samples)", ImPlotAxisFlags.None);
            ImPlot.setupAxis(ImPlotAxis.Y1, "ms", ImPlotAxisFlags.None);

            ImPlot.setupAxisLimits(ImPlotAxis.X1, 0, HISTORY_SIZE, ImPlotCond.Always);
            ImPlot.setupAxisLimits(ImPlotAxis.Y1, 0, 50, ImPlotCond.Once);

            // Frame time (Red)
            ImPlot.pushStyleColor(ImPlotCol.Line, ImGe.getColorU32(255, 100, 100, 255));
            ImPlot.plotLine("Frame Time", frameTimeHistory, sampleCount, 1, 0, historyIndex);
            ImPlot.popStyleColor();

            // 16.67ms target line (60 FPS)
            double[] targetLine = {16.67, 16.67};
            double[] targetX = {0, HISTORY_SIZE};
            ImPlot.pushStyleColor(ImPlotCol.Line, ImGe.getColorU32(100, 100, 100, 255));
            ImPlot.plotLine("60 FPS Target", targetX, targetLine, 2);
            ImPlot.popStyleColor();

            ImPlot.endPlot();
        }

        ImGe.spacing();

        // Memory Graph
        if (ImPlot.beginPlot("##Memory", -1, 200, ImPlotFlags.None)) {
            ImPlot.setupAxis(ImPlotAxis.X1, "Time (samples)", ImPlotAxisFlags.None);
            ImPlot.setupAxis(ImPlotAxis.Y1, "MB", ImPlotAxisFlags.None);

            ImPlot.setupAxisLimits(ImPlotAxis.X1, 0, HISTORY_SIZE, ImPlotCond.Always);
            if (!autoScaleMemory) {
                ImPlot.setupAxisLimits(ImPlotAxis.Y1, 0, maxMemory * 1.2, ImPlotCond.Always);
            }

            // Memory usage (Blue)
            ImPlot.pushStyleColor(ImPlotCol.Line, ImGe.getColorU32(100, 150, 255, 255));
            ImPlot.plotLine("Memory Usage", memoryHistory, sampleCount, 1, 0, historyIndex);
            ImPlot.popStyleColor();

            ImPlot.endPlot();
        }

        ImGe.spacing();

        // Entity Count Graph
        if (ImPlot.beginPlot("##Entities", -1, 200, ImPlotFlags.None)) {
            ImPlot.setupAxis(ImPlotAxis.X1, "Time (samples)", ImPlotAxisFlags.None);
            ImPlot.setupAxis(ImPlotAxis.Y1, "Count", ImPlotAxisFlags.None);

            ImPlot.setupAxisLimits(ImPlotAxis.X1, 0, HISTORY_SIZE, ImPlotCond.Always);

            // Entity count (Yellow)
            ImPlot.pushStyleColor(ImPlotCol.Line, ImGe.getColorU32(255, 200, 0, 255));
            ImPlot.plotLine("Entities", entityHistory, sampleCount, 1, 0, historyIndex);
            ImPlot.popStyleColor();

            ImPlot.endPlot();
        }

        ImGe.spacing();
    }

    @Override
    public void onOpen() {
        // Initialize history arrays
        for (int i = 0; i < HISTORY_SIZE; i++) {
            fpsHistory[i] = 60.0;
            memoryHistory[i] = 500.0;
            frameTimeHistory[i] = 16.67;
            entityHistory[i] = 0.0;
        }
        historyIndex = 0;
        sampleCount = 0;
        lastFrameTime = System.nanoTime();
    }
}