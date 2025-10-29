package io.github.luckymcdev.groovyengine.threads.api.attachments.global;

import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;

import java.util.List;

/**
 * Attachment for script lifecycle events.
 * Use this to track when scripts are loaded, reloaded, or encounter errors.
 */
public abstract class ScriptAttachment implements BaseAttachment<String> {

    private final String scriptId;

    /**
     * Constructor for script attachment
     *
     * @param scriptId The identifier for the script this attachment monitors
     */
    protected ScriptAttachment(String scriptId) {
        this.scriptId = scriptId;
    }

    @Override
    public final boolean appliesTo(String scriptId) {
        return this.scriptId.equals(scriptId);
    }

    /**
     * Get the script ID this attachment monitors
     */
    public final String getScriptId() {
        return scriptId;
    }

    /**
     * Called when the script is first loaded
     */
    public void onScriptLoad() {
    }

    /**
     * Called when the script encounters an error
     */
    public void onScriptError(Exception error) {
    }
}