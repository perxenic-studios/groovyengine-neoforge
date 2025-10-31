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

package io.github.luckymcdev.groovyengine.threads.client.screen.entry;

import io.github.luckymcdev.groovyengine.threads.core.scripting.error.ScriptErrors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ThreadsEntry extends ObjectSelectionList.Entry<ThreadsEntry> {
    private final ScriptErrors.ErrorEntry error;
    private final List<FormattedCharSequence> tooltipLines = new ArrayList<>();

    public ThreadsEntry(ScriptErrors.ErrorEntry error) {
        this.error = error;
        prepareTooltip();
    }

    private void prepareTooltip() {
        Font font = Minecraft.getInstance().font;
        int maxTooltipWidth = 700; // Reasonable max width for tooltips

        // Script name
        addWrappedText(font, "§cScript: " + error.scriptName(), maxTooltipWidth);
        addWrappedText(font, "", maxTooltipWidth);

        // Error message
        addWrappedText(font, "§6Message:", maxTooltipWidth);
        String message = error.exception().getMessage();
        if (message != null && !message.isEmpty()) {
            addWrappedText(font, "§e" + message, maxTooltipWidth);
        } else {
            addWrappedText(font, "§eNo message provided", maxTooltipWidth);
        }
        addWrappedText(font, "", maxTooltipWidth);

        // Error type
        addWrappedText(font, "§6Exception Type:", maxTooltipWidth);
        addWrappedText(font, "§e" + error.exception().getClass().getSimpleName(), maxTooltipWidth);
        addWrappedText(font, "", maxTooltipWidth);

        // Human-readable description
        String description = ScriptErrors.generateErrorDescription(error.exception());
        addWrappedText(font, "§6Description:", maxTooltipWidth);
        addWrappedText(font, "§a" + description, maxTooltipWidth);
        addWrappedText(font, "", maxTooltipWidth);

        // Stack trace (first few lines)
        addWrappedText(font, "§6Stack Trace:", maxTooltipWidth);
        String stackTrace = getStackTrace(error.exception());
        String[] stackLines = stackTrace.split("\n");
        for (int i = 0; i < Math.min(6, stackLines.length); i++) {
            addWrappedText(font, "§7" + stackLines[i].trim(), maxTooltipWidth);
        }
        if (stackLines.length > 6) {
            addWrappedText(font, "§7... and " + (stackLines.length - 6) + " more lines", maxTooltipWidth);
        }
    }

    private void addWrappedText(Font font, String text, int maxWidth) {
        if (text.isEmpty()) {
            // Add empty line
            tooltipLines.add(FormattedCharSequence.EMPTY);
        } else {
            // Split the text into multiple lines if needed
            List<FormattedCharSequence> lines = font.split(Component.literal(text), maxWidth);
            tooltipLines.addAll(lines);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int index, int y, int x, int width, int height,
                       int mouseX, int mouseY, boolean hovered, float partialTick) {
        Font font = Minecraft.getInstance().font;
        int maxWidth = width - 10;

        // Script name
        String scriptText = "Script: " + error.scriptName();
        if (font.width(scriptText) > maxWidth) {
            scriptText = font.plainSubstrByWidth(scriptText, maxWidth - font.width("...")) + "...";
        }
        guiGraphics.drawString(font, scriptText, x + 5, y + 2, 0xFFFF5555, false);

        // Error message preview
        String message = error.exception().getMessage();
        if (message != null && !message.isEmpty()) {
            String previewText = font.plainSubstrByWidth(message, maxWidth - font.width("...")) + "...";
            guiGraphics.drawString(font, previewText, x + 5, y + 12, 0xFFFFFFAA, false);
        } else {
            guiGraphics.drawString(font, "No error message", x + 5, y + 12, 0xFFAAAAAA, false);
        }

        // Render hover effect
        if (hovered) {
            guiGraphics.fill(x, y, x + width, y + height, 0x20FFFFFF);
        }
    }

    /**
     * Render the tooltip when hovered
     */
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!tooltipLines.isEmpty()) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltipLines, mouseX, mouseY);
        }
    }

    private String getStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    @Override
    public Component getNarration() {
        return Component.literal("Error in script " + error.scriptName());
    }
}