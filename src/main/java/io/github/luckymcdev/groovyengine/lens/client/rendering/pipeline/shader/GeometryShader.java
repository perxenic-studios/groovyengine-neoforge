package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.shader;

public class GeometryShader extends Shader {
    public GeometryShader(String source) {
        super(ShaderType.GEOMETRY, source);
    }

    public GeometryShader() {
        super(ShaderType.GEOMETRY);
    }
}