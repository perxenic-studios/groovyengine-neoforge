package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.compute;

import io.github.luckymcdev.groovyengine.lens.client.rendering.LensRenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import static org.lwjgl.opengl.GL43C.*;

/**
 * A generic compute shader implementation that supports various data types
 * through ByteBuffer-based data management.
 */
public class ComputeShader implements AutoCloseable {
    private int ssbo;
    private int program;
    private int elementCount;
    private int dataSizeBytes;

    // Default memory barrier flags for common use cases
    private static final int DEFAULT_MEMORY_BARRIER =
            GL_SHADER_STORAGE_BARRIER_BIT |
                    GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT |
                    GL_TEXTURE_FETCH_BARRIER_BIT;

    /**
     * Constructs a compute shader with initial data
     *
     * @param initialData The initial data to load into the SSBO
     * @param elementSize The size of each element in bytes
     * @param shaderPath The resource location of the compute shader
     */
    public ComputeShader(ByteBuffer initialData, int elementSize, ResourceLocation shaderPath) {
        init(initialData, elementSize, shaderPath);
    }

    /**
     * Constructs a compute shader with float data (convenience constructor)
     */
    public ComputeShader(float[] floatData, ResourceLocation shaderPath) {
        init(floatData, shaderPath);
    }

    /**
     * Default constructor for deferred initialization
     */
    public ComputeShader() {
        // Empty constructor for deferred initialization
    }

    /**
     * Initializes the compute shader with initial data
     *
     * @param initialData The initial data to load into the SSBO
     * @param elementSize The size of each element in bytes
     * @param shaderPath The resource location of the compute shader
     */
    public void init(ByteBuffer initialData, int elementSize, ResourceLocation shaderPath) {
        if (ssbo != 0 || program != 0) {
            cleanup(); // Clean up any existing resources
        }

        this.elementCount = initialData.remaining() / elementSize;
        this.dataSizeBytes = initialData.remaining();

        // Create and initialize SSBO
        createSSBO(initialData);

        // Compile and link the compute shader
        compileAndLinkShader(shaderPath);
    }

    /**
     * Initializes the compute shader with float data (convenience method)
     */
    public void init(float[] floatData, ResourceLocation shaderPath) {
        ByteBuffer buffer = MemoryUtil.memAlloc(floatData.length * Float.BYTES);
        try {
            buffer.asFloatBuffer().put(floatData).flip();
            init(buffer, Float.BYTES, shaderPath);
        } finally {
            MemoryUtil.memFree(buffer);
        }
    }

    /**
     * Creates and initializes the Shader Storage Buffer Object (SSBO)
     */
    private void createSSBO(ByteBuffer initialData) {
        ssbo = LensRenderSystem.genBuffers();
        LensRenderSystem.bindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        LensRenderSystem.bufferData(GL_SHADER_STORAGE_BUFFER, initialData, GL_DYNAMIC_DRAW);
        LensRenderSystem.bindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssbo);
        LensRenderSystem.bindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    /**
     * Compiles and links the compute shader
     */
    private void compileAndLinkShader(ResourceLocation shaderPath) {
        String source = loadComputeSource(shaderPath);

        int computeShader = LensRenderSystem.createShader(GL_COMPUTE_SHADER);
        LensRenderSystem.shaderSource(computeShader, source);
        LensRenderSystem.compileShader(computeShader);
        LensRenderSystem.checkShaderCompile(computeShader, "compute");

        program = LensRenderSystem.createProgram();
        LensRenderSystem.attachShader(program, computeShader);
        LensRenderSystem.linkProgram(program);
        LensRenderSystem.checkProgramLink(program);

        LensRenderSystem.deleteShader(computeShader);
    }

    /**
     * Dispatches the compute shader with specified work group sizes
     *
     * @param localSizeX Local work group size in X dimension
     * @param localSizeY Local work group size in Y dimension
     * @param localSizeZ Local work group size in Z dimension
     */
    public void dispatch(int localSizeX, int localSizeY, int localSizeZ) {
        int groupsX = (int) Math.ceil((double) elementCount / localSizeX);
        int groupsY = (int) Math.ceil((double) 1 / localSizeY); // Default 1D
        int groupsZ = (int) Math.ceil((double) 1 / localSizeZ); // Default 1D

        dispatchCompute(groupsX, groupsY, groupsZ, DEFAULT_MEMORY_BARRIER);
    }

    /**
     * Dispatches the compute shader with default 1D work group (128 threads)
     */
    public void dispatch() {
        dispatch(128, 1, 1);
    }

    /**
     * Dispatches compute with custom memory barrier flags
     */
    public void dispatchCompute(int groupsX, int groupsY, int groupsZ, int memoryBarrierFlags) {
        LensRenderSystem.useProgram(program);
        LensRenderSystem.bindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssbo);

        LensRenderSystem.dispatchCompute(groupsX, groupsY, groupsZ);
        LensRenderSystem.memoryBarrier(memoryBarrierFlags);

        LensRenderSystem.useProgram(0);
    }

    /**
     * Reads back data from the SSBO as a ByteBuffer
     */
    public ByteBuffer readBackByteBuffer() {
        LensRenderSystem.bindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        ByteBuffer mapped = LensRenderSystem.mapBuffer(GL_SHADER_STORAGE_BUFFER, GL_READ_ONLY);

        if (mapped == null) {
            LensRenderSystem.bindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
            return MemoryUtil.memAlloc(0);
        }

        // Create a copy since the mapped buffer might be temporary
        ByteBuffer result = MemoryUtil.memAlloc(dataSizeBytes);
        result.put(mapped.slice().limit(dataSizeBytes)).flip();

        LensRenderSystem.unmapBuffer(GL_SHADER_STORAGE_BUFFER);
        LensRenderSystem.bindBuffer(GL_SHADER_STORAGE_BUFFER, 0);

        return result;
    }

    /**
     * Reads back data and converts it using a provided function
     */
    public <T> T readBack(Function<ByteBuffer, T> converter) {
        ByteBuffer buffer = readBackByteBuffer();
        try {
            return converter.apply(buffer);
        } finally {
            if (buffer != null) {
                MemoryUtil.memFree(buffer);
            }
        }
    }

    /**
     * Reads back data as float array (convenience method)
     */
    public float[] readBackFloats() {
        return readBack(buffer -> {
            FloatBuffer floatBuffer = buffer.asFloatBuffer();
            float[] result = new float[floatBuffer.remaining()];
            floatBuffer.get(result);
            return result;
        });
    }

    /**
     * Reads back data as int array (convenience method)
     */
    public int[] readBackInts() {
        return readBack(buffer -> {
            IntBuffer intBuffer = buffer.asIntBuffer();
            int[] result = new int[intBuffer.remaining()];
            intBuffer.get(result);
            return result;
        });
    }

    /**
     * Updates the SSBO with new data
     */
    public void updateData(ByteBuffer newData) {
        LensRenderSystem.bindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        LensRenderSystem.bufferData(GL_SHADER_STORAGE_BUFFER, newData, GL_DYNAMIC_DRAW);
        LensRenderSystem.bindBuffer(GL_SHADER_STORAGE_BUFFER, 0);

        this.dataSizeBytes = newData.remaining();
        this.elementCount = dataSizeBytes / (dataSizeBytes / elementCount); // Maintain element count ratio
    }

    /**
     * Loads compute shader source from resource location
     */
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

    /**
     * Initializes from source string instead of file
     */
    public void initFromSource(String source, ByteBuffer initialData, int elementSize) {
        if (ssbo != 0 || program != 0) {
            cleanup(); // Clean up any existing resources
        }

        this.elementCount = initialData.remaining() / elementSize;
        this.dataSizeBytes = initialData.remaining();

        createSSBO(initialData);
        compileFromSource(source);
    }

    /**
     * Compiles shader from source string
     */
    private void compileFromSource(String source) {
        int computeShader = LensRenderSystem.createShader(GL_COMPUTE_SHADER);
        LensRenderSystem.shaderSource(computeShader, source);
        LensRenderSystem.compileShader(computeShader);
        LensRenderSystem.checkShaderCompile(computeShader, "compute");

        program = LensRenderSystem.createProgram();
        LensRenderSystem.attachShader(program, computeShader);
        LensRenderSystem.linkProgram(program);
        LensRenderSystem.checkProgramLink(program);

        LensRenderSystem.deleteShader(computeShader);
    }

    /**
     * Gets the SSBO handle
     */
    public int getSSBO() {
        return ssbo;
    }

    /**
     * Gets the program handle
     */
    public int getProgram() {
        return program;
    }

    /**
     * Gets the element count
     */
    public int getElementCount() {
        return elementCount;
    }

    /**
     * Gets the data size in bytes
     */
    public int getDataSizeBytes() {
        return dataSizeBytes;
    }

    /**
     * Checks if the shader is initialized and ready for use
     */
    public boolean isInitialized() {
        return ssbo != 0 && program != 0;
    }

    /**
     * Cleans up resources
     */
    public void cleanup() {
        if (ssbo != 0) {
            LensRenderSystem.deleteBuffers(ssbo);
            ssbo = 0;
        }
        if (program != 0) {
            LensRenderSystem.deleteProgram(program);
            program = 0;
        }
        elementCount = 0;
        dataSizeBytes = 0;
    }

    /**
     * AutoCloseable implementation for try-with-resources
     */
    @Override
    public void close() {
        cleanup();
    }
}