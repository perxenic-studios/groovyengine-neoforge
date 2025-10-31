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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileTreeGenerator {

    private static final String MAPPINGS_VERSION = "2024.11.17";
    private static String neoforgeVersion = "21.1.201";
    private static String minecraftVersion = "1.21.1";

    /**
     * A Debug Deprecated method to just generate the file structure without having to launch the game.
     *
     * @param args args
     */
    @Deprecated
    public static void main(String[] args) {

        if (!validateStructure()) {
            GE.CORE_LOG.info("File structure is not valid, attempting to generate file structure");
            generateFileStructure();
        } else {
            GE.CORE_LOG.info("File structure is valid, skipping file structure generation");
        }


    }

    /**
     * Generates the file structure for GroovyEngine. This method creates all directories and default files required by GroovyEngine.
     * <p>
     * This method is called automatically by GroovyEngine when it is initialized.
     */
    public static void generateFileStructure() {
        GE.CORE_LOG.info("Generating file structure for GroovyEngine");

        try {
            net.neoforged.fml.loading.VersionInfo versionInfo = net.neoforged.fml.loading.FMLLoader.versionInfo();
            if (versionInfo != null) {
                neoforgeVersion = versionInfo.neoForgeVersion();
                minecraftVersion = versionInfo.mcVersion();
                GE.CORE_LOG.debug("Using runtime versions - NeoForge: {}, Minecraft: {}", neoforgeVersion, minecraftVersion);
            } else {
                GE.CORE_LOG.debug("FMLLoader.versionInfo() is null, using default versions - NeoForge: {}, Minecraft: {}", neoforgeVersion, minecraftVersion);
            }
        } catch (Exception e) {
            GE.CORE_LOG.warn("Failed to retrieve version info from FMLLoader, using defaults", e);
        }


        // Create all directories
        GE.CORE_LOG.info("Creating directories");
        List.of(
                FileConstants.WORKSPACE_DIR,
                FileConstants.MOD_ROOT,
                FileConstants.SRC_DIR,
                FileConstants.RESOURCES_DIR,
                FileConstants.SCRIPTS_DIR,
                FileConstants.COMMON_SCRIPTS_DIR,
                FileConstants.CLIENT_SCRIPTS_DIR,
                FileConstants.SERVER_SCRIPTS_DIR,
                FileConstants.MODULES_DIR
        ).forEach(FileTreeGenerator::createDirectory);

        // Create default files
        createDefaultScriptFiles();

        // Create Gradle files
        createGradleFiles();

        GE.CORE_LOG.info("File structure generation completed");
    }

    /**
     * Creates a directory at the specified path if it does not already exist.
     *
     * <p>
     * This method logs a message to the console if the directory is successfully created or if an error occurs.
     * </p>
     *
     * @param path The path to the directory to create.
     */
    private static void createDirectory(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                GE.CORE_LOG.info("Created directory: {}", path);
            }
        } catch (IOException e) {
            GE.CORE_LOG.error("Failed to create directory: {}", path, e);
        }
    }

    /**
     * Creates all Gradle files required by GroovyEngine. This method creates the build.gradle, internal.gradle, and
     * settings.gradle files in the MOD_ROOT directory.
     * <p>
     * The build.gradle file contains all configuration required for building GroovyEngine modules. The
     * internal.gradle file contains logic for applying the build.gradle file to GroovyEngine modules. The
     * settings.gradle file contains configuration for the Gradle plugin manager.
     * </p>
     */
    private static void createGradleFiles() {
        createFile(FileConstants.BUILD_GRADLE, "build.gradle", """
                plugins {
                     id 'maven-publish'
                     id 'net.neoforged.moddev' version '2.0.107'
                     id 'idea'
                     id 'groovy'
                     id 'java'
                 }
                
                
                
                
                 // Apply internal build logic
                 apply from: 'internal.gradle'
                """);

        createFile(FileConstants.INTERNAL_GRADLE, "internal.gradle", """
                sourceSets {
                    main {
                        groovy {
                            srcDirs = ['src/main/groovy']
                        }
                        resources {
                            srcDirs = ['src/main/resources']
                        }
                        compileClasspath += fileTree('../mods') { include '*.jar' }
                        runtimeClasspath += fileTree('../mods') { include '*.jar' }
                    }
                }
                
                neoForge {
                    version = project.hasProperty('neoforge_version') ? project.neoforge_version : '20.4.0-beta'
                
                    parchment {
                        mappingsVersion = project.hasProperty('mappings_version') ? project.mappings_version : '23.0.0'
                        minecraftVersion = project.hasProperty('minecraft_version') ? project.minecraft_version : '1.21.1'
                    }
                }
                
                repositories {
                    mavenCentral()
                    maven { name = 'NeoForged'; url = 'https://maven.neoforged.net/releases' }
                    maven { name = 'ParchmentMC'; url = 'https://maven.parchmentmc.org' }
                    maven { name = 'LWJGL'; url = 'https://repo.lwjgl.org/releases' }
                }
                
                dependencies {
                    implementation 'org.apache.groovy:groovy:4.0.14'
                    implementation 'org.apache.groovy:groovy-json:4.0.14'
                
                    implementation "io.github.spair:imgui-java-binding:1.87.7"
                    implementation("io.github.spair:imgui-java-lwjgl3:1.87.7") {
                        exclude group: 'org.lwjgl'
                    }
                    implementation "io.github.spair:imgui-java-natives-windows:1.87.7"
                    implementation "io.github.spair:imgui-java-natives-linux:1.87.7"
                    implementation "io.github.spair:imgui-java-natives-macos:1.87.7"
                }
                
                idea {
                    module {
                        downloadSources = true
                        downloadJavadoc = true
                    }
                }
                
                tasks.forEach { task ->
                    task.group = 'Zinternal'
                }
                
                tasks.named('test') {
                    group = 'verification'
                }
                
                tasks.register('buildModule', Copy) {
                    group = 'build'
                    description = 'Copies the src folder to modules directory'
                
                    def moduleName = project.hasProperty('module_name') ? project.property('module_name') : project.name
                    println "Module name determined as: $moduleName" // This runs at configuration time
                
                    from 'src'
                    into "../modules/${moduleName}"
                
                    doFirst {
                        println "Starting copy from src to ../../modules/${moduleName}"
                    }
                
                    doLast {
                        println "Copy completed!"
                    }
                }
                
                tasks.register('cleanWorkspace') {
                    group = 'build'
                    description = 'Cleans the workspace directory and rebuilds module (with confirmation)'
                    
                    dependsOn 'buildModule'
                
                    doFirst {
                        def console = System.console()
                        def answer = null
                
                        if (console) {
                            // Running in terminal - use console
                            answer = console.readLine('\\n WARNING: This will DELETE the entire workspace directory!\\n' +
                                                      'Are you SURE you want to do this? (yes/no): ')
                        } else {
                            // Running in IDE - use Swing dialog
                            try {
                                javax.swing.SwingUtilities.invokeAndWait({
                                    def result = javax.swing.JOptionPane.showConfirmDialog(
                                        null,
                                        "WARNING: This will DELETE the entire workspace directory!\\n\\nAre you SURE you want to do this?",
                                        "Clean Workspace Confirmation",
                                        javax.swing.JOptionPane.YES_NO_OPTION,
                                        javax.swing.JOptionPane.WARNING_MESSAGE
                                    )
                                    answer = (result == javax.swing.JOptionPane.YES_OPTION) ? 'yes' : 'no'
                                } as Runnable)
                            } catch (Exception e) {
                                println "Failed to show dialog: ${e.message}"
                                throw new GradleException("Could not get user confirmation")
                            }
                        }
                
                        if (!answer?.toLowerCase()?.startsWith('y')) {
                            throw new GradleException("Workspace cleanup cancelled by user")
                        }
                
                        println "Confirmation received. Proceeding with workspace cleanup..."
                    }
                
                    doLast {
                        def workspaceDir = file('.')
                        println "Deleting workspace directory: ${workspaceDir.absolutePath}"
                
                        // Delete all contents except .gradle directory
                        workspaceDir.listFiles()?.each { file ->
                            if (file.name != '.gradle' && file.name != 'gradle' && file.name != 'gradlew' && file.name != 'gradlew.bat') {
                                println "Deleting: ${file.name}"
                                project.delete(file)
                            }
                        }
                
                        println "Workspace cleaned successfully!"
                    }
                }
                
                """);

        createFile(FileConstants.SETTINGS_GRADLE, "settings.gradle", """
                pluginManagement {
                    repositories {
                        gradlePluginPortal()
                        maven {
                            name = 'NeoForged'
                            url = 'https://maven.neoforged.net/releases'
                        }
                    }
                }
                """);

        createFile(FileConstants.GRADLE_PROPERTIES, "gradle.properties", """
                org.gradle.jvmargs=-Xmx2G
                minecraft_version=%s
                neoforge_version=%s
                mappings_channel=parchment
                mappings_version=%s
                
                
                ## Here is your Modules Definition
                
                module_name=mymodule
                """.formatted(minecraftVersion, neoforgeVersion, MAPPINGS_VERSION));
    }

    /**
     * Creates sample script files in the common, client, and server script directories.
     *
     * <p>
     * This method creates sample scripts with a logging statement and a comment to add script logic.
     * </p>
     */
    private static void createDefaultScriptFiles() {
        createFile(FileConstants.COMMON_SCRIPTS_DIR.resolve("CommonMain.groovy"), "CommonMain.groovy", """
                //priority=0
                package common
                import io.github.luckymcdev.groovyengine.GE;
                GE.LOG.info('Hello from common scripts!')
                // Add your common script logic here
                """);

        createFile(FileConstants.CLIENT_SCRIPTS_DIR.resolve("ClientMain.groovy"), "ClientMain.groovy", """
                //priority=0
                package client
                import io.github.luckymcdev.groovyengine.GE;
                GE.LOG.info('Hello from client scripts!')
                // Add your client-side script logic here
                """);

        createFile(FileConstants.SERVER_SCRIPTS_DIR.resolve("ServerMain.groovy"), "ServerMain.groovy", """
                //priority=0
                package server
                import io.github.luckymcdev.groovyengine.GE;
                GE.LOG.info('Hello from server scripts!')
                // Add your server-side script logic here
                """);
    }

    /**
     * Creates a file at the specified path with the given content if it does not already exist.
     *
     * <p>
     * This method will log a message to the console if the file is successfully created or if an error occurs.
     * </p>
     *
     * @param path     The path to the file to create.
     * @param fileType A descriptive name for the file type being created (e.g., "Gradle file", "sample script").
     * @param content  The content of the file.
     */
    private static void createFile(Path path, String fileType, String content) {
        try {
            if (!Files.exists(path)) {
                Files.writeString(path, content);
                GE.CORE_LOG.debug("Created {}: {}", fileType, path);
            }
        } catch (IOException e) {
            GE.CORE_LOG.error("Failed to create {}: {}", fileType, path, e);
        }
    }

    /**
     * Validates the file structure of GroovyEngine by checking if all required directories exist.
     *
     * <p>
     * This method will log a warning message to the console for each required directory that does not exist.
     * </p>
     *
     * @return true if all required directories exist, false otherwise.
     */
    public static boolean validateStructure() {
        boolean valid = true;

        for (Path dir : List.of(
                FileConstants.MOD_ROOT,
                FileConstants.SCRIPTS_DIR,
                FileConstants.COMMON_SCRIPTS_DIR,
                FileConstants.CLIENT_SCRIPTS_DIR,
                FileConstants.SERVER_SCRIPTS_DIR
        )) {
            if (!Files.exists(dir)) {
                GE.CORE_LOG.warn("Required directory does not exist: {}", dir);
                valid = false;
            }
        }

        return valid;
    }
}
