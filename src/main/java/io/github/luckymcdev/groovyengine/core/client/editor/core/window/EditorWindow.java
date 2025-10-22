package io.github.luckymcdev.groovyengine.core.client.editor.core.window;

import imgui.ImGuiIO;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public abstract class EditorWindow {
    protected final String title;
    protected final String id;
    protected boolean enabled = false;

    public EditorWindow(String title, String id) {
        this.title = title;
        this.id = id;
    }

    public EditorWindow(String title) {
        this(title, title.toLowerCase().replace(' ', '_'));
    }

    /**
     * Called every frame to render the window content
     */
    public abstract void render(ImGuiIO io);

    /**
     * Called when the window is first opened
     */
    public void onOpen() {
    }

    /**
     * Called when the window is closed
     */
    public void onClose() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            if (enabled) {
                onOpen();
            } else {
                onClose();
            }
            this.enabled = enabled;
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }
}