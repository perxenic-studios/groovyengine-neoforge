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