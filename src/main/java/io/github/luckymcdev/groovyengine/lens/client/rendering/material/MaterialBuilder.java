package io.github.luckymcdev.groovyengine.lens.client.rendering.material;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.state.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MaterialBuilder {
    private String name;
    private ResourceLocation texture;
    private ShaderInstance shader = GameRenderer.getRendertypeSolidShader();
    private PipelineStateBuilder pipelineBuilder = PipelineState.builder();
    private VertexFormat format = DefaultVertexFormat.BLOCK;
    private VertexFormat.Mode vertexMode = VertexFormat.Mode.QUADS;
    private int bufferSize = 256;
    private boolean affectsCrumbling = false;
    private boolean sortOnUpload = false;

    public MaterialBuilder(String name) {
        this.name = name;
    }

    public MaterialBuilder(ResourceLocation texture) {
        this.texture = texture;
        this.name = "material_" + texture.getPath();
    }

    public MaterialBuilder name(String name) { this.name = name; return this; }
    public MaterialBuilder texture(ResourceLocation texture) { this.texture = texture; return this; }
    public MaterialBuilder shader(ShaderInstance shader) { this.shader = shader; return this; }
    public MaterialBuilder format(VertexFormat format) { this.format = format; return this; }
    public MaterialBuilder vertexMode(VertexFormat.Mode mode) { this.vertexMode = mode; return this; }
    public MaterialBuilder bufferSize(int size) { this.bufferSize = size; return this; }
    public MaterialBuilder affectsCrumbling(boolean affects) { this.affectsCrumbling = affects; return this; }
    public MaterialBuilder sortOnUpload(boolean sort) { this.sortOnUpload = sort; return this; }

    // Pipeline configuration methods
    public MaterialBuilder depthTest(boolean enabled) { pipelineBuilder.depthTest(enabled); return this; }
    public MaterialBuilder depthWrite(boolean enabled) { pipelineBuilder.depthWrite(enabled); return this; }
    public MaterialBuilder cullFaces(boolean enabled) { pipelineBuilder.cullFaces(enabled); return this; }
    public MaterialBuilder blendMode(BlendMode mode) { pipelineBuilder.blendMode(mode); return this; }
    public MaterialBuilder lightmap(boolean enabled) { pipelineBuilder.lightmap(enabled); return this; }
    public MaterialBuilder overlay(boolean enabled) { pipelineBuilder.overlay(enabled); return this; }
    public MaterialBuilder transparency(TransparencyMode mode) { pipelineBuilder.transparency(mode); return this; }
    public MaterialBuilder writeMask(WriteMask mask) { pipelineBuilder.writeMask(mask); return this; }

    // Convenience methods
    public MaterialBuilder opaque() { pipelineBuilder.opaque(); return this; }
    public MaterialBuilder translucent() { pipelineBuilder.translucent(); return this; }
    public MaterialBuilder additive() { pipelineBuilder.additive(); return this; }
    public MaterialBuilder noCull() { pipelineBuilder.noCull(); return this; }
    public MaterialBuilder noDepthTest() { pipelineBuilder.noDepthTest(); return this; }
    public MaterialBuilder noDepthWrite() { pipelineBuilder.noDepthWrite(); return this; }

    // Common shader shortcuts
    public MaterialBuilder solidShader() { return shader(GameRenderer.getRendertypeSolidShader()); }
    public MaterialBuilder cutoutShader() { return shader(GameRenderer.getRendertypeCutoutShader()); }
    public MaterialBuilder translucentShader() { return shader(GameRenderer.getRendertypeTranslucentShader()); }
    public MaterialBuilder entityShader() { return shader(GameRenderer.getRendertypeEntitySolidShader()); }
    public MaterialBuilder guiShader() { return shader(GameRenderer.getRendertypeGuiShader()); }

    public Material build() {
        PipelineState pipeline = pipelineBuilder.build();
        return new Material(name, shader, texture, pipeline, format, vertexMode, bufferSize, affectsCrumbling, sortOnUpload);
    }
}