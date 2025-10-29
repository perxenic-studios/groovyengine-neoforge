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

package io.github.luckymcdev.groovyengine.core.systems.structure;

import io.github.luckymcdev.groovyengine.GE;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class FileConstants {
    // Root directory - handles both in-game and standalone execution
    public static final Path MOD_ROOT = getModRoot();

    // Source directories
    public static final Path WORKSPACE_DIR = MOD_ROOT.resolve("workspace");
    public static final Path SRC_DIR = WORKSPACE_DIR.resolve("src/main");
    public static final Path RESOURCES_DIR = SRC_DIR.resolve("resources");
    public static final Path SCRIPTS_DIR = SRC_DIR.resolve("groovy");

    // Script subdirectories
    public static final Path COMMON_SCRIPTS_DIR = SCRIPTS_DIR.resolve("common");
    public static final Path CLIENT_SCRIPTS_DIR = SCRIPTS_DIR.resolve("client");
    public static final Path SERVER_SCRIPTS_DIR = SCRIPTS_DIR.resolve("server");

    // Module directory
    public static final Path MODULES_DIR = MOD_ROOT.resolve("modules");

    // Gradle files
    public static final Path BUILD_GRADLE = WORKSPACE_DIR.resolve("build.gradle");
    public static final Path INTERNAL_GRADLE = WORKSPACE_DIR.resolve("internal.gradle");
    public static final Path SETTINGS_GRADLE = WORKSPACE_DIR.resolve("settings.gradle");
    public static final Path GRADLE_PROPERTIES = WORKSPACE_DIR.resolve("gradle.properties");

    /**
     * Gets the mod root directory, handling both in-game and standalone execution.
     * When running in-game, uses FMLPaths.GAMEDIR. When running standalone (like from main()),
     * uses the current working directory.
     *
     * @return The path to the mod root directory
     */
    private static Path getModRoot() {
        try {
            Path gamePath = FMLPaths.GAMEDIR.get();
            if (gamePath != null) {
                Path modRoot = gamePath.resolve("GroovyEngine");
                GE.CORE_LOG.info("Using game directory: {}", modRoot);
                return modRoot;
            }
        } catch (Exception e) {
            GE.CORE_LOG.warn("FMLPaths not available, using current directory", e);
        }

        // Fallback to current working directory
        Path modRoot = Path.of(System.getProperty("user.dir")).resolve("run").resolve("GroovyEngine");
        GE.CORE_LOG.info("Using current directory: {}", modRoot);
        return modRoot;
    }
}