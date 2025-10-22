package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.shader;

public class FragmentShader extends Shader {
    public FragmentShader(String source) {
        super(ShaderType.FRAGMENT, source);
    }

    public FragmentShader() {
        super(ShaderType.FRAGMENT);
    }

    /**
     * Create a simple passthrough fragment shader
     */
    public static FragmentShader createPassthrough() {
        String source = """
                #version 330 core
                in vec2 texCoord;
                out vec4 fragColor;
                uniform sampler2D screenTexture;
                void main() {
                    fragColor = texture(screenTexture, texCoord);
                }
                """;
        return new FragmentShader(source);
    }
}