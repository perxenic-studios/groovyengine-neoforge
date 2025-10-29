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

package io.github.luckymcdev.groovyengine.core.systems.packs.generator.unified;

import dev.perxenic.acidapi.api.datagen.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Block;
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
