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
    public static GroovyClassLoader createClassLoader() {
        return new GroovyClassLoader(ScriptShellFactory.class.getClassLoader());
    }

    public static Binding createBinding() {
        Binding binding = new Binding();
        binding.setVariable("logger", GE.SCRIPT_LOG);

        // Fire event to allow other mods to add their own bindings
        GroovyShell tempShell = new GroovyShell(createClassLoader(), binding);
        NeoForge.EVENT_BUS.post(new ScriptEvent.BindingSetupEvent(tempShell));

        return binding;
    }

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

    public static GroovyShell createSharedShell() {
        GE.THREADS_LOG.info("Creating shared Groovy shell");
        return new GroovyShell(
                createClassLoader(),
                createBinding(),
                createCompilerConfig()
        );
    }
}