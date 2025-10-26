package io.github.luckymcdev.groovyengine.core.systems.packs.generator.item;

import dev.perxenic.acidapi.api.datagen.AcidItemModelProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static io.github.luckymcdev.groovyengine.GE.MODID;

public class AutoItemModelProvider extends AcidItemModelProvider {
    public AutoItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MODID, existingFileHelper);
    }

    private String name(ItemLike item) {
        return item.asItem().getDescriptionId().replace("item." + modid + ".", "");
    }

    @Override
    protected void registerModels() {

    }
}