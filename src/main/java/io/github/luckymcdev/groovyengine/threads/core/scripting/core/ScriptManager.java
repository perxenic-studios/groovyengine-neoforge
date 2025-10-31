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
package io.github.luckymcdev.groovyengine.threads.core.scripting.core;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.core.systems.structure.FileConstants;
import io.github.luckymcdev.groovyengine.threads.api.attachments.AttachmentManager;
import io.github.luckymcdev.groovyengine.threads.api.attachments.global.ScriptAttachment;
import io.github.luckymcdev.groovyengine.threads.core.scripting.error.ScriptErrors;
import io.github.luckymcdev.groovyengine.threads.core.scripting.event.ScriptEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.luckymcdev.groovyengine.core.systems.structure.FileConstants.MODULES_DIR;
import static io.github.luckymcdev.groovyengine.core.systems.structure.FileConstants.SCRIPTS_DIR;

public class ScriptManager {
    private static GroovyShell shell;

    public static void initialize() {
        GE.THREADS_LOG.info("Initializing script manager");
        shell = ScriptShellFactory.createSharedShell();
        loadAllScripts();
    }

    public static void reloadScripts() {
        GE.THREADS_LOG.info("Reloading scripts");
        shell = ScriptShellFactory.createSharedShell();
        loadAllScripts();
    }

    private static void loadAllScripts() {
        Dist dist = FMLLoader.getDist();
        runScriptsIn(SCRIPTS_DIR, "common");
        if (dist.isClient()) runScriptsIn(SCRIPTS_DIR, "client");
        else runScriptsIn(SCRIPTS_DIR, "server");

        runScriptsInModules(dist);
    }

    private static void runScriptsInModules(Dist dist) {
        if (!Files.exists(MODULES_DIR) || !Files.isDirectory(MODULES_DIR)) {
            GE.THREADS_LOG.info("Modules directory not found, skipping module script loading.");
            return;
        }

        try (Stream<Path> modulePaths = Files.list(MODULES_DIR)) {
            modulePaths.filter(Files::isDirectory).forEach(modulePath -> {
                GE.THREADS_LOG.info("Loading scripts from module: {}", modulePath.getFileName());
                Path moduleScriptsDir = modulePath.resolve("main/groovy");
                runScriptsIn(moduleScriptsDir, "common");
                if (dist.isClient()) runScriptsIn(moduleScriptsDir, "client");
                else runScriptsIn(moduleScriptsDir, "server");
            });
        } catch (IOException e) {
            GE.THREADS_LOG.error("Error accessing modules directory", e);
        }
    }

    private static void runScriptsIn(Path baseDir, String environment) {
        Path envDir = baseDir.resolve(environment);
        if (!Files.exists(envDir)) return;

        try (Stream<Path> paths = Files.walk(envDir)) {
            List<Path> scripts = paths
                .filter(p -> p.toString().endsWith(".groovy"))
                .filter(p -> !ScriptMetadata.isDisabled(p))
                .collect(Collectors.toList());

            scripts.sort(Comparator.comparingInt(ScriptMetadata::getPriority).reversed());

            scripts.forEach(ScriptManager::evaluateScript);
        } catch (IOException e) {
            GE.THREADS_LOG.error("Error loading scripts from {}:{}", baseDir.getFileName(), environment, e);
        }
    }

    private static void evaluateScript(Path scriptPath) {
        GE.THREADS_LOG.info("Evaluating script: {}", scriptPath.getFileName());

        AttachmentManager.getInstance().getScriptAttachments(scriptPath.getFileName().toString()).forEach(ScriptAttachment::onScriptLoad);

        try {
            NeoForge.EVENT_BUS.post(new ScriptEvent.PreExecutionEvent(shell, scriptPath.toString()));
            Script compiledScript = shell.parse(scriptPath.toFile());
            GE.THREADS_LOG.info(compiledScript.toString());
            Object result = compiledScript.run();
            NeoForge.EVENT_BUS.post(new ScriptEvent.PostExecutionEvent(shell, scriptPath.toString(), result));
        } catch (Exception ex) {
            GE.THREADS_LOG.error("Script error in {}", scriptPath.getFileName(), ex);

            AttachmentManager.getInstance().getScriptAttachments(scriptPath.getFileName().toString()).forEach(scriptAttachment -> scriptAttachment.onScriptError(ex));

            String description = ScriptErrors.generateErrorDescription(ex);
            ScriptErrors.addError(scriptPath.getFileName().toString(), ex.getMessage(), description);
        }
    }

    public static GroovyShell getShell() {
        return shell;
    }
}
