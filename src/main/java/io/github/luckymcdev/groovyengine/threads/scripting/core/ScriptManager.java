package io.github.luckymcdev.groovyengine.threads.scripting.core;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.threads.scripting.event.ScriptEvent;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.luckymcdev.groovyengine.core.systems.structure.FileConstants.*;

public class ScriptManager {
    private static GroovyShell shell;

    public static void initialize() {
        GE.LOG.info("Initializing script manager");
        shell = ScriptShellFactory.createSharedShell();
        loadAllScripts();
    }

    public static void reloadScripts() {
        GE.LOG.info("Reloading scripts");
        shell = ScriptShellFactory.createSharedShell();
        loadAllScripts();
    }

    private static void loadAllScripts() {
        Dist dist = FMLLoader.getDist();
        runScriptsIn("common");
        if (dist.isClient()) runScriptsIn("client");
        else runScriptsIn("server");
    }

    private static void runScriptsIn(String environment) {
        Path envDir = SCRIPTS_DIR.resolve(environment);
        if (!Files.exists(envDir)) return;

        try (Stream<Path> paths = Files.walk(envDir)) {
            List<Path> scripts = paths
                    .filter(p -> p.toString().endsWith(".groovy"))
                    .filter(p -> !ScriptMetadata.isDisabled(p))
                    .collect(Collectors.toList());

            scripts.sort(Comparator.comparingInt(ScriptMetadata::getPriority));

            scripts.forEach(ScriptManager::evaluateScript);
        } catch (IOException e) {
            GE.LOG.error("Error loading scripts from {}", environment, e);
        }
    }

    private static void evaluateScript(Path scriptPath) {
        GE.LOG.info("Evaluating script: {}", scriptPath.getFileName());

        try {
            // Fire pre-execution event
            NeoForge.EVENT_BUS.post(new ScriptEvent.PreExecutionEvent(shell, scriptPath.toString()));

            Script compiledScript = shell.parse(scriptPath.toFile());
            Object result = compiledScript.run();

            // Fire post-execution event
            NeoForge.EVENT_BUS.post(new ScriptEvent.PostExecutionEvent(shell, scriptPath.toString(), result));
        } catch (Exception ex) {
            GE.LOG.error("Script error in {}", scriptPath.getFileName(), ex);
        }
    }

    public static GroovyShell getShell() {
        return shell;
    }
}