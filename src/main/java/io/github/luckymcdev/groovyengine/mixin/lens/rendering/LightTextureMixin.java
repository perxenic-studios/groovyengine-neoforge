package io.github.luckymcdev.groovyengine.mixin.lens.rendering;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.*;

import java.util.HashMap;
import java.util.Map;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin {

    @Shadow
    private boolean updateLightTexture;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract float getDarknessGamma(float partialTick);

    @Shadow
    protected abstract float calculateDarknessScale(LivingEntity entity, float gamma, float partialTick);

    @Shadow
    public static float getBrightness(DimensionType dimensionType, int lightLevel) {
        return 0;
    }
    @Shadow
    @Final
    private DynamicTexture lightTexture;

    @Shadow
    private float blockLightRedFlicker;

    @Shadow
    private static void clampColor(Vector3f color) {
    }

    @Shadow
    @Final
    private GameRenderer renderer;

    @Shadow
    protected abstract float notGamma(float value);

    @Shadow
    @Final
    private NativeImage lightPixels;

    // Block color mapping - RGB values from 0.0 to 1.0
    private static final Map<Block, Vector3f> BLOCK_COLORS = new HashMap<>();

    static {
        // Fire/warm lights
        BLOCK_COLORS.put(Blocks.TORCH, new Vector3f(1.0f, 0.6f, 0.2f));
        BLOCK_COLORS.put(Blocks.WALL_TORCH, new Vector3f(1.0f, 0.6f, 0.2f));
        BLOCK_COLORS.put(Blocks.CAMPFIRE, new Vector3f(1.0f, 0.4f, 0.1f));
        BLOCK_COLORS.put(Blocks.SOUL_CAMPFIRE, new Vector3f(0.2f, 0.6f, 1.0f));
        BLOCK_COLORS.put(Blocks.FIRE, new Vector3f(1.0f, 0.5f, 0.1f));
        BLOCK_COLORS.put(Blocks.SOUL_FIRE, new Vector3f(0.2f, 0.6f, 1.0f));
        BLOCK_COLORS.put(Blocks.SOUL_TORCH, new Vector3f(0.2f, 0.6f, 1.0f));
        BLOCK_COLORS.put(Blocks.SOUL_WALL_TORCH, new Vector3f(0.2f, 0.6f, 1.0f));

        // Lanterns
        BLOCK_COLORS.put(Blocks.LANTERN, new Vector3f(1.0f, 0.7f, 0.3f));
        BLOCK_COLORS.put(Blocks.SOUL_LANTERN, new Vector3f(0.2f, 0.6f, 1.0f));

        // Glowstone and similar
        BLOCK_COLORS.put(Blocks.GLOWSTONE, new Vector3f(1.0f, 0.5f, 0.0f));
        BLOCK_COLORS.put(Blocks.SHROOMLIGHT, new Vector3f(1.0f, 0.5f, 0.0f));
        BLOCK_COLORS.put(Blocks.SEA_LANTERN, new Vector3f(0.0f, 0.5f, 1.0f));

        // Redstone
        BLOCK_COLORS.put(Blocks.REDSTONE_TORCH, new Vector3f(1.0f, 0.0f, 0.0f));
        BLOCK_COLORS.put(Blocks.REDSTONE_WALL_TORCH, new Vector3f(1.0f, 0.0f, 0.0f));
        BLOCK_COLORS.put(Blocks.REDSTONE_BLOCK, new Vector3f(1.0f, 0.0f, 0.0f));

        // Lava
        BLOCK_COLORS.put(Blocks.LAVA, new Vector3f(1.0f, 0.3f, 0.1f));

        // End-related
        BLOCK_COLORS.put(Blocks.END_ROD, new Vector3f(0.9f, 0.8f, 1.0f));

        // Amethyst
        BLOCK_COLORS.put(Blocks.AMETHYST_CLUSTER, new Vector3f(0.7f, 0.4f, 1.0f));

        BLOCK_COLORS.put(Blocks.COPPER_BULB, new Vector3f(1.0f, 0.6f, 0.3f));
    }

    /**
     * Get the color tint for a light source block
     */
    private Vector3f getBlockLightColor(Block block) {
        return BLOCK_COLORS.getOrDefault(block, new Vector3f(1.0f, 0.5f, 0.2f)); // Default warm color
    }

    @Unique
    public DynamicTexture getLightTexture() {
        return lightTexture;
    }

    /**
     * Get the appropriate light color for a specific light level combination
     * This samples a smaller, more localized area to avoid sudden changes
     */
    private Vector3f getLocalizedLightColor(ClientLevel level, int blockLightLevel) {
        if (level == null || this.minecraft.player == null) {
            return new Vector3f(1.0f, 0.5f, 0.2f); // Default warm color
        }

        BlockPos playerPos = this.minecraft.player.blockPosition();
        Map<Vector3f, Float> colorWeights = new HashMap<>();
        int radius = 4; // Smaller radius for more localized effect

        for (int x = -radius; x <= radius; x++) {
            for (int y = -2; y <= 2; y++) { // Smaller vertical range
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = playerPos.offset(x, y, z);
                    Block block = level.getBlockState(checkPos).getBlock();

                    if (BLOCK_COLORS.containsKey(block)) {
                        Vector3f color = BLOCK_COLORS.get(block);
                        float distance = (float) Math.sqrt(x*x + y*y + z*z);
                        float weight = Math.max(0.1f, 1.0f / (1.0f + distance * 0.5f)); // Distance-based weighting

                        colorWeights.put(color, colorWeights.getOrDefault(color, 0.0f) + weight);
                    }
                }
            }
        }

        if (colorWeights.isEmpty()) {
            lastCalculatedColor = new Vector3f(1.0f, 0.5f, 0.2f); // Default warm color
            return lastCalculatedColor;
        }

        // Get the most weighted color
        Vector3f dominantColor = colorWeights.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(new Vector3f(1.0f, 0.5f, 0.2f));

        // Smooth transition to avoid sudden changes
        if (lastCalculatedColor != null) {
            dominantColor = new Vector3f(lastCalculatedColor).lerp(dominantColor, 0.3f);
        }

        lastCalculatedColor = dominantColor;
        return dominantColor;
    }

    // Cache the last calculated color to reduce flickering
    private Vector3f lastCalculatedColor = null;

    /**
     * @author
     * @reason Custom block light coloring
     */
    @Overwrite
    public void updateLightTexture(float partialTicks) {
        if (this.updateLightTexture) {
            this.updateLightTexture = false;
            this.minecraft.getProfiler().push("lightTex");
            ClientLevel clientLevel = this.minecraft.level;
            if (clientLevel != null) {
                float skyDarken = clientLevel.getSkyDarken(1.0F);
                float skyBrightness;
                if (clientLevel.getSkyFlashTime() > 0) {
                    skyBrightness = 1.0F;
                } else {
                    skyBrightness = skyDarken * 0.95F + 0.05F;
                }

                float darknessEffectScale = ((Double)this.minecraft.options.darknessEffectScale().get()).floatValue();
                float darknessGamma = this.getDarknessGamma(partialTicks) * darknessEffectScale;
                float darknessScale = this.calculateDarknessScale(this.minecraft.player, darknessGamma, partialTicks) * darknessEffectScale;
                float waterVision = this.minecraft.player.getWaterVision();
                float nightVisionScale;
                if (this.minecraft.player.hasEffect(MobEffects.NIGHT_VISION)) {
                    nightVisionScale = GameRenderer.getNightVisionScale(this.minecraft.player, partialTicks);
                } else if (waterVision > 0.0F && this.minecraft.player.hasEffect(MobEffects.CONDUIT_POWER)) {
                    nightVisionScale = waterVision;
                } else {
                    nightVisionScale = 0.0F;
                }

                Vector3f baseSkyColor = (new Vector3f(skyDarken, skyDarken, 1.0F)).lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
                float blockLightFactor = this.blockLightRedFlicker + 1.5F;
                Vector3f finalColor = new Vector3f();

                // Get localized light color (cached and smoothed)
                Vector3f dominantLightColor = getLocalizedLightColor(clientLevel, 0);

                for(int skyLightIndex = 0; skyLightIndex < 16; ++skyLightIndex) {
                    for(int blockLightIndex = 0; blockLightIndex < 16; ++blockLightIndex) {
                        float skyLightValue = getBrightness(clientLevel.dimensionType(), skyLightIndex) * skyBrightness;
                        float blockLightValue = getBrightness(clientLevel.dimensionType(), blockLightIndex) * blockLightFactor;

                        // Calculate sky light color (always use original)
                        Vector3f skyLightColor = (new Vector3f(baseSkyColor)).mul(skyLightValue);

                        // Apply red tint to block light, but exclude very bright combinations used by UI
                        // UI typically uses high block light + high sky light combinations
                        boolean isUIBrightness = (skyLightIndex >= 15 && blockLightIndex >= 15) ||
                                (skyLightValue >= 0.95F && blockLightValue >= 0.95F);

                        Vector3f blockLightColor;
                        if (!isUIBrightness && blockLightIndex > 0) {
                            // Apply color based on dominant light source
                            blockLightColor = new Vector3f(
                                    blockLightValue * dominantLightColor.x,  // R
                                    blockLightValue * dominantLightColor.y,  // G
                                    blockLightValue * dominantLightColor.z   // B
                            );
                        } else {
                            // Use vanilla white block light
                            blockLightColor = new Vector3f(blockLightValue, blockLightValue, blockLightValue);
                        }

                        // Combine sky and block light
                        finalColor.set(skyLightColor).add(blockLightColor);

                        boolean forceBrightLightmap = clientLevel.effects().forceBrightLightmap();
                        if (forceBrightLightmap) {
                            finalColor.lerp(new Vector3f(0.99F, 1.12F, 1.0F), 0.25F);
                            clampColor(finalColor);
                        } else {
                            finalColor.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                            if (this.renderer.getDarkenWorldAmount(partialTicks) > 0.0F) {
                                float darkenAmount = this.renderer.getDarkenWorldAmount(partialTicks);
                                Vector3f darkenedColor = (new Vector3f(finalColor)).mul(0.7F, 0.6F, 0.6F);
                                finalColor.lerp(darkenedColor, darkenAmount);
                            }
                        }

                        clientLevel.effects().adjustLightmapColors(clientLevel, partialTicks, skyDarken, blockLightFactor, skyLightValue, blockLightIndex, skyLightIndex, finalColor);

                        if (nightVisionScale > 0.0F) {
                            float maxComponent = Math.max(finalColor.x(), Math.max(finalColor.y(), finalColor.z()));
                            if (maxComponent < 1.0F) {
                                float scaleFactor = 1.0F / maxComponent;
                                Vector3f scaledColor = (new Vector3f(finalColor)).mul(scaleFactor);
                                finalColor.lerp(scaledColor, nightVisionScale);
                            }
                        }

                        if (!forceBrightLightmap) {
                            if (darknessScale > 0.0F) {
                                finalColor.add(-darknessScale, -darknessScale, -darknessScale);
                            }
                            clampColor(finalColor);
                        }

                        float gammaValue = (this.minecraft.options.gamma().get()).floatValue();
                        Vector3f gammaAdjustedColor = new Vector3f(
                                this.notGamma(finalColor.x),
                                this.notGamma(finalColor.y),
                                this.notGamma(finalColor.z)
                        );
                        finalColor.lerp(gammaAdjustedColor, Math.max(0.0F, gammaValue - darknessGamma));
                        finalColor.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                        clampColor(finalColor);

                        // Convert to int RGB
                        finalColor.mul(255.0F);
                        int red = (int) finalColor.x();
                        int green = (int) finalColor.y();
                        int blue = (int) finalColor.z();
                        int alpha = 255;
                        this.lightPixels.setPixelRGBA(blockLightIndex, skyLightIndex, (alpha << 24) | (blue << 16) | (green << 8) | red);
                    }
                }

                this.lightTexture.upload();
                this.minecraft.getProfiler().pop();
            }
        }
    }
}