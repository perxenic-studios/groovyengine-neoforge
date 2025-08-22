package dev.lucky.groovyengine.internal.packs.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lucky.groovyengine.GE;
import dev.lucky.groovyengine.internal.packs.generator.block.ModBlockStateProvider;
import dev.lucky.groovyengine.internal.packs.generator.block.ModBlockTagProvider;
import dev.lucky.groovyengine.internal.packs.generator.item.ModItemModelProvider;
import dev.lucky.groovyengine.internal.packs.generator.item.ModItemTagProvider;
import dev.lucky.groovyengine.internal.packs.generator.recipe.ModRecipeProvider;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class GroovyDatagen {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

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

        PackOutput packOutput = generator.getPackOutput();

        /*
        generator.addProvider(true, (DataProvider.Factory<DataProvider>) output ->
                new ModBlockLootTableProvider(lookupProvider.join()));
        */ // TODO: ModBlockLootTableProvider is broken

        generator.addProvider(true, (DataProvider.Factory<DataProvider>) output ->
                new ModRecipeProvider(output, lookupProvider));

        generator.addProvider(true, (DataProvider.Factory<DataProvider>) output ->
                new ModBlockTagProvider(output, lookupProvider, existingFileHelper));

        generator.addProvider(true, (DataProvider.Factory<DataProvider>) output ->
                new ModItemTagProvider(output, lookupProvider, CompletableFuture.completedFuture(null), existingFileHelper));

        generator.addProvider(true, (DataProvider.Factory<DataProvider>) output ->
                new ModItemModelProvider(output, existingFileHelper));

        generator.addProvider(true, (DataProvider.Factory<DataProvider>) output ->
                new ModBlockStateProvider(output, existingFileHelper));

        generator.run();
    }
}