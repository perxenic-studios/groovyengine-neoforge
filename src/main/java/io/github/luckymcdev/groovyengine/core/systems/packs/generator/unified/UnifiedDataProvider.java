package io.github.luckymcdev.groovyengine.core.systems.packs.generator.unified;

import dev.perxenic.acidapi.api.datagen.AcidBlockLootTableProvider;
import dev.perxenic.acidapi.api.datagen.AcidBlockStateProvider;
import dev.perxenic.acidapi.api.datagen.AcidBlockTagProvider;
import dev.perxenic.acidapi.api.datagen.AcidItemModelProvider;
import dev.perxenic.acidapi.api.datagen.AcidItemTagProvider;
import dev.perxenic.acidapi.api.datagen.AcidRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.concurrent.CompletableFuture;

public abstract class UnifiedDataProvider {

    protected abstract DeferredRegister<Block> getBlockRegistry();

    protected abstract String getModId();

    public class BlockLootTables extends AcidBlockLootTableProvider {
        public BlockLootTables(HolderLookup.Provider registries) {
            super(registries);
        }

        @Override
        protected DeferredRegister<Block> getBlockRegistry() {
            return UnifiedDataProvider.this.getBlockRegistry();
        }

        @Override
        protected void generate() {
            UnifiedDataProvider.this.generateBlockLoot(this);
        }
    }

    protected abstract void generateBlockLoot(AcidBlockLootTableProvider provider);

    public class BlockTags extends AcidBlockTagProvider {
        public BlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, UnifiedDataProvider.this.getModId(), existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            UnifiedDataProvider.this.generateBlockTags(this, provider);
        }
    }

    protected abstract void generateBlockTags(AcidBlockTagProvider provider, HolderLookup.Provider lookupProvider);

    public class BlockStates extends AcidBlockStateProvider {
        public BlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
            super(output, UnifiedDataProvider.this.getModId(), exFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            UnifiedDataProvider.this.generateBlockStates(this);
        }
    }

    protected abstract void generateBlockStates(AcidBlockStateProvider provider);

    public class ItemModels extends AcidItemModelProvider {
        public ItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, UnifiedDataProvider.this.getModId(), existingFileHelper);
        }

        @Override
        protected void registerModels() {
            UnifiedDataProvider.this.generateItemModels(this);
        }
    }

    protected abstract void generateItemModels(AcidItemModelProvider provider);

    public class ItemTags extends AcidItemTagProvider {
        public ItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                        CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, blockTags, UnifiedDataProvider.this.getModId(), existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            UnifiedDataProvider.this.generateItemTags(this, provider);
        }
    }

    protected abstract void generateItemTags(AcidItemTagProvider provider, HolderLookup.Provider lookupProvider);

    public class Recipes extends AcidRecipeProvider {
        public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected void buildRecipes(RecipeOutput recipeOutput) {
            UnifiedDataProvider.this.generateRecipes(this, recipeOutput);
        }
    }

    protected abstract void generateRecipes(AcidRecipeProvider provider, RecipeOutput recipeOutput);

    public BlockLootTables createBlockLootTableProvider(HolderLookup.Provider registries) {
        return new BlockLootTables(registries);
    }

    public BlockTags createBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        return new BlockTags(output, lookupProvider, existingFileHelper);
    }

    public BlockStates createBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        return new BlockStates(output, exFileHelper);
    }

    public ItemModels createItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        return new ItemModels(output, existingFileHelper);
    }

    public ItemTags createItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                          CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        return new ItemTags(output, lookupProvider, blockTags, existingFileHelper);
    }

    public Recipes createRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        return new Recipes(output, registries);
    }
}