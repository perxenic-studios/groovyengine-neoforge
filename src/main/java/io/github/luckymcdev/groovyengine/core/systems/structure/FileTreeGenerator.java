package io.github.luckymcdev.groovyengine.core.systems.structure;

import io.github.luckymcdev.groovyengine.GE;
import net.neoforged.fml.loading.FMLLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileTreeGenerator {

    private static final String NEOFORGE_VERSION = FMLLoader.versionInfo().neoForgeVersion();
    private static final String MINECRAFT_VERSION = FMLLoader.versionInfo().mcVersion();
    private static final String MAPPINGS_VERSION = "2024.11.17";

    public static void generateFileStructure() {
        GE.CORE_LOG.info("Generating file structure for GroovyEngine");

        // Create all directories
        createDirectory(FileConstants.MOD_ROOT);
        createDirectory(FileConstants.SRC_DIR);
        createDirectory(FileConstants.RESOURCES_DIR);
        createDirectory(FileConstants.SCRIPTS_DIR);
        createDirectory(FileConstants.COMMON_SCRIPTS_DIR);
        createDirectory(FileConstants.CLIENT_SCRIPTS_DIR);
        createDirectory(FileConstants.SERVER_SCRIPTS_DIR);
        createDirectory(FileConstants.MODULES_DIR);

        // Create default files
        createDefaultScriptFiles();

        // Create Gradle files
        createGradleFiles();

        GE.CORE_LOG.info("File structure generation completed");
    }

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

    private static void createGradleFiles() {
        createGradleFile(FileConstants.BUILD_GRADLE, """
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

        createGradleFile(FileConstants.INTERNAL_GRADLE, """
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
                
                // Optional: Configure specific task groups if needed
                tasks.named('build') {
                    group = 'build'
                }
                
                tasks.named('test') {
                    group = 'verification'
                }
                
                tasks.named('clean') {
                    group = 'build'
                }
                
                tasks.register('buildModule', Copy) {
                    group = 'build'
                    description = 'Copies the src folder to modules directory'
                
                    def moduleName = project.hasProperty('module_name') ? project.property('module_name') : project.name
                    println "Module name determined as: $moduleName" // This runs at configuration time
                
                    from 'src'
                    into "modules/${moduleName}"
                
                    doFirst {
                        println "Starting copy from src to ../modules/${moduleName}"
                    }
                
                    doLast {
                        println "Copy completed!"
                    }
                }
                
                """);

        createGradleFile(FileConstants.SETTINGS_GRADLE, """
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

        createGradleFile(FileConstants.GRADLE_PROPERTIES, """
                org.gradle.jvmargs=-Xmx2G
                minecraft_version=%s
                neoforge_version=%s
                mappings_channel=parchment
                mappings_version=%s
                
                
                ## Here is your Modules Definition
                
                module_name=myModule
                """.formatted(MINECRAFT_VERSION, NEOFORGE_VERSION, MAPPINGS_VERSION));
    }

    private static void createGradleFile(Path filePath, String content) {
        try {
            if (!Files.exists(filePath)) {
                Files.writeString(filePath, content);
                GE.CORE_LOG.info("Created Gradle file: {}", filePath);
            }
        } catch (IOException e) {
            GE.CORE_LOG.error("Failed to create Gradle file: {}", filePath, e);
        }
    }

    private static void createDefaultScriptFiles() {
        createSampleScript(FileConstants.COMMON_SCRIPTS_DIR, "CommonMain.groovy", """
                //priority=0
                package common
                import io.github.luckymcdev.groovyengine.GE;
                GE.LOG.info('Hello from common scripts!')
                // Add your common script logic here
                """);

        createSampleScript(FileConstants.CLIENT_SCRIPTS_DIR, "ClientMain.groovy", """
                //priority=0
                package client
                import io.github.luckymcdev.groovyengine.GE;
                GE.LOG.info('Hello from client scripts!')
                // Add your client-side script logic here
                """);

        createSampleScript(FileConstants.SERVER_SCRIPTS_DIR, "ServerMain.groovy", """
                //priority=0
                package server
                import io.github.luckymcdev.groovyengine.GE;
                GE.LOG.info('Hello from server scripts!')
                // Add your server-side script logic here
                """);
    }

    private static void createSampleScript(Path directory, String filename, String content) {
        Path scriptPath = directory.resolve(filename);
        try {
            if (!Files.exists(scriptPath)) {
                Files.writeString(scriptPath, content);
                GE.CORE_LOG.info("Created sample script: {}", scriptPath);
            }
        } catch (IOException e) {
            GE.CORE_LOG.error("Failed to create sample script: {}", scriptPath, e);
        }
    }

    public static boolean validateStructure() {
        boolean valid = true;

        Path[] requiredDirs = {
                FileConstants.MOD_ROOT,
                FileConstants.SCRIPTS_DIR,
                FileConstants.COMMON_SCRIPTS_DIR,
                FileConstants.CLIENT_SCRIPTS_DIR,
                FileConstants.SERVER_SCRIPTS_DIR
        };

        for (Path dir : requiredDirs) {
            if (!Files.exists(dir)) {
                GE.CORE_LOG.warn("Required directory does not exist: {}", dir);
                valid = false;
            }
        }

        return valid;
    }
}
