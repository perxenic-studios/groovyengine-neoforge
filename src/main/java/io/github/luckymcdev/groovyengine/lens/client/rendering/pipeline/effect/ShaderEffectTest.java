package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.PostProcessChain;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.ShaderProgram;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.ShaderUtils;

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

    private static ShaderProgram chromaticShader;
    private static ShaderProgram waveShader;
    private static ShaderProgram glitchShader;

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
                .addShader(ShaderProgram.ShaderType.VERTEX, FULLSCREEN_VERTEX)
                .addShader(ShaderProgram.ShaderType.FRAGMENT, CHROMATIC_FRAGMENT)
                .link();

        waveShader = new ShaderProgram()
                .addShader(ShaderProgram.ShaderType.VERTEX, FULLSCREEN_VERTEX)
                .addShader(ShaderProgram.ShaderType.FRAGMENT, WAVE_FRAGMENT)
                .link();

        glitchShader = new ShaderProgram()
                .addShader(ShaderProgram.ShaderType.VERTEX, FULLSCREEN_VERTEX)
                .addShader(ShaderProgram.ShaderType.FRAGMENT, GLITCH_FRAGMENT)
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