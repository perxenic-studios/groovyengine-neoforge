// Update FileTreeGenerator.java to generate Gradle files
package io.github.luckymcdev.groovyengine.core.systems.structure;

import io.github.luckymcdev.groovyengine.GE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileTreeGenerator {

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
        // Create build.gradle with proper source set configuration
        createGradleFile(FileConstants.BUILD_GRADLE,
                "plugins {\n" +
                        "    id 'groovy'\n" +
                        "    id 'java'\n" +
                        "}\n\n" +
                        "sourceSets {\n" +
                        "    main {\n" +
                        "        groovy {\n" +
                        "            srcDirs = ['src/main/groovy']\n" +
                        "        }\n" +
                        "        resources {\n" +
                        "            srcDirs = ['src/main/resources']\n" +
                        "        }\n" +
                        "        compileClasspath += fileTree('../mods') { include '*.jar' }\n" +
                        "        runtimeClasspath += fileTree('../mods') { include '*.jar' }\n" +
                        "    }\n" +
                        "}\n\n" +
                        "repositories {\n" +
                        "    mavenCentral()\n" +
                        "}\n\n" +
                        "dependencies {\n" +
                        "    implementation 'org.apache.groovy:groovy:4.0.14'\n" +
                        "    implementation 'org.apache.groovy:groovy-json:4.0.14'\n" +
                        "}"
        );

        // Create settings.gradle
        createGradleFile(FileConstants.SETTINGS_GRADLE,
                "rootProject.name = 'groovyengine'"
        );

        // Create gradle.properties
        createGradleFile(FileConstants.GRADLE_PROPERTIES,
                 " "
        );
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
        // Create a sample script in each environment
        createSampleScript(FileConstants.COMMON_SCRIPTS_DIR, "CommonMain.groovy",
                "//priority=0\n" +
                        "import io.github.luckymcdev.groovyengine.GE;\n" +
                        "GE.LOG.info('Hello from common scripts!')\n" +
                        "// Add your common script logic here"
        );

        createSampleScript(FileConstants.CLIENT_SCRIPTS_DIR, "ClientMain.groovy",
                "//priority=0\n" +
                        "import io.github.luckymcdev.groovyengine.GE;\n" +
                        "GE.LOG.info('Hello from client scripts!')\n" +
                        "// Add your client-side script logic here"
        );

        createSampleScript(FileConstants.SERVER_SCRIPTS_DIR, "ServerMain.groovy",
                "//priority=0\n" +
                        "import io.github.luckymcdev.groovyengine.GE;\n" +
                        "GE.LOG.info('Hello from server scripts!')\n" +
                        "// Add your server-side script logic here"
        );
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

        // Check if all required directories exist
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