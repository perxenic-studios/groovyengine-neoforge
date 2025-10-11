package io.github.luckymcdev.groovyengine.core.client.editor.windows;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotAxisFlags;
import imgui.extension.implot.flag.ImPlotCol;
import imgui.extension.implot.flag.ImPlotCond;
import imgui.extension.implot.flag.ImPlotFlags;
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

@OnlyIn(Dist.CLIENT)
public class MetricsWindow extends EditorWindow {
    private static final Minecraft mc = Minecraft.getInstance();

    // Circular buffers for graphs
    private final float[] fpsHistory = new float[100];
    private final float[] memoryHistory = new float[100];
    private final float[] frameTimeHistory = new float[100];
    private final float[] entityHistory = new float[100];
    private int historyIndex = 0;

    private final int graphWidth = 600;
    private final int graphHeight = 200;

    // Frame time tracking
    private long lastFrameTime = System.nanoTime();

    // X-axis data for plots
    private final float[] xAxis = new float[100];

    public MetricsWindow() {
        super(ImIcons.GRAPH.get() + " Debug Overlay");

        // Initialize x-axis (time points)
        for (int i = 0; i < xAxis.length; i++) {
            xAxis[i] = i;
        }
    }

    @Override
    public void render(ImGuiIO io) {
        if (mc.player == null || mc.level == null) {
            ImGe.text("Waiting for world...");
            return;
        }

        updateHistory();

        ImGe.window(title, () -> {
            renderPerformanceSection();
            ImGe.separator();
            renderPlayerSection();
            ImGe.separator();
            renderWorldSection();
            ImGe.separator();
            renderGraphsSection();
        });
    }

    private void updateHistory() {
        // Update FPS history
        fpsHistory[historyIndex] = mc.getFps();

        // Update memory usage (MB)
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        memoryHistory[historyIndex] = usedMemory;

        // Update frame time
        long currentTime = System.nanoTime();
        float frameTimeMs = (currentTime - lastFrameTime) / 1_000_000.0f;
        frameTimeHistory[historyIndex] = frameTimeMs;
        lastFrameTime = currentTime;

        // Update entity count
        if (mc.level != null) {
            entityHistory[historyIndex] = mc.level.getEntityCount();
        }

        historyIndex = (historyIndex + 1) % fpsHistory.length;
    }

    private void renderPerformanceSection() {
        ImGe.textColored(0xFF00FF00, ImIcons.SPEED.get() + " Performance");
        ImGe.spacing();

        ImGe.text("FPS: " + mc.getFps());

        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;

        ImGe.text(String.format("Memory: %d / %d / %d MB", usedMemory, totalMemory, maxMemory));
        ImGe.text(String.format("Frame Time: %.2f ms", frameTimeHistory[(historyIndex - 1 + frameTimeHistory.length) % frameTimeHistory.length]));

        ImGe.text("Render Distance: " + mc.options.getEffectiveRenderDistance() + " chunks");

        ImGe.spacing();
    }

    private void renderPlayerSection() {
        ImGe.textColored(0xFF00FFFF, ImIcons.PERSON.get() + " Player");
        ImGe.spacing();

        // Position
        ImGe.text(String.format("XYZ: %.3f / %.3f / %.3f",
                mc.player.getX(), mc.player.getY(), mc.player.getZ()));

        // Block position
        BlockPos blockPos = mc.player.blockPosition();
        ImGe.text(String.format("Block: %d %d %d",
                blockPos.getX(), blockPos.getY(), blockPos.getZ()));

        // Rotation
        ImGe.text(String.format("Rotation: %.1f / %.1f",
                mc.player.getYRot(), mc.player.getXRot()));

        // Facing direction
        Direction facing = mc.player.getDirection();
        ImGe.text("Facing: " + facing.getName().toUpperCase());

        // Motion
        ImGe.text(String.format("Motion: %.3f / %.3f / %.3f",
                mc.player.getDeltaMovement().x,
                mc.player.getDeltaMovement().y,
                mc.player.getDeltaMovement().z));

        // On ground
        ImGe.text("On Ground: " + mc.player.onGround());

        ImGe.spacing();
    }

    private void renderWorldSection() {
        ImGe.textColored(0xFFFFFF00, ImIcons.WORLD.get() + " World");
        ImGe.spacing();

        Level level = mc.level;

        // Dimension info
        ImGe.text("Dimension: " + level.dimension().location());

        // Game time
        ImGe.text("Game Time: " + level.getGameTime() + " ticks");
        ImGe.text("Day Time: " + level.getDayTime() + " ticks");

        // Chunk info
        BlockPos playerPos = mc.player.blockPosition();
        ChunkPos chunkPos = new ChunkPos(playerPos);
        ImGe.text(String.format("Chunk: %d, %d (Section %d)",
                chunkPos.x, chunkPos.z, playerPos.getY() >> 4));

        // Loaded chunks and entities
        ImGe.text("Loaded Chunks: " + level.getChunkSource().getLoadedChunksCount());

        // Biome
        ImGe.text("Biome: " + level.getBiome(playerPos).unwrap().map(
                key -> key.location().toString(),
                biome -> "unknown"
        ));

        // Light level
        ImGe.text("Light Level: " + level.getMaxLocalRawBrightness(playerPos));

        // Weather
        ImGe.text("Raining: " + level.isRaining());
        ImGe.text("Thundering: " + level.isThundering());

        ImGe.spacing();
    }

    private void renderGraphsSection() {
        ImGe.textColored(0xFFFF00FF, ImIcons.POLYLINE.get() + " Graphs");
        ImGe.spacing();

        // Combined plot with multiple lines
        if (ImPlot.beginPlot("Performance Metrics", graphWidth, graphHeight, ImPlotFlags.None)) {
            ImPlot.setupAxesLimits(0, 100, 0, 200, ImPlotCond.Always);

            // FPS (green)
            ImPlot.pushStyleColor(ImPlotCol.Line, 0xFF00FF00);
            ImPlot.plotLine("FPS", xAxis, fpsHistory);
            ImPlot.popStyleColor();

            // Memory (blue) scaled
            float[] scaledMemory = new float[memoryHistory.length];
            for (int i = 0; i < memoryHistory.length; i++) scaledMemory[i] = memoryHistory[i] / 20f;
            ImPlot.pushStyleColor(ImPlotCol.Line, 0xFF0088FF);
            ImPlot.plotLine("Memory/20", xAxis, scaledMemory);
            ImPlot.popStyleColor();

            // Frame time (red)
            float[] scaledFrameTime = new float[frameTimeHistory.length];
            for (int i = 0; i < frameTimeHistory.length; i++) scaledFrameTime[i] = frameTimeHistory[i] * 2f;
            ImPlot.pushStyleColor(ImPlotCol.Line, 0xFFFF0000);
            ImPlot.plotLine("Frame Time*2", xAxis, scaledFrameTime);
            ImPlot.popStyleColor();

            ImPlot.endPlot();
        }


        ImGe.spacing();

        // Individual detailed plots
        if (ImPlot.beginPlot("FPS Detailed", graphWidth, graphHeight, ImPlotFlags.None)) {
            ImPlot.setupAxesLimits(0, 100, 0, 120, ImPlotCond.Always);
            ImPlot.pushStyleColor(ImPlotCol.Line, ImGe.getColorU32(0, 255, 0, 255));
            ImPlot.plotLine("FPS", xAxis, fpsHistory, fpsHistory.length, historyIndex);
            ImPlot.popStyleColor();
            ImPlot.endPlot();
        }


        ImGe.spacing();

        if (ImPlot.beginPlot("Memory Usage", graphWidth, graphHeight, ImPlotFlags.None)) {
            ImPlot.setupAxesLimits(0, 100, 0, 2000, ImPlotCond.Always);
            ImPlot.pushStyleColor(ImPlotCol.Line, ImGui.getColorU32(0, 136, 255, 255));
            ImPlot.plotLine("Memory", xAxis, memoryHistory, memoryHistory.length, historyIndex);
            ImPlot.popStyleColor();
            ImPlot.endPlot();
        }

        ImGe.spacing();

        if (ImPlot.beginPlot("Frame Time", graphWidth, graphHeight, ImPlotFlags.None)) {
            ImPlot.setupAxesLimits(0, 100, 0, 50, ImPlotCond.Always);
            ImPlot.pushStyleColor(ImPlotCol.Line, ImGui.getColorU32(255, 0, 0, 255));
            ImPlot.plotLine("Frame Time", xAxis, frameTimeHistory, frameTimeHistory.length, historyIndex);
            ImPlot.popStyleColor();
            ImPlot.endPlot();
        }

        ImGe.spacing();
    }

    @Override
    public void onOpen() {
        // Initialize history arrays
        for (int i = 0; i < fpsHistory.length; i++) {
            fpsHistory[i] = 60f;
            memoryHistory[i] = 500f;
            frameTimeHistory[i] = 16.67f;
            entityHistory[i] = 0f;
        }
        lastFrameTime = System.nanoTime();
    }
}