package dev.lucky.groovyengine.internal.packs.generator.block;

import dev.perxenic.acidapi.common.datagen.AcidBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockLootTableProvider extends AcidBlockLootTableProvider {
    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(registries);
    }

    @Override
    protected DeferredRegister<Block> getBlockRegistry() {
        return null;
    }

    @Override
    protected void generate() {
    }
}