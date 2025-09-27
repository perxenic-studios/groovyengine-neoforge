package io.github.luckymcdev.groovyengine.construct.client.editor;

import imgui.ImGui;
import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.construct.api.worldedit.WorldEditAPI;
import io.github.luckymcdev.groovyengine.construct.client.input.KeyCombo;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.util.ImUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class ConstructEditorWindow extends EditorWindow {
    private static final WorldEditAPI editor = WorldEditAPI.INSTANCE;
    private static final Minecraft mc = Minecraft.getInstance();

    private final Map<KeyCombo, Runnable> macros = new HashMap<>();
    private final Map<KeyCombo, Boolean> pressedLast = new HashMap<>();

    private final int[] brushRadius = new int[]{5}; // default radius

    public ConstructEditorWindow() {
        super("Construct Editor", "construct_window");
        initMacros();
    }


    private void initMacros() {
        macros.put(new KeyCombo(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_Z), editor::undo);
        macros.put(new KeyCombo(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_Y), editor::redo);
        macros.put(new KeyCombo(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_C), editor::copy);
        macros.put(new KeyCombo(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_V), editor::paste);
    }

    @Override
    public void render(ImGuiIO io) {

        handleKeys();
        handleScroll(io);

        // Main Construct Editor Window
        ImUtil.window("Construct Editor", () -> {

            // SELECTIONS
            ImUtil.collapsingHeader("Selections", () -> {
                ImUtil.button("Pos1", () -> editor.setPos1());
                ImUtil.button("Pos2", () -> editor.setPos2());
                ImUtil.button("Clear History", () -> editor.clearHistory());
            });

            // TRANSFORM
            ImUtil.collapsingHeader("Transform", () -> {
                ImUtil.button("Expand 5", () -> editor.expand(5));
                ImUtil.button("Contract 5", () -> editor.contract(5));
                ImUtil.button("Shift Up 3", () -> editor.shift(3, "up"));
            });

            // CLIPBOARD
            ImUtil.collapsingHeader("Clipboard", () -> {
                ImUtil.button("Copy", editor::copy);
                ImUtil.button("Cut", editor::cut);
                ImUtil.button("Paste", editor::paste);
                ImUtil.button("Undo", editor::undo);
                ImUtil.button("Redo", editor::redo);
            });

            ImUtil.collapsingHeader("Brushes", () -> {
                ImUtil.collapsingHeader("Brushes", () -> {
                    if (ImGui.sliderInt("Brush Radius", brushRadius, 1, 50)) {
                        // optional: live feedback if needed
                    }

                    ImUtil.button("Stone Sphere", () -> {
                        giveOrUpdateBrush("sphere", "minecraft:stone", brushRadius[0]);
                    });

                    ImUtil.button("Dirt Cylinder", () -> {
                        giveOrUpdateBrush("cylinder", "minecraft:dirt", brushRadius[0]);
                    });
                });
            });

            // UTILITIES
            ImUtil.collapsingHeader("Utilities", () -> {
                ImUtil.button("Count Blocks", () -> editor.count("minecraft:stone"));
                ImUtil.button("Size", editor::size);
                ImUtil.button("Distr", editor::distr);
            });

        });
    }

    private void giveOrUpdateBrush(String type, String pattern, int radius) {
        // run WorldEdit command
        editor.brush(type, pattern, radius);

        var player = mc.player;
        var inHand = player.getMainHandItem();

        if (inHand.getItem() == Items.BRUSH) {
            // override existing brush item
            inHand.set(DataComponents.CUSTOM_NAME, Component.literal("Brush: " + type + " " + pattern + " " + radius));
        } else {
            // give new brush item
            ItemStack brush = new ItemStack(Items.BRUSH);
            brush.set(DataComponents.CUSTOM_NAME, Component.literal("Brush: " + type + " " + pattern + " " + radius));
            player.addItem(brush);
        }
    }

    private void handleKeys() {
        long window = mc.getWindow().getWindow();

        for (var entry : macros.entrySet()) {
            KeyCombo combo = entry.getKey();
            boolean isPressed = GLFW.glfwGetKey(window, combo.modifier()) == GLFW.GLFW_PRESS &&
                    GLFW.glfwGetKey(window, combo.key()) == GLFW.GLFW_PRESS;
            boolean wasPressed = pressedLast.getOrDefault(combo, false);

            if (isPressed && !wasPressed) {
                entry.getValue().run(); // trigger macro
            }

            pressedLast.put(combo, isPressed);
        }
    }

    private void handleScroll(ImGuiIO io) {
        float scroll = io.getMouseWheel();
        if (scroll == 0) return;

        io.setMouseWheel(0); // stop hotbar scroll
        Vec3 look = mc.player.getLookAngle();
        String dir = getDirectionFromVec(look);
        int amount = (int) Math.abs(scroll);

        if (io.getKeyCtrl()) {
            if (scroll > 0) editor.expand(amount, dir);
            else editor.contract(amount, invertDirection(dir));
        } else if (io.getKeyAlt()) {
            if (scroll > 0) editor.shift(amount, dir);
            else editor.shift(-amount, dir);
        }
    }


    private String getDirectionFromVec(Vec3 look) {
        double absX = Math.abs(look.x);
        double absY = Math.abs(look.y);
        double absZ = Math.abs(look.z);

        if (absY >= absX && absY >= absZ) return look.y > 0 ? "up" : "down";
        if (absZ >= absX && absZ >= absY) return look.z > 0 ? "south" : "north";
        return look.x > 0 ? "east" : "west";
    }

    private String invertDirection(String dir) {
        return switch (dir) {
            case "up" -> "down";
            case "down" -> "up";
            case "north" -> "south";
            case "south" -> "north";
            case "east" -> "west";
            case "west" -> "east";
            default -> dir;
        };
    }

}
