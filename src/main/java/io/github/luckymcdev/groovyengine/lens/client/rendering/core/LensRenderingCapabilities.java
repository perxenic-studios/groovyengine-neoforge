package io.github.luckymcdev.groovyengine.lens.client.rendering.core;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class LensRenderingCapabilities {
    /**
     * A BooleanSupplier that checks if compute shaders are supported on this device.
     * This is a cached value that will only be computed once.
     */
    public static final BooleanSupplier COMPUTE_SUPPORTED = glCapability(caps -> caps.OpenGL43 || caps.GL_ARB_compute_shader);

    /**
     * A helper method to create a BooleanSupplier that checks a given OpenGL capability.
     * The supplier will only query the OpenGL capabilities once, and then return the cached result.
     * This is useful for checking if certain OpenGL features are available, such as compute shaders.
     *
     * @param delegate a function that takes a GLCapabilities object and returns a boolean value
     * @return a BooleanSupplier that checks the given OpenGL capability
     */
    private static BooleanSupplier glCapability(Function<GLCapabilities, Boolean> delegate) {
        return new BooleanSupplier() {
            private boolean value;
            private boolean initialized;

            @Override
            public boolean getAsBoolean() {
                LensRenderSystem.assertOnRenderThreadOrInit();
                if (!this.initialized) {
                    this.initialized = true;
                    return this.value = delegate.apply(GL.getCapabilities());
                }
                return this.value;
            }
        };
    }
}
