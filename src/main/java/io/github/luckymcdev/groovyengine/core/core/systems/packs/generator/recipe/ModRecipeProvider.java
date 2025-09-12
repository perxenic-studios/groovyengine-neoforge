package io.github.luckymcdev.groovyengine.core.core.systems.packs.generator.recipe;


import dev.perxenic.acidapi.api.datagen.AcidRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends AcidRecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
}