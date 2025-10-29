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
     * <p>
     * For a script with priority 0, it is loaded later than a script with priority 1, and so on.
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

