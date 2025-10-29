package io.github.luckymcdev.groovyengine.core.systems.packs.generator.recipe;


import dev.perxenic.acidapi.api.datagen.AcidRecipeProvider;
import io.github.luckymcdev.groovyengine.threads.api.attachments.AttachmentManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AutoRecipeProvider extends AcidRecipeProvider {
    public AutoRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        AttachmentManager.getInstance().getRecipeAttachments().forEach(att -> att.onGenerate(recipeOutput));
    }
}