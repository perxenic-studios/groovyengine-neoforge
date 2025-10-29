
package io.github.luckymcdev.groovyengine.threads.api.attachments.global;

/**
 * Attachment for client-side only events.
 * Use this for UI, rendering, input handling, etc.
 */
public abstract class ClientAttachment implements GlobalAttachment<Void> {

    /**
     * Called when the client starts up
     */
    public void onClientStart() {
    }

    /**
     * Called every client tick
     */
    public void onClientTick() {
    }

    /**
     * Called when a key is pressed, released, or held
     */
    public void onKeyPress(int key, int action, int modifiers) {
    }

    /**
     * Called when a mouse button is clicked
     */
    public void onMouseClick(int button, int action, int modifiers) {
    }

    /**
     * Called when the mouse wheel is scrolled
     */
    public void onMouseScroll(double horizontal, double vertical) {
    }
}