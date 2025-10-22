package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.PostProcessChain;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.ShaderProgram;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.ShaderUtils;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.shader.FragmentShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.shader.VertexShader;
import net.minecraft.client.Minecraft;

public class ShaderEffectTest {

    // Simple fullscreen vertex shader - just passes through position and UV
    private static final String FULLSCREEN_VERTEX = """
            #version 330 core
            
            layout (location = 0) in vec2 aPos;
            layout (location = 1) in vec2 aTexCoord;
            
            out vec2 texCoord;
            
            void main() {
                gl_Position = vec4(aPos, 0.0, 1.0);
                texCoord = aTexCoord;
            }
            """;

    // Cool chromatic aberration + vignette effect
    private static final String CHROMATIC_FRAGMENT = """
            #version 330 core
            
            in vec2 texCoord;
            out vec4 fragColor;
            
            uniform sampler2D screenTexture;
            uniform float time;
            uniform float aberrationAmount;
            uniform float vignetteStrength;
            
            void main() {
                vec2 uv = texCoord;
            
                // Chromatic aberration - offset RGB channels
                vec2 offset = (uv - 0.5) * aberrationAmount;
            
                float r = texture(screenTexture, uv - offset).r;
                float g = texture(screenTexture, uv).g;
                float b = texture(screenTexture, uv + offset).b;
            
                vec3 color = vec3(r, g, b);
            
                // Vignette effect
                vec2 vignetteUV = uv * (1.0 - uv.yx);
                float vignette = vignetteUV.x * vignetteUV.y * 15.0;
                vignette = pow(vignette, vignetteStrength);
            
                color *= vignette;
            
                fragColor = vec4(color, 1.0);
            }
            """;

    // Wavy distortion effect
    private static final String WAVE_FRAGMENT = """
            #version 330 core
            
            in vec2 texCoord;
            out vec4 fragColor;
            
            uniform sampler2D screenTexture;
            uniform float time;
            uniform float waveStrength;
            uniform float waveFrequency;
            
            void main() {
                vec2 uv = texCoord;
            
                // Create wave distortion
                float wave = sin(uv.y * waveFrequency + time) * waveStrength;
                uv.x += wave;
            
                // Add vertical wave too for more interesting effect
                wave = cos(uv.x * waveFrequency * 0.8 + time * 0.7) * waveStrength * 0.5;
                uv.y += wave;
            
                vec3 color = texture(screenTexture, uv).rgb;
            
                fragColor = vec4(color, 1.0);
            }
            """;

    // RGB split effect with scanlines
    private static final String GLITCH_FRAGMENT = """
            #version 330 core
            
            in vec2 texCoord;
            out vec4 fragColor;
            
            uniform sampler2D screenTexture;
            uniform float time;
            uniform float glitchAmount;
            
            float random(vec2 co) {
                return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
            }
            
            void main() {
                vec2 uv = texCoord;
            
                // Random glitch lines
                float glitchLine = step(0.98, random(vec2(floor(uv.y * 100.0), floor(time * 10.0))));
                float glitchOffset = (random(vec2(floor(time * 5.0))) - 0.5) * glitchAmount * glitchLine;
            
                // RGB split
                float r = texture(screenTexture, uv + vec2(glitchOffset, 0.0)).r;
                float g = texture(screenTexture, uv).g;
                float b = texture(screenTexture, uv - vec2(glitchOffset, 0.0)).b;
            
                vec3 color = vec3(r, g, b);
            
                // Scanlines
                float scanline = sin(uv.y * 800.0) * 0.1;
                color -= scanline;
            
                fragColor = vec4(color, 1.0);
            }
            """;

    private static final String DEPTH_VISUALIZATION_FRAGMENT = """
            #version 330 core
            
            in vec2 texCoord;
            out vec4 fragColor;
            
            uniform sampler2D screenTexture;
            uniform sampler2D depthTexture;
            uniform float nearPlane;
            uniform float farPlane;
            uniform int visualizationMode; // 0: Linear, 1: Non-linear, 2: Heat map
            
            float linearizeDepth(float depth) {
                float z = depth * 2.0 - 1.0; // Back to NDC
                return (2.0 * nearPlane * farPlane) / (farPlane + nearPlane - z * (farPlane - nearPlane));
            }
            
            vec3 heatMap(float value) {
                vec3 color = vec3(0.0);
            
                // Red to yellow
                if (value < 0.5) {
                    color.r = 1.0;
                    color.g = value * 2.0;
                }
                // Yellow to green
                else if (value < 0.75) {
                    color.r = 2.0 - value * 2.0;
                    color.g = 1.0;
                }
                // Green to blue
                else {
                    color.g = 4.0 - value * 4.0;
                    color.b = value * 4.0 - 3.0;
                }
            
                return color;
            }
            
            void main() {
                float depth = texture(depthTexture, texCoord).r;
                vec3 color = texture(screenTexture, texCoord).rgb;
            
                if (visualizationMode == 0) {
                    // Linear depth visualization
                    float linearDepth = linearizeDepth(depth) / farPlane;
                    fragColor = vec4(vec3(linearDepth), 1.0);
                }
                else if (visualizationMode == 1) {
                    // Non-linear depth (raw depth buffer)
                    fragColor = vec4(vec3(depth), 1.0);
                }
                else if (visualizationMode == 2) {
                    // Heat map visualization
                    float linearDepth = linearizeDepth(depth) / farPlane;
                    fragColor = vec4(heatMap(linearDepth), 1.0);
                }
                else {
                    // Overlay depth on original scene
                    float linearDepth = linearizeDepth(depth) / farPlane;
                    vec3 depthColor = heatMap(linearDepth);
                    fragColor = vec4(mix(color, depthColor, 0.7), 1.0);
                }
            }
            """;

    private static ShaderProgram chromaticShader;
    private static ShaderProgram waveShader;
    private static ShaderProgram glitchShader;
    private static ShaderProgram depthVisualizationShader;

    private static ShaderUtils.PostProcessHelper depthVisualizationHelper;
    private static ShaderUtils.PostProcessHelper chromaticHelper;
    private static ShaderUtils.PostProcessHelper waveHelper;
    private static ShaderUtils.PostProcessHelper glitchHelper;

    private static boolean initialized = false;

    /**
     * Initialize all test shaders - MUST be called on render thread
     * This is automatically called on first use, no manual initialization needed
     */
    private static void initialize() {
        if (initialized) return;

        RenderSystem.assertOnRenderThread();

        chromaticShader = new ShaderProgram()
                .addShader(VertexShader.createFullscreen())
                .addShader(new FragmentShader(CHROMATIC_FRAGMENT))
                .link();

        waveShader = new ShaderProgram()
                .addShader(VertexShader.createFullscreen())
                .addShader(new FragmentShader(WAVE_FRAGMENT))
                .link();

        glitchShader = new ShaderProgram()
                .addShader(VertexShader.createFullscreen())
                .addShader(new FragmentShader(GLITCH_FRAGMENT))
                .link();

        // Create helpers for easy rendering
        chromaticHelper = new ShaderUtils.PostProcessHelper(chromaticShader);
        waveHelper = new ShaderUtils.PostProcessHelper(waveShader);
        glitchHelper = new ShaderUtils.PostProcessHelper(glitchShader);

        initialized = true;
    }

    /**
     * Render chromatic aberration effect with default settings
     */
    public static void renderChromaticAberration() {
        renderChromaticAberration(0.02f, 0.4f);
    }

    /**
     * Render chromatic aberration effect with time-based animation
     */
    public static void renderChromaticAberration(float aberrationAmount, float vignetteStrength) {
        if (!initialized) initialize();

        chromaticHelper.execute(shader -> {
            ShaderUtils.UniformPresets.setChromaticAberrationUniforms(shader, aberrationAmount, vignetteStrength);
        });
    }

    /**
     * Render wave distortion effect with default settings
     */
    public static void renderWaveEffect() {
        renderWaveEffect(0.01f, 20.0f);
    }

    /**
     * Render wave distortion effect
     */
    public static void renderWaveEffect(float strength, float frequency) {
        if (!initialized) initialize();

        waveHelper.execute(shader -> {
            ShaderUtils.UniformPresets.setWaveUniforms(shader, strength, frequency);
        });
    }

    /**
     * Render glitch effect with default intensity
     */
    public static void renderGlitchEffect() {
        renderGlitchEffect(0.05f);
    }

    /**
     * Render glitch effect with custom intensity
     */
    public static void renderGlitchEffect(float intensity) {
        if (!initialized) initialize();

        glitchHelper.execute(shader -> {
            ShaderUtils.UniformPresets.setGlitchUniforms(shader, intensity);
        });
    }

    private static void initializeDepthVisualization() {
        if (depthVisualizationShader != null) return;

        RenderSystem.assertOnRenderThread();

        depthVisualizationShader = new ShaderProgram()
                .addShader(VertexShader.createFullscreen())
                .addShader(new FragmentShader(DEPTH_VISUALIZATION_FRAGMENT))
                .link();

        depthVisualizationHelper = new ShaderUtils.PostProcessHelper(depthVisualizationShader);
    }

    /**
     * Render depth buffer visualization
     */
    public static void renderDepthVisualization(int depthTexture, int visualizationMode) {
        if (!initialized) initialize();
        initializeDepthVisualization();

        depthVisualizationHelper.execute(shader -> {
            // Bind depth texture to texture unit 1
            RenderSystem.activeTexture(com.mojang.blaze3d.platform.GlConst.GL_TEXTURE1);
            RenderSystem.bindTexture(depthTexture);
            RenderSystem.activeTexture(com.mojang.blaze3d.platform.GlConst.GL_TEXTURE0);

            shader.setUniform("depthTexture", 1);
            shader.setUniform("visualizationMode", visualizationMode);

            shader.setUniform("nearPlane", 0.05f);
            shader.setUniform("farPlane", (float) Minecraft.getInstance().options.getEffectiveRenderDistance() * 16.0f);
        });
    }

    /**
     * Create a PostProcessChain effect for depth visualization
     */
    public static PostProcessChain.PostProcessEffect createDepthVisualizationEffect(int depthTexture, int visualizationMode) {
        return () -> {
            if (!initialized) initialize();
            initializeDepthVisualization();

            depthVisualizationShader.bind();

            // Set common uniforms
            ShaderUtils.CommonUniforms.setScreenUniforms(depthVisualizationShader);

            // Bind depth texture
            RenderSystem.activeTexture(com.mojang.blaze3d.platform.GlConst.GL_TEXTURE1);
            RenderSystem.bindTexture(depthTexture);
            RenderSystem.activeTexture(com.mojang.blaze3d.platform.GlConst.GL_TEXTURE0);

            depthVisualizationShader.setUniform("depthTexture", 1);
            depthVisualizationShader.setUniform("visualizationMode", visualizationMode);

            depthVisualizationShader.setUniform("nearPlane", 0.05f);
            depthVisualizationShader.setUniform("farPlane", (float) Minecraft.getInstance().options.getEffectiveRenderDistance() * 16.0f);

            depthVisualizationShader.drawFullscreenQuad();
        };
    }

    /**
     * Clean up all shader resources - call on shutdown
     */
    public static void dispose() {
        if (chromaticShader != null) {
            chromaticShader.dispose();
            chromaticShader = null;
        }
        if (waveShader != null) {
            waveShader.dispose();
            waveShader = null;
        }
        if (glitchShader != null) {
            glitchShader.dispose();
            glitchShader = null;
        }
        if (depthVisualizationShader != null) {
            depthVisualizationShader.dispose();
            depthVisualizationShader = null;
        }
        initialized = false;
    }

    /**
     * Create a PostProcessChain effect for chromatic aberration
     */
    public static PostProcessChain.PostProcessEffect createChromaticEffect(float aberrationAmount, float vignetteStrength) {
        return () -> {
            if (!initialized) initialize();
            chromaticShader.bind();
            ShaderUtils.CommonUniforms.setScreenUniforms(chromaticShader);
            ShaderUtils.CommonUniforms.setTimeUniforms(chromaticShader);
            ShaderUtils.UniformPresets.setChromaticAberrationUniforms(chromaticShader, aberrationAmount, vignetteStrength);
            chromaticShader.drawFullscreenQuad();
        };
    }

    /**
     * Create a PostProcessChain effect for wave distortion
     */
    public static PostProcessChain.PostProcessEffect createWaveEffect(float strength, float frequency) {
        return () -> {
            if (!initialized) initialize();
            waveShader.bind();
            ShaderUtils.CommonUniforms.setScreenUniforms(waveShader);
            ShaderUtils.CommonUniforms.setTimeUniforms(waveShader);
            ShaderUtils.UniformPresets.setWaveUniforms(waveShader, strength, frequency);
            waveShader.drawFullscreenQuad();
        };
    }

    /**
     * Create a PostProcessChain effect for glitch
     */
    public static PostProcessChain.PostProcessEffect createGlitchEffect(float intensity) {
        return () -> {
            if (!initialized) initialize();
            glitchShader.bind();
            ShaderUtils.CommonUniforms.setScreenUniforms(glitchShader);
            ShaderUtils.CommonUniforms.setTimeUniforms(glitchShader);
            ShaderUtils.UniformPresets.setGlitchUniforms(glitchShader, intensity);
            glitchShader.drawFullscreenQuad();
        };
    }
}