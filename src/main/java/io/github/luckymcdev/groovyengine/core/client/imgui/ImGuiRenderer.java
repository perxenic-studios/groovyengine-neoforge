package io.github.luckymcdev.groovyengine.core.client.imgui;

import com.mojang.blaze3d.platform.Window;
import io.github.luckymcdev.groovyengine.construct.client.FlightController;
import io.github.luckymcdev.groovyengine.core.client.editor.EditorScreen;
import io.github.luckymcdev.groovyengine.core.client.editor.EditorState;
import io.github.luckymcdev.groovyengine.core.client.editor.GroovyEngineEditor;
import imgui.ImGui;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.CrtPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.SuperDuperPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.rendertarget.LensRenderTargets;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.GrassBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber
public class ImGuiRenderer {
    @SubscribeEvent
    public static void onRender(RenderGuiEvent.Post event) {
        ImGuiImpl.draw(io -> {

            if(GroovyEngineEditor.getEditorState().equals(EditorState.ENABLED)) {
                Minecraft mc = Minecraft.getInstance();
                Window window = mc.getWindow();

                // Setup docking
                ImGui.setNextWindowBgAlpha(0);
                int mainDock = ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), ImGuiDockNodeFlags.NoDockingInCentralNode);
                imgui.internal.ImGui.dockBuilderGetCentralNode(mainDock).addLocalFlags(imgui.internal.flag.ImGuiDockNodeFlags.NoTabBar);

                ImGui.setNextWindowDockID(mainDock);

                if (ImGui.begin("Main", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoNavInputs | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoCollapse |
                        ImGuiWindowFlags.NoSavedSettings)) {
                    ImGui.end();
                }


                ImGui.showAboutWindow();
                ImGui.showDemoWindow();
                ImGui.showMetricsWindow();

                int[] flySpeedInt = new int[]{(int)(FlightController.CUSTOM_FLY_SPEED * 100)};
                if (ImGui.begin("Movement Debugging")) {
                    if (ImGui.sliderInt("Fly Speed", flySpeedInt, 0, 100)) {
                        FlightController.CUSTOM_FLY_SPEED = flySpeedInt[0] / 100.0f;
                    }
                }
                ImGui.end();

            }

            if(ImGui.begin("EditorDebugging")) {
                if (ImGui.button("Enable ImGui")) {
                    GroovyEngineEditor.setEditorState(EditorState.ENABLED);
                }
                if (ImGui.button("Disable ImGui")) {
                    GroovyEngineEditor.setEditorState(EditorState.DISABLED);
                }
                if (ImGui.button("Set screen to correct screen")) {
                    Minecraft.getInstance().setScreen(new EditorScreen());
                }
            } ImGui.end();




            if(ImGui.begin("Rendering Debugging")) {
                if (ImGui.button("Toggle CrtPostShader")) {
                    CrtPostShader.INSTANCE.setActive(!CrtPostShader.INSTANCE.isActive());
                }

                if (ImGui.button("Toggle SuperDuperPostShader")) {
                    SuperDuperPostShader.INSTANCE.setActive(!SuperDuperPostShader.INSTANCE.isActive());
                }

                ImGui.separator();

                ImGui.text("Render Minecraft Textues in imgui:");

                int dirtTextureId = TextureHelper.loadMinecraftTexture(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/dirt.png"));

                float width = 64;
                float height = 64;

                ImGui.image(dirtTextureId, width, height);

                ImGui.separator();

                ImGui.text("Framebuffers / RenderTargets at stages:");

                // Create tabs for render targets
                if (ImGui.beginTabBar("RenderTargetTabs")) {

                    if (ImGui.beginTabItem("After Sky")) {
                        renderRenderTarget("After Sky", LensRenderTargets.getAfterSkyTextureId());
                        ImGui.endTabItem();
                    }

                    if (ImGui.beginTabItem("After Solid Blocks")) {
                        renderRenderTarget("After Solid Blocks", LensRenderTargets.getAfterSolidBlocksTextureId());
                        ImGui.endTabItem();
                    }

                    if (ImGui.beginTabItem("After Cutout Mipped Blocks")) {
                        renderRenderTarget("After Cutout Mipped Blocks", LensRenderTargets.getAfterCutoutMippedBlocksTextureId());
                        ImGui.endTabItem();
                    }

                    if (ImGui.beginTabItem("After Entities")) {
                        renderRenderTarget("After Entities", LensRenderTargets.getAfterEntitiesTextureId());
                        ImGui.endTabItem();
                    }

                    if (ImGui.beginTabItem("After Block Entities")) {
                        renderRenderTarget("After Block Entities", LensRenderTargets.getAfterBlockEntitiesTextureId());
                        ImGui.endTabItem();
                    }

                    if (ImGui.beginTabItem("After Translucent Blocks")) {
                        renderRenderTarget("After Translucent Blocks", LensRenderTargets.getAfterTranslucentBlocksTextureId());
                        ImGui.endTabItem();
                    }

                    if (ImGui.beginTabItem("After Tripwire Blocks")) {
                        renderRenderTarget("After Tripwire Blocks", LensRenderTargets.getAfterTripwireBlocksTextureId());
                        ImGui.endTabItem();
                    }

                    if (ImGui.beginTabItem("After Particles")) {
                        renderRenderTarget("After Particles", LensRenderTargets.getAfterParticlesTextureId());
                        ImGui.endTabItem();
                    }

                    if (ImGui.beginTabItem("After Weather")) {
                        renderRenderTarget("After Weather", LensRenderTargets.getAfterWeatherTextureId());
                        ImGui.endTabItem();
                    }

                    if (ImGui.beginTabItem("After Level")) {
                        renderRenderTarget("After Level", LensRenderTargets.getAfterLevelTextureId());
                        ImGui.endTabItem();
                    }

                    if (ImGui.beginTabItem("After PostProcessing")) {
                        renderRenderTargetFlipped("After PostProcessing", Minecraft.getInstance().getMainRenderTarget().getColorTextureId());
                        ImGui.endTabItem();
                    }

                    ImGui.endTabBar();
                }
            } ImGui.end();

        });
    }

    private static void renderRenderTarget(String label, int textureId) {
        float mcWidth = Minecraft.getInstance().getWindow().getWidth();
        float mcHeight = Minecraft.getInstance().getWindow().getHeight();
        float displayWidth = mcWidth / 2;  // Larger display in individual tabs
        float displayHeight = mcHeight / 2;

        ImGui.text(label + " (ID: " + textureId + ")");

        if (textureId > 0) {
            ImGui.image(textureId, displayWidth, displayHeight);

            // Add some hover info
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.text("Texture ID: " + textureId);
                ImGui.text("Size: " + (int)displayWidth + "x" + (int)displayHeight);
                ImGui.text(label);
                ImGui.endTooltip();
            }
        } else {
            // Show placeholder when texture isn't available
            ImGui.text("[ No texture captured ]");
            ImGui.dummy(displayWidth, displayHeight); // Reserve space
        }
    }

    private static void renderRenderTargetFlipped(String label, int textureId) {
        float mcWidth = Minecraft.getInstance().getWindow().getWidth();
        float mcHeight = Minecraft.getInstance().getWindow().getHeight();
        float displayWidth = mcWidth / 2;  // Larger display in individual tabs
        float displayHeight = mcHeight / 2;

        ImGui.text(label + " (ID: " + textureId + ")");

        if (textureId > 0) {
            ImGui.image(textureId, displayWidth, displayHeight, 0f, 1f, 1f, 0f); // UVs flipped

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

    // Keep the original methods for backward compatibility if needed elsewhere
    private static void renderLabeledTarget(String label, int textureId, float width, float height) {
        renderRenderTarget(label, textureId);
    }

    private static void renderLabeledTargetFlipped(String label, int textureId, float width, float height) {
        renderRenderTargetFlipped(label, textureId);
    }
}