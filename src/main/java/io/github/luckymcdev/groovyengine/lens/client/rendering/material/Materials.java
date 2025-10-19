package io.github.luckymcdev.groovyengine.lens.client.rendering.material;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Materials {
    private Materials() {}

    // Factory methods
    public static MaterialBuilder builder(String name) {
        return new MaterialBuilder(name);
    }

    public static MaterialBuilder builder(ResourceLocation texture) {
        return new MaterialBuilder(texture);
    }

    public static MaterialBuilder builder() {
        return new MaterialBuilder(TextureAtlas.LOCATION_BLOCKS);
    }

    // Quick material creation
    public static Material createSolid(ResourceLocation texture) {
        return builder(texture)
                .solidShader()
                .opaque()
                .build();
    }

    public static Material createCutout(ResourceLocation texture) {
        return builder(texture)
                .cutoutShader()
                .opaque()
                .build();
    }

    public static Material createTranslucent(ResourceLocation texture) {
        return builder(texture)
                .translucentShader()
                .translucent()
                .build();
    }

    public static Material createEntity(ResourceLocation texture) {
        return builder(texture)
                .entityShader()
                .opaque()
                .noCull()
                .lightmap(true)
                .overlay(true)
                .build();
    }

    // Common predefined materials (updated)
    public static final Material SOLID = createSolid(TextureAtlas.LOCATION_BLOCKS)
            .withName("solid_blocks");

    public static final Material CUTOUT = createCutout(TextureAtlas.LOCATION_BLOCKS)
            .withName("cutout_blocks");

    public static final Material TRANSLUCENT = createTranslucent(TextureAtlas.LOCATION_BLOCKS)
            .withName("translucent_blocks");

    public static final Material ENTITY_SOLID = builder()
            .name("entity_solid")
            .entityShader()
            .opaque()
            .noCull()
            .lightmap(true)
            .overlay(true)
            .build();
}