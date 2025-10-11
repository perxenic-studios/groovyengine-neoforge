package io.github.luckymcdev.groovyengine.construct.core.flags;

/**
 * Defines flags for block placement operations that control update behavior.
 * These flags map to Minecraft's internal block update flags.
 */
public enum BlockPlacementFlags {
    UPDATE_NEIGHBORS(1),
    UPDATE_CLIENTS(2),
    UPDATE_INVISIBLE(4),
    UPDATE_IMMEDIATE(8),
    UPDATE_KNOWN_SHAPE(16),
    UPDATE_SUPPRESS_DROPS(32),
    UPDATE_MOVE_BY_PISTON(64),
    UPDATE_NONE(0),
    UPDATE_ALL(3),
    UPDATE_ALL_IMMEDIATE(11);

    public final int value;

    BlockPlacementFlags(int value) {
        this.value = value;
    }

    public int asInt() {
        return value;
    }

    /**
     * Combines multiple flags into a single integer value.
     * @param flags The flags to combine
     * @return Combined flag value
     */
    public static int combine(BlockPlacementFlags... flags) {
        int result = 0;
        for (BlockPlacementFlags f : flags) result |= f.value;
        return result;
    }

    /**
     * Checks if a combined flag value contains a specific flag.
     * @param combined The combined flag value
     * @param flag The flag to check for
     * @return true if the flag is present
     */
    public static boolean hasFlag(int combined, BlockPlacementFlags flag) {
        return (combined & flag.value) != 0;
    }
}