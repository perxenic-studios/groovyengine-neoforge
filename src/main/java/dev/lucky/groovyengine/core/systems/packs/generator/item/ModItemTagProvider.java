package dev.lucky.groovyengine.core.systems.packs.generator.item;


import dev.lucky.groovyengine.GE;
import dev.perxenic.acidapi.api.datagen.AcidItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static dev.lucky.groovyengine.GE.MODID;

public class ModItemTagProvider extends AcidItemTagProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        GE.LOG.info("Starting Tags!");

        tag(ItemTags.CREEPER_DROP_MUSIC_DISCS)
                .add(Items.DIAMOND);

        tag(ItemTags.FOOT_ARMOR)
                .add(Items.DIAMOND);
    }
}