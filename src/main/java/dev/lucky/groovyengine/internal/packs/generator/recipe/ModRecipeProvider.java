package dev.lucky.groovyengine.internal.packs.generator.recipe;

import dev.perxenic.acidapi.common.datagen.AcidRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends AcidRecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
}