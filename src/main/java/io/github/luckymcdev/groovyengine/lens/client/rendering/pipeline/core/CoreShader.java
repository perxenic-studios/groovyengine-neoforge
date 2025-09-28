package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

/**
 * Base class for registering core shaders.
 * Handles registration and initialization only.
 */
@OnlyIn(Dist.CLIENT)
public abstract class CoreShader {
    protected ShaderInstance shader;

    // Force subclasses to expose a static INSTANCE
    public abstract ResourceLocation getShaderLocation();
    public abstract VertexFormat getVertexFormat();

    // Called after shader is created and assigned
    public abstract void init();

    private void _init(ShaderInstance instance) {
        this.shader = instance;
        init();
    }

    public ShaderInstance getShader() {
        return shader;
    }

    public final void register(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(), getShaderLocation(), getVertexFormat()),
                this::_init
        );
    }

    public void bind() {
        RenderSystem.setShader(() -> getShader());
    }

    public void update() {

    }
}

