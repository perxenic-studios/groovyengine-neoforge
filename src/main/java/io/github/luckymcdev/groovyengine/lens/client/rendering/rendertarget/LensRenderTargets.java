package io.github.luckymcdev.groovyengine.lens.client.rendering.rendertarget;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;

@EventBusSubscriber
public class LensRenderTargets {

    private static RenderTarget afterSkyTarget;
    private static RenderTarget afterSolidBlocksTarget;
    private static RenderTarget afterCutoutMippedBlocksTarget;
    private static RenderTarget afterEntitiesTarget;
    private static RenderTarget afterBlockEntitiesTarget;
    private static RenderTarget afterTranslucentBlocksTarget;
    private static RenderTarget afterTripwireBlocksTarget;
    private static RenderTarget afterParticlesTarget;
    private static RenderTarget afterWeatherTarget;
    private static RenderTarget afterLevelTarget;

    @SubscribeEvent
    private static void getRenderTargets(RenderLevelStageEvent event) {
        Stage currStage = event.getStage();
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();

        if (mainTarget == null) return;

        if (currStage == Stage.AFTER_SKY) {
            afterSkyTarget = copyRenderTarget(mainTarget, afterSkyTarget);
        } else if (currStage == Stage.AFTER_SOLID_BLOCKS) {
            afterSolidBlocksTarget = copyRenderTarget(mainTarget, afterSolidBlocksTarget);
        } else if (currStage == Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS) {
            afterCutoutMippedBlocksTarget = copyRenderTarget(mainTarget, afterCutoutMippedBlocksTarget);
        } else if (currStage == Stage.AFTER_ENTITIES) {
            afterEntitiesTarget = copyRenderTarget(mainTarget, afterEntitiesTarget);
        } else if (currStage == Stage.AFTER_BLOCK_ENTITIES) {
            afterBlockEntitiesTarget = copyRenderTarget(mainTarget, afterBlockEntitiesTarget);
        } else if (currStage == Stage.AFTER_TRANSLUCENT_BLOCKS) {
            afterTranslucentBlocksTarget = copyRenderTarget(mainTarget, afterTranslucentBlocksTarget);
        } else if (currStage == Stage.AFTER_TRIPWIRE_BLOCKS) {
            afterTripwireBlocksTarget = copyRenderTarget(mainTarget, afterTripwireBlocksTarget);
        } else if (currStage == Stage.AFTER_PARTICLES) {
            afterParticlesTarget = copyRenderTarget(mainTarget, afterParticlesTarget);
        } else if (currStage == Stage.AFTER_WEATHER) {
            afterWeatherTarget = copyRenderTarget(mainTarget, afterWeatherTarget);
        } else if (currStage == Stage.AFTER_LEVEL) {
            afterLevelTarget = copyRenderTarget(mainTarget, afterLevelTarget);
        }
    }

    private static RenderTarget copyRenderTarget(RenderTarget source, RenderTarget existing) {
        // Create or resize the target if needed
        if (existing == null || existing.width != source.width || existing.height != source.height) {
            if (existing != null) {
                existing.destroyBuffers();
            }
            // Use MainTarget class instead of abstract RenderTarget
            existing = new com.mojang.blaze3d.pipeline.MainTarget(source.width, source.height);
        }

        // Copy the framebuffer content using OpenGL directly
        int currentReadFB = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER_BINDING);
        int currentDrawFB = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER_BINDING);

        // Bind source as read framebuffer and destination as draw framebuffer
        org.lwjgl.opengl.GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER, source.frameBufferId);
        org.lwjgl.opengl.GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER, existing.frameBufferId);

        // Blit (copy) from source to destination with Y-flip
        org.lwjgl.opengl.GL30.glBlitFramebuffer(
                0, 0, source.width, source.height,           // source rectangle
                0, existing.height, existing.width, 0,       // destination rectangle (flipped Y)
                org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT,
                org.lwjgl.opengl.GL11.GL_NEAREST
        );

        // Restore previous framebuffer bindings
        org.lwjgl.opengl.GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER, currentReadFB);
        org.lwjgl.opengl.GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER, currentDrawFB);

        return existing;
    }

    // Getter methods that return texture IDs
    public static int getAfterSkyTextureId() {
        return afterSkyTarget != null ? afterSkyTarget.getColorTextureId() : 0;
    }

    public static int getAfterSolidBlocksTextureId() {
        return afterSolidBlocksTarget != null ? afterSolidBlocksTarget.getColorTextureId() : 0;
    }

    public static int getAfterCutoutMippedBlocksTextureId() {
        return afterCutoutMippedBlocksTarget != null ? afterCutoutMippedBlocksTarget.getColorTextureId() : 0;
    }

    public static int getAfterEntitiesTextureId() {
        return afterEntitiesTarget != null ? afterEntitiesTarget.getColorTextureId() : 0;
    }

    public static int getAfterBlockEntitiesTextureId() {
        return afterBlockEntitiesTarget != null ? afterBlockEntitiesTarget.getColorTextureId() : 0;
    }

    public static int getAfterTranslucentBlocksTextureId() {
        return afterTranslucentBlocksTarget != null ? afterTranslucentBlocksTarget.getColorTextureId() : 0;
    }

    public static int getAfterTripwireBlocksTextureId() {
        return afterTripwireBlocksTarget != null ? afterTripwireBlocksTarget.getColorTextureId() : 0;
    }

    public static int getAfterParticlesTextureId() {
        return afterParticlesTarget != null ? afterParticlesTarget.getColorTextureId() : 0;
    }

    public static int getAfterWeatherTextureId() {
        return afterWeatherTarget != null ? afterWeatherTarget.getColorTextureId() : 0;
    }

    public static int getAfterLevelTextureId() {
        return afterLevelTarget != null ? afterLevelTarget.getColorTextureId() : 0;
    }

    public static void cleanup() {
        if (afterSkyTarget != null) {
            afterSkyTarget.destroyBuffers();
            afterSkyTarget = null;
        }
        if (afterSolidBlocksTarget != null) {
            afterSolidBlocksTarget.destroyBuffers();
            afterSolidBlocksTarget = null;
        }
        if (afterCutoutMippedBlocksTarget != null) {
            afterCutoutMippedBlocksTarget.destroyBuffers();
            afterCutoutMippedBlocksTarget = null;
        }
        if (afterEntitiesTarget != null) {
            afterEntitiesTarget.destroyBuffers();
            afterEntitiesTarget = null;
        }
        if (afterBlockEntitiesTarget != null) {
            afterBlockEntitiesTarget.destroyBuffers();
            afterBlockEntitiesTarget = null;
        }
        if (afterTranslucentBlocksTarget != null) {
            afterTranslucentBlocksTarget.destroyBuffers();
            afterTranslucentBlocksTarget = null;
        }
        if (afterTripwireBlocksTarget != null) {
            afterTripwireBlocksTarget.destroyBuffers();
            afterTripwireBlocksTarget = null;
        }
        if (afterParticlesTarget != null) {
            afterParticlesTarget.destroyBuffers();
            afterParticlesTarget = null;
        }
        if (afterWeatherTarget != null) {
            afterWeatherTarget.destroyBuffers();
            afterWeatherTarget = null;
        }
        if (afterLevelTarget != null) {
            afterLevelTarget.destroyBuffers();
            afterLevelTarget = null;
        }
    }
}