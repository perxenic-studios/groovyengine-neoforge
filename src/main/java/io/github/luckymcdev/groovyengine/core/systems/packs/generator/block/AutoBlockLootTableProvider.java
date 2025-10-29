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

package io.github.luckymcdev.groovyengine.core.systems.packs.generator.block;


import dev.perxenic.acidapi.api.datagen.AcidBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AutoBlockLootTableProvider extends AcidBlockLootTableProvider {
    //TODO: Fix, this is just temp for not crashing
    public static final DeferredRegister<Block> TEMPORAROY =
            DeferredRegister.create(Registries.BLOCK, "temp");


    public AutoBlockLootTableProvider(HolderLookup.Provider registries) {
        super(registries);
    }

    @Override
    protected DeferredRegister<Block> getBlockRegistry() {
        return TEMPORAROY;
    }

    @Override
    protected void generate() {
    }
}