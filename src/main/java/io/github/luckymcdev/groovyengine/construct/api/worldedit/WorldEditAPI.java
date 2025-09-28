package io.github.luckymcdev.groovyengine.construct.api.worldedit;

import io.github.luckymcdev.groovyengine.construct.core.command.WorldEditCommands;
import net.neoforged.fml.ModList;

public class WorldEditAPI {
    public static final WorldEditAPI INSTANCE = new WorldEditAPI();
    private static Boolean worldEditLoaded = null; // Cache the check result

    private WorldEditAPI() {
    }

    private static boolean isWorldEditLoaded() {
        if (worldEditLoaded == null) {
            worldEditLoaded = ModList.get().isLoaded("worldedit");
        }
        return worldEditLoaded;
    }

    // Basic selection command
    public void setPos(int pos) {
        if (!isWorldEditLoaded()) return;
        command("pos" + pos);
    }

    public void setPos1() {
        setPos(1);
    }

    public void setPos2() {
        setPos(2);
    }

    // History command
    public void undo(int amount) {
        if (!isWorldEditLoaded()) return;
        command("undo " + amount);
    }

    public void undo() {
        undo(1);
    }

    public void redo(int amount) {
        if (!isWorldEditLoaded()) return;
        command("redo " + amount);
    }

    public void redo() {
        redo(1);
    }

    public void clearHistory() {
        if (!isWorldEditLoaded()) return;
        Vcommand("clearhistory");
    }

    // Selection manipulation
    public void expand(int amount) {
        if (!isWorldEditLoaded()) return;
        command("expand " + amount);
    }

    public void expand(int amount, String direction) {
        if (!isWorldEditLoaded()) return;
        command("expand " + amount + " " + direction);
    }

    public void contract(int amount) {
        if (!isWorldEditLoaded()) return;
        command("contract " + amount);
    }

    public void contract(int amount, String direction) {
        if (!isWorldEditLoaded()) return;
        command("contract " + amount + " " + direction);
    }

    public void shift(int amount) {
        if (!isWorldEditLoaded()) return;
        command("shift " + amount);
    }

    public void shift(int amount, String direction) {
        if (!isWorldEditLoaded()) return;
        command("shift " + amount + " " + direction);
    }

    // Basic block operations
    public void set(String block) {
        if (!isWorldEditLoaded()) return;
        command("set " + block);
    }

    public void replace(String fromBlock, String toBlock) {
        if (!isWorldEditLoaded()) return;
        command("replace " + fromBlock + " " + toBlock);
    }

    // Copy/paste operations
    public void copy() {
        if (!isWorldEditLoaded()) return;
        command("copy");
    }

    public void cut() {
        if (!isWorldEditLoaded()) return;
        command("cut");
    }

    public void paste() {
        if (!isWorldEditLoaded()) return;
        command("paste");
    }

    public void rotate(int angle) {
        if (!isWorldEditLoaded()) return;
        command("rotate " + angle);
    }

    public void flip(String direction) {
        if (!isWorldEditLoaded()) return;
        command("flip " + direction);
    }

    public void sphere(String block, double radius) {
        if (!isWorldEditLoaded()) return;
        command("sphere " + block + " " + radius);
    }

    public void cylinder(String block, double radius, int height) {
        if (!isWorldEditLoaded()) return;
        command("cyl " + block + " " + radius + " " + height);
    }

    public void pyramid(String block, int size) {
        if (!isWorldEditLoaded()) return;
        command("pyramid " + block + " " + size);
    }

    public void brush(String type, String pattern, int radius) {
        if (!isWorldEditLoaded()) return;
        command("brush " + type + " " + pattern + " " + radius);
    }

    // Utility command
    public void count(String block) {
        if (!isWorldEditLoaded()) return;
        command("count " + block);
    }

    public void distr() {
        if (!isWorldEditLoaded()) return;
        command("distr");
    }

    public void size() {
        if (!isWorldEditLoaded()) return;
        command("size");
    }

    // Public method to check if WorldEdit is available
    public boolean isAvailable() {
        return isWorldEditLoaded();
    }

    private void command(String command) {
        WorldEditCommands.sendWeCommand(command);
    }

    private void Vcommand(String command) {
        WorldEditCommands.sendCommand(command);
    }
}