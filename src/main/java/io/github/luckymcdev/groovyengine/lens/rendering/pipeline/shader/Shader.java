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

package io.github.luckymcdev.groovyengine.lens.rendering.pipeline.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class Shader {
    protected final int shaderId;
    protected final ShaderType type;
    protected boolean compiled = false;

    protected Shader(ShaderType type, String source) {
        this.type = type;
        this.shaderId = GlStateManager.glCreateShader(type.glType);
        compile(source);
    }

    protected Shader(ShaderType type) {
        this.type = type;
        this.shaderId = GlStateManager.glCreateShader(type.glType);
    }

    /**
     * Compile the shader from source code
     */
    public void compile(String source) {
        GL20.glShaderSource(shaderId, source);
        GlStateManager.glCompileShader(shaderId);

        if (GlStateManager.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GlStateManager.glGetShaderInfoLog(shaderId, 1024);
            dispose();
            throw new RuntimeException("Failed to compile " + type + " shader:\n" + log);
        }

        compiled = true;
    }

    /**
     * Load and compile shader from a resource file
     */
    public void compileFromResource(String resourcePath) {
        String source = loadShaderSource(resourcePath);
        compile(source);
    }

    /**
     * Load shader source from resource file
     */
    protected String loadShaderSource(String resourcePath) {
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
     * Get the OpenGL shader ID
     */
    public int getShaderId() {
        return shaderId;
    }

    /**
     * Get the shader type
     */
    public ShaderType getType() {
        return type;
    }

    /**
     * Check if the shader is compiled
     */
    public boolean isCompiled() {
        return compiled;
    }

    /**
     * Clean up shader resources
     */
    public void dispose() {
        if (shaderId != 0) {
            GlStateManager.glDeleteShader(shaderId);
        }
    }

    public enum ShaderType {
        VERTEX(GL20.GL_VERTEX_SHADER),
        FRAGMENT(GL20.GL_FRAGMENT_SHADER),
        GEOMETRY(org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER),
        TESS_CONTROL(org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER),
        TESS_EVALUATION(org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER),
        COMPUTE(org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER);

        public final int glType;

        ShaderType(int glType) {
            this.glType = glType;
        }
    }
}