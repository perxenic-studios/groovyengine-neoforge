package io.github.luckymcdev.groovyengine.lens.client.rendering.fbo;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
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
     * Use Minecraft's main framebuffer depth buffer with this FBO
     */
    public void withMcDepthBuffer() {
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        this.depthBufferId = mainTarget.getDepthTextureId();
    }

    public void copyDepthFromMain() {
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        this.bindWrite(false);
        mainTarget.bindRead();
        GlStateManager._glCopyTexSubImage2D(GlConst.GL_DEPTH_ATTACHMENT, 0, 0, 0, 0, 0, this.width, this.height);
    }

    /**
     * Alternative method that attaches to main depth buffer during FBO creation
     */
    public void createWithSharedDepth(int width, int height, boolean onMac) {
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        int mainDepthBuffer = mainTarget.getDepthTextureId();
        super.createBuffers(width, height, onMac);
        this.depthBufferId = mainDepthBuffer;
        this.bindWrite(false);
    }

    /**
     * Resize the FBO while preserving contents (if possible)
     */
    public void resize(int newWidth, int newHeight, boolean onMac) {
        if (this.width != newWidth || this.height != newHeight) {
            int oldDepthBuffer = this.depthBufferId;
            this.destroyBuffers();
            this.createBuffers(newWidth, newHeight, onMac);
            this.depthBufferId = oldDepthBuffer;
        }
    }

    /**
     * Check if this FBO is using the main Minecraft depth buffer
     */
    public boolean isUsingMainDepthBuffer() {
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        return this.depthBufferId == mainTarget.getDepthTextureId();
    }
}