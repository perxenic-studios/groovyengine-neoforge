package io.github.luckymcdev.groovyengine.construct.core.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldEditCommands {
    private static Player player = Minecraft.getInstance().player;

    public static void sendCommand(String command) {
        if (player == null) player = Minecraft.getInstance().player;
        if (player == null) return;

        String clean = command.startsWith("/") ? command : "/" + command;
        localSend(clean);
    }

    public static void sendWeCommand(String command) {
        if (player == null) player = Minecraft.getInstance().player;
        if (player == null) return;

        // ensure starts with two slashes
        String clean = command.startsWith("//") ? command : "//" + command;
        localSend(clean);
    }

    // internal helper to actually send
    private static void localSend(String command) {
        if (player instanceof LocalPlayer local) {
            local.connection.sendCommand(command.substring(1)); // remove the first slash
        }
    }

    // simplified execute methods
    public static void executeCommand(String command, String... params) {
        String full = buildCommand(command, params);
        sendCommand(full);
    }

    public static void executeWeCommand(String command, String... params) {
        String full = buildCommand(command, params);
        sendWeCommand(full);
    }

    private static String buildCommand(String base, String... params) {
        StringBuilder sb = new StringBuilder(base);
        for (String p : params) if (p != null && !p.isBlank()) sb.append(" ").append(p.trim());
        return sb.toString();
    }

}