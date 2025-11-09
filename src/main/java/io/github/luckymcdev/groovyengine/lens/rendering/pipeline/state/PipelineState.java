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

package io.github.luckymcdev.groovyengine.lens.rendering.pipeline.state;

import net.minecraft.client.renderer.RenderStateShard;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record PipelineState(boolean depthTest, boolean depthWrite, boolean cullFaces, BlendMode blendMode,
                            boolean lightmap, boolean overlay, TransparencyMode transparency, WriteMask writeMask) {
    public PipelineState(boolean depthTest, boolean depthWrite, boolean cullFaces, BlendMode blendMode) {
        this(depthTest, depthWrite, cullFaces, blendMode, true, false, TransparencyMode.NONE, WriteMask.COLOR_DEPTH);
    }

    // Builder for easier creation
    public static PipelineStateBuilder builder() {
        return new PipelineStateBuilder();
    }

    /**
     * Returns a RenderStateShard.TransparencyStateShard based on the given transparency mode
     *
     * @return a RenderStateShard.TransparencyStateShard representing the given transparency mode
     */
    public RenderStateShard.TransparencyStateShard toTransparencyState() {
        return switch (transparency) {
            case NONE -> RenderStateShard.NO_TRANSPARENCY;
            case TRANSLUCENT -> RenderStateShard.TRANSLUCENT_TRANSPARENCY;
            case ADDITIVE -> RenderStateShard.ADDITIVE_TRANSPARENCY;
            case GLINT -> RenderStateShard.GLINT_TRANSPARENCY;
            case CRUMBLING -> RenderStateShard.CRUMBLING_TRANSPARENCY;
        };
    }

    /**
     * Returns a RenderStateShard.DepthTestStateShard based on the given depth test setting
     *
     * @return a RenderStateShard.DepthTestStateShard representing the given depth test setting
     */
    public RenderStateShard.DepthTestStateShard toDepthState() {
        return depthTest ? RenderStateShard.LEQUAL_DEPTH_TEST : RenderStateShard.NO_DEPTH_TEST;
    }

    /**
     * Returns a RenderStateShard.LightmapStateShard representing the given lightmap setting
     *
     * @return a RenderStateShard.LightmapStateShard representing the given lightmap setting
     */
    public RenderStateShard.LightmapStateShard toLightmapState() {
        return lightmap ? RenderStateShard.LIGHTMAP : RenderStateShard.NO_LIGHTMAP;
    }

    /**
     * Returns a RenderStateShard.OverlayStateShard based on the given overlay setting
     *
     * @return a RenderStateShard.OverlayStateShard representing the given overlay setting
     */
    public RenderStateShard.OverlayStateShard toOverlayState() {
        return overlay ? RenderStateShard.OVERLAY : RenderStateShard.NO_OVERLAY;
    }

    /**
     * Returns a RenderStateShard.WriteMaskStateShard based on the given write mask setting
     *
     * @return a RenderStateShard.WriteMaskStateShard representing the given write mask setting
     */
    public RenderStateShard.WriteMaskStateShard toWriteMaskState() {
        return switch (writeMask) {
            case COLOR_DEPTH -> RenderStateShard.COLOR_DEPTH_WRITE;
            case COLOR_ONLY -> RenderStateShard.COLOR_WRITE;
            case DEPTH_ONLY -> RenderStateShard.DEPTH_WRITE;
            case NONE -> new RenderStateShard.WriteMaskStateShard(false, false);
        };
    }
}