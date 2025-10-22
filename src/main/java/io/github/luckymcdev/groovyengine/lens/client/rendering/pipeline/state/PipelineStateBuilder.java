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

    public PipelineStateBuilder depthTest(boolean enabled) {
        this.depthTest = enabled;
        return this;
    }

    public PipelineStateBuilder depthWrite(boolean enabled) {
        this.depthWrite = enabled;
        return this;
    }

    public PipelineStateBuilder cullFaces(boolean enabled) {
        this.cullFaces = enabled;
        return this;
    }

    public PipelineStateBuilder blendMode(BlendMode mode) {
        this.blendMode = mode;
        return this;
    }

    public PipelineStateBuilder lightmap(boolean enabled) {
        this.lightmap = enabled;
        return this;
    }

    public PipelineStateBuilder overlay(boolean enabled) {
        this.overlay = enabled;
        return this;
    }

    public PipelineStateBuilder transparency(TransparencyMode mode) {
        this.transparency = mode;
        return this;
    }

    public PipelineStateBuilder writeMask(WriteMask mask) {
        this.writeMask = mask;
        return this;
    }

    public PipelineStateBuilder opaque() {
        return blendMode(BlendMode.OPAQUE).transparency(TransparencyMode.NONE);
    }

    public PipelineStateBuilder translucent() {
        return blendMode(BlendMode.ALPHA_BLEND).transparency(TransparencyMode.TRANSLUCENT);
    }

    public PipelineStateBuilder additive() {
        return blendMode(BlendMode.ADDITIVE).transparency(TransparencyMode.ADDITIVE);
    }

    public PipelineStateBuilder noCull() {
        return cullFaces(false);
    }

    public PipelineStateBuilder noDepthTest() {
        return depthTest(false);
    }

    public PipelineStateBuilder noDepthWrite() {
        return depthWrite(false);
    }

    public PipelineState build() {
        return new PipelineState(depthTest, depthWrite, cullFaces, blendMode, lightmap, overlay, transparency, writeMask);
    }
}
