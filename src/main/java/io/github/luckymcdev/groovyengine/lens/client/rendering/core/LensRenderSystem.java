package io.github.luckymcdev.groovyengine.lens.client.rendering.core;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.IBufferObject;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL43C.*;

@OnlyIn(Dist.CLIENT)
public class LensRenderSystem extends RenderSystem {
    private static final List<IBufferObject> bufferObjects = new ArrayList<>();
    private static final Vector3f viewBobOffset = new Vector3f();

    /**
     * Wraps a render call with the RenderSystem's onRenderThread check.
     * If the current thread is not the render thread, records the render call.
     * Otherwise, executes the render call directly.
     *
     * @param renderCall the render call to wrap
     */
    public static void wrap(RenderCall renderCall) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(renderCall);
        } else {
            renderCall.execute();
        }
    }

    /**
     * Asserts that the current stage matches the expected stage.
     * @param event the RenderLevelStageEvent to check
     * @param expected the expected stage
     * @return true if the current stage matches the expected stage, false otherwise
     */
    public static boolean assertStage(RenderLevelStageEvent event, RenderLevelStageEvent.Stage expected) {
        return event.getStage() == expected;
    }

    /**
     * Registers a buffer object with the render system. This allows the render system to clean up the buffer object when it is no longer needed.
     * @param bufferObject the buffer object to register
     */
    public static void registerBufferObject(IBufferObject bufferObject) {
        bufferObjects.add(bufferObject);
    }

    /**
     * Unregisters a buffer object from the render system. This prevents the render system from cleaning up the buffer object when it is no longer needed.
     * @param bufferObject the buffer object to unregister
     */
    public static void unregisterBufferObject(IBufferObject bufferObject) {
        bufferObjects.remove(bufferObject);
    }

    /**
     * Destroys all registered buffer objects.
     *<p>
     *This method iterates over all registered buffer objects and calls their destroy method.
     *Afterwards, the buffer objects are removed from the render system's internal list.
     *<p>
     *This method should be called when the render system is about to be shut down.
     */
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

    /**
     * Deletes the specified OpenGL buffer object.
     * <p>
     * The buffer object is deleted and its name is freed.
     * If the buffer object is currently bound to a target, it is unbound.
     * If the buffer object's data store is currently mapped, it is unmapped.
     * @param buffer the OpenGL buffer object to delete
     */
    public static void deleteBuffers(int buffer) {
        GlStateManager._glDeleteBuffers(buffer);
    }

    /**
     * Binds the specified OpenGL buffer object to the specified target.
     * <p>
     * The buffer object is bound to the target and its data store is mapped.
     * The buffer object's data store is mapped into the client's address space.
     * The data can be modified and used until the buffer object is unmapped.
     * @param target the OpenGL buffer target
     * @param buffer the OpenGL buffer object to bind
     */
    public static void bindBuffer(int target, int buffer) {
        GlStateManager._glBindBuffer(target, buffer);
    }

    /**
     * Specifies the data store for the buffer object bound to the specified target.
     * <p>
     * The data store for the buffer object is updated according to the value of <code>usage</code>.
     * If <code>usage</code> is <code>GL_STATIC_DRAW</code>, the data is modified once and used as-is.
     * If <code>usage</code> is <code>GL_DYNAMIC_DRAW</code>, the data may be modified repeatedly by the application.
     * <p>
     * The data store for the buffer object is mapped into the client's address space.
     * The data can be modified and used until the buffer object is unmapped.
     * @param target the target of the buffer object
     * @param data the data to store in the buffer object's data store
     * @param usage the expected usage of the buffer object's data store
     */
    public static void bufferData(int target, ByteBuffer data, int usage) {
        GlStateManager._glBufferData(target, data, usage);
    }

    /**
     * Binds a buffer object to an indexed buffer target.
     * <p>
     * The buffer object is bound to the target and its data store is mapped.
     * The data store for the buffer object is mapped into the client's address space.
     * The data can be modified and used until the buffer object is unmapped.
     * @param target the indexed buffer target
     * @param index the index of the buffer target
     * @param buffer the OpenGL buffer object to bind
     */
    public static void bindBufferBase(int target, int index, int buffer) {
        wrap(() -> glBindBufferBase(target, index, buffer));
    }

    // Use Minecraft's existing shader operations
    public static int createShader(int type) {
        return GlStateManager.glCreateShader(type);
    }

    /**
     * Replaces the source code of the specified shader object with the
     * specified source code.
     * <p>
     * The source code is a CharSequence, which is converted to a
     * List of Strings before being passed to GlStateManager.
     * @param shader the shader object to modify
     * @param source the source code to replace the shader object's source code
     */
    public static void shaderSource(int shader, CharSequence source) {
        // Convert to list format expected by GlStateManager
        GlStateManager.glShaderSource(shader, List.of(source.toString()));
    }

    /**
     * Compile the specified shader object.
     * <p>
     * The compile status of the shader object can be queried with
     * {@link #getShaderi(int, int)}.
     * @param shader the shader object to compile
     */
    public static void compileShader(int shader) {
        GlStateManager.glCompileShader(shader);
    }

    /**
     * Queries the compile status of the specified shader object.
     * <p>
     * After calling {@link #compileShader(int)}, the compile status of the shader object can be queried with this method.
     * The value of <code>GL_COMPILE_STATUS</code> is queried and returned as an integer.
     * @param shader the shader object to query
     * @param pname the parameter to query, must be <code>GL_COMPILE_STATUS</code>
     * @return the queried value as an integer
     */
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