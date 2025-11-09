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

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * Utility class for common shader and post-processing operations
 */
public class ShaderUtils {

    /**
     * Binds the main render target's color texture to texture unit 0
     * Use this before rendering post-processing effects
     */
    public static void bindMainFramebuffer() {
        bindMainFramebuffer(0);
    }

    /**
     * Binds the main render target's color texture to a specific texture unit
     *
     * @param textureUnit The texture unit (0-31)
     */
    public static void bindMainFramebuffer(int textureUnit) {
        Minecraft mc = Minecraft.getInstance();
        int textureId = mc.getMainRenderTarget().getColorTextureId();

        RenderSystem.activeTexture(33984 + textureUnit); // GL_TEXTURE0 + unit
        RenderSystem.bindTexture(textureId);
    }

    /**
     * Gets the main framebuffer's color texture ID
     */
    public static int getMainFramebufferTexture() {
        return Minecraft.getInstance().getMainRenderTarget().getColorTextureId();
    }

    /**
     * Gets the main framebuffer's depth texture ID
     */
    public static int getMainFramebufferDepth() {
        return Minecraft.getInstance().getMainRenderTarget().getDepthTextureId();
    }

    /**
     * Gets the screen width
     */
    public static int getScreenWidth() {
        return Minecraft.getInstance().getMainRenderTarget().width;
    }

    /**
     * Gets the screen height
     */
    public static int getScreenHeight() {
        return Minecraft.getInstance().getMainRenderTarget().height;
    }

    /**
     * Sets common uniforms that most post-processing shaders need
     *
     * @param shader The shader program to set uniforms on
     */
    public static void setCommonUniforms(ShaderProgram shader) {
        shader.setUniform("screenTexture", 0);
        shader.setUniform("time", getTime());
        shader.setUniform("resolution", (float) getScreenWidth(), (float) getScreenHeight());
    }

    /**
     * Gets the current game time in seconds (loops every ~27 hours)
     */
    public static float getTime() {
        return (float) (System.currentTimeMillis() % 100000) / 1000.0f;
    }

    /**
     * Gets the partial tick time for smooth animations
     */
    public static float getPartialTick() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
    }

    /**
     * Creates a simple render target for post-processing
     *
     * @param width  Width of the render target
     * @param height Height of the render target
     * @return The OpenGL framebuffer ID
     */
    public static int createRenderTarget(int width, int height) {
        RenderSystem.assertOnRenderThreadOrInit();

        int framebufferId = GlStateManager.glGenFramebuffers();
        int textureId = GlStateManager._genTexture();

        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferId);

        // Create texture
        GlStateManager._bindTexture(textureId);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        GlStateManager._texImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, null);

        // Attach texture to framebuffer
        GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, textureId, 0);

        // Check framebuffer status
        int status = GlStateManager.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer is not complete! Status: " + status);
        }

        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        return framebufferId;
    }

    /**
     * Functional interface for setting custom uniforms
     */
    @FunctionalInterface
    public interface UniformSetter {
        void setUniforms(ShaderProgram shader);
    }

    /**
     * Helper class to manage common shader uniforms in a structured way
     */
    public static class CommonUniforms {
        public static final String SCREEN_TEXTURE = "screenTexture";
        public static final String TIME = "time";
        public static final String RESOLUTION = "resolution";
        public static final String MOUSE = "mouse";
        public static final String CAMERA_POS = "cameraPos";
        public static final String LOOK_VECTOR = "lookVector";
        public static final String UP_VECTOR = "upVector";
        public static final String LEFT_VECTOR = "leftVector";
        public static final String VIEW_MATRIX = "viewMatrix";
        public static final String PROJECTION_MATRIX = "projectionMatrix";
        public static final String INV_VIEW_MATRIX = "invViewMat";
        public static final String INV_PROJECTION_MATRIX = "invProjMat";
        public static final String NEAR_PLANE = "nearPlaneDistance";
        public static final String FAR_PLANE = "farPlaneDistance";
        public static final String FOV = "fov";
        public static final String ASPECT_RATIO = "aspectRatio";

        /**
         * Set all time-related uniforms
         */
        public static void setTimeUniforms(ShaderProgram shader) {
            shader.setUniform(TIME, getTime());
            shader.setUniform("deltaTime", Minecraft.getInstance().getTimer().getRealtimeDeltaTicks() / 20.0f);
        }

        /**
         * Set all screen-related uniforms
         */
        public static void setScreenUniforms(ShaderProgram shader) {
            shader.setUniform(SCREEN_TEXTURE, 0);
            shader.setUniform(RESOLUTION, (float) getScreenWidth(), (float) getScreenHeight());
            shader.setUniform(ASPECT_RATIO, (float) getScreenWidth() / (float) getScreenHeight());
        }

        /**
         * Set all camera-related uniforms
         */
        public static void setCameraUniforms(ShaderProgram shader) {
            Minecraft mc = Minecraft.getInstance();
            var camera = mc.gameRenderer.getMainCamera();
            var pos = camera.getPosition();

            // Position
            shader.setUniform(CAMERA_POS, (float) pos.x, (float) pos.y, (float) pos.z);

            // Direction vectors
            var lookVec = camera.getLookVector();
            shader.setUniform(LOOK_VECTOR, lookVec.x, lookVec.y, lookVec.z);

            var upVec = camera.getUpVector();
            shader.setUniform(UP_VECTOR, upVec.x, upVec.y, upVec.z);

            var leftVec = camera.getLeftVector();
            shader.setUniform(LEFT_VECTOR, leftVec.x, leftVec.y, leftVec.z);
        }

        /**
         * Set projection-related uniforms (FOV, near/far plane, aspect ratio)
         */
        public static void setProjectionUniforms(ShaderProgram shader) {
            Minecraft mc = Minecraft.getInstance();

            // Near and far plane
            shader.setUniform(NEAR_PLANE, 0.05f); // GameRenderer.PROJECTION_Z_NEAR
            shader.setUniform(FAR_PLANE, mc.gameRenderer.getDepthFar());

            // FOV in radians
            float partialTick = mc.getTimer().getGameTimeDeltaPartialTick(false);
            float fov = (float) Math.toRadians(mc.gameRenderer.getFov(mc.gameRenderer.getMainCamera(), partialTick, true));
            shader.setUniform(FOV, fov);

            // Aspect ratio
            shader.setUniform(ASPECT_RATIO, (float) mc.getWindow().getWidth() / (float) mc.getWindow().getHeight());
        }

        /**
         * Set view and projection matrix uniforms (including inverted versions)
         */
        public static void setMatrixUniforms(ShaderProgram shader) {
            // Projection matrix
            Matrix4f projMat = new Matrix4f(RenderSystem.getProjectionMatrix());
            float[] projArray = new float[16];
            projMat.get(projArray);
            shader.setUniformMatrix4(PROJECTION_MATRIX, false, projArray);

            // Inverted projection matrix
            Matrix4f invProjMat = new Matrix4f(projMat).invert();
            float[] invProjArray = new float[16];
            invProjMat.get(invProjArray);
            shader.setUniformMatrix4(INV_PROJECTION_MATRIX, false, invProjArray);

            // Note: View matrix needs to be passed in or accessed from your rendering system
            // as it's not directly available from RenderSystem
        }

        /**
         * Set view matrix uniforms (requires the view matrix to be passed in)
         */
        public static void setViewMatrixUniforms(ShaderProgram shader, Matrix4f viewMatrix) {
            float[] viewArray = new float[16];
            viewMatrix.get(viewArray);
            shader.setUniformMatrix4(VIEW_MATRIX, false, viewArray);

            // Inverted view matrix
            Matrix4f invViewMat = new Matrix4f(viewMatrix).invert();
            float[] invViewArray = new float[16];
            invViewMat.get(invViewArray);
            shader.setUniformMatrix4(INV_VIEW_MATRIX, false, invViewArray);
        }

        /**
         * Set mouse position uniforms (normalized 0-1)
         */
        public static void setMouseUniforms(ShaderProgram shader) {
            Minecraft mc = Minecraft.getInstance();
            double mouseX = mc.mouseHandler.xpos() / mc.getWindow().getScreenWidth();
            double mouseY = mc.mouseHandler.ypos() / mc.getWindow().getScreenHeight();
            shader.setUniform(MOUSE, (float) mouseX, (float) mouseY);
        }

        /**
         * Set ALL common uniforms at once
         */
        public static void setAllUniforms(ShaderProgram shader) {
            setScreenUniforms(shader);
            setTimeUniforms(shader);
            setCameraUniforms(shader);
            setProjectionUniforms(shader);
            setMatrixUniforms(shader);
        }
    }

    /**
     * Helper class for managing post-processing passes
     */
    public static class PostProcessHelper {
        private final ShaderProgram shader;

        public PostProcessHelper(ShaderProgram shader) {
            this.shader = shader;
        }

        /**
         * Execute a full post-processing pass with common setup
         */
        public void execute() {
            execute(true);
        }

        /**
         * Execute a post-processing pass
         *
         * @param bindMainFramebuffer Whether to automatically bind the main framebuffer texture
         */
        public void execute(boolean bindMainFramebuffer) {
            if (bindMainFramebuffer) {
                ShaderUtils.bindMainFramebuffer();
            }

            shader.bind();
            CommonUniforms.setScreenUniforms(shader);
            CommonUniforms.setTimeUniforms(shader);
            shader.drawFullscreenQuad();
            ShaderProgram.unbind();
        }

        /**
         * Execute with custom uniform setup
         */
        public void execute(UniformSetter uniformSetter) {
            ShaderUtils.bindMainFramebuffer();
            shader.bind();
            CommonUniforms.setScreenUniforms(shader);
            CommonUniforms.setTimeUniforms(shader);
            uniformSetter.setUniforms(shader);
            shader.drawFullscreenQuad();
            ShaderProgram.unbind();
        }
    }

    /**
     * Preset uniform configurations for common effects
     */
    public static class UniformPresets {

        public static void setBlurUniforms(ShaderProgram shader, float blurAmount) {
            shader.setUniform("blurAmount", blurAmount);
        }

        public static void setChromaticAberrationUniforms(ShaderProgram shader, float amount, float vignetteStrength) {
            shader.setUniform("aberrationAmount", amount);
            shader.setUniform("vignetteStrength", vignetteStrength);
        }

        public static void setWaveUniforms(ShaderProgram shader, float strength, float frequency) {
            shader.setUniform("waveStrength", strength);
            shader.setUniform("waveFrequency", frequency);
        }

        public static void setGlitchUniforms(ShaderProgram shader, float intensity) {
            shader.setUniform("glitchAmount", intensity);
        }

        public static void setVignetteUniforms(ShaderProgram shader, float strength, float radius) {
            shader.setUniform("vignetteStrength", strength);
            shader.setUniform("vignetteRadius", radius);
        }

        public static void setColorGradingUniforms(ShaderProgram shader, float saturation, float contrast, float brightness) {
            shader.setUniform("saturation", saturation);
            shader.setUniform("contrast", contrast);
            shader.setUniform("brightness", brightness);
        }
    }
}