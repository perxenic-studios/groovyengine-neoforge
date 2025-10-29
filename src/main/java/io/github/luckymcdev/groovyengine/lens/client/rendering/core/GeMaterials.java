/*
 * Copyright 2025 LuckyMcDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.luckymcdev.groovyengine.lens.client.rendering.core;

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
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GeMaterials {
    /**
     * Default Pipeline for static rendering
     */
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

    /**
     * Default Pipeline for V2 rendering
     */
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


    /**
     * Default Material for custom quads
     */
    public static final Material CUSTOM_QUADS = Materials.builder("custom_quads")
            .texture(ResourceLocation.withDefaultNamespace("textures/block/dirt.png"))
            .shader(TestShader.INSTANCE.getShader())
            .format(DefaultVertexFormat.POSITION_TEX)
            .vertexMode(VertexFormat.Mode.QUADS)
            .bufferSize(256)
            .build().withPipeline(STATIC);

    /**
     * Default Material for debug triangles
     */
    public static final Material DEBUG_TRIANGLES = Materials.builder("debug_triangles")
            .shader(GameRenderer.getPositionColorLightmapShader())
            .format(DefaultVertexFormat.POSITION_COLOR_LIGHTMAP)
            .vertexMode(VertexFormat.Mode.TRIANGLES)
            .bufferSize(256)
            .build()
            .withPipeline(V2);

    /**
     * Default Material for OBJ models (approved by Lio)
     */
    public static final Material OBJ_MODEL = Materials.builder("obj_model")
            .shader(GameRenderer.getRendertypeEntitySolidShader())
            .format(DefaultVertexFormat.NEW_ENTITY)
            .vertexMode(VertexFormat.Mode.QUADS)
            .bufferSize(256)
            .texture(ResourceLocation.withDefaultNamespace("textures/block/white_concrete.png"))
            .build().withPipeline(OBJ_PIPELINE);
}