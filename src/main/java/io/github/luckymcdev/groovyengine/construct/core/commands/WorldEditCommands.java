package io.github.luckymcdev.groovyengine.construct.core.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WorldEditCommands {
    private static Player player = Minecraft.getInstance().player;

    // Selection Commands
    public static final CommandInfo POS1 = new CommandInfo(
            "pos1",
            "Set position 1 to the block you're looking at",
            "/pos1 [x y z]",
            "1"
    );

    public static final CommandInfo POS2 = new CommandInfo(
            "pos2",
            "Set position 2 to the block you're looking at",
            "/pos2 [x y z]",
            "2"
    );

    public static final CommandInfo WAND = new CommandInfo(
            "wand",
            "Get the selection wand",
            "/wand",
            "tool"
    );

    public static final CommandInfo EXPAND = new CommandInfo(
            "expand",
            "Expand the selection",
            "/expand <amount> [direction]",
            "exp"
    );

    public static final CommandInfo CONTRACT = new CommandInfo(
            "contract",
            "Contract the selection",
            "/contract <amount> [direction]",
            "con"
    );

    public static final CommandInfo SHIFT = new CommandInfo(
            "shift",
            "Shift the selection",
            "/shift <amount> [direction]"
    );

    public static final CommandInfo SIZE = new CommandInfo(
            "size",
            "Get the selection size",
            "/size"
    );

    public static final CommandInfo COUNT = new CommandInfo(
            "count",
            "Count blocks in selection",
            "/count <block>"
    );

    // Region Commands
    public static final CommandInfo SET = new CommandInfo(
            "set",
            "Set all blocks in selection",
            "/set <block>"
    );

    public static final CommandInfo REPLACE = new CommandInfo(
            "replace",
            "Replace blocks in selection",
            "/replace <from-block> <to-block>",
            "rep"
    );

    public static final CommandInfo CENTER = new CommandInfo(
            "center",
            "Set the selection center",
            "/center <block>"
    );

    public static final CommandInfo SMOOTH = new CommandInfo(
            "smooth",
            "Smooth the selection",
            "/smooth [iterations]"
    );

    public static final CommandInfo DEFORM = new CommandInfo(
            "deform",
            "Deform the selection",
            "/deform <expression>"
    );

    // Utility Commands
    public static final CommandInfo UNDO = new CommandInfo(
            "undo",
            "Undo your last action",
            "/undo [steps]"
    );

    public static final CommandInfo REDO = new CommandInfo(
            "redo",
            "Redo your last undo",
            "/redo [steps]"
    );

    public static final CommandInfo CLEAR = new CommandInfo(
            "clear",
            "Clear your selection",
            "/clear",
            "desel"
    );

    // Brush Commands
    public static final CommandInfo BRUSH = new CommandInfo(
            "brush",
            "Manage brushes",
            "/brush <brush-type> [parameters]"
    );

    public static final CommandInfo SPHERE = new CommandInfo(
            "sphere",
            "Create a sphere",
            "/sphere <block> <radius> [filled]"
    );

    public static final CommandInfo CYLINDER = new CommandInfo(
            "cylinder",
            "Create a cylinder",
            "/cylinder <block> <radius> <height> [filled]",
            "cyl"
    );

    // Clipboard Commands
    public static final CommandInfo COPY = new CommandInfo(
            "copy",
            "Copy the selection",
            "/copy"
    );

    public static final CommandInfo CUT = new CommandInfo(
            "cut",
            "Cut the selection",
            "/cut"
    );

    public static final CommandInfo PASTE = new CommandInfo(
            "paste",
            "Paste the clipboard",
            "/paste"
    );

    // Get all commands as a list
    public static List<CommandInfo> getAllCommands() {
        return Arrays.asList(
                POS1, POS2, WAND, EXPAND, CONTRACT, SHIFT, SIZE, COUNT,
                SET, REPLACE, CENTER, SMOOTH, DEFORM,
                UNDO, REDO, CLEAR,
                BRUSH, SPHERE, CYLINDER,
                COPY, CUT, PASTE
        );
    }

    // Get commands by category
    public static List<CommandInfo> getSelectionCommands() {
        return Arrays.asList(POS1, POS2, WAND, EXPAND, CONTRACT, SHIFT, SIZE, COUNT);
    }

    public static List<CommandInfo> getRegionCommands() {
        return Arrays.asList(SET, REPLACE, CENTER, SMOOTH, DEFORM);
    }

    public static List<CommandInfo> getUtilityCommands() {
        return Arrays.asList(UNDO, REDO, CLEAR);
    }

    public static List<CommandInfo> getBrushCommands() {
        return Arrays.asList(BRUSH, SPHERE, CYLINDER);
    }

    public static List<CommandInfo> getClipboardCommands() {
        return Arrays.asList(COPY, CUT, PASTE);
    }

    // Find command by name or alias
    public static CommandInfo findCommand(String search) {
        return getAllCommands().stream()
                .filter(cmd ->
                        cmd.getName().equalsIgnoreCase(search) ||
                                Arrays.stream(cmd.getAliases()).anyMatch(alias -> alias.equalsIgnoreCase(search)))
                .findFirst()
                .orElse(null);
    }

    // Print all commands (useful for debugging/help)
    public static void printAllCommands() {
        System.out.println("=== WorldEdit-like Commands Reference ===");

        System.out.println("\n## Selection Commands ##");
        getSelectionCommands().forEach(cmd -> System.out.println(cmd + "\n"));

        System.out.println("## Region Commands ##");
        getRegionCommands().forEach(cmd -> System.out.println(cmd + "\n"));

        System.out.println("## Utility Commands ##");
        getUtilityCommands().forEach(cmd -> System.out.println(cmd + "\n"));

        System.out.println("## Brush Commands ##");
        getBrushCommands().forEach(cmd -> System.out.println(cmd + "\n"));

        System.out.println("## Clipboard Commands ##");
        getClipboardCommands().forEach(cmd -> System.out.println(cmd + "\n"));
    }

    public static void sendCommand(String command) {
        if (player == null) {
            player = Minecraft.getInstance().player;
            if (player == null) {
                System.err.println("Cannot send command: Player is not available");
                return;
            }
        }
        if (player instanceof LocalPlayer localPlayer) {
            localPlayer.connection.sendCommand(command);
        }
    }

    public static void sendWeCommand(String command) {
        sendCommand("/"+command);
    }
}