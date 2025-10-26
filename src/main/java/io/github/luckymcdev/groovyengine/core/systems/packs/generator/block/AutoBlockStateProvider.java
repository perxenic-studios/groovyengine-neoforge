package io.github.luckymcdev.groovyengine.core.systems.packs.generator.block;

import dev.perxenic.acidapi.api.datagen.AcidBlockStateProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static io.github.luckymcdev.groovyengine.GE.MODID;

public class AutoBlockStateProvider extends AcidBlockStateProvider {
    public AutoBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

    }
}