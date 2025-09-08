package dev.lucky.groovyengine.threads;

import dev.lucky.groovyengine.threads.shell.ScriptShell;
import net.neoforged.bus.api.IEventBus;

public class Threads {
    public static void initThreads(IEventBus modEventBus) {
        ScriptShell shellInstance = ScriptShell.getInstance();
    }
}
