package io.github.luckymcdev.groovyengine.core.client.editor.core;

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
