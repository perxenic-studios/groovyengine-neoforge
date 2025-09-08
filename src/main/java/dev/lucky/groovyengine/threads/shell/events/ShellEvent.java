package dev.lucky.groovyengine.threads.shell.events;

import groovy.lang.GroovyShell;
import net.neoforged.bus.api.Event;

public abstract class ShellEvent extends Event {

    private final GroovyShell shell;

    public ShellEvent(GroovyShell shell) {
        this.shell = shell;
    }

    public GroovyShell getShell() {
        return shell;
    }

    public static class ShellRunPreEvent extends ShellEvent {
        private final String script;

        public ShellRunPreEvent(GroovyShell shell, String script) {
            super(shell);
            this.script = script;
        }

        public String getScript() {
            return script;
        }
    }

    public static class ShellRunPostEvent extends ShellRunPreEvent {
        private final Object result;

        public ShellRunPostEvent(GroovyShell shell, String script, Object result) {
            super(shell, script);
            this.result = result;
        }

        public Object getResult() {
            return result;
        }
    }

    public static class AddShellBindingEvent extends ShellEvent {

        public AddShellBindingEvent(GroovyShell shell) {
            super(shell);
        }

    }
}
