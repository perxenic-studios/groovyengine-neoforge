package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class ShaderProgram {
    private int programId;
    private final Map<ShaderType, Integer> shaders;
    private final Map<String, Integer> uniformLocations;
    private static int quadVAO = -1;
    private static int quadVBO = -1;

    public enum ShaderType {
        VERTEX(GL20.GL_VERTEX_SHADER),
        FRAGMENT(GL20.GL_FRAGMENT_SHADER),
        GEOMETRY(GL32.GL_GEOMETRY_SHADER),
        TESS_CONTROL(GL40.GL_TESS_CONTROL_SHADER),
        TESS_EVALUATION(GL40.GL_TESS_EVALUATION_SHADER),
        COMPUTE(GL43.GL_COMPUTE_SHADER);

        public final int glType;

        ShaderType(int glType) {
            this.glType = glType;
        }
    }

    public ShaderProgram() {
        this.programId = GlStateManager.glCreateProgram();
        this.shaders = new HashMap<>();
        this.uniformLocations = new HashMap<>();
    }

    /**
     * Add a shader from source code string
     */
    public ShaderProgram addShader(ShaderType type, String source) {
        int shaderId = GlStateManager.glCreateShader(type.glType);
        GL20.glShaderSource(shaderId, source);
        GlStateManager.glCompileShader(shaderId);

        if (GlStateManager.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GlStateManager.glGetShaderInfoLog(shaderId, 1024);
            GlStateManager.glDeleteShader(shaderId);
            throw new RuntimeException("Failed to compile " + type + " shader:\n" + log);
        }

        shaders.put(type, shaderId);
        return this;
    }

    /**
     * Add a shader from a resource file
     */
    public ShaderProgram addShaderFromResource(ShaderType type, String resourcePath) {
        String source = loadShaderSource(resourcePath);
        return addShader(type, source);
    }

    /**
     * Link all added shaders into the program
     */
    public ShaderProgram link() {
        for (int shaderId : shaders.values()) {
            GlStateManager.glAttachShader(programId, shaderId);
        }

        GlStateManager.glLinkProgram(programId);

        if (GlStateManager.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            String log = GlStateManager.glGetProgramInfoLog(programId, 1024);
            throw new RuntimeException("Failed to link shader program:\n" + log);
        }

        // Detach and delete shaders after linking
        for (int shaderId : shaders.values()) {
            GL20.glDetachShader(programId, shaderId);
            GlStateManager.glDeleteShader(shaderId);
        }

        return this;
    }

    /**
     * Bind this shader program for use
     */
    public void bind() {
        GlStateManager._glUseProgram(programId);
    }

    /**
     * Unbind the current shader program
     */
    public static void unbind() {
        GlStateManager._glUseProgram(0);
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

    // Uniform setters - CORRECTED VERSION using GlStateManager
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
     * Initialize the fullscreen quad (only done once)
     */
    private static void initFullscreenQuad() {
        // Vertices for a fullscreen quad using triangle strip
        // Position (x, y) and UV (u, v)
        float[] quadVertices = {
                -1.0f,  1.0f,  0.0f, 1.0f,  // Top-left
                -1.0f, -1.0f,  0.0f, 0.0f,  // Bottom-left
                1.0f,  1.0f,  1.0f, 1.0f,  // Top-right
                1.0f, -1.0f,  1.0f, 0.0f   // Bottom-right
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
     * Load shader source from resource file
     */
    private String loadShaderSource(String resourcePath) {
        try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new RuntimeException("Shader resource not found: " + resourcePath);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder source = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                source.append(line).append("\n");
            }

            return source.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shader: " + resourcePath, e);
        }
    }

    /**
     * Clean up shader program
     */
    public void dispose() {
        if (programId != 0) {
            GlStateManager.glDeleteProgram(programId);
            programId = 0;
        }
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

    public int getProgramId() {
        return programId;
    }
}