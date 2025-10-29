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

package io.github.luckymcdev.groovyengine.lens.client.rendering.material;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.state.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class MaterialBuilder {
    private final PipelineStateBuilder pipelineBuilder = PipelineState.builder();
    private String name;
    private ResourceLocation texture;
    private ShaderInstance shader = GameRenderer.getRendertypeSolidShader();
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

    /**
     * Sets the name of this material.
     *
     * @param name The name to set.
     * @return This builder instance.
     */
    public MaterialBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the texture of this material.
     *
     * @param texture The texture to set.
     * @return This builder instance.
     */
    public MaterialBuilder texture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    /**
     * Sets the shader of this material.
     *
     * @param shader The shader to set.
     * @return This builder instance.
     */
    public MaterialBuilder shader(ShaderInstance shader) {
        this.shader = shader;
        return this;
    }

    /**
     * Sets the vertex format of this material.
     * The vertex format determines what data is available in each vertex of this material.
     *
     * @param format The vertex format to set.
     * @return This builder instance.
     */
    public MaterialBuilder format(VertexFormat format) {
        this.format = format;
        return this;
    }

    /**
     * Sets the vertex mode of this material.
     * The vertex mode determines how the vertices of this material are rendered.
     *
     * @param mode The vertex mode to set.
     * @return This builder instance.
     */
    public MaterialBuilder vertexMode(VertexFormat.Mode mode) {
        this.vertexMode = mode;
        return this;
    }


    /**
     * Sets the size of the vertex buffer of this material.
     * The vertex buffer size is the number of vertices that can be stored in the vertex buffer.
     * A larger buffer size means that more vertices can be stored in the buffer, but it also means that more memory is used.
     *
     * @param size The size of the vertex buffer to set.
     * @return This builder instance.
     */
    public MaterialBuilder bufferSize(int size) {
        this.bufferSize = size;
        return this;
    }

    /**
     * Sets whether this material affects the crumbling shader.
     * The crumbling shader is a shader that is used to simulate the appearance of crumbling blocks.
     * If this material affects the crumbling shader, then the shader will be used when rendering it.
     * If this material does not affect the crumbling shader, then it will not be used when rendering it.
     *
     * @param affects Whether this material affects the crumbling shader.
     * @return This builder instance.
     */
    public MaterialBuilder affectsCrumbling(boolean affects) {
        this.affectsCrumbling = affects;
        return this;
    }

    /**
     * Sets whether this material should sort its vertex buffer on upload.
     * Sorting the vertex buffer on upload can be useful for optimizing rendering performance.
     * If this material should sort its vertex buffer on upload, then the buffer will be sorted when it is uploaded.
     * If this material does not sort its vertex buffer on upload, then it will not be sorted when it is uploaded.
     *
     * @param sort Whether this material should sort its vertex buffer on upload.
     * @return This builder instance.
     */
    public MaterialBuilder sortOnUpload(boolean sort) {
        this.sortOnUpload = sort;
        return this;
    }

    public MaterialBuilder setupPipeline(Consumer<PipelineStateBuilder> builder) {
        builder.accept(this.pipelineBuilder);
        return this;
    }

    // Common shader shortcuts
    public MaterialBuilder solidShader() {
        return shader(GameRenderer.getRendertypeSolidShader());
    }

    public MaterialBuilder cutoutShader() {
        return shader(GameRenderer.getRendertypeCutoutShader());
    }

    public MaterialBuilder translucentShader() {
        return shader(GameRenderer.getRendertypeTranslucentShader());
    }

    public MaterialBuilder entityShader() {
        return shader(GameRenderer.getRendertypeEntitySolidShader());
    }

    public MaterialBuilder guiShader() {
        return shader(GameRenderer.getRendertypeGuiShader());
    }

    public Material build() {
        PipelineState pipeline = pipelineBuilder.build();
        return new Material(name, shader, texture, pipeline, format, vertexMode, bufferSize, affectsCrumbling, sortOnUpload);
    }
}