package io.github.luckymcdev.groovyengine.lens.client.rendering.core;

import com.mojang.datafixers.optics.Lens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class LensRenderingCapabilities {
    public static final BooleanSupplier COMPUTE_SUPPORTED = glCapability(caps -> caps.OpenGL43 || caps.GL_ARB_compute_shader);

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
