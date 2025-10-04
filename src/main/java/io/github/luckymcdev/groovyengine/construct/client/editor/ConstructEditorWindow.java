package io.github.luckymcdev.groovyengine.construct.client.editor;

import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import io.github.luckymcdev.groovyengine.core.client.imgui.util.ImUtil;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConstructEditorWindow extends EditorWindow {
    private static final Minecraft mc = Minecraft.getInstance();

    public ConstructEditorWindow() {
        super(ImIcons.WRENCH.get() + " Construct Editor", "construct_window");
    }

    @Override
    public void render(ImGuiIO io) {
        ImUtil.window(title, () -> {

        });
    }
}