package dev.lucky.groovyengine.internal.packs.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lucky.groovyengine.GE;
import dev.lucky.groovyengine.internal.packs.generator.block.*;
import dev.lucky.groovyengine.internal.packs.generator.item.*;
import dev.lucky.groovyengine.internal.packs.generator.recipe.*;
import dev.perxenic.acidapi.common.datagen.DataGeneratorHelper;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GroovyDatagen {

    public static void run() throws IOException {

        GE.LOG.info("Starting Datagen Runtime");
        Path outputPath = new File(FMLPaths.GAMEDIR.get().toFile(),
                GE.MODID + "/src/resources").toPath();
        boolean includeClient = true;

        DataGenerator generator = new DataGenerator(outputPath, SharedConstants.getCurrentVersion(), includeClient);

        CompletableFuture<HolderLookup.Provider> lookupProvider = CompletableFuture.completedFuture(VanillaRegistries.createLookup());
        ExistingFileHelper existingFileHelper = new ExistingFileHelper(
                Collections.emptyList(),
                Collections.emptySet(),
                false,
                null,
                null
        );

        DataGeneratorHelper generatorHelper = new DataGeneratorHelper(true, true, generator, existingFileHelper, lookupProvider);

        generatorHelper.addLootTableProvider(ModBlockLootTableProvider::new);

        generatorHelper.addRecipeProvider(ModRecipeProvider::new);

        generatorHelper.addBlockTagsProvider(ModBlockTagProvider::new);

        generatorHelper.addItemTagsProvider(ModItemTagProvider::new);

        generatorHelper.addItemModelProvider(ModItemModelProvider::new);

        generatorHelper.addBlockStateProvider(ModBlockStateProvider::new);

        generator.run();
    }
}