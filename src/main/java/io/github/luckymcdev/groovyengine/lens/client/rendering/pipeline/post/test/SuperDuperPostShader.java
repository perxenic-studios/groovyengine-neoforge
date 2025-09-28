package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.PostProcessShader;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class SuperDuperPostShader extends PostProcessShader {
    public static final SuperDuperPostShader INSTANCE = new SuperDuperPostShader();

    @Override
    public ResourceLocation getPostChainLocation() {
        return GE.id("super_duper");
    }

    @Override
    public void beforeProcess(Matrix4f viewModelMatrix) {

    }

    @Override
    public void afterProcess() {

    }
}
