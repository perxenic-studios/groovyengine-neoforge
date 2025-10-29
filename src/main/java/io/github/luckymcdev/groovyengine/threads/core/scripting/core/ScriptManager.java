package io.github.luckymcdev.groovyengine.threads.core.scripting.core;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.threads.core.scripting.attachment.AttachmentEventManager;
import io.github.luckymcdev.groovyengine.threads.core.scripting.error.ScriptErrors;
import io.github.luckymcdev.groovyengine.threads.core.scripting.event.ScriptEvent;
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

import static io.github.luckymcdev.groovyengine.core.systems.structure.FileConstants.SCRIPTS_DIR;

public class ScriptManager {
    private static final AttachmentEventManager attachmentEventManager = AttachmentEventManager.getInstance();
    private static GroovyShell shell;

    /**
     * Initializes the script manager. This method is called when the mod is initialized.
     * It creates a shared Groovy shell and loads all scripts.
     */
    public static void initialize() {
        GE.THREADS_LOG.info("Initializing script manager");
        shell = ScriptShellFactory.createSharedShell();
        loadAllScripts();
    }

    /**
     * Reloads all scripts in the script manager. This method is called when the mod is reinitialized.
     * It creates a new shared Groovy shell and loads all scripts.
     */
    public static void reloadScripts() {
        GE.THREADS_LOG.info("Reloading scripts");
        shell = ScriptShellFactory.createSharedShell();
        loadAllScripts();
    }

    /**
     * Loads all scripts in the script manager. This method first loads all scripts in the
     * "common" environment, and then loads all scripts in the "client" environment if
     * the mod is running on the client, or the "server" environment if the mod is
     * running on the server.
     */
    private static void loadAllScripts() {
        Dist dist = FMLLoader.getDist();
        runScriptsIn("common");
        if (dist.isClient()) runScriptsIn("client");
        else runScriptsIn("server");
    }

    /**
     * Runs all scripts in the given environment directory.
     * The scripts are sorted by their priority, and then executed in order.
     * If an error occurs while loading the scripts, an error message will be logged.
     *
     * @param environment The environment directory to load scripts from.
     */
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
            GE.THREADS_LOG.error("Error loading scripts from {}", environment, e);
        }
    }

    /**
     * Evaluates a script and logs any errors that occur.
     *
     * <p>This method will first log an info message about the script being evaluated,
     * and then fire a {@link ScriptEvent.PreExecutionEvent} to allow other mods to
     * attach to the script evaluation process. After that, it will parse the script and
     * run it. The result of the script will be posted to a
     * {@link ScriptEvent.PostExecutionEvent}.</p>
     *
     * The error message, along with the error description and the script name, will be added
     * to the {@link ScriptErrors} list.</p>
     *
     * @param scriptPath The path to the script to evaluate.
     */
    private static void evaluateScript(Path scriptPath) {
        GE.THREADS_LOG.info("Evaluating script: {}", scriptPath.getFileName());

        attachmentEventManager.fireScriptLoad(scriptPath.getFileName().toString());
        attachmentEventManager.fireScriptReload(scriptPath.getFileName().toString());

        try {
            NeoForge.EVENT_BUS.post(new ScriptEvent.PreExecutionEvent(shell, scriptPath.toString()));
            Script compiledScript = shell.parse(scriptPath.toFile());
            Object result = compiledScript.run();
            NeoForge.EVENT_BUS.post(new ScriptEvent.PostExecutionEvent(shell, scriptPath.toString(), result));
        } catch (Exception ex) {
            GE.THREADS_LOG.error("Script error in {}", scriptPath.getFileName(), ex);

            attachmentEventManager.fireScriptError(scriptPath.getFileName().toString(), ex);

            String description = ScriptErrors.generateErrorDescription(ex);
            ScriptErrors.addError(scriptPath.getFileName().toString(), ex.getMessage(), description);
        }
    }


    public static GroovyShell getShell() {
        return shell;
    }
}