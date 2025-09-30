package io.github.luckymcdev.groovyengine.threads.client.editor;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import io.github.luckymcdev.groovyengine.core.client.imgui.util.ImUtil;
import io.github.luckymcdev.groovyengine.threads.core.logging.InMemoryLogAppender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ThreadsWindow extends EditorWindow {

    private final ImString commandBuffer;
    private boolean autoScroll;
    private float scrollY;
    private float scrollMaxY;
    private int previousLogCount;

    public ThreadsWindow() {
        super(ImIcons.CODE.get() + " Script Console");
        this.commandBuffer = new ImString();
        this.autoScroll = true;
        this.previousLogCount = 0;
    }

    @Override
    public void render(ImGuiIO io) {
        ImUtil.window(ImIcons.CODE.get() + " Script Console", () -> {
            renderLogsSection();
            ImGui.separator();
            renderCommandSection();
        });
    }

    private void renderLogsSection() {
        // Header with controls
        ImGui.beginGroup();
        ImGui.sameLine();
        if (ImGui.button("Copy")) {
            copyLogsToClipboard();
        }
        ImGui.sameLine();
        ImGui.checkbox("Auto-scroll", autoScroll);
        ImGui.sameLine();
        ImGui.textDisabled("Lines: " + InMemoryLogAppender.getLogLines().size());
        ImGui.endGroup();

        ImGui.spacing();

        // Logs display with styled background
        ImGui.pushStyleVar(ImGuiStyleVar.ChildRounding, 3.0f);
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0xFF1E1E1E);

        if (ImGui.beginChild("LogsScrollArea", 0, -ImGui.getFrameHeightWithSpacing() * 1.5f, true, ImGuiWindowFlags.HorizontalScrollbar)) {
            List<String> logLines = InMemoryLogAppender.getLogLines();

            // Use text wrapped for better readability
            ImGui.pushTextWrapPos(0);
            for (String logLine : logLines) {
                // Apply different colors based on log level
                if (logLine.contains("[ERROR]") || logLine.toLowerCase().contains("error")) {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xFFFF5555);
                } else if (logLine.contains("[WARN]") || logLine.toLowerCase().contains("warn")) {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xFFFFFF55);
                } else if (logLine.contains("[DEBUG]") || logLine.toLowerCase().contains("debug")) {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xFF55FFFF);
                } else if (logLine.contains("[INFO]") || logLine.startsWith("> ")) {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xFF55FF55);
                } else {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xFFCCCCCC);
                }

                ImGui.textUnformatted(logLine);
                ImGui.popStyleColor();
            }
            ImGui.popTextWrapPos();

            // Auto-scroll logic
            handleAutoScroll(logLines);
        }
        ImGui.endChild();
        ImGui.popStyleColor();
        ImGui.popStyleVar();
    }

    private void handleAutoScroll(List<String> logLines) {
        int currentLogCount = logLines.size();

        // Check if new logs were added
        if (currentLogCount > previousLogCount) {
            previousLogCount = currentLogCount;

            // Only auto-scroll if we're already near the bottom or auto-scroll is enabled
            scrollY = ImGui.getScrollY();
            scrollMaxY = ImGui.getScrollMaxY();

            if (autoScroll || scrollY >= scrollMaxY - 50.0f) {
                ImGui.setScrollHereY(1.0f);
                autoScroll = true; // Re-enable auto-scroll if user was at bottom
            }
        }

        // If user manually scrolls up, disable auto-scroll
        if (ImGui.isMouseDragging(0) && ImGui.isWindowHovered()) {
            scrollY = ImGui.getScrollY();
            scrollMaxY = ImGui.getScrollMaxY();
            if (scrollY < scrollMaxY - 100.0f) {
                autoScroll = false;
            }
        }
    }

    private void renderCommandSection() {
        ImGui.text("Command:");
        ImGui.sameLine();

        // Command input with execute button
        ImGui.pushItemWidth(ImGui.getContentRegionAvailX() - 80.0f);
        boolean execute = ImGui.inputText("##CommandInput", commandBuffer,
                ImGuiInputTextFlags.EnterReturnsTrue | ImGuiInputTextFlags.CallbackResize);
        ImGui.popItemWidth();

        ImGui.sameLine();

        if (ImGui.button("Execute") || execute) {
            executeCommand(commandBuffer.toString());
            commandBuffer.clear();
            ImGui.setKeyboardFocusHere(-1);
        }

        // Command history hint
        if (commandBuffer.getLength() == 0) {
            ImGui.sameLine();
            ImGui.textDisabled("Type a command and press Enter...");
        }
    }

    private void executeCommand(String command) {
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null;
        player.connection.sendCommand(command);
        autoScroll = true;
    }

    private void copyLogsToClipboard() {
        StringBuilder logs = new StringBuilder();
        List<String> logLines = InMemoryLogAppender.getLogLines();

        for (String line : logLines) {
            logs.append(line).append("\n");
        }

        ImGui.setClipboardText(logs.toString());
    }
}