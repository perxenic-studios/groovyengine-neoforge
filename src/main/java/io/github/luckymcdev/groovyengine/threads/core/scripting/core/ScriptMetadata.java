package io.github.luckymcdev.groovyengine.threads.core.scripting.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ScriptMetadata {
    /**
     * Gets the priority of the given script.
     * <p>
     * The priority of a script is the integer value following the "//priority=" string in the script's
     * first non-empty line. If no such string is present, the priority is 0.
     *
     * @param script the script to get the priority from
     * @return the priority of the script
     */
    public static int getPriority(Path script) {
        try {
            List<String> lines = Files.readAllLines(script, StandardCharsets.UTF_8);
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("//priority=")) {
                    return Integer.parseInt(line.substring("//priority=".length()).trim());
                }
                if (!line.isEmpty()) break;
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    /**
     * Checks if the given script is disabled.
     * <p>
     * A script is considered disabled if it contains the string "//disabled" on its first non-empty line.
     * If the script does not contain such a string, or if an I/O error occurs while reading the script,
     * the method returns false.
     *
     * @param script the script to check for being disabled
     * @return true if the script is disabled, false otherwise
     */
    public static boolean isDisabled(Path script) {
        try {
            List<String> lines = Files.readAllLines(script, StandardCharsets.UTF_8);
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.equalsIgnoreCase("//disabled")) return true;
                break;
            }
        } catch (IOException ignored) {
        }
        return false;
    }
}

