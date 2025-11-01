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
 *
 * Last modified: 2025-11-01 14:36:25
 */

package io.github.luckymcdev.groovyengine.core.systems.packs.generator;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * See AcidApi, thanks to @Auseawesome.
 */
public class DataGeneratorHelper {
    private final boolean isServer;
    private final boolean isClient;
    private final DataGenerator generator;
    private final PackOutput packOutput;
    private final ExistingFileHelper existingFileHelper;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;
    private BlockTagsProvider blockTagsProvider;

    public DataGeneratorHelper(boolean isServer, boolean isClient, DataGenerator generator, ExistingFileHelper existingFileHelper, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.isServer = isServer;
        this.isClient = isClient;
        this.generator = generator;
        this.packOutput = generator.getPackOutput();
        this.existingFileHelper = existingFileHelper;
        this.lookupProvider = lookupProvider;
    }

    public DataGeneratorHelper(GatherDataEvent event) {
        this(event.includeServer(), event.includeClient(), event.getGenerator(), event.getExistingFileHelper(), event.getLookupProvider());
    }

    public void addProvider(boolean run, DataProvider provider) {
        this.generator.addProvider(run, provider);
    }

    public void addServerProvider(DataProvider provider) {
        this.addProvider(this.isServer, provider);
    }

    public void addClientProvider(DataProvider provider) {
        this.addProvider(this.isClient, provider);
    }

    public void addLootTableProvider(Function<HolderLookup.Provider, LootTableSubProvider> constructor) {
        this.addServerProvider(new LootTableProvider(this.packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(constructor, LootContextParamSets.BLOCK)), this.lookupProvider));
    }

    public void addRecipeProvider(BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, RecipeProvider> constructor) {
        this.addServerProvider(constructor.apply(this.packOutput, this.lookupProvider));
    }

    public void addBlockTagsProvider(Function3<PackOutput, CompletableFuture<HolderLookup.Provider>, ExistingFileHelper, BlockTagsProvider> constructor) {
        this.blockTagsProvider = constructor.apply(this.packOutput, this.lookupProvider, this.existingFileHelper);
        this.addServerProvider(this.blockTagsProvider);
    }

    public void addItemTagsProvider(Function4<PackOutput, CompletableFuture<HolderLookup.Provider>, CompletableFuture<TagsProvider.TagLookup<Block>>, ExistingFileHelper, ItemTagsProvider> constructor) {
        this.addServerProvider(constructor.apply(this.packOutput, this.lookupProvider, this.blockTagsProvider.contentsGetter(), this.existingFileHelper));
    }

    public void addDataMapProvider(BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, DataMapProvider> constructor) {
        this.addServerProvider(constructor.apply(this.packOutput, this.lookupProvider));
    }

    public void addItemModelProvider(BiFunction<PackOutput, ExistingFileHelper, ItemModelProvider> constructor) {
        this.addClientProvider(constructor.apply(this.packOutput, this.existingFileHelper));
    }

    public void addBlockStateProvider(BiFunction<PackOutput, ExistingFileHelper, BlockStateProvider> constructor) {
        this.addClientProvider(constructor.apply(this.packOutput, this.existingFileHelper));
    }

    public void run() throws IOException {
        this.generator.run();
    }
}
