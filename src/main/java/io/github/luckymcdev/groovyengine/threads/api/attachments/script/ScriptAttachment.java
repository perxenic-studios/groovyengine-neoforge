package io.github.luckymcdev.groovyengine.threads.api.attachments.script;

import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;

import java.util.List;

public abstract class ScriptAttachment implements BaseAttachment<Object> {

    public final String scriptId;

    /**
     * Constructor for script attachment
     * @param scriptId The identifier for the script this attachment belongs to
     */
    public ScriptAttachment(String scriptId) {
        this.scriptId = scriptId;
    }

    @Override
    public Object getTarget() {
        return scriptId;
    }

    @Override
    public List<Object> getTargets() {
        return List.of(scriptId);
    }

    /**
     * Get the script ID this attachment is associated with
     */
    public String getScriptId() {
        return scriptId;
    }

    // Script Lifecycle Events
    public void onScriptLoad() {}
    public void onScriptReload() {}
    public void onScriptError(Exception error) {}

    // Server Events (for server-wide scripts)
    public void onServerStart() {}
    public void onServerStop() {}
}