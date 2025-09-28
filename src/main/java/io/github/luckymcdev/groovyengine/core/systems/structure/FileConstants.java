// Update FileConstants.java to include Gradle file paths
package io.github.luckymcdev.groovyengine.core.systems.structure;

import io.github.luckymcdev.groovyengine.GE;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class FileConstants {
    // Root directory
    public static final Path MOD_ROOT = Path.of(FMLPaths.GAMEDIR.get() + "/" + GE.MODID);

    // Source directories
    public static final Path SRC_DIR = MOD_ROOT.resolve("src/main");
    public static final Path RESOURCES_DIR = SRC_DIR.resolve("resources");
    public static final Path SCRIPTS_DIR = SRC_DIR.resolve("groovy");

    // Script subdirectories
    public static final Path COMMON_SCRIPTS_DIR = SCRIPTS_DIR.resolve("common");
    public static final Path CLIENT_SCRIPTS_DIR = SCRIPTS_DIR.resolve("client");
    public static final Path SERVER_SCRIPTS_DIR = SCRIPTS_DIR.resolve("server");

    // Module directory
    public static final Path MODULES_DIR = MOD_ROOT.resolve("modules");

    // Gradle files
    public static final Path BUILD_GRADLE = MOD_ROOT.resolve("build.gradle");
    public static final Path SETTINGS_GRADLE = MOD_ROOT.resolve("settings.gradle");
    public static final Path GRADLE_PROPERTIES = MOD_ROOT.resolve("gradle.properties");
}