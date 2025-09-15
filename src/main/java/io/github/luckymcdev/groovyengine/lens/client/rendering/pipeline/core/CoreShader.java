package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

public abstract class CoreShader {
    private static final Minecraft MC = Minecraft.getInstance();

    protected static ShaderInstance shader;

    /**
     * Example: "foo:bar" points to shaders/core/bar.json
     */
    public abstract ResourceLocation getShaderLocation();

    public abstract VertexFormat getVertexFormat();

    public abstract void setUniforms();

    public void init(ShaderInstance instance) {
        this.shader = instance;
        setUniforms();
    }

    public static ShaderInstance getShader() {
        return shader;
    }

    public ShaderInstance register(RegisterShadersEvent event) throws IOException {
        shader = new ShaderInstance(event.getResourceProvider(), getShaderLocation(), getVertexFormat());

        event.registerShader(
                shader,
                this::init // store it
        );
        return getShader();
    }

}

