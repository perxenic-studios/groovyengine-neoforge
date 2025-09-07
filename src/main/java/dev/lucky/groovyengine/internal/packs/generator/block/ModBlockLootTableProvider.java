package dev.lucky.groovyengine.internal.packs.generator.block;


import dev.perxenic.acidapi.api.datagen.AcidBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockLootTableProvider extends AcidBlockLootTableProvider {
    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(registries);
    }


    //TODO: Fix, this is just temp for not crashing
    public static final DeferredRegister<Block> TEMPORAROY =
            DeferredRegister.create(Registries.BLOCK, "temp");

    @Override
    protected DeferredRegister<Block> getBlockRegistry() {
        return TEMPORAROY;
    }

    @Override
    protected void generate() {
    }
}