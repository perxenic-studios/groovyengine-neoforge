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

import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;

import java.io.File;
import java.util.Locale;
import java.util.function.Function;

public enum PackFileType {

    /**
     * The file is a valid ZIP archive, such as a ZIP file or a JAR file.
     */
    ARCHIVE(true, FilePackResources.FileResourcesSupplier::new),

    /**
     * The file is a directory that contains a valid pack.mcmeta file.
     */
    FOLDER(true, file -> new PathPackResources.PathResourcesSupplier(file.toPath())),

    /**
     * The file can not be handled by OpenLoader.
     */
    INVALID(false, file -> null);

    private final boolean loadable;
    private final Function<File, Pack.ResourcesSupplier> packSupplier;

    PackFileType(boolean loadable, Function<File, Pack.ResourcesSupplier> packSupplier) {
        this.loadable = loadable;
        this.packSupplier = packSupplier;
    }

    /**
     * Determines the type of the given pack candidate file.
     *
     * @param candidate The file candidate to load.
     * @return The pack type of the file.
     */
    public static PackFileType from(File candidate) {
        if (candidate.isFile() && (endsWithIgnoreCase(candidate.getName(), ".zip") || endsWithIgnoreCase(candidate.getName(), ".jar"))) {
            return ARCHIVE;
        } else if (candidate.isDirectory() && new File(candidate, "pack.mcmeta").isFile()) {
            return FOLDER;
        }
        return INVALID;
    }

    /**
     * Checks if a string ends with another case-insensitive string.
     *
     * @param str    The source string.
     * @param suffix The case-insensitive suffix string.
     * @return If the source string ends with the case-insensitive string.
     */
    private static boolean endsWithIgnoreCase(String str, String suffix) {
        final int suffixLength = suffix.length();
        return str.regionMatches(true, str.length() - suffixLength, suffix, 0, suffixLength);
    }

    /**
     * Determines if packs of this type should be loaded by openloader.
     *
     * @return If OpenLoader can load this type of pack file.
     */
    public boolean isLoadable() {
        return this.loadable;
    }

    /**
     * Gets a displayable name for this type of pack file type.
     *
     * @return The displayable name of the pack file type.
     */
    public String typeName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    /**
     * Creates a resource supplier for the pack file. This differs between the different type of files, for example
     * compressed archives vs directories.
     *
     * @param packFile The file to create a resource supplier for.
     * @return The resource supplier for the pack type.
     */
    public Pack.ResourcesSupplier createPackSupplier(File packFile) {
        return this.packSupplier.apply(packFile);
    }
}