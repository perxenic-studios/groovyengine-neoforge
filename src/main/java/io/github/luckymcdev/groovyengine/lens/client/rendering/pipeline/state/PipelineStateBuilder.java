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

package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.state;

public class PipelineStateBuilder {
    private boolean depthTest = true;
    private boolean depthWrite = true;
    private boolean cullFaces = true;
    private BlendMode blendMode = BlendMode.OPAQUE;
    private boolean lightmap = true;
    private boolean overlay = false;
    private TransparencyMode transparency = TransparencyMode.NONE;
    private WriteMask writeMask = WriteMask.COLOR_DEPTH;

    /**
     * Enables or disables depth testing for the pipeline.
     *
     * @param enabled Whether to enable depth testing.
     * @return This builder.
     */
    public PipelineStateBuilder depthTest(boolean enabled) {
        this.depthTest = enabled;
        return this;
    }

    /**
     * Enables or disables depth writing for the pipeline.
     * This controls whether the pipeline writes to the depth buffer.
     *
     * @param enabled Whether to enable depth writing.
     * @return This builder.
     */
    public PipelineStateBuilder depthWrite(boolean enabled) {
        this.depthWrite = enabled;
        return this;
    }


    /**
     * Enables or disables face culling for the pipeline.
     * Face culling is used to remove back-facing polygons from being rendered.
     * This can be useful for reducing the amount of geometry rendered, but may also
     * result in some visual artifacts.
     *
     * @param enabled Whether to enable face culling.
     * @return This builder.
     */
    public PipelineStateBuilder cullFaces(boolean enabled) {
        this.cullFaces = enabled;
        return this;
    }

    /**
     * Sets the blend mode for the pipeline.
     * The blend mode determines how the pixels of this pipeline are blended with the pixels of the underlying render target.
     *
     * @param mode The blend mode to set.
     * @return This builder.
     */
    public PipelineStateBuilder blendMode(BlendMode mode) {
        this.blendMode = mode;
        return this;
    }

    /**
     * Enables or disables lightmap rendering for the pipeline.
     * Lightmap rendering is a technique used to visualize the lighting information of a scene.
     * This can be useful for debugging lighting issues.
     *
     * @param enabled Whether to enable lightmap rendering.
     * @return This builder.
     */
    public PipelineStateBuilder lightmap(boolean enabled) {
        this.lightmap = enabled;
        return this;
    }

    /**
     * Enables or disables overlay rendering for the pipeline.
     * Overlay rendering is a technique used to render a pipeline on top of another render target.
     * This can be useful for rendering GUI elements or other visual information on top of a scene.
     *
     * @param enabled Whether to enable overlay rendering.
     * @return This builder.
     */
    public PipelineStateBuilder overlay(boolean enabled) {
        this.overlay = enabled;
        return this;
    }

    /**
     * Sets the transparency mode for the pipeline.
     * The transparency mode determines how the pipeline is composited with the underlying render target.
     *
     * @param mode The transparency mode to set.
     * @return This builder.
     */
    public PipelineStateBuilder transparency(TransparencyMode mode) {
        this.transparency = mode;
        return this;
    }

    /**
     * Sets the write mask for the pipeline.
     * The write mask determines which buffers the pipeline is allowed to write to.
     *
     * @param mask The write mask to set.
     * @return This builder.
     */
    public PipelineStateBuilder writeMask(WriteMask mask) {
        this.writeMask = mask;
        return this;
    }

    /**
     * Creates a pipeline state with opaque blending and no transparency.
     * This pipeline state is suitable for rendering opaque objects such as solid objects or terrain.
     *
     * @return A pipeline state with opaque blending and no transparency.
     */
    public PipelineStateBuilder opaque() {
        return blendMode(BlendMode.OPAQUE).transparency(TransparencyMode.NONE);
    }

    /**
     * Creates a pipeline state with alpha blending and translucent transparency.
     * This pipeline state is suitable for rendering translucent objects such as transparent blocks or water.
     *
     * @return A pipeline state with alpha blending and translucent transparency.
     */
    public PipelineStateBuilder translucent() {
        return blendMode(BlendMode.ALPHA_BLEND).transparency(TransparencyMode.TRANSLUCENT);
    }

    /**
     * Creates a pipeline state with additive blending and translucent transparency.
     * This pipeline state is suitable for rendering translucent objects such as transparent blocks or water.
     *
     * @return A pipeline state with additive blending and translucent transparency.
     */
    public PipelineStateBuilder additive() {
        return blendMode(BlendMode.ADDITIVE).transparency(TransparencyMode.ADDITIVE);
    }

    /**
     * Disables face culling for the pipeline.
     *
     * @return This builder.
     */
    public PipelineStateBuilder noCull() {
        return cullFaces(false);
    }

    /**
     * Disables depth testing for the pipeline.
     *
     * @return This builder.
     */
    public PipelineStateBuilder noDepthTest() {
        return depthTest(false);
    }

    /**
     * Disables depth writing for the pipeline.
     * This can be useful for optimizing rendering performance when depth writing is not needed.
     *
     * @return This builder.
     */
    public PipelineStateBuilder noDepthWrite() {
        return depthWrite(false);
    }

    /**
     * Builds a pipeline state from the current configuration.
     *
     * @return A pipeline state with the current configuration.
     */
    public PipelineState build() {
        return new PipelineState(depthTest, depthWrite, cullFaces, blendMode, lightmap, overlay, transparency, writeMask);
    }
}
