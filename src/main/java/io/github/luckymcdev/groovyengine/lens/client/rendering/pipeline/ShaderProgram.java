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

package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.shader.FragmentShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.shader.GeometryShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.shader.Shader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.shader.VertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShaderProgram {
    private static int quadVAO = -1;
    private static int quadVBO = -1;
    private final List<Shader> attachedShaders;
    private final Map<String, Integer> uniformLocations;
    private int programId;

    public ShaderProgram() {
        this.programId = GlStateManager.glCreateProgram();
        this.attachedShaders = new ArrayList<>();
        this.uniformLocations = new HashMap<>();
    }

    /**
     * Unbind the current shader program
     */
    public static void unbind() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUseProgram(0);
    }

    /**
     * Initialize the fullscreen quad (only done once)
     */
    private static void initFullscreenQuad() {
        // Vertices for a fullscreen quad using triangle strip
        // Position (x, y) and UV (u, v)
        float[] quadVertices = {
                -1.0f, 1.0f, 0.0f, 1.0f,  // Top-left
                -1.0f, -1.0f, 0.0f, 0.0f,  // Bottom-left
                1.0f, 1.0f, 1.0f, 1.0f,  // Top-right
                1.0f, -1.0f, 1.0f, 0.0f   // Bottom-right
        };

        quadVAO = GlStateManager._glGenVertexArrays();
        quadVBO = GlStateManager._glGenBuffers();

        GlStateManager._glBindVertexArray(quadVAO);
        GlStateManager._glBindBuffer(GL20.GL_ARRAY_BUFFER, quadVBO);

        // Convert float array to ByteBuffer
        java.nio.ByteBuffer buffer = org.lwjgl.BufferUtils.createByteBuffer(quadVertices.length * 4);
        buffer.asFloatBuffer().put(quadVertices);
        buffer.rewind();

        GlStateManager._glBufferData(GL20.GL_ARRAY_BUFFER, buffer, GL20.GL_STATIC_DRAW);

        // Position attribute
        GlStateManager._enableVertexAttribArray(0);
        GlStateManager._vertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * 4, 0);

        // UV attribute
        GlStateManager._enableVertexAttribArray(1);
        GlStateManager._vertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * 4, 2 * 4);

        GlStateManager._glBindVertexArray(0);
    }

    /**
     * Clean up the fullscreen quad resources (call on shutdown)
     */
    public static void disposeQuad() {
        if (quadVAO != -1) {
            GlStateManager._glDeleteVertexArrays(quadVAO);
            quadVAO = -1;
        }
        if (quadVBO != -1) {
            GlStateManager._glDeleteBuffers(quadVBO);
            quadVBO = -1;
        }
    }

    /**
     * Add a shader to the program
     */
    public ShaderProgram addShader(Shader shader) {
        if (!shader.isCompiled()) {
            throw new IllegalStateException("Shader must be compiled before adding to program");
        }

        attachedShaders.add(shader);
        GlStateManager.glAttachShader(programId, shader.getShaderId());
        return this;
    }

    /**
     * Add a vertex shader from source
     */
    public ShaderProgram addVertexShader(String source) {
        Shader shader = new VertexShader(source);
        return addShader(shader);
    }

    /**
     * Add a fragment shader from source
     */
    public ShaderProgram addFragmentShader(String source) {
        Shader shader = new FragmentShader(source);
        return addShader(shader);
    }

    /**
     * Add a geometry shader from source
     */
    public ShaderProgram addGeometryShader(String source) {
        Shader shader = new GeometryShader(source);
        return addShader(shader);
    }

    /**
     * Link all added shaders into the program
     */
    public ShaderProgram link() {
        RenderSystem.assertOnRenderThread();

        GlStateManager.glLinkProgram(programId);

        if (GlStateManager.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            String log = GlStateManager.glGetProgramInfoLog(programId, 1024);
            throw new RuntimeException("Failed to link shader program:\n" + log);
        }

        // Detach shaders after linking (they're still needed for disposal)
        for (Shader shader : attachedShaders) {
            GL20.glDetachShader(programId, shader.getShaderId());
        }

        return this;
    }

    /**
     * Bind this shader program for use
     */
    public void bind() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUseProgram(programId);
    }

    /**
     * Get uniform location (cached)
     */
    private int getUniformLocation(String name) {
        if (uniformLocations.containsKey(name)) {
            return uniformLocations.get(name);
        }

        int location = GlStateManager._glGetUniformLocation(programId, name);
        uniformLocations.put(name, location);
        return location;
    }

    // Uniform setters
    public void setUniform(String name, int value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffer = stack.mallocInt(1);
            buffer.put(0, value);
            GlStateManager._glUniform1i(getUniformLocation(name), value);
        }
    }

    public void setUniform(String name, float value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(1);
            buffer.put(0, value);
            GlStateManager._glUniform1(getUniformLocation(name), buffer);
        }
    }

    public void setUniform(String name, float x, float y) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(2);
            buffer.put(0, x);
            buffer.put(1, y);
            GlStateManager._glUniform2(getUniformLocation(name), buffer);
        }
    }

    public void setUniform(String name, float x, float y, float z) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3);
            buffer.put(0, x);
            buffer.put(1, y);
            buffer.put(2, z);
            GlStateManager._glUniform3(getUniformLocation(name), buffer);
        }
    }

    public void setUniform(String name, float x, float y, float z, float w) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4);
            buffer.put(0, x);
            buffer.put(1, y);
            buffer.put(2, z);
            buffer.put(3, w);
            GlStateManager._glUniform4(getUniformLocation(name), buffer);
        }
    }

    public void setUniform(String name, boolean value) {
        GlStateManager._glUniform1i(getUniformLocation(name), value ? 1 : 0);
    }

    public void setUniformMatrix4(String name, boolean transpose, float[] matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            buffer.put(matrix);
            buffer.flip();
            GlStateManager._glUniformMatrix4(getUniformLocation(name), transpose, buffer);
        }
    }

    /**
     * Draw a fullscreen quad - perfect for post-processing effects
     */
    public void drawFullscreenQuad() {
        if (quadVAO == -1) {
            initFullscreenQuad();
        }

        GlStateManager._glBindVertexArray(quadVAO);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
        GlStateManager._glBindVertexArray(0);
    }

    /**
     * Clean up shader program and attached shaders
     */
    public void dispose() {
        if (programId != 0) {
            // Dispose of all attached shaders
            for (Shader shader : attachedShaders) {
                shader.dispose();
            }
            attachedShaders.clear();

            GlStateManager.glDeleteProgram(programId);
            programId = 0;
        }
    }

    public int getProgramId() {
        return programId;
    }

    /**
     * Get the number of attached shaders
     */
    public int getShaderCount() {
        return attachedShaders.size();
    }
}