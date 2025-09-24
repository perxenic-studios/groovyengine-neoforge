package io.github.luckymcdev.groovyengine.construct.client.editor;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.SelectionCommands;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldedit.session.SessionManager;
import imgui.ImGui;
import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.construct.core.commands.WorldEditCommands;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.util.ImUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class ConstructEditorWindow extends EditorWindow {
    private WorldEdit worldEdit = WorldEdit.getInstance();

    public ConstructEditorWindow() {
        super("Construct Editor", "construct_window");
    }

    @Override
    public void render(ImGuiIO io) {
        ImUtil.window("Construct Editor", () -> {
            ImUtil.collapsingHeader("Selections", () -> {
                // Button to set position 1 to the block the player is looking at.
                ImUtil.button("Pos1", this::handlePos1);
                ImUtil.button("Pos2", this::handlePos2);
            });
        });
    }


    private void handlePos1() {
        WorldEditCommands.sendWeCommand("pos1");
    }

    private void handlePos2() {
        WorldEditCommands.sendWeCommand("pos2");
    }
}
