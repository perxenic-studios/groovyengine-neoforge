package io.github.luckymcdev.groovyengine.lens.client.rendering.core;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.luckymcdev.groovyengine.lens.client.rendering.material.Material;
import io.github.luckymcdev.groovyengine.lens.client.rendering.material.Materials;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core.test.TestShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.state.BlendMode;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.state.PipelineState;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.state.TransparencyMode;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.state.WriteMask;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GeMaterials {
    public static final PipelineState STATIC = new PipelineState(
            true,  // depthTest
            true,  // depthWrite
            true,  // cullFaces
            BlendMode.OPAQUE,  // no blending
            false, // lightmap
            false, // overlay
            TransparencyMode.NONE,  // no transparency
            WriteMask.COLOR_DEPTH // write both color and depth
    );

    public static final PipelineState V2 = new PipelineState(
            true,  // depthTest
            true,  // depthWrite
            false,  // cullFaces
            BlendMode.OPAQUE,  // no blending
            true, // lightmap
            false, // overlay
            TransparencyMode.NONE,  // no transparency
            WriteMask.COLOR_DEPTH // write both color and depth
    );

    /**
     * Default Pipeline for OBJ rendering (approved by Lio)
     */
    public static final PipelineState OBJ_PIPELINE = new PipelineState(
            true,  // depthTest
            true,  // depthWrite
            false,  // cullFaces
            BlendMode.OPAQUE,  // no blending
            true, // lightmap
            false, // overlay
            TransparencyMode.NONE,  // no transparency
            WriteMask.COLOR_DEPTH // write both color and depth
    );


    public static final Material CUSTOM_QUADS = Materials.builder("custom_quads")
            .texture(ResourceLocation.withDefaultNamespace("textures/block/dirt.png"))
            .shader(TestShader.INSTANCE.getShader())
            .transparency(TransparencyMode.NONE)
            .lightmap(false)
            .overlay(false)
            .format(DefaultVertexFormat.POSITION_TEX)
            .vertexMode(VertexFormat.Mode.QUADS)
            .bufferSize(256)
            .build().withPipeline(STATIC);

    public static final Material DEBUG_TRIANGLES = Materials.builder("debug_triangles")
            .shader(GameRenderer.getPositionColorLightmapShader())
            .format(DefaultVertexFormat.POSITION_COLOR_LIGHTMAP)
            .vertexMode(VertexFormat.Mode.TRIANGLES)
            .bufferSize(256)
            .transparency(TransparencyMode.TRANSLUCENT)
            .cullFaces(false)
            .build()
            .withPipeline(V2);

    public static final Material OBJ_MODEL = Materials.builder("obj_model")
            .shader(GameRenderer.getRendertypeEntitySolidShader())
            .format(DefaultVertexFormat.NEW_ENTITY)
            .vertexMode(VertexFormat.Mode.QUADS)
            .bufferSize(256)
            .texture(ResourceLocation.withDefaultNamespace("textures/block/white_concrete.png"))
            .build().withPipeline(OBJ_PIPELINE);
}