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

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.core.systems.structure.FileConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.function.Consumer;

public class GroovyEngineRepositorySource implements RepositorySource {

    private static final PackSource SOURCE = PackSource.create(packText -> packText, true);
    private final PackType type;
    private final File modulesLocation; // Changed from packLocation to modulesLocation

    public GroovyEngineRepositorySource(PackType type) {
        this.type = type;
        this.modulesLocation = FileConstants.MODULES_DIR.toFile(); // Point to the modules directory

        GE.CORE_LOG.info("GroovyEngine modules location for {}: {}", type.name(), modulesLocation.getAbsolutePath());
    }

    /**
     * Loads all packs from the modules directory and calls the given consumer for each valid pack.
     * The consumer will be called with each pack that is successfully loaded from a module.
     *
     * @param consumer The consumer to call for each valid pack.
     */
    @Override
    public void loadPacks(@NotNull Consumer<Pack> consumer) {
        GE.CORE_LOG.info("Scan started for {} in modules directory: {}", type.name(), modulesLocation.getAbsolutePath());
        final long startTime = System.nanoTime();

        int validPackCount = 0;
        if (!modulesLocation.exists() || !modulesLocation.isDirectory()) {
            GE.CORE_LOG.warn("Modules directory does not exist or is not a directory: {}", modulesLocation.getAbsolutePath());
            final long endTime = System.nanoTime();
            GE.CORE_LOG.info("Located {} packs. Took {}ms.", validPackCount, GE.DECIMAL_2.format((endTime - startTime) / 1000000d));
            return;
        }

        File[] moduleDirs = modulesLocation.listFiles(File::isDirectory);
        if (moduleDirs != null) {
            for (File moduleDir : moduleDirs) {
                validPackCount += loadFromModule(consumer, moduleDir);
            }
        }

        final long endTime = System.nanoTime();
        GE.CORE_LOG.info("Located {} packs. Took {}ms.", validPackCount, GE.DECIMAL_2.format((endTime - startTime) / 1000000d));
    }

    /**
     * Loads a pack from a specific module directory and calls the given consumer if valid.
     *
     * @param consumer The consumer to call for the valid pack.
     * @param moduleDir The directory of the module to load.
     * @return 1 if a valid pack was found and loaded, 0 otherwise.
     */
    private int loadFromModule(@NotNull Consumer<Pack> consumer, File moduleDir) {
        // Each module directory should contain a 'resources' subdirectory
        File moduleResourcesDir = new File(moduleDir, "resources");

        if (!moduleResourcesDir.exists() || !moduleResourcesDir.isDirectory()) {
            GE.CORE_LOG.debug("Module '{}' does not contain a 'resources' directory or it's not a directory. Skipping.", moduleDir.getName());
            return 0;
        }

        // Create pack.mcmeta if it doesn't exist in the module's resources directory
        File mcmetaFile = new File(moduleResourcesDir, "pack.mcmeta");
        if (!mcmetaFile.exists()) {
            try {
                String mcmetaContent = "{\n" +
                        "  \"pack\": {\n" +
                        "    \"pack_format\": 48,\n" +
                        "    \"description\": \"GroovyEngine Module Resources: " + moduleDir.getName() + "\"\n" +
                        "  }\n" +
                        "}";
                Files.write(mcmetaFile.toPath(), mcmetaContent.getBytes());
                GE.CORE_LOG.info("Created pack.mcmeta file for module '{}' at: {}", moduleDir.getName(), mcmetaFile.getAbsolutePath());
            } catch (IOException e) {
                GE.CORE_LOG.error("Failed to create pack.mcmeta file for module '{}'", moduleDir.getName(), e);
                return 0;
            }
        }

        PackContentType contentType = PackContentType.fromResourcesDirectory(moduleResourcesDir.toPath());

        if (contentType.isFor(this.type)) {
            final PackLocationInfo locationInfo = new PackLocationInfo(
                    GE.MODID + "/" + moduleDir.getName() + "/" + type.name().toLowerCase(),
                    Component.literal("GroovyEngine Module: " + moduleDir.getName() + " (" + type.name() + ")"),
                    SOURCE,
                    Optional.empty()
            );
            final PackSelectionConfig selectionConfig = new PackSelectionConfig(true, Pack.Position.TOP, false);

            Pack pack = Pack.readMetaAndCreate(
                    locationInfo,
                    PackFileType.FOLDER.createPackSupplier(moduleResourcesDir),
                    this.type,
                    selectionConfig
            );

            if (pack != null) {
                consumer.accept(pack);
                GE.CORE_LOG.info("Successfully loaded GroovyEngine pack for module '{}' ({})", moduleDir.getName(), type.name());
                return 1;
            } else {
                GE.CORE_LOG.warn("Failed to create pack for module '{}' ({})", moduleDir.getName(), type.name());
            }
        } else {
            GE.CORE_LOG.info("No content found for pack type {} in module '{}'", type.name(), moduleDir.getName());
        }

        return 0;
    }
}
