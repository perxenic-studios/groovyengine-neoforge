package dev.lucky.groovyengine.internal.shell;

import dev.lucky.groovyengine.api.shell.events.ShellEvent;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;

import java.io.File;

public class ScriptShell {
    private final GroovyShell shell;
    private final GroovyClassLoader loader;

    private static final ScriptShell INSTANCE = new ScriptShell();

    private ScriptShell() {
        Binding binding = new Binding();
        this.loader = new GroovyClassLoader(FMLLoader.class.getClassLoader());
        this.shell = new GroovyShell(loader, binding);

        NeoForge.EVENT_BUS.post(new ShellEvent.AddShellBindingEvent(shell));
    }

    public static ScriptShell getInstance() {
        return INSTANCE;
    }

    public Object run(String code) {
        NeoForge.EVENT_BUS.post(new ShellEvent.ShellRunPreEvent(shell, code));
        Object result = shell.evaluate(code);
        NeoForge.EVENT_BUS.post(new ShellEvent.ShellRunPostEvent(shell, code, result));
        return result;
    }

    public Object runScript(String className) throws Exception {
        NeoForge.EVENT_BUS.post(new ShellEvent.ShellRunPreEvent(shell, className));
        Class<?> scriptClass = loader.loadClass(className);
        Script script = (Script) scriptClass.getDeclaredConstructor().newInstance();
        script.setBinding(shell.getContext());
        Object result = script.run();
        NeoForge.EVENT_BUS.post(new ShellEvent.ShellRunPostEvent(shell, className, result));
        return result;
    }

    public void addSourceDir(File dir) {
        loader.addClasspath(dir.getAbsolutePath());
    }

    public void setVariable(String name, Object value) {
        shell.getContext().setVariable(name, value);
    }

    public Object getVariable(String name) {
        return shell.getContext().getVariable(name);
    }
}
