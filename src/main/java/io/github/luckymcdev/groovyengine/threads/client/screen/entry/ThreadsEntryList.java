package io.github.luckymcdev.groovyengine.threads.client.screen.entry;

import io.github.luckymcdev.groovyengine.threads.client.screen.ThreadsErrorScreen;
import io.github.luckymcdev.groovyengine.threads.core.scripting.error.ScriptErrors;
import net.minecraft.client.gui.components.ObjectSelectionList;

public class ThreadsEntryList extends ObjectSelectionList<ThreadsEntry> {
    public ThreadsEntryList(ThreadsErrorScreen parent) {
        super(parent.getMinecraft(), parent.width, parent.height - 40, 35, 20);

        for (ScriptErrors.ErrorEntry error : ScriptErrors.getErrors()) {
            this.addEntry(new ThreadsEntry(error));
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getRight() - 6;
    }

    @Override
    public int getRowWidth() {
        return this.width - 15;
    }
}