package io.github.luckymcdev.groovyengine.lens.client.rendering;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.IBufferObject;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL43C.*;

public class LensRenderSystem extends RenderSystem {
    private static final List<IBufferObject> bufferObjects = new ArrayList<>();
    private static final Vector3f viewBobOffset = new Vector3f();

    public static void wrap(RenderCall renderCall) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(renderCall);
        } else {
            renderCall.execute();
        }
    }

    public static boolean assertStage(RenderLevelStageEvent event, RenderLevelStageEvent.Stage expected) {
        return event.getStage() == expected;
    }

    public static void registerBufferObject(IBufferObject bufferObject) {
        bufferObjects.add(bufferObject);
    }

    public static void unregisterBufferObject(IBufferObject bufferObject) {
        bufferObjects.remove(bufferObject);
    }

    public static void destroyBufferObjects() {
        Iterator<IBufferObject> objects = bufferObjects.iterator();
        while (objects.hasNext()) {
            IBufferObject object = objects.next();
            object.destroy();
            objects.remove();
        }
    }

    // Use Minecraft's existing GlStateManager for buffer operations
    public static int genBuffers() {
        return GlStateManager._glGenBuffers();
    }

    public static void deleteBuffers(int buffer) {
        GlStateManager._glDeleteBuffers(buffer);
    }

    public static void bindBuffer(int target, int buffer) {
        GlStateManager._glBindBuffer(target, buffer);
    }

    public static void bufferData(int target, ByteBuffer data, int usage) {
        GlStateManager._glBufferData(target, data, usage);
    }

    public static void bindBufferBase(int target, int index, int buffer) {
        wrap(() -> glBindBufferBase(target, index, buffer));
    }

    // Use Minecraft's existing shader operations
    public static int createShader(int type) {
        return GlStateManager.glCreateShader(type);
    }

    public static void shaderSource(int shader, CharSequence source) {
        // Convert to list format expected by GlStateManager
        GlStateManager.glShaderSource(shader, List.of(source.toString()));
    }

    public static void compileShader(int shader) {
        GlStateManager.glCompileShader(shader);
    }

    public static int getShaderi(int shader, int pname) {
        return GlStateManager.glGetShaderi(shader, pname);
    }

    public static String getShaderInfoLog(int shader) {
        return GlStateManager.glGetShaderInfoLog(shader, 32768); // Use reasonable max length
    }

    public static void deleteShader(int shader) {
        GlStateManager.glDeleteShader(shader);
    }

    // Program Operations using GlStateManager
    public static int createProgram() {
        return GlStateManager.glCreateProgram();
    }

    public static void attachShader(int program, int shader) {
        GlStateManager.glAttachShader(program, shader);
    }

    public static void linkProgram(int program) {
        GlStateManager.glLinkProgram(program);
    }

    public static int getProgrami(int program, int pname) {
        return GlStateManager.glGetProgrami(program, pname);
    }

    public static String getProgramInfoLog(int program) {
        return GlStateManager.glGetProgramInfoLog(program, 32768);
    }

    public static void useProgram(int program) {
        GlStateManager._glUseProgram(program);
    }

    public static void deleteProgram(int program) {
        GlStateManager.glDeleteProgram(program);
    }

    // Compute Operations (still need custom wrapping for GL43C functions)
    public static void dispatchCompute(int num_groups_x, int num_groups_y, int num_groups_z) {
        wrap(() -> glDispatchCompute(num_groups_x, num_groups_y, num_groups_z));
    }

    public static void memoryBarrier(int barriers) {
        wrap(() -> glMemoryBarrier(barriers));
    }

    // Buffer Mapping Operations using GlStateManager
    public static void mapBuffer(int target, int access, Consumer<ByteBuffer> byteBufferConsumer) {
        wrap(() -> {
            ByteBuffer buffer = GlStateManager._glMapBuffer(target, access);
            if (buffer != null) {
                byteBufferConsumer.accept(buffer);
            }
        });
    }

    public static ByteBuffer mapBuffer(int target, int access) {
        return GlStateManager._glMapBuffer(target, access);
    }

    public static void unmapBuffer(int target) {
        GlStateManager._glUnmapBuffer(target);
    }

    // Utility Methods
    public static void setViewBobOffset(float x, float y, float z) {
        LensRenderSystem.viewBobOffset.set(x, y, z);
    }

    public static Vector3fc getViewBobOffset() {
        return LensRenderSystem.viewBobOffset;
    }

    // Additional utility for common operations
    public static void checkShaderCompile(int shader, String name) {
        int status = getShaderi(shader, GL_COMPILE_STATUS);
        if (status == 0) {
            String log = getShaderInfoLog(shader);
            throw new RuntimeException("Failed to compile " + name + " shader:\n" + log);
        }
    }

    public static void checkProgramLink(int program) {
        int status = getProgrami(program, GL_LINK_STATUS);
        if (status == 0) {
            String log = getProgramInfoLog(program);
            throw new RuntimeException("Failed to link program:\n" + log);
        }
    }
}