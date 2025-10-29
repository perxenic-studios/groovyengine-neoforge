package io.github.luckymcdev.groovyengine.threads.api.attachments.global;

import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.List;

/**
 * Attachment for recipe generation during data generation.
 */
public abstract class RecipeAttachment implements GlobalAttachment<Void> {

    /**
     * Called during recipe generation
     */
    public void onGenerate(net.minecraft.data.recipes.RecipeOutput recipeOutput) {
    }
}
