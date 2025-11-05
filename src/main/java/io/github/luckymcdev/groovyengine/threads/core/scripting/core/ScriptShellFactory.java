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

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.threads.core.scripting.event.ScriptEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;

import java.util.List;

public class ScriptShellFactory {
    private static final GroovyShell SHELL = new GroovyShell(
            createClassLoader(),
            createBinding(),
            createCompilerConfig()
    );

    public static GroovyClassLoader createClassLoader() {
        return new GroovyClassLoader(ScriptShellFactory.class.getClassLoader());
    }

    /**
     * Creates a binding for use in a Groovy shell.
     * <p>
     * The binding contains a single variable, "Logger", which is set to
     * {@link io.github.luckymcdev.groovyengine.GE#SCRIPT_LOG}.
     * <p>
     * After creating the binding, a {@link ScriptEvent.BindingSetupEvent} is fired
     * to allow other mods to add their own bindings.
     *
     * @return the created binding
     */
    public static Binding createBinding() {
        Binding binding = new Binding();
        binding.setVariable("Logger", GE.SCRIPT_LOG);

        // Fire event to allow other mods to add their own bindings
        GroovyShell tempShell = new GroovyShell(createClassLoader(), binding);
        NeoForge.EVENT_BUS.post(new ScriptEvent.BindingSetupEvent(tempShell));

        return binding;
    }

    /**
     * Creates a compiler configuration for use in a Groovy shell.
     *
     * @return the created compiler configuration
     */
    public static CompilerConfiguration createCompilerConfig() {
        CompilerConfiguration config = new CompilerConfiguration();
        ImportCustomizer imports = new ImportCustomizer();
        imports.addStarImports(
                "java.lang", "java.util", "net.minecraft", "net.minecraft.util",
                "net.minecraft.item", "net.minecraft.block", "net.minecraft.entity",
                "net.minecraft.text", "com.mojang.brigadier", "net.minecraft.fluid"
        );
        config.addCompilationCustomizers(imports);

        SecureASTCustomizer secure = new SecureASTCustomizer();
        secure.setClosuresAllowed(true);
        secure.setMethodDefinitionAllowed(true);
        secure.setDisallowedImports(List.of(
                "java.io.*", "java.net.*", "javax.*", "sun.*", "com.sun.*", "jdk.*"
        ));
        secure.setDisallowedReceivers(List.of("System", "Runtime", "Thread", "Class"));
        config.addCompilationCustomizers(secure);

        return config;
    }

    /**
     * Creates a shared Groovy shell for use in script execution.
     * <p>
     * This method creates a new Groovy shell with the class loader, binding, and compiler configuration
     * created by the respective factory methods.
     *
     * @return the created shared Groovy shell
     */
    public static GroovyShell geSharedShell() {
        GE.THREADS_LOG.info("Creating shared Groovy shell");
        return SHELL;
    }
}