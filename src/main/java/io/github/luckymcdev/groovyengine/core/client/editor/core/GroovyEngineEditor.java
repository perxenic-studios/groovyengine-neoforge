package io.github.luckymcdev.groovyengine.core.client.editor.core;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GroovyEngineEditor {

    private static EditorState editorState = EditorState.DISABLED;

    public static void render() {

    }

    public static EditorState getEditorState() {
        return editorState;
    }

    public static void setEditorState(EditorState editorState) {
        GroovyEngineEditor.editorState = editorState;
    }
}
