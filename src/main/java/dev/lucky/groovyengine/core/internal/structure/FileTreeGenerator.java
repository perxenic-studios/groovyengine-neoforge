package dev.lucky.groovyengine.core.internal.structure;

import dev.lucky.groovyengine.core.GE;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class FileTreeGenerator {

    private static final Path MAIN_PATH = Path.of(FMLPaths.GAMEDIR.get() +
            GE.MODID);

    private static final Path SRC_PATH = Path.of(FMLPaths.GAMEDIR.get() +
            GE.MODID+ "/src");

    private static final Path RESOURCE_PATH = Path.of(FMLPaths.GAMEDIR.get() +
            GE.MODID+ "/src/resources");

    private static final Path SCRIPT_PATH = Path.of(FMLPaths.GAMEDIR.get() +
            GE.MODID+ "/src/scripts");

    private static final Path MODULES_PATH = Path.of(FMLPaths.GAMEDIR.get() +
            GE.MODID+ "/modules");
}
