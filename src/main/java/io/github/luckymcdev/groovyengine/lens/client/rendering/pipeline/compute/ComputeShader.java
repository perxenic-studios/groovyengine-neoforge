package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.compute;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL43C.*;

public class ComputeShader {
    private int ssbo;
    private int program;
    private int elementCount;

    public void init(float[] initialData, ResourceLocation shaderPath) {
        elementCount = initialData.length / 4; // 4 floats per vec4

        // create SSBO
        ssbo = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);

        FloatBuffer fb = MemoryUtil.memAllocFloat(initialData.length);
        fb.put(initialData).flip();
        glBufferData(GL_SHADER_STORAGE_BUFFER, fb, GL_DYNAMIC_DRAW);
        MemoryUtil.memFree(fb);

        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssbo);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);

        // compile compute shader
        String source = loadComputeSource(shaderPath);
        int cs = glCreateShader(GL_COMPUTE_SHADER);
        glShaderSource(cs, source);
        glCompileShader(cs);
        checkCompile(cs, "compute");

        program = glCreateProgram();
        glAttachShader(program, cs);
        glLinkProgram(program);
        checkLink(program);

        glDeleteShader(cs);
    }

    public void dispatch() {
        glUseProgram(program);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssbo);

        int localSizeX = 128; // must match shader
        int groupsX = (elementCount + localSizeX - 1) / localSizeX;
        glDispatchCompute(groupsX, 1, 1);

        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT
                | GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT
                | GL_TEXTURE_FETCH_BARRIER_BIT);

        glUseProgram(0);
    }

    public float[] readBack() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        ByteBuffer mapped = glMapBuffer(GL_SHADER_STORAGE_BUFFER, GL_READ_ONLY);
        if (mapped == null) return new float[0];
        FloatBuffer fb = mapped.asFloatBuffer();
        float[] data = new float[fb.remaining()];
        fb.get(data);
        glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        return data;
    }

    public String loadComputeSource(ResourceLocation rl) {
        try {
            Resource res = Minecraft.getInstance().getResourceManager().getResource(rl).orElseThrow();
            try (InputStream in = res.open()) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load compute shader " + rl, e);
        }
    }

    public void initFromSource(String source) {
        int cs = glCreateShader(GL_COMPUTE_SHADER);
        glShaderSource(cs, source);
        glCompileShader(cs);
        checkCompile(cs, "compute");

        program = glCreateProgram();
        glAttachShader(program, cs);
        glLinkProgram(program);
        checkLink(program);

        glDeleteShader(cs);
    }

    private void checkCompile(int shader, String name) {
        int status = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (status == 0) {
            String log = glGetShaderInfoLog(shader);
            throw new RuntimeException("Failed to compile " + name + " shader:\n" + log);
        }
    }

    private void checkLink(int prog) {
        int status = glGetProgrami(prog, GL_LINK_STATUS);
        if (status == 0) {
            String log = glGetProgramInfoLog(prog);
            throw new RuntimeException("Failed to link program:\n" + log);
        }
    }
}