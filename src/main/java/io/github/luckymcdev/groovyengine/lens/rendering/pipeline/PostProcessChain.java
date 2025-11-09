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

package io.github.luckymcdev.groovyengine.lens.rendering.pipeline;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.luckymcdev.groovyengine.lens.rendering.fbo.ExtendedFBO;
import io.github.luckymcdev.groovyengine.lens.rendering.pipeline.shader.FragmentShader;
import io.github.luckymcdev.groovyengine.lens.rendering.pipeline.shader.VertexShader;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages chaining multiple post-processing effects together using ping-pong buffers
 */
public class PostProcessChain {

    private static final String BLIT_VERTEX = """
            #version 330 core
            
            layout (location = 0) in vec2 aPos;
            layout (location = 1) in vec2 aTexCoord;
            
            out vec2 texCoord;
            
            void main() {
                gl_Position = vec4(aPos, 0.0, 1.0);
                texCoord = aTexCoord;
            }
            """;
    private static final String BLIT_FRAGMENT = """
            #version 330 core
            
            in vec2 texCoord;
            out vec4 fragColor;
            
            uniform sampler2D screenTexture;
            
            void main() {
                fragColor = texture(screenTexture, texCoord);
            }
            """;
    private final List<PostProcessEffect> effects = new ArrayList<>();
    private ExtendedFBO pingBuffer;
    private ExtendedFBO pongBuffer;
    private boolean initialized = false;
    private int currentWidth = -1;
    private int currentHeight = -1;
    private ShaderProgram blitShader;

    /**
     * Initialize or resize the ping-pong buffers
     */
    private void initializeBuffers() {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getMainRenderTarget().width;
        int height = mc.getMainRenderTarget().height;

        // Initialize blit shader if needed
        if (blitShader == null) {
            blitShader = new ShaderProgram()
                    .addShader(new VertexShader(BLIT_VERTEX))
                    .addShader(new FragmentShader(BLIT_FRAGMENT))
                    .link();
        }

        // Check if we need to resize
        if (initialized && currentWidth == width && currentHeight == height) {
            return;
        }

        // Clean up old buffers
        if (pingBuffer != null) {
            pingBuffer.destroyBuffers();
        }
        if (pongBuffer != null) {
            pongBuffer.destroyBuffers();
        }

        // Create new buffers
        pingBuffer = new ExtendedFBO(true);
        pongBuffer = new ExtendedFBO(true);

        pingBuffer.resize(width, height, Minecraft.ON_OSX);
        pongBuffer.resize(width, height, Minecraft.ON_OSX);

        pingBuffer.setClearColor(0, 0, 0, 0);
        pongBuffer.setClearColor(0, 0, 0, 0);

        currentWidth = width;
        currentHeight = height;
        initialized = true;
    }

    /**
     * Add an effect to the chain
     */
    public PostProcessChain addEffect(PostProcessEffect effect) {
        effects.add(effect);
        return this;
    }

    /**
     * Clear all effects from the chain
     */
    public void clearEffects() {
        effects.clear();
    }

    /**
     * Execute the entire post-processing chain
     */
    public void execute() {
        if (effects.isEmpty()) {
            return;
        }

        RenderSystem.assertOnRenderThread();
        initializeBuffers();

        Minecraft mc = Minecraft.getInstance();
        RenderTarget mainTarget = mc.getMainRenderTarget();

        // Start with main framebuffer as input
        RenderTarget source = mainTarget;
        ExtendedFBO destination = pingBuffer;

        // Save the original viewport
        int originalViewportX = GlStateManager.Viewport.x();
        int originalViewportY = GlStateManager.Viewport.y();
        int originalViewportWidth = GlStateManager.Viewport.width();
        int originalViewportHeight = GlStateManager.Viewport.height();

        // Process each effect
        for (int i = 0; i < effects.size(); i++) {
            PostProcessEffect effect = effects.get(i);

            // Bind destination framebuffer
            destination.bindWrite(true);

            // Clear destination
            GlStateManager._clearColor(0, 0, 0, 0);
            GlStateManager._clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, false);

            // Set viewport to match buffer size
            GlStateManager._viewport(0, 0, destination.width, destination.height);

            // Bind source texture
            RenderSystem.activeTexture(33984); // GL_TEXTURE0
            RenderSystem.bindTexture(source.getColorTextureId());

            // Apply the effect
            effect.apply();

            // Unbind shader
            ShaderProgram.unbind();

            // Swap buffers for next pass
            if (i < effects.size() - 1) {
                // For all but the last effect, swap ping-pong
                RenderTarget temp = source;
                source = destination;
                destination = (destination == pingBuffer) ? pongBuffer : pingBuffer;
            }
            // For the last effect, we keep the result in the current destination
        }

        // Final blit: Copy the result back to the main framebuffer
        mainTarget.bindWrite(false);
        GlStateManager._viewport(originalViewportX, originalViewportY, originalViewportWidth, originalViewportHeight);

        // Bind the final result texture (from the destination buffer)
        RenderSystem.activeTexture(33984);
        RenderSystem.bindTexture(destination.getColorTextureId());

        // Simple blit shader (passthrough)
        blitToScreen();

        // Restore state
        mainTarget.bindWrite(true);
    }

    /**
     * Simple passthrough blit to copy the final result to screen
     */
    private void blitToScreen() {
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask(false);
        GlStateManager._disableBlend();

        // Use our blit shader
        blitShader.bind();
        blitShader.setUniform("screenTexture", 0);
        blitShader.drawFullscreenQuad();
        ShaderProgram.unbind();

        GlStateManager._enableDepthTest();
        GlStateManager._depthMask(true);
    }

    /**
     * Clean up resources
     */
    public void dispose() {
        if (pingBuffer != null) {
            pingBuffer.destroyBuffers();
            pingBuffer = null;
        }
        if (pongBuffer != null) {
            pongBuffer.destroyBuffers();
            pongBuffer = null;
        }
        if (blitShader != null) {
            blitShader.dispose();
            blitShader = null;
        }
        initialized = false;
    }

    /**
     * Get the number of effects in the chain
     */
    public int getEffectCount() {
        return effects.size();
    }

    /**
     * Check if the chain has any effects
     */
    public boolean hasEffects() {
        return !effects.isEmpty();
    }

    /**
     * Abstract class for a post-processing effect
     */
    public static abstract class PostProcessEffect {
        /**
         * Apply the effect. The input texture is already bound to texture unit 0.
         * Just bind your shader, set uniforms, and draw the fullscreen quad.
         */
        public abstract void apply();
    }
}
