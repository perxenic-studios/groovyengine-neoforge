package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.state;

import net.minecraft.client.renderer.RenderStateShard;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PipelineState {
    public final boolean depthTest;
    public final boolean depthWrite;
    public final boolean cullFaces;
    public final BlendMode blendMode;
    public final boolean lightmap;
    public final boolean overlay;
    public final TransparencyMode transparency;
    public final WriteMask writeMask;

    public PipelineState(boolean depthTest, boolean depthWrite, boolean cullFaces, BlendMode blendMode) {
        this(depthTest, depthWrite, cullFaces, blendMode, true, false, TransparencyMode.NONE, WriteMask.COLOR_DEPTH);
    }

    public PipelineState(boolean depthTest, boolean depthWrite, boolean cullFaces, BlendMode blendMode,
                         boolean lightmap, boolean overlay, TransparencyMode transparency, WriteMask writeMask) {
        this.depthTest = depthTest;
        this.depthWrite = depthWrite;
        this.cullFaces = cullFaces;
        this.blendMode = blendMode;
        this.lightmap = lightmap;
        this.overlay = overlay;
        this.transparency = transparency;
        this.writeMask = writeMask;
    }

    public RenderStateShard.TransparencyStateShard toTransparencyState() {
        return switch (transparency) {
            case NONE -> RenderStateShard.NO_TRANSPARENCY;
            case TRANSLUCENT -> RenderStateShard.TRANSLUCENT_TRANSPARENCY;
            case ADDITIVE -> RenderStateShard.ADDITIVE_TRANSPARENCY;
            case GLINT -> RenderStateShard.GLINT_TRANSPARENCY;
            case CRUMBLING -> RenderStateShard.CRUMBLING_TRANSPARENCY;
        };
    }

    public RenderStateShard.DepthTestStateShard toDepthState() {
        return depthTest ? RenderStateShard.LEQUAL_DEPTH_TEST : RenderStateShard.NO_DEPTH_TEST;
    }

    public RenderStateShard.LightmapStateShard toLightmapState() {
        return lightmap ? RenderStateShard.LIGHTMAP : RenderStateShard.NO_LIGHTMAP;
    }

    public RenderStateShard.OverlayStateShard toOverlayState() {
        return overlay ? RenderStateShard.OVERLAY : RenderStateShard.NO_OVERLAY;
    }

    public RenderStateShard.WriteMaskStateShard toWriteMaskState() {
        return switch (writeMask) {
            case COLOR_DEPTH -> RenderStateShard.COLOR_DEPTH_WRITE;
            case COLOR_ONLY -> RenderStateShard.COLOR_WRITE;
            case DEPTH_ONLY -> RenderStateShard.DEPTH_WRITE;
            case NONE -> new RenderStateShard.WriteMaskStateShard(false, false);
        };
    }

    // Builder for easier creation
    public static PipelineStateBuilder builder() {
        return new PipelineStateBuilder();
    }
}