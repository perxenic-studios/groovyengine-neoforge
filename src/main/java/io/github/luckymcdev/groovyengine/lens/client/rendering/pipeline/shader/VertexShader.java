package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.shader;

public class VertexShader extends Shader {
    public VertexShader(String source) {
        super(ShaderType.VERTEX, source);
    }

    public VertexShader() {
        super(ShaderType.VERTEX);
    }

    /**
     * Create a basic fullscreen vertex shader
     */
    public static VertexShader createFullscreen() {
        String source = """
                #version 330 core
                layout (location = 0) in vec2 aPos;
                layout (location = 1) in vec2 aTexCoord;
                out vec2 texCoord;
                void main() {
                    gl_Position = vec4(aPos, 0.0, 1.0);
                    texCoord = aTexCoord;
                }
                """;
        return new VertexShader(source);
    }
}