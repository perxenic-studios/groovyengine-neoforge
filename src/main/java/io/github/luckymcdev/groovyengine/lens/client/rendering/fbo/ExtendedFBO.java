package io.github.luckymcdev.groovyengine.lens.client.rendering.fbo;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;

public class ExtendedFBO extends RenderTarget {

    public ExtendedFBO(boolean useDepth) {
        super(useDepth);
    }

    /**
     * Clear the FBO with a specific color
     */
    public void clear(float red, float green, float blue, float alpha) {
        this.setClearColor(red, green, blue, alpha);
        this.clear(false);
    }

    /**
     * Get the color texture ID for use in shaders
     */
    public int getColorTexture() {
        return super.getColorTextureId();
    }

    public void createBuffers(int width, int height, boolean onMac) {
        super.createBuffers(width, height, onMac);
    }

    /**
     * Resize the FBO while preserving contents (if possible)
     */
    public void resize(int newWidth, int newHeight, boolean onMac) {
        if (this.width != newWidth || this.height != newHeight) {
            this.destroyBuffers();
            this.createBuffers(newWidth, newHeight, onMac);
        }
    }
}