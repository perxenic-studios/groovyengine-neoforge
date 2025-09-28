package io.github.luckymcdev.groovyengine.core.client.editor.core;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum EditorState {
    /**
     *  Enables ImGui rendering.
     */
    ENABLED,
    /**
     * Disables ImGui rendering.
     */
    DISABLED
}
