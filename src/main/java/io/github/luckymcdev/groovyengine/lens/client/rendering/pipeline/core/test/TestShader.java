package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core.test;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core.CoreShader;
import net.minecraft.resources.ResourceLocation;

public class TestShader extends CoreShader {
    private Uniform color;

    @Override
    public ResourceLocation getShaderLocation() {
        return ResourceLocation.fromNamespaceAndPath(GE.MODID, "test");
    }

    @Override
    public VertexFormat getVertexFormat() {
        return DefaultVertexFormat.POSITION_COLOR;
    }

    @Override
    public void setUniforms() {
        this.color = shader.getUniform("ColorModulator");
    }

    public void updateUniforms() {
        if (color != null) {
            color.set(0f, 1f, 0f, 1f); // turn it green dynamically
        }
    }
}

