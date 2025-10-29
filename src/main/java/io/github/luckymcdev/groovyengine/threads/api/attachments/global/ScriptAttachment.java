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