package io.github.luckymcdev.groovyengine.core.systems.packs.generator.unified;

import dev.perxenic.acidapi.api.datagen.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredRegister;

@Deprecated
public class ModUnifiedDataProvider extends UnifiedDataProvider {

    @Override
    protected DeferredRegister<Block> getBlockRegistry() {
        return null;
    }

    @Override
    protected String getModId() {
        return "";
    }

    @Override
    protected void generateBlockLoot(AcidBlockLootTableProvider provider) {

    }

    @Override
    protected void generateBlockTags(AcidBlockTagProvider provider, HolderLookup.Provider lookupProvider) {
    }

    @Override
    protected void generateBlockStates(AcidBlockStateProvider provider) {

    }

    @Override
    protected void generateItemModels(AcidItemModelProvider provider) {

    }

    @Override
    protected void generateItemTags(AcidItemTagProvider provider, HolderLookup.Provider lookupProvider) {

    }

    @Override
    protected void generateRecipes(AcidRecipeProvider provider, RecipeOutput recipeOutput) {

    }
}
