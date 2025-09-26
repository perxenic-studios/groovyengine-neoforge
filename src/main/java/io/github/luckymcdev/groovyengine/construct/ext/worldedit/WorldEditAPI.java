package io.github.luckymcdev.groovyengine.construct.ext.worldedit;

import io.github.luckymcdev.groovyengine.construct.core.commands.WorldEditCommands;

public class WorldEditAPI {
    public static final WorldEditAPI INSTANCE = new WorldEditAPI();


    private WorldEditAPI() {

    }

    // Basic selection commands
    public void setPos(int pos) {
        command("pos" + pos);
    }

    public void setPos1() {
        setPos(1);
    }

    public void setPos2() {
        setPos(2);
    }

    // History commands
    public void undo(int amount) {
        command("undo " + amount);
    }

    public void undo() {
        undo(1);
    }

    public void redo(int amount) {
        command("redo " + amount);
    }

    public void redo() {
        redo(1);
    }

    public void clearHistory() {
        Vcommand("clearhistory");
    }

    // Selection manipulation
    public void expand(int amount) {
        command("expand " + amount);
    }

    public void expand(int amount, String direction) {
        command("expand " + amount + " " + direction);
    }

    public void contract(int amount) {
        command("contract " + amount);
    }


    public void contract(int amount, String direction) {
        command("contract " + amount + " " + direction);
    }

    public void shift(int amount) {
        command("shift " + amount);
    }

    public void shift(int amount, String direction) {
        command("shift " + amount + " " + direction);
    }

    // Basic block operations
    public void set(String block) {
        command("set " + block);
    }

    public void replace(String fromBlock, String toBlock) {
        command("replace " + fromBlock + " " + toBlock);
    }

    // Copy/paste operations
    public void copy() {
        command("copy");
    }

    public void cut() {
        command("cut");
    }

    public void paste() {
        command("paste");
    }

    public void rotate(int angle) {
        command("rotate " + angle);
    }

    public void flip(String direction) {
        command("flip " + direction);
    }

    public void sphere(String block, double radius) {
        command("sphere " + block + " " + radius);
    }

    public void cylinder(String block, double radius, int height) {
        command("cyl " + block + " " + radius + " " + height);
    }

    public void pyramid(String block, int size) {
        command("pyramid " + block + " " + size);
    }

    public void brush(String type, String pattern, int radius) {
        command("brush "+type+" "+pattern+" "+radius);
    }

    // Utility commands
    public void count(String block) {
        command("count " + block);
    }

    public void distr() {
        command("distr");
    }

    public void size() {
        command("size");
    }

    private void command(String command) {
        WorldEditCommands.sendWeCommand(command);
    }


    private void Vcommand(String command) {
        WorldEditCommands.sendCommand(command);
    }
}