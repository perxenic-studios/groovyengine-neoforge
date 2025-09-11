package io.github.luckymcdev.groovyengine.core.systems.imgui;

import com.mojang.blaze3d.platform.Window;
import io.github.luckymcdev.groovyengine.core.systems.editor.EditorScreen;
import io.github.luckymcdev.groovyengine.core.systems.editor.EditorState;
import io.github.luckymcdev.groovyengine.core.systems.editor.GroovyEngineEditor;
import imgui.ImGui;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
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

                int dirtTextureId = TextureHelper.loadMinecraftTexture(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/dirt.png"));
                ImGui.end();
            }

        });
    }
}
