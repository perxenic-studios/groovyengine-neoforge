package io.github.luckymcdev.groovyengine.threads.client.editor;

import imgui.ImGuiIO;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.ImGe;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
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

    /**
     * Renders the Script Console window.
     * This method is called every frame to update the UI and handle user interactions.
     *
     * @param io the ImGui IO context for handling input/output operations
     * @see ImGuiIO
     */
    @Override
    public void render(ImGuiIO io) {
        ImGe.window(ImIcons.CODE.get() + " Script Console", () -> {
            renderLogsSection();
            ImGe.separator();
            renderCommandSection();
        });
    }

    /**
     * Renders the Logs section of the Script Console window.
     * This section contains a header with controls (copy, auto-scroll) and a scrollable area
     * displaying the current logs. The logs are styled with different colors based on their log level.
     *
     * @see InMemoryLogAppender#getLogLines() for the list of log lines
     */
    private void renderLogsSection() {
        // Header with controls
        ImGe.beginGroup();
        ImGe.sameLine();
        if (ImGe.button("Copy")) {
            copyLogsToClipboard();
        }
        ImGe.sameLine();
        ImGe.checkbox("Auto-scroll", autoScroll);
        ImGe.sameLine();
        ImGe.textDisabled("Lines: " + InMemoryLogAppender.getLogLines().size());
        ImGe.endGroup();

        ImGe.spacing();

        // Logs display with styled background
        ImGe.pushStyleVar(ImGuiStyleVar.ChildRounding, 3.0f);
        ImGe.pushStyleColor(ImGuiCol.ChildBg, 0xFF1E1E1E);

        if (ImGe.beginChild("LogsScrollArea", 0, -ImGe.getFrameHeightWithSpacing() * 1.5f, true, ImGuiWindowFlags.HorizontalScrollbar)) {
            List<String> logLines = InMemoryLogAppender.getLogLines();

            // Use text wrapped for better readability
            ImGe.pushTextWrapPos(0);
            for (String logLine : logLines) {
                // Apply different colors based on log level
                if (logLine.contains("[ERROR]") || logLine.toLowerCase().contains("error")) {
                    ImGe.pushStyleColor(ImGuiCol.Text, 0xFFFF5555);
                } else if (logLine.contains("[WARN]") || logLine.toLowerCase().contains("warn")) {
                    ImGe.pushStyleColor(ImGuiCol.Text, 0xFFFFFF55);
                } else if (logLine.contains("[DEBUG]") || logLine.toLowerCase().contains("debug")) {
                    ImGe.pushStyleColor(ImGuiCol.Text, 0xFF55FFFF);
                } else if (logLine.contains("[INFO]") || logLine.startsWith("> ")) {
                    ImGe.pushStyleColor(ImGuiCol.Text, 0xFF55FF55);
                } else {
                    ImGe.pushStyleColor(ImGuiCol.Text, 0xFFCCCCCC);
                }

                ImGe.textUnformatted(logLine);
                ImGe.popStyleColor();
            }
            ImGe.popTextWrapPos();

            // Auto-scroll logic
            handleAutoScroll(logLines);
        }
        ImGe.endChild();
        ImGe.popStyleColor();
        ImGe.popStyleVar();
    }

    /**
     * Handles auto-scrolling for the logs section.
     * This method checks if new logs were added and if the user is near the bottom of the logs section.
     * If either condition is true, it will auto-scroll the logs section to the bottom.
     * If the user manually scrolls up, it will disable auto-scroll until new logs are added.
     *
     * @param logLines the list of log lines to check for new additions
     */
    private void handleAutoScroll(List<String> logLines) {
        int currentLogCount = logLines.size();

        // Check if new logs were added
        if (currentLogCount > previousLogCount) {
            previousLogCount = currentLogCount;

            // Only auto-scroll if we're already near the bottom or auto-scroll is enabled
            scrollY = ImGe.getScrollY();
            scrollMaxY = ImGe.getScrollMaxY();

            if (autoScroll || scrollY >= scrollMaxY - 50.0f) {
                ImGe.setScrollHereY(1.0f);
                autoScroll = true; // Re-enable auto-scroll if user was at bottom
            }
        }

        // If user manually scrolls up, disable auto-scroll
        if (ImGe.isMouseDragging(0) && ImGe.isWindowHovered()) {
            scrollY = ImGe.getScrollY();
            scrollMaxY = ImGe.getScrollMaxY();
            if (scrollY < scrollMaxY - 100.0f) {
                autoScroll = false;
            }
        }
    }

    /**
     * Renders the Command section of the Script Console window.
     * This section contains a header with a command input field and an execute button.
     * When the execute button is clicked or the user presses Enter, it will execute the command
     * in the command input field and clear the input field.
     */
    private void renderCommandSection() {
        ImGe.text("Command:");
        ImGe.sameLine();

        // Command input with execute button
        ImGe.pushItemWidth(ImGe.getContentRegionAvailX() - 80.0f);
        boolean execute = ImGe.inputText("##CommandInput", commandBuffer,
                ImGuiInputTextFlags.EnterReturnsTrue | ImGuiInputTextFlags.CallbackResize);
        ImGe.popItemWidth();

        ImGe.sameLine();

        if (ImGe.button("Execute") || execute) {
            executeCommand(commandBuffer.toString());
            commandBuffer.clear();
            ImGe.setKeyboardFocusHere(-1);
        }
    }

    /**
     * Executes a command in the game by sending it to the server.
     * <p>
     * This method is used by the Script Console window to execute commands entered
     * by the user. It will also enable auto-scrolling for the logs section.
     *
     * @param command the command to be executed
     */
    private void executeCommand(String command) {
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null;
        player.connection.sendCommand(command);
        autoScroll = true;
    }

    /**
     * Copies the current logs in the Script Console window to the system clipboard.
     * This method is used by the Script Console window to copy the logs to the clipboard
     * when the user clicks the "Copy" button.
     */
    private void copyLogsToClipboard() {
        StringBuilder logs = new StringBuilder();
        List<String> logLines = InMemoryLogAppender.getLogLines();

        for (String line : logLines) {
            logs.append(line).append("\n");
        }

        ImGe.setClipboardText(logs.toString());
    }
}