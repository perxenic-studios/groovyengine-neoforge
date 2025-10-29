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

package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.lens.client.rendering.core.LensRenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

import static com.mojang.blaze3d.platform.GlConst.GL_DRAW_FRAMEBUFFER;

@OnlyIn(Dist.CLIENT)
public abstract class PostProcessShader {
    protected static final Minecraft MC = Minecraft.getInstance();
    /**
     * Being updated every frame before calling applyPostProcess() by PostProcessHandler
     */
    public static Matrix4f viewModelMatrix;
    public static final Collection<Pair<String, Consumer<Uniform>>> COMMON_UNIFORMS = Lists.newArrayList(
            Pair.of("cameraPos", u -> u.set(new Vector3f(MC.gameRenderer.getMainCamera().getPosition().toVector3f()))),
            Pair.of("lookVector", u -> u.set(MC.gameRenderer.getMainCamera().getLookVector())),
            Pair.of("upVector", u -> u.set(MC.gameRenderer.getMainCamera().getUpVector())),
            Pair.of("leftVector", u -> u.set(MC.gameRenderer.getMainCamera().getLeftVector())),
            Pair.of("invViewMat", u -> u.set(PostProcessShader.viewModelMatrix.invert(new Matrix4f()))),
            Pair.of("invProjMat", u -> u.set(RenderSystem.getProjectionMatrix().invert(new Matrix4f()))),
            Pair.of("nearPlaneDistance", u -> u.set(GameRenderer.PROJECTION_Z_NEAR)),
            Pair.of("farPlaneDistance", u -> u.set(MC.gameRenderer.getDepthFar())),
            Pair.of("fov", u -> u.set((float) Math.toRadians(MC.gameRenderer.getFov(MC.gameRenderer.getMainCamera(), MC.getTimer().getGameTimeDeltaPartialTick(false), true)))),
            Pair.of("aspectRatio", u -> u.set((float) MC.getWindow().getWidth() / (float) MC.getWindow().getHeight())),
            Pair.of("bobOffset", u -> u.set(PostProcessShader.viewModelMatrix.invert(new Matrix4f()).transformPosition(LensRenderSystem.getViewBobOffset(), new Vector3f())))
    );
    protected PostChain postChain;
    protected EffectInstance[] effects;
    protected double time;
    private boolean initialized = false;
    private RenderTarget tempDepthBuffer;
    private Collection<Pair<Uniform, Consumer<Uniform>>> defaultUniforms;
    private boolean isActive = true;

    /**
     * Example: "octus:foo" points to octus:shaders/post/foo.json
     */
    public abstract ResourceLocation getPostChainLocation();

    public void init() {
        loadPostChain();

        if (postChain != null) {
            tempDepthBuffer = postChain.getTempTarget("depthMain");

            defaultUniforms = new ArrayList<>();
            for (EffectInstance e : effects) {
                for (Pair<String, Consumer<Uniform>> pair : COMMON_UNIFORMS) {
                    Uniform u = e.getUniform(pair.getFirst());
                    if (u != null) {
                        defaultUniforms.add(Pair.of(u, pair.getSecond()));
                    }
                }
            }
        }

        initialized = true;
    }

    /**
     * Load or reload the shader
     */
    public final void loadPostChain() {
        if (postChain != null) {
            postChain.close();
            postChain = null;
        }

        try {
            ResourceLocation file = getPostChainLocation();
            file = ResourceLocation.fromNamespaceAndPath(file.getNamespace(), "shaders/post/" + file.getPath() + ".json");
            postChain = new PostChain(
                    MC.getTextureManager(),
                    MC.getResourceManager(),
                    MC.getMainRenderTarget(),
                    file
            );
            postChain.resize(MC.getWindow().getWidth(), MC.getWindow().getHeight());
            effects = postChain.passes.stream().map(PostPass::getEffect).toArray(EffectInstance[]::new);
        } catch (IOException | JsonParseException e) {
            GE.LENS_LOG.error("Failed to load post-processing shader: ", e);
        }
    }

    public final void copyDepthBuffer() {
        if (isActive) {
            if (postChain == null || tempDepthBuffer == null) return;

            tempDepthBuffer.copyDepthFrom(MC.getMainRenderTarget());

            // rebind the main framebuffer so that we don't mess up other things
            GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, MC.getMainRenderTarget().frameBufferId);
        }
    }

    public void resize(int width, int height) {
        if (postChain != null) {
            postChain.resize(width, height);
            if (tempDepthBuffer != null)
                tempDepthBuffer.resize(width, height, Minecraft.ON_OSX);
        }
    }

    public void addUniform(String name, Consumer<Uniform> uniform) {
        Pair<String, Consumer<Uniform>> uniformPair = Pair.of(name, uniform);

        for (EffectInstance e : effects) {
            Uniform u = e.getUniform(uniformPair.getFirst());
            if (u != null) {
                defaultUniforms.add(Pair.of(u, uniformPair.getSecond()));
            }
        }
    }

    private void applyDefaultUniforms() {
        Arrays.stream(effects).forEach(e -> e.safeGetUniform("time").set((float) time));

        defaultUniforms.forEach(pair -> pair.getSecond().accept(pair.getFirst()));
    }

    public final void applyPostProcess() {
        if (isActive) {
            if (!initialized)
                init();

            if (postChain != null) {
                time += MC.getTimer().getGameTimeDeltaPartialTick(false) / 20.0;

                beforeProcess(viewModelMatrix);

                applyDefaultUniforms();
                if (!isActive) return;
                postChain.process(MC.getTimer().getGameTimeDeltaPartialTick(false));

                GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, MC.getMainRenderTarget().frameBufferId);
                afterProcess();
            }
        }
    }

    /**
     * Set uniforms and bind textures here
     */
    public abstract void beforeProcess(Matrix4f viewModelMatrix);

    /**
     * Unbind textures
     */
    public abstract void afterProcess();

    public final boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;

        if (!active)
            time = 0.0;
    }

}