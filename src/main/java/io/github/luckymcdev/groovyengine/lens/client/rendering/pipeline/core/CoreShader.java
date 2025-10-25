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

    /**
     * Gets the resource location of the shader.
     *
     * @return The resource location of the shader.
     */
    public abstract ResourceLocation getShaderLocation();

    /**
     * Returns the vertex format for this shader.
     * The vertex format determines what data is available in each vertex of this material.
     *
     * @return The vertex format for this shader.
     */
    public abstract VertexFormat getVertexFormat();

    // Called after shader is created and assigned
    public abstract void init();

    /**
     * Initializes this core shader with the given shader instance.
     * Calls {@link #init()} after setting the shader instance.
     *
     * @param instance The shader instance to initialize this core shader with.
     */
    private void _init(ShaderInstance instance) {
        this.shader = instance;
        init();
    }

    /**
     * Gets the shader instance of this core shader.
     *
     * @return The shader instance of this core shader.
     */
    public ShaderInstance getShader() {
        return shader;
    }

    /**
     * Registers this core shader with the given register shaders event.
     * This method is responsible for registering the shader instance with the event,
     * and for initializing the shader instance after it has been registered.
     *
     * @param event The register shaders event to register this core shader with.
     * @throws IOException If there is an error during registration.
     */
    public final void register(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(), getShaderLocation(), getVertexFormat()),
                this::_init
        );
    }

    /**
     * Binds this core shader to the rendering pipeline.
     * This method is called when this core shader should be used for rendering.
     * It sets the shader instance of this core shader as the current shader instance
     * in the rendering pipeline.
     */
    public void bind() {
        RenderSystem.setShader(() -> getShader());
    }

    public void update() {

    }
}

