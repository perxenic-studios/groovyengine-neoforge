package io.github.luckymcdev.groovyengine.construct.client.editor;

import imgui.ImGui;
import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.construct.api.worldedit.WorldEditAPI;
import io.github.luckymcdev.groovyengine.construct.client.input.KeyCombo;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import io.github.luckymcdev.groovyengine.core.client.imgui.util.ImUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ConstructEditorWindow extends EditorWindow {
    private static final WorldEditAPI editor = WorldEditAPI.INSTANCE;
    private static final Minecraft mc = Minecraft.getInstance();

    private final Map<KeyCombo, Runnable> macros = new HashMap<>();
    private final Map<KeyCombo, Boolean> pressedLast = new HashMap<>();

    private final int[] brushRadius = new int[]{5}; // default radius

    private void initMacros() {
        macros.put(new KeyCombo(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_Z), editor::undo);
        macros.put(new KeyCombo(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_Y), editor::redo);
        macros.put(new KeyCombo(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_C), editor::copy);
        macros.put(new KeyCombo(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_V), editor::paste);
    }

    public ConstructEditorWindow() {
        super(ImIcons.WRENCH.get() + " Construct Editor", "construct_window");
        initMacros();
    }

    @Override
    public void render(ImGuiIO io) {
        // Only handle input if WorldEdit is loaded
        if (editor.isAvailable()) {
            handleKeys();
            handleScroll(io);
        }

        ImUtil.window(title, () -> {
            if (!editor.isAvailable()) {
                ImGui.text(ImIcons.ERROR.get() + " WorldEdit is not loaded!");
                ImGui.text("This editor requires WorldEdit to function.");
                return;
            }

            ImUtil.collapsingHeader(ImIcons.SELECT.get() + " Selections", () -> {
                ImUtil.button(ImIcons.TARGET.get() + " Pos1", () -> editor.setPos1());
                ImUtil.button(ImIcons.TARGET.get() + " Pos2", () -> editor.setPos2());
                ImUtil.button(ImIcons.DELETE.get() + " Clear History", () -> editor.clearHistory());
            });

            ImUtil.collapsingHeader(ImIcons.ROTATE.get() + " Transform", () -> {
                ImUtil.button(ImIcons.EXPAND.get() + " Expand 5", () -> editor.expand(5));
                ImUtil.button(ImIcons.COLLAPSE.get() + " Contract 5", () -> editor.contract(5));
                ImUtil.button(ImIcons.ARROW_UP.get() + " Shift Up 3", () -> editor.shift(3, "up"));
            });

            ImUtil.collapsingHeader(ImIcons.COPY.get() + " Clipboard", () -> {
                ImUtil.button(ImIcons.COPY.get() + " Copy", editor::copy);
                ImUtil.button(ImIcons.CUT.get() + " Cut", editor::cut);
                ImUtil.button(ImIcons.PASTE.get() + " Paste", editor::paste);
                ImUtil.button(ImIcons.UNDO.get() + " Undo", editor::undo);
                ImUtil.button(ImIcons.REDO.get() + " Redo", editor::redo);
            });

            ImUtil.collapsingHeader(ImIcons.BRUSH.get() + " Brushes", () -> {
                if (ImGui.sliderInt(ImIcons.SQUARE.get() + " Brush Radius", brushRadius, 1, 50)) {
                    // optional: live feedback if needed
                }

                ImUtil.button(ImIcons.CIRCLE.get() + " Stone Sphere", () -> {
                    giveOrUpdateBrush("sphere", "minecraft:stone", brushRadius[0]);
                });

                ImUtil.button(ImIcons.HEXAGON.get() + " Dirt Cylinder", () -> {
                    giveOrUpdateBrush("cylinder", "minecraft:dirt", brushRadius[0]);
                });
            });

            ImUtil.collapsingHeader(ImIcons.SETTINGS.get() + " Utilities", () -> {
                ImUtil.button(ImIcons.NUMBERS.get() + " Count Blocks", () -> editor.count("minecraft:stone"));
                ImUtil.button(ImIcons.FRAMED_CUBE.get() + " Size", editor::size);
                ImUtil.button(ImIcons.PERCENT.get() + " Distr", editor::distr);
            });
        });
    }

    private void giveOrUpdateBrush(String type, String pattern, int radius) {
        // run WorldEdit command
        editor.brush(type, pattern, radius);

        var player = mc.player;
        if (player == null) return;

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
        if (mc.player == null) return;

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