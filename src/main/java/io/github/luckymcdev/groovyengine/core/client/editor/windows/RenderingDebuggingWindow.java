package io.github.luckymcdev.groovyengine.core.client.editor.windows;

import imgui.ImGui;
import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.styles.ImGraphics;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.CrtPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.SuperDuperPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.rendertarget.LensRenderTargets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;

public class RenderingDebuggingWindow extends EditorWindow {

    public RenderingDebuggingWindow() {
        super("Rendering Debug", "rendering_debug");
    }

    @Override
    public void render(ImGuiIO io) {
        if (ImGui.begin(title)) {
            // Post-processing controls
            if (ImGui.collapsingHeader("Post Processing")) {
                if (ImGui.button("Toggle CrtPostShader")) {
                    CrtPostShader.INSTANCE.setActive(!CrtPostShader.INSTANCE.isActive());
                }

                if (ImGui.button("Toggle SuperDuperPostShader")) {
                    SuperDuperPostShader.INSTANCE.setActive(!SuperDuperPostShader.INSTANCE.isActive());
                }
            }

            ImGui.separator();

            // Texture preview
            if (ImGui.collapsingHeader("Texture Preview")) {
                ImGui.text("Render Minecraft Textures in imgui:");

                float width = 64;
                float height = 64;

                ImGraphics.texture(ResourceLocation.withDefaultNamespace("textures/block/dirt.png"), width, height);
            }

            ImGui.separator();

            // Render targets
            if (ImGui.collapsingHeader("Render Targets")) {
                if (ImGui.beginTabBar("RenderTargetTabs")) {
                    renderRenderTargetTab("After Sky", LensRenderTargets.getAfterSkyTextureId());
                    renderRenderTargetTab("After Solid Blocks", LensRenderTargets.getAfterSolidBlocksTextureId());
                    renderRenderTargetTab("After Cutout Mipped Blocks", LensRenderTargets.getAfterCutoutMippedBlocksTextureId());
                    renderRenderTargetTab("After Entities", LensRenderTargets.getAfterEntitiesTextureId());
                    renderRenderTargetTab("After Block Entities", LensRenderTargets.getAfterBlockEntitiesTextureId());
                    renderRenderTargetTab("After Translucent Blocks", LensRenderTargets.getAfterTranslucentBlocksTextureId());
                    renderRenderTargetTab("After Tripwire Blocks", LensRenderTargets.getAfterTripwireBlocksTextureId());
                    renderRenderTargetTab("After Particles", LensRenderTargets.getAfterParticlesTextureId());
                    renderRenderTargetTab("After Weather", LensRenderTargets.getAfterWeatherTextureId());
                    renderRenderTargetTab("After Level", LensRenderTargets.getAfterLevelTextureId());
                    renderRenderTargetTabFlipped("After PostProcessing", Minecraft.getInstance().getMainRenderTarget().getColorTextureId());

                    ImGui.endTabBar();
                }
            }

            // Light texture
            if (ImGui.collapsingHeader("Light Texture")) {
                LightTexture lightText = Minecraft.getInstance().gameRenderer.lightTexture();
                float lightWidth = 500;
                float lightHeight = 500;

                ImGui.image(lightText.lightTexture.getId(), lightWidth, lightHeight);
            }
        }
        ImGui.end();
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