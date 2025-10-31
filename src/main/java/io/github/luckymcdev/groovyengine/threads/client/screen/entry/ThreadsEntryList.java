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

import io.github.luckymcdev.groovyengine.threads.client.screen.ThreadsErrorScreen;
import io.github.luckymcdev.groovyengine.threads.core.scripting.error.ScriptErrors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThreadsEntryList extends ObjectSelectionList<ThreadsEntry> {
    private final ThreadsErrorScreen parent;

    public ThreadsEntryList(ThreadsErrorScreen parent) {
        super(parent.getMinecraft(), parent.width, parent.height - 40, 35, 20);
        this.parent = parent;

        for (ScriptErrors.ErrorEntry error : ScriptErrors.getErrors()) {
            this.addEntry(new ThreadsEntry(error));
        }
    }

    /**
     * Retrieves the position of the scrollbar.
     * By default, the scrollbar is positioned on the right of the list
     * and 6 pixels from the edge of the list.
     *
     * @return The position of the scrollbar.
     */
    @Override
    protected int getScrollbarPosition() {
        return this.getRight() - 6;
    }

    /**
     * Gets the width of a row in this list.
     * The width of a row is the width of the list minus 15 pixels.
     *
     * @return The width of a row in this list.
     */
    @Override
    public int getRowWidth() {
        return this.width - 15;
    }

    /**
     * Render tooltip after the main render
     */
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        ThreadsEntry hoveredEntry = this.getEntryAtPosition(mouseX, mouseY);
        if (hoveredEntry != null) {
            hoveredEntry.renderTooltip(guiGraphics, mouseX, mouseY);
        }
    }
}