package dev.lucky.groovyengine.internal.packs.generator.block;

import dev.perxenic.acidapi.common.datagen.AcidBlockStateProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static dev.lucky.groovyengine.GE.MODID;

public class ModBlockStateProvider extends AcidBlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

    }
}