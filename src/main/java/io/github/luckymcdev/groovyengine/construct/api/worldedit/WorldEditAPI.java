package io.github.luckymcdev.groovyengine.construct.api.worldedit;

import io.github.luckymcdev.groovyengine.construct.core.command.WorldEditCommands;
import net.neoforged.fml.ModList;

/**
 * A comprehensive API wrapper for WorldEdit functionality within GroovyEngine.
 * This class provides a type-safe, Java-friendly interface for executing WorldEdit commands
 * with built-in mod availability checks.
 *
 * <p><b>Usage Example:</b></p>
 * <pre>
 * {@code
 * WorldEditAPI.INSTANCE.setPos1();
 * WorldEditAPI.INSTANCE.setPos2();
 * WorldEditAPI.INSTANCE.set("minecraft:stone");
 * }
 * </pre>
 *
 * @see WorldEditAPI#INSTANCE
 */
public class WorldEditAPI {

    /**
     * Singleton instance of the WorldEditAPI.
     * Use this instance to access all WorldEdit functionality.
     */
    public static final WorldEditAPI INSTANCE = new WorldEditAPI();

    /**
     * Cached result of WorldEdit mod availability check.
     * Null indicates not yet checked, Boolean value indicates mod presence.
     */
    private static Boolean worldEditLoaded = null;

    /**
     * Private constructor to enforce singleton pattern.
     * Use {@link #INSTANCE} to access the API.
     */
    private WorldEditAPI() {
    }

    /**
     * Checks if the WorldEdit mod is loaded and available.
     * Results are cached for performance.
     *
     * @return true if WorldEdit is installed and available, false otherwise
     */
    private static boolean isWorldEditLoaded() {
        if (worldEditLoaded == null) {
            worldEditLoaded = ModList.get().isLoaded("worldedit");
        }
        return worldEditLoaded;
    }

    // ===== SELECTION COMMANDS =====

    /**
     * Sets a selection position at the player's current location.
     *
     * @param pos the position index to set (1 for pos1, 2 for pos2)
     * @throws IllegalArgumentException if pos is not 1 or 2
     */
    public void setPos(int pos) {
        if (!isWorldEditLoaded()) return;
        if (pos != 1 && pos != 2) {
            throw new IllegalArgumentException("Position must be 1 or 2");
        }
        command("pos" + pos);
    }

    /**
     * Sets the first selection position (pos1) at the player's current location.
     * Equivalent to {@code //pos1} in WorldEdit.
     */
    public void setPos1() {
        setPos(1);
    }

    /**
     * Sets the second selection position (pos2) at the player's current location.
     * Equivalent to {@code //pos2} in WorldEdit.
     */
    public void setPos2() {
        setPos(2);
    }

    // ===== HISTORY COMMANDS =====

    /**
     * Undoes a specified number of WorldEdit operations.
     *
     * @param amount the number of operations to undo
     * @throws IllegalArgumentException if amount is less than 1
     */
    public void undo(int amount) {
        if (!isWorldEditLoaded()) return;
        if (amount < 1) {
            throw new IllegalArgumentException("Undo amount must be at least 1");
        }
        command("undo " + amount);
    }

    /**
     * Undoes the most recent WorldEdit operation.
     * Equivalent to {@code //undo} in WorldEdit.
     */
    public void undo() {
        undo(1);
    }

    /**
     * Redoes a specified number of previously undone WorldEdit operations.
     *
     * @param amount the number of operations to redo
     * @throws IllegalArgumentException if amount is less than 1
     */
    public void redo(int amount) {
        if (!isWorldEditLoaded()) return;
        if (amount < 1) {
            throw new IllegalArgumentException("Redo amount must be at least 1");
        }
        command("redo " + amount);
    }

    /**
     * Redoes the most recently undone WorldEdit operation.
     * Equivalent to {@code //redo} in WorldEdit.
     */
    public void redo() {
        redo(1);
    }

    /**
     * Clears the entire WorldEdit operation history for the current player.
     * Equivalent to {@code //clearhistory} in WorldEdit.
     */
    public void clearHistory() {
        if (!isWorldEditLoaded()) return;
        Vcommand("clearhistory");
    }

    // ===== SELECTION MANIPULATION COMMANDS =====

    /**
     * Expands the current selection equally in all directions.
     *
     * @param amount the number of blocks to expand in each direction
     * @throws IllegalArgumentException if amount is less than 1
     */
    public void expand(int amount) {
        if (!isWorldEditLoaded()) return;
        if (amount < 1) {
            throw new IllegalArgumentException("Expand amount must be at least 1");
        }
        command("expand " + amount);
    }

    /**
     * Expands the current selection in a specific direction.
     *
     * @param amount the number of blocks to expand
     * @param direction the direction to expand (e.g., "north", "south", "up", "down", etc.)
     * @throws IllegalArgumentException if amount is less than 1 or direction is invalid
     */
    public void expand(int amount, String direction) {
        if (!isWorldEditLoaded()) return;
        if (amount < 1) {
            throw new IllegalArgumentException("Expand amount must be at least 1");
        }
        command("expand " + amount + " " + direction);
    }

    /**
     * Contracts the current selection equally in all directions.
     *
     * @param amount the number of blocks to contract in each direction
     * @throws IllegalArgumentException if amount is less than 1
     */
    public void contract(int amount) {
        if (!isWorldEditLoaded()) return;
        if (amount < 1) {
            throw new IllegalArgumentException("Contract amount must be at least 1");
        }
        command("contract " + amount);
    }

    /**
     * Contracts the current selection in a specific direction.
     *
     * @param amount the number of blocks to contract
     * @param direction the direction to contract (e.g., "north", "south", "up", "down", etc.)
     * @throws IllegalArgumentException if amount is less than 1 or direction is invalid
     */
    public void contract(int amount, String direction) {
        if (!isWorldEditLoaded()) return;
        if (amount < 1) {
            throw new IllegalArgumentException("Contract amount must be at least 1");
        }
        command("contract " + amount + " " + direction);
    }

    /**
     * Shifts the entire selection equally in all directions.
     *
     * @param amount the number of blocks to shift the selection
     * @throws IllegalArgumentException if amount is less than 1
     */
    public void shift(int amount) {
        if (!isWorldEditLoaded()) return;
        if (amount < 1) {
            throw new IllegalArgumentException("Shift amount must be at least 1");
        }
        command("shift " + amount);
    }

    /**
     * Shifts the entire selection in a specific direction.
     *
     * @param amount the number of blocks to shift the selection
     * @param direction the direction to shift (e.g., "north", "south", "up", "down", etc.)
     * @throws IllegalArgumentException if amount is less than 1 or direction is invalid
     */
    public void shift(int amount, String direction) {
        if (!isWorldEditLoaded()) return;
        if (amount < 1) {
            throw new IllegalArgumentException("Shift amount must be at least 1");
        }
        command("shift " + amount + " " + direction);
    }

    // ===== BASIC BLOCK OPERATIONS =====

    /**
     * Sets all blocks in the current selection to the specified block type.
     *
     * @param block the block ID to set (e.g., "minecraft:stone", "minecraft:oak_planks")
     * @throws IllegalArgumentException if block ID is null or empty
     */
    public void set(String block) {
        if (!isWorldEditLoaded()) return;
        if (block == null || block.trim().isEmpty()) {
            throw new IllegalArgumentException("Block ID cannot be null or empty");
        }
        command("set " + block);
    }

    /**
     * Replaces all instances of one block type with another within the current selection.
     *
     * @param fromBlock the block ID to replace (e.g., "minecraft:stone")
     * @param toBlock the block ID to replace with (e.g., "minecraft:dirt")
     * @throws IllegalArgumentException if either block ID is null or empty
     */
    public void replace(String fromBlock, String toBlock) {
        if (!isWorldEditLoaded()) return;
        if (fromBlock == null || fromBlock.trim().isEmpty() ||
                toBlock == null || toBlock.trim().isEmpty()) {
            throw new IllegalArgumentException("Block IDs cannot be null or empty");
        }
        command("replace " + fromBlock + " " + toBlock);
    }

    // ===== COPY/PASTE OPERATIONS =====

    /**
     * Copies the current selection to the clipboard.
     * Equivalent to {@code //copy} in WorldEdit.
     */
    public void copy() {
        if (!isWorldEditLoaded()) return;
        command("copy");
    }

    /**
     * Cuts the current selection (copies and then deletes) to the clipboard.
     * Equivalent to {@code //cut} in WorldEdit.
     */
    public void cut() {
        if (!isWorldEditLoaded()) return;
        command("cut");
    }

    /**
     * Pastes the clipboard contents at the player's current location.
     * Equivalent to {@code //paste} in WorldEdit.
     */
    public void paste() {
        if (!isWorldEditLoaded()) return;
        command("paste");
    }

    /**
     * Rotates the clipboard contents by the specified angle.
     *
     * @param angle the rotation angle in degrees (typically 90, 180, or 270)
     * @throws IllegalArgumentException if angle is not a multiple of 90
     */
    public void rotate(int angle) {
        if (!isWorldEditLoaded()) return;
        if (angle % 90 != 0) {
            throw new IllegalArgumentException("Rotation angle must be a multiple of 90");
        }
        command("rotate " + angle);
    }

    /**
     * Flips the clipboard contents along the specified axis.
     *
     * @param direction the axis to flip along (e.g., "north", "east", "up", etc.)
     * @throws IllegalArgumentException if direction is null or empty
     */
    public void flip(String direction) {
        if (!isWorldEditLoaded()) return;
        if (direction == null || direction.trim().isEmpty()) {
            throw new IllegalArgumentException("Direction cannot be null or empty");
        }
        command("flip " + direction);
    }

    // ===== GENERATION COMMANDS =====

    /**
     * Creates a sphere of the specified block type.
     *
     * @param block the block ID for the sphere
     * @param radius the radius of the sphere
     * @throws IllegalArgumentException if block ID is null/empty or radius is invalid
     */
    public void sphere(String block, double radius) {
        if (!isWorldEditLoaded()) return;
        if (block == null || block.trim().isEmpty()) {
            throw new IllegalArgumentException("Block ID cannot be null or empty");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be greater than 0");
        }
        command("sphere " + block + " " + radius);
    }

    /**
     * Creates a cylinder of the specified block type.
     *
     * @param block the block ID for the cylinder
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @throws IllegalArgumentException if block ID is null/empty or dimensions are invalid
     */
    public void cylinder(String block, double radius, int height) {
        if (!isWorldEditLoaded()) return;
        if (block == null || block.trim().isEmpty()) {
            throw new IllegalArgumentException("Block ID cannot be null or empty");
        }
        if (radius <= 0 || height <= 0) {
            throw new IllegalArgumentException("Radius and height must be greater than 0");
        }
        command("cyl " + block + " " + radius + " " + height);
    }

    /**
     * Creates a pyramid of the specified block type.
     *
     * @param block the block ID for the pyramid
     * @param size the size (height and base) of the pyramid
     * @throws IllegalArgumentException if block ID is null/empty or size is invalid
     */
    public void pyramid(String block, int size) {
        if (!isWorldEditLoaded()) return;
        if (block == null || block.trim().isEmpty()) {
            throw new IllegalArgumentException("Block ID cannot be null or empty");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Pyramid size must be greater than 0");
        }
        command("pyramid " + block + " " + size);
    }

    /**
     * Sets the WorldEdit brush for the player.
     *
     * @param type the brush type (e.g., "sphere", "cylinder")
     * @param pattern the block pattern to use with the brush
     * @param radius the brush radius
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public void brush(String type, String pattern, int radius) {
        if (!isWorldEditLoaded()) return;
        if (type == null || type.trim().isEmpty() ||
                pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("Brush type and pattern cannot be null or empty");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("Brush radius must be greater than 0");
        }
        command("brush " + type + " " + pattern + " " + radius);
    }

    // ===== UTILITY COMMANDS =====

    /**
     * Counts the number of a specific block type in the current selection.
     *
     * @param block the block ID to count
     * @throws IllegalArgumentException if block ID is null or empty
     */
    public void count(String block) {
        if (!isWorldEditLoaded()) return;
        if (block == null || block.trim().isEmpty()) {
            throw new IllegalArgumentException("Block ID cannot be null or empty");
        }
        command("count " + block);
    }

    /**
     * Shows the distribution of block types in the current selection.
     * Equivalent to {@code //distr} in WorldEdit.
     */
    public void distr() {
        if (!isWorldEditLoaded()) return;
        command("distr");
    }

    /**
     * Shows the dimensions (size) of the current selection.
     * Equivalent to {@code //size} in WorldEdit.
     */
    public void size() {
        if (!isWorldEditLoaded()) return;
        command("size");
    }

    // ===== PUBLIC UTILITY METHODS =====

    /**
     * Checks if WorldEdit is available for use.
     * This method is safe to call at any time and will return the cached availability status.
     *
     * @return true if WorldEdit is installed and available, false otherwise
     */
    public boolean isAvailable() {
        return isWorldEditLoaded();
    }
    // ===== PRIVATE IMPLEMENTATION METHODS =====

    /**
     * Internal method to execute a WorldEdit command with the standard prefix.
     *
     * @param command the command to execute (without the "//" prefix)
     */
    private void command(String command) {
        WorldEditCommands.sendWeCommand(command);
    }

    /**
     * Internal method to execute a vanilla-style command.
     *
     * @param command the command to execute
     */
    private void Vcommand(String command) {
        WorldEditCommands.sendCommand(command);
    }
}