package io.github.luckymcdev.groovyengine.core.systems.structure;

import io.github.luckymcdev.groovyengine.GE;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class FileConstants {
    // Root directory
    public static final Path MOD_ROOT = Path.of(FMLPaths.GAMEDIR.get() + "/" + GE.MODID);

    // Source directories
    public static final Path SRC_DIR = MOD_ROOT.resolve("src");
    public static final Path RESOURCES_DIR = SRC_DIR.resolve("resources");
    public static final Path SCRIPTS_DIR = SRC_DIR.resolve("scripts");

    // Script subdirectories
    public static final Path COMMON_SCRIPTS_DIR = SCRIPTS_DIR.resolve("common");
    public static final Path CLIENT_SCRIPTS_DIR = SCRIPTS_DIR.resolve("client");
    public static final Path SERVER_SCRIPTS_DIR = SCRIPTS_DIR.resolve("server");

    // Module directory
    public static final Path MODULES_DIR = MOD_ROOT.resolve("modules");
}