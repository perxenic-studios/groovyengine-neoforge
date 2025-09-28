package io.github.luckymcdev.groovyengine.core.systems.packs.generator;

import io.github.luckymcdev.groovyengine.GE;
import dev.perxenic.acidapi.api.datagen.DataGeneratorHelper;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.block.ModBlockLootTableProvider;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.block.ModBlockStateProvider;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.block.ModBlockTagProvider;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.item.ModItemModelProvider;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.item.ModItemTagProvider;
import io.github.luckymcdev.groovyengine.core.systems.packs.generator.recipe.ModRecipeProvider;
import net.minecraft.SharedConstants;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.registries.VanillaRegistries;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class GroovyDatagen {

    public static void run() throws IOException {

        GE.CORE_LOG.info("Starting Datagen Runtime");
        Path outputPath = new File(FMLPaths.GAMEDIR.get().toFile(),
                GE.MODID + "/src/resources").toPath();
        boolean includeClient = FMLEnvironment.dist.isClient();

        DataGeneratorHelper generator = new DataGeneratorHelper(
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

        generator.addLootTableProvider(ModBlockLootTableProvider::new);

        generator.addRecipeProvider(ModRecipeProvider::new);

        generator.addBlockTagsProvider(ModBlockTagProvider::new);

        generator.addItemTagsProvider(ModItemTagProvider::new);

        generator.addItemModelProvider(ModItemModelProvider::new);

        generator.addBlockStateProvider(ModBlockStateProvider::new);

        generator.run();
    }
}