package io.github.luckymcdev.groovyengine.lens.client.rendering.core;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core.test.TestShader;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class GERenderTypes extends RenderStateShard {
    public static final RenderType CUSTOM_QUADS = RenderType.create(
            "groovyengine:custom_quads",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new ShaderStateShard(TestShader.INSTANCE::getShader))
                    .setTextureState(new TextureStateShard(ResourceLocation.withDefaultNamespace("textures/block/dirt.png"), false, false))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setLightmapState(NO_LIGHTMAP)
                    .setOverlayState(NO_OVERLAY)
                    .createCompositeState(false)
    );

    private GERenderTypes(String name, Runnable setup, Runnable clear) {
        super(name, setup, clear);
    }
}