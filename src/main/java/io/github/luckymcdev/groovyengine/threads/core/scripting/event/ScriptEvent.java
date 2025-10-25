package io.github.luckymcdev.groovyengine.threads.core.scripting.event;

import groovy.lang.GroovyShell;
import net.neoforged.bus.api.Event;

public abstract class ScriptEvent extends Event {
    private final GroovyShell shell;

    public ScriptEvent(GroovyShell shell) {
        this.shell = shell;
    }

    /**
     * Gets the Groovy shell associated with this event.
     *
     * @return the Groovy shell associated with this event
     */
    public GroovyShell getShell() {
        return shell;
    }

    public static class PreExecutionEvent extends ScriptEvent {
        private final String script;

        public PreExecutionEvent(GroovyShell shell, String script) {
            super(shell);
            this.script = script;
        }

        public String getScript() {
            return script;
        }
    }

    public static class PostExecutionEvent extends PreExecutionEvent {
        private final Object result;

        public PostExecutionEvent(GroovyShell shell, String script, Object result) {
            super(shell, script);
            this.result = result;
        }

        public Object getResult() {
            return result;
        }
    }

    public static class BindingSetupEvent extends ScriptEvent {
        public BindingSetupEvent(GroovyShell shell) {
            super(shell);
        }
    }
}