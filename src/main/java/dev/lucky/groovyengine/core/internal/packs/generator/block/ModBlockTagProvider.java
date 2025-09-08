package dev.lucky.groovyengine.core.internal.packs.generator.block;


import dev.perxenic.acidapi.api.datagen.AcidBlockTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static dev.lucky.groovyengine.core.GE.MODID;

public class ModBlockTagProvider extends AcidBlockTagProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.ANVIL).add(Blocks.DARK_OAK_LEAVES);
    }
}