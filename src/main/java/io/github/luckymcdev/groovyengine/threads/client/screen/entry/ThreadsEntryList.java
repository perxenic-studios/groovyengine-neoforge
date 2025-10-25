package io.github.luckymcdev.groovyengine.threads.client.screen.entry;

import io.github.luckymcdev.groovyengine.threads.client.screen.ThreadsErrorScreen;
import io.github.luckymcdev.groovyengine.threads.core.scripting.error.ScriptErrors;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThreadsEntryList extends ObjectSelectionList<ThreadsEntry> {
    public ThreadsEntryList(ThreadsErrorScreen parent) {
        super(parent.getMinecraft(), parent.width, parent.height - 40, 35, 20);

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
}