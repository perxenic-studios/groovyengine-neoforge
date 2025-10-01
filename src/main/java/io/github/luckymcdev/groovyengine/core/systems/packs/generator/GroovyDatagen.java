package io.github.luckymcdev.groovyengine.core.systems.packs.generator;

import io.github.luckymcdev.groovyengine.GE;
import dev.perxenic.acidapi.api.datagen.DataGeneratorHelper;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.unified.UnifiedDataProvider;
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

    public static void run() throws IOException {
        GE.CORE_LOG.info("Starting Datagen Runtime");
        INSTANCE.run();
    }

    public static void addUnified(UnifiedDataProvider provider) {
        INSTANCE.addBlockStateProvider(provider::createBlockStateProvider);
        INSTANCE.addBlockTagsProvider(provider::createBlockTagProvider);
        INSTANCE.addLootTableProvider(provider::createBlockLootTableProvider);
        INSTANCE.addItemTagsProvider(provider::createItemTagProvider);
        INSTANCE.addItemModelProvider(provider::createItemModelProvider);
        INSTANCE.addRecipeProvider(provider::createRecipeProvider);
    }
}