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

package io.github.luckymcdev.groovyengine.core.systems.packs.generator;

import dev.perxenic.acidapi.api.datagen.DataGeneratorHelper;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.block.AutoBlockLootTableProvider;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.block.AutoBlockStateProvider;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.block.AutoBlockTagProvider;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.item.AutoItemModelProvider;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.item.AutoItemTagProvider;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.recipe.AutoRecipeProvider;
import io.github.luckymcdev.groovyengine.core.systems.structure.FileConstants;
import net.minecraft.SharedConstants;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.registries.VanillaRegistries;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class GroovyDatagen {
    private static final Path outputPath = FileConstants.RESOURCES_DIR;
    private static final boolean includeClient = FMLEnvironment.dist.isClient();

    /**
     * The instance of the data generator helper.
     * This instance is used to generate data for the mod.
     */
    public static final DataGeneratorHelper INSTANCE = new DataGeneratorHelper(
            true,
            includeClient,
            new DataGenerator(outputPath, SharedConstants.getCurrentVersion(), includeClient),
            new ExistingFileHelper(
                    Collections.emptyList(),
                    Collections.emptySet(),
                    false,
                    null,
                    null
            ),
            CompletableFuture.completedFuture(VanillaRegistries.createLookup())
    );

    /**
     * Starts the data generator runtime.
     * This method is responsible for calling the {@link DataGeneratorHelper#run()} method on the {@link #INSTANCE} instance.
     * It is called automatically by the module system when the server is starting.
     * Modules should not call this method manually, as it may interfere with the module system's internal workings.
     *
     * @throws IOException If an exception occurs while running the data generator.
     */
    public static void run() throws IOException {
        GE.CORE_LOG.info("Starting Datagen Runtime");
        addAuto();
        INSTANCE.run();
    }

    /**
     * Automatically adds all the necessary data generators to the helper.
     * This method is used internally by the data generator runtime to add all the necessary data generators.
     * It is not recommended for modules to call this method manually, as it may interfere with the module system's internal workings.
     */
    private static void addAuto() {
        INSTANCE.addBlockStateProvider(AutoBlockStateProvider::new);
        INSTANCE.addBlockTagsProvider(AutoBlockTagProvider::new);
        INSTANCE.addLootTableProvider(AutoBlockLootTableProvider::new);
        INSTANCE.addItemTagsProvider(AutoItemTagProvider::new);
        INSTANCE.addItemModelProvider(AutoItemModelProvider::new);
        INSTANCE.addRecipeProvider(AutoRecipeProvider::new);
    }
}