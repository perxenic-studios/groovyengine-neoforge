package io.github.luckymcdev.groovyengine.construct.core.flags;

/**
 * Defines flags for block placement operations that control update behavior.
 * These flags map to Minecraft's internal block update flags.
 */
public class BlockPlacementFlags {

    private BlockPlacementFlags() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static final int UPDATE_NEIGHBORS = 1;
    public static final int UPDATE_CLIENTS = 2;
    public static final int UPDATE_INVISIBLE = 4;
    public static final int UPDATE_IMMEDIATE = 8;
    public static final int UPDATE_KNOWN_SHAPE = 16;
    public static final int UPDATE_SUPPRESS_DROPS = 32;
    public static final int UPDATE_MOVE_BY_PISTON = 64;
    public static final int UPDATE_NONE = 0;
    public static final int UPDATE_ALL = 3;
    public static final int UPDATE_ALL_IMMEDIATE = 11;

    /**
     * Combines multiple flags into a single integer value.
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
     * @param combined The combined flag value
     * @param flag The flag to check for
     * @return true if the flag is present
     */
    public static boolean hasFlag(int combined, int flag) {
        return (combined & flag) != 0;
    }
}