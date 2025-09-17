package io.github.luckymcdev.groovyengine.lens.client.rendering.util;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class RenderTargetUtil {

    public static RenderTarget copyRenderTarget(RenderTarget source, RenderTarget existing) {
        // Create or resize the target if needed
        if (existing == null || existing.width != source.width || existing.height != source.height) {
            if (existing != null) {
                existing.destroyBuffers();
            }
            // Use MainTarget class instead of abstract RenderTarget
            existing = new MainTarget(source.width, source.height);
        }

        // Copy the framebuffer content using OpenGL directly
        int currentReadFB = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        int currentDrawFB =GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);

        // Bind source as read framebuffer and destination as draw framebuffer
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, source.frameBufferId);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, existing.frameBufferId);

        // Blit (copy) from source to destination with Y-flip
        GL30.glBlitFramebuffer(
                0, 0, source.width, source.height,           // source rectangle
                0, existing.height, existing.width, 0,       // destination rectangle (flipped Y)
                GL11.GL_COLOR_BUFFER_BIT,
                GL11.GL_NEAREST
        );

        // Restore previous framebuffer bindings
        GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER, currentReadFB);
        GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER, currentDrawFB);

        return existing;
    }
}
