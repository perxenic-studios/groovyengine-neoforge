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

package io.github.luckymcdev.groovyengine.lens.rendering.util;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

@OnlyIn(Dist.CLIENT)
public class RenderTargetUtil {

    /**
     * Copy the contents of the source RenderTarget into the existing RenderTarget (if null, create a new one).
     * If the existing RenderTarget is not null and does not match the source RenderTarget's dimensions,
     * it will be destroyed and recreated with the correct dimensions.
     * The contents of the source RenderTarget are copied into the destination RenderTarget using
     * OpenGL's glBlitFramebuffer function with Y-flip.
     *
     * @param source   The source RenderTarget to copy from
     * @param existing The destination RenderTarget to copy into (or null to create a new one)
     * @return The copied RenderTarget
     */
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
        int currentDrawFB = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);

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
