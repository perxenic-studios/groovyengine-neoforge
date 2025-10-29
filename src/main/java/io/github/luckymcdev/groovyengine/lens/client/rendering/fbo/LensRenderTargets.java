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

package io.github.luckymcdev.groovyengine.lens.client.rendering.fbo;

import com.mojang.blaze3d.pipeline.RenderTarget;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.RenderTargetUtil;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;

@EventBusSubscriber(value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class LensRenderTargets implements AutoCloseable {

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

    /**
     * Get the render targets for the current render stage.
     * @param event the RenderLevelStageEvent
     */
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

    public static int getAfterSkyDepthTextureId() {
        return afterSkyTarget.getDepthTextureId();
    }

    public static int getAfterSolidBlocksDepthTextureId() {
        return afterSolidBlocksTarget.getDepthTextureId();
    }

    public static int getAfterTranslucentBlocksDepthTextureId() {
        return afterTranslucentBlocksTarget.getDepthTextureId();
    }

    /**
     * Destroys all render targets created by the LensRenderTargets class.
     *<p>
     *This method is called when the LensRenderTargets class is about to be shut down.
     *It iterates over all render targets and calls their destroyBuffers method.
     *Afterwards, the render targets are set to null.
     */
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

    /**
     * Destroys all render targets created by the LensRenderTargets class.
     *<p>
     *This method is called when the LensRenderTargets class is about to be shut down.
     *It iterates over all render targets and calls their destroyBuffers method.
     *Afterward, the render targets are set to null.
     *
     * @throws Exception if an error occurs when destroying the render targets
     */
    @Override
    public void close() throws Exception {
        cleanup();
    }
}