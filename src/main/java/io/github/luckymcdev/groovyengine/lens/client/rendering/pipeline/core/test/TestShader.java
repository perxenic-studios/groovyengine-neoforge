package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core.test;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core.CoreShader;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TestShader extends CoreShader {
    public static final TestShader INSTANCE = new TestShader();

    @Override
    public ResourceLocation getShaderLocation() {
        return GE.id("test");
    }

    @Override
    public VertexFormat getVertexFormat() {
        return DefaultVertexFormat.POSITION_TEX;
    }

    @Override
    public void init() {

    }
}