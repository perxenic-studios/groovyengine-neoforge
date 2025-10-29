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

package io.github.luckymcdev.groovyengine.core.systems.packs.loader;

import net.minecraft.server.packs.PackType;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public record PackContentType(boolean data, boolean resources) {

    private static final PackContentType INVALID = new PackContentType(false, false);

    /**
     * Attempts to determine the type of the given pack candidate file.
     * <p>
     * This method will check if the given file is a regular file, and if so, attempts to open it as a ZIP archive.
     * If the file is an archive, it will check if the archive contains a pack.mcmeta file, and if it does,
     * it will return a new PackContentType instance with the data and resources directories set accordingly.
     * <p>
     * If the file is a directory, it will check if the directory contains a pack.mcmeta file, and if it does,
     * it will return a new PackContentType instance with the data and resources directories set accordingly.
     * <p>
     * If the file is neither a regular file nor a directory, or if it does not contain a pack.mcmeta file,
     * this method will return the INVALID PackContentType instance.
     *
     * @param filePath The file candidate to load.
     * @return The pack type of the file, or the INVALID pack type if the file is invalid.
     */
    public static PackContentType from(Path filePath) {
        // Archive
        if (Files.isRegularFile(filePath)) {
            try (FileSystem fs = FileSystems.newFileSystem(filePath)) {
                if (Files.isRegularFile(fs.getPath("pack.mcmeta"))) {
                    return new PackContentType(Files.isDirectory(fs.getPath("data/")), Files.isDirectory(fs.getPath("assets/")));
                }
            } catch (IOException e) {
                // no-op
            }
        }
        // Folder
        else if (Files.isDirectory(filePath)) {
            if (Files.isRegularFile(filePath.resolve("pack.mcmeta"))) {
                return new PackContentType(Files.isDirectory(filePath.resolve("data")), Files.isDirectory(filePath.resolve("assets")));
            }
        }
        return INVALID;
    }

    /**
     * Attempts to determine the type of the given resources directory.
     * <p>
     * This method will check if the given path is a directory, and if so, attempts to check if the directory
     * contains a "data" or "assets" subdirectory. If either of these directories exist, it will return a new
     * PackContentType instance with the data and resources directories set accordingly.
     * <p>
     * If the path is not a directory, or if neither "data" nor "assets" exist, this method will return the
     * INVALID pack type.
     *
     * @param resourcesPath The resources directory to check.
     * @return The pack type of the resources directory, or the INVALID pack type if the resources directory is invalid.
     */
    public static PackContentType fromResourcesDirectory(Path resourcesPath) {
        if (Files.isDirectory(resourcesPath)) {
            boolean hasData = Files.isDirectory(resourcesPath.resolve("data"));
            boolean hasAssets = Files.isDirectory(resourcesPath.resolve("assets"));

            // Create a virtual pack.mcmeta if either data or assets exist
            if (hasData || hasAssets) {
                return new PackContentType(hasData, hasAssets);
            }
        }
        return INVALID;
    }

    public boolean isFor(PackType type) {
        return (type == PackType.SERVER_DATA && this.data) || (type == PackType.CLIENT_RESOURCES && this.resources);
    }
}