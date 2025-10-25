package io.github.luckymcdev.groovyengine.construct.core.flags;

/**
 * Defines flags for block placement operations that control update behavior.
 * These flags map to Minecraft's internal block update flags.
 *
 * @author lukymcdev
 */
public class BlockPlacementFlags {

    /**
     * Updates neighboring blocks.
     */
    public static final int UPDATE_NEIGHBORS = 1;

    /**
     * Updates the client-side block state.
     */
    public static final int UPDATE_CLIENTS = 2;

    /**
     * Updates the visible block state.
     */
    public static final int UPDATE_INVISIBLE = 4;

    /**
     * Updates the intermediate block state.
     */
    public static final int UPDATE_IMEDIATE = 8;

    /**
     * Updates the block state of the block's known shape.
     */
    public static final int UPDATE_KNOWN_SHAPE = 16;

    /**
     * Suppresses the dropping of items from the block.
     */
    public static final int UPDATE_SUPPRESS_DROPS = 32;

    /**
     * Updates the block state when moved by a piston.
     */
    public static final int UPDATE_MOVE_BY_PISTON = 64;

    /**
     * No block updates are performed.
     */
    public static final int UPDATE_NONE = 0;

    /**
     * All block updates are performed.
     */
    public static final int UPDATE_ALL = 3;

    /**
     * All block updates are performed, except for the intermediate block state.
     */
    public static final int UPDATE_ALL_IMMEDIATE = 11;

    private BlockPlacementFlags() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Combines multiple flags into a single integer value.
     *
     * @param flags The flags to combine
     * @return Combined flag value
     */
    public static int combine(int... flags) {
        int result = 0;
        for (int f : flags) result |= f;
        return result;
    }

    /**
     * Checks if a combined flag value contains a specific flag.
     *
     * @param combined The combined flag value
     * @param flag     The flag to check for
     * @return true if the flag is present
     */
    public static boolean hasFlag(int combined, int flag) {
        return (combined & flag) != 0;
    }
}