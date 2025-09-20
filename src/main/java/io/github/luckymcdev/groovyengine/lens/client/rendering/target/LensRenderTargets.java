package io.github.luckymcdev.groovyengine.lens.client.rendering.target;

import com.mojang.blaze3d.pipeline.RenderTarget;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.RenderTargetUtil;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;

@EventBusSubscriber
public class LensRenderTargets {

    public static RenderTarget afterSkyTarget;
    public static RenderTarget afterSolidBlocksTarget;
    public static RenderTarget afterCutoutMippedBlocksTarget;
    public static RenderTarget afterEntitiesTarget;
    public static RenderTarget afterBlockEntitiesTarget;
    public static RenderTarget afterTranslucentBlocksTarget;
    public static RenderTarget afterTripwireBlocksTarget;
    public static RenderTarget afterParticlesTarget;
    public static RenderTarget afterWeatherTarget;
    public static RenderTarget afterLevelTarget;

    @SubscribeEvent
    private static void getRenderTargets(RenderLevelStageEvent event) {
        Stage currStage = event.getStage();
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();

        if (mainTarget == null) return;

        if (currStage == Stage.AFTER_SKY) {
            afterSkyTarget = RenderTargetUtil.copyRenderTarget(mainTarget, afterSkyTarget);
        } else if (currStage == Stage.AFTER_SOLID_BLOCKS) {
            afterSolidBlocksTarget = RenderTargetUtil.copyRenderTarget(mainTarget, afterSolidBlocksTarget);
        } else if (currStage == Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS) {
            afterCutoutMippedBlocksTarget = RenderTargetUtil.copyRenderTarget(mainTarget, afterCutoutMippedBlocksTarget);
        } else if (currStage == Stage.AFTER_ENTITIES) {
            afterEntitiesTarget = RenderTargetUtil.copyRenderTarget(mainTarget, afterEntitiesTarget);
        } else if (currStage == Stage.AFTER_BLOCK_ENTITIES) {
            afterBlockEntitiesTarget = RenderTargetUtil.copyRenderTarget(mainTarget, afterBlockEntitiesTarget);
        } else if (currStage == Stage.AFTER_TRANSLUCENT_BLOCKS) {
            afterTranslucentBlocksTarget = RenderTargetUtil.copyRenderTarget(mainTarget, afterTranslucentBlocksTarget);
        } else if (currStage == Stage.AFTER_TRIPWIRE_BLOCKS) {
            afterTripwireBlocksTarget = RenderTargetUtil.copyRenderTarget(mainTarget, afterTripwireBlocksTarget);
        } else if (currStage == Stage.AFTER_PARTICLES) {
            afterParticlesTarget = RenderTargetUtil.copyRenderTarget(mainTarget, afterParticlesTarget);
        } else if (currStage == Stage.AFTER_WEATHER) {
            afterWeatherTarget = RenderTargetUtil.copyRenderTarget(mainTarget, afterWeatherTarget);
        } else if (currStage == Stage.AFTER_LEVEL) {
            afterLevelTarget = RenderTargetUtil.copyRenderTarget(mainTarget, afterLevelTarget);
        }
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