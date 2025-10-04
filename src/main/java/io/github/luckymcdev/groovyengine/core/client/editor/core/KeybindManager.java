package io.github.luckymcdev.groovyengine.core.client.editor.core;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class KeybindManager {

    public static final KeyMapping TOGGLE_IMGUI = new KeyMapping(
            "key.groovyengine.toggle_imgui",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F6, // Default to F6, can be changed by user
            "key.categories.groovyengine"
    );

    public static final KeyMapping OPEN_EDITOR_SCREEN = new KeyMapping(
            "key.groovyengine.open_editor_screen",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F2, // Default to F2, can be changed by user
            "key.categories.groovyengine"
    );

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_IMGUI);
        event.register(OPEN_EDITOR_SCREEN);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null && !(mc.screen instanceof EditorScreen)) {
            return; // Don't handle keybinds when other screens are open
        }

        if (TOGGLE_IMGUI.consumeClick()) {
            toggleImGui();
        }

        if (OPEN_EDITOR_SCREEN.consumeClick()) {
            openEditorScreen();
            enableImGui();
        }
    }

    private static void toggleImGui() {
        EditorState currentState = GroovyEngineEditor.getEditorState();
        if (currentState == EditorState.ENABLED) {
            GroovyEngineEditor.setEditorState(EditorState.DISABLED);
        } else {
            GroovyEngineEditor.setEditorState(EditorState.ENABLED);
        }
    }

    private static void enableImGui() {
        GroovyEngineEditor.setEditorState(EditorState.ENABLED);
    }

    private static void openEditorScreen() {
        Minecraft.getInstance().setScreen(new EditorScreen());
    }
}