package io.github.luckymcdev.groovyengine.lens.client.rendering.material;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.state.PipelineState;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record Material(String name, ShaderInstance shader, ResourceLocation texture, PipelineState pipeline,
                       VertexFormat format, VertexFormat.Mode vertexMode, int bufferSize, boolean affectsCrumbling,
                       boolean sortOnUpload) {

    // Simplified constructor with defaults
    public Material(ShaderInstance shader, ResourceLocation texture, PipelineState pipeline) {
        this("material_" + (texture != null ? texture.getPath() : "unnamed"),
                shader, texture, pipeline,
                DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256,
                false, false);
    }

    /**
     * Convert this Material to a Minecraft RenderType
     */
    public RenderType renderType() {
        RenderType.CompositeState.CompositeStateBuilder stateBuilder = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> shader))
                .setTransparencyState(pipeline.toTransparencyState())
                .setDepthTestState(pipeline.toDepthState())
                .setCullState(pipeline.cullFaces() ? RenderStateShard.CULL : RenderStateShard.NO_CULL)
                .setLightmapState(pipeline.toLightmapState())
                .setOverlayState(pipeline.toOverlayState())
                .setWriteMaskState(pipeline.toWriteMaskState());

        // Only add texture state if we have a texture
        if (texture != null) {
            stateBuilder.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false));
        } else {
            stateBuilder.setTextureState(RenderStateShard.NO_TEXTURE);
        }

        return RenderType.create(name, format, vertexMode, bufferSize,
                affectsCrumbling, sortOnUpload, stateBuilder.createCompositeState(true));
    }

    public Material withName(String newName) {
        return new Material(newName, shader, texture, pipeline, format, vertexMode, bufferSize, affectsCrumbling, sortOnUpload);
    }

    public Material withShader(ShaderInstance newShader) {
        return new Material(name, newShader, texture, pipeline, format, vertexMode, bufferSize, affectsCrumbling, sortOnUpload);
    }

    public Material withTexture(ResourceLocation newTexture) {
        return new Material(name, shader, newTexture, pipeline, format, vertexMode, bufferSize, affectsCrumbling, sortOnUpload);
    }

    public Material withPipeline(PipelineState newPipeline) {
        return new Material(name, shader, texture, newPipeline, format, vertexMode, bufferSize, affectsCrumbling, sortOnUpload);
    }

    public Material withFormat(VertexFormat newFormat) {
        return new Material(name, shader, texture, pipeline, newFormat, vertexMode, bufferSize, affectsCrumbling, sortOnUpload);
    }

    public Material withVertexMode(VertexFormat.Mode newMode) {
        return new Material(name, shader, texture, pipeline, format, newMode, bufferSize, affectsCrumbling, sortOnUpload);
    }

    public Material withBufferSize(int newSize) {
        return new Material(name, shader, texture, pipeline, format, vertexMode, newSize, affectsCrumbling, sortOnUpload);
    }
}