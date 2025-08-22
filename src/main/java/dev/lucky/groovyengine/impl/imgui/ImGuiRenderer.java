package dev.lucky.groovyengine.impl.imgui;

import imgui.ImGui;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber
public class ImGuiRenderer {
    @SubscribeEvent
    public static void onRender(RenderGuiEvent.Post event) {
        ImGuiImpl.draw(io -> {
            ImGui.showAboutWindow();
            ImGui.showDemoWindow();
            ImGui.showMetricsWindow();
        });
    }
}
