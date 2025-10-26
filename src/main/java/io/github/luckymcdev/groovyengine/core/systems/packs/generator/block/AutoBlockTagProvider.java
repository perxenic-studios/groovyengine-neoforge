package io.github.luckymcdev.groovyengine.core.systems.packs.generator.block;


import dev.perxenic.acidapi.api.datagen.AcidBlockTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static io.github.luckymcdev.groovyengine.GE.MODID;

public class AutoBlockTagProvider extends AcidBlockTagProvider {
    public AutoBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.ANVIL).add(Blocks.DARK_OAK_LEAVES);
    }
}