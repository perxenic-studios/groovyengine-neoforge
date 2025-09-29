package io.github.luckymcdev.groovyengine.lens.client.editor;

import imgui.ImGui;
import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import io.github.luckymcdev.groovyengine.core.client.imgui.styles.ImGraphics;
import io.github.luckymcdev.groovyengine.core.client.imgui.util.ImUtil;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.CrtPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.SuperDuperPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.target.LensRenderTargets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderingDebuggingWindow extends EditorWindow {

    public static boolean lightingChangesEnabled = false;

    public RenderingDebuggingWindow() {
        super(ImIcons.CAMERA.get() + " Rendering Debug", "rendering_debug");
    }

    @Override
    public void render(ImGuiIO io) {
        ImUtil.window(title, () -> {
            ImUtil.collapsingHeader(ImIcons.TUNE.get() + " Post Processing", () -> {
                ImUtil.button(ImIcons.APERTURE.get() + " Toggle CrtPostShader", () -> {
                    CrtPostShader.INSTANCE.setActive(!CrtPostShader.INSTANCE.isActive());
                });

                ImUtil.button(ImIcons.APERTURE.get() + " Toggle SuperDuperPostShader", () -> {
                    SuperDuperPostShader.INSTANCE.setActive(!SuperDuperPostShader.INSTANCE.isActive());
                });
            });

            ImUtil.collapsingHeader(ImIcons.TEXTURE.get() + " Texture Preview", () -> {
                ImGui.text(ImIcons.VISIBLE.get() + " Render Minecraft Textures in imgui:");
                float width = 64;
                float height = 64;
                ImGraphics.texture(ResourceLocation.withDefaultNamespace("textures/block/dirt.png"), width, height);
            });

            ImUtil.collapsingHeader(ImIcons.LAYERS.get() + " Render Targets", () -> {
                if (ImGui.beginTabBar("RenderTargetTabs")) {
                    renderRenderTargetTab(ImIcons.SUN.get() + " After Sky", LensRenderTargets.getAfterSkyTextureId());
                    renderRenderTargetTab(ImIcons.BLOCK.get() + " After Solid Blocks", LensRenderTargets.getAfterSolidBlocksTextureId());
                    renderRenderTargetTab(ImIcons.LAYERS.get() + " After Cutout Mipped Blocks", LensRenderTargets.getAfterCutoutMippedBlocksTextureId());
                    renderRenderTargetTab(ImIcons.PERSON.get() + " After Entities", LensRenderTargets.getAfterEntitiesTextureId());
                    renderRenderTargetTab(ImIcons.FRAMED_CUBE.get() + " After Block Entities", LensRenderTargets.getAfterBlockEntitiesTextureId());
                    renderRenderTargetTab(ImIcons.BLUR.get() + " After Translucent Blocks", LensRenderTargets.getAfterTranslucentBlocksTextureId());
                    renderRenderTargetTab(ImIcons.SLASH.get() + " After Tripwire Blocks", LensRenderTargets.getAfterTripwireBlocksTextureId());
                    renderRenderTargetTab(ImIcons.ANIMATION.get() + " After Particles", LensRenderTargets.getAfterParticlesTextureId());
                    renderRenderTargetTab(ImIcons.WEATHER.get() + " After Weather", LensRenderTargets.getAfterWeatherTextureId());
                    renderRenderTargetTab(ImIcons.WORLD.get() + " After Level", LensRenderTargets.getAfterLevelTextureId());
                    renderRenderTargetTabFlipped(ImIcons.CAMERA.get() + " After PostProcessing", Minecraft.getInstance().getMainRenderTarget().getColorTextureId());
                    ImGui.endTabBar();
                }
            });

            ImUtil.collapsingHeader(ImIcons.LIGHTBULB.get() + " Lighting Changes", () -> {
                ImUtil.button(ImIcons.TOGGLE_ON.get() + " Toggle Lighting Changes", () -> {
                    lightingChangesEnabled = !lightingChangesEnabled;
                });
                ImGui.sameLine();
                ImGui.text("" + lightingChangesEnabled);

                ImGui.text("Current Light Texture: ");
                LightTexture lightText = Minecraft.getInstance().gameRenderer.lightTexture();
                float lightWidth = 500;
                float lightHeight = 500;
                ImGui.image(lightText.lightTexture.getId(), lightWidth, lightHeight);
            });
        });
    }

    private void renderRenderTargetTab(String label, int textureId) {
        if (ImGui.beginTabItem(label)) {
            renderRenderTarget(label, textureId);
            ImGui.endTabItem();
        }
    }

    private void renderRenderTargetTabFlipped(String label, int textureId) {
        if (ImGui.beginTabItem(label)) {
            renderRenderTargetFlipped(label, textureId);
            ImGui.endTabItem();
        }
    }

    private void renderRenderTarget(String label, int textureId) {
        float mcWidth = Minecraft.getInstance().getWindow().getWidth();
        float mcHeight = Minecraft.getInstance().getWindow().getHeight();
        float displayWidth = mcWidth / 2;
        float displayHeight = mcHeight / 2;

        ImGui.text(label + " (ID: " + textureId + ")");

        if (textureId > 0) {
            ImGui.image(textureId, displayWidth, displayHeight);

            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.text("Texture ID: " + textureId);
                ImGui.text("Size: " + (int)displayWidth + "x" + (int)displayHeight);
                ImGui.text(label);
                ImGui.endTooltip();
            }
        } else {
            ImGui.text("[ No texture captured ]");
            ImGui.dummy(displayWidth, displayHeight);
        }
    }

    private void renderRenderTargetFlipped(String label, int textureId) {
        float mcWidth = Minecraft.getInstance().getWindow().getWidth();
        float mcHeight = Minecraft.getInstance().getWindow().getHeight();
        float displayWidth = mcWidth / 2;
        float displayHeight = mcHeight / 2;

        ImGui.text(label + " (ID: " + textureId + ")");

        if (textureId > 0) {
            ImGui.image(textureId, displayWidth, displayHeight, 0f, 1f, 1f, 0f);

            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.text("Texture ID: " + textureId);
                ImGui.text("Size: " + (int)displayWidth + "x" + (int)displayHeight);
                ImGui.text(label);
                ImGui.endTooltip();
            }
        } else {
            ImGui.text("[ No texture captured ]");
            ImGui.dummy(displayWidth, displayHeight);
        }
    }
}