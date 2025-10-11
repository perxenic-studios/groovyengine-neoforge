package io.github.luckymcdev.groovyengine.construct.core.placement;

import io.github.luckymcdev.groovyengine.construct.core.flags.BlockPlacementFlags;
import io.github.luckymcdev.groovyengine.construct.core.pattern.BlockPattern;
import io.github.luckymcdev.groovyengine.construct.core.selection.Selection;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Asynchronously places blocks in the world with performance optimization.
 * This system uses queues to batch block placements and updates to maintain game performance.
 */
@EventBusSubscriber(value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class AsyncBlockPlacer {
    private static final AsyncBlockPlacer INSTANCE = new AsyncBlockPlacer();

    /** Gets the singleton instance of the AsyncBlockPlacer */
    public static AsyncBlockPlacer getInstance() {
        return INSTANCE;
    }

    // Queues for managing block placements
    private final Queue<PlacementTask> immediatePlacementQueue = new ConcurrentLinkedQueue<>();
    private final Queue<PlacementTask> delayedUpdateQueue = new ConcurrentLinkedQueue<>();
    private final Random random = new Random();

    // Performance tuning parameters
    private int blocksPerTick = 1000;
    private int updatesPerTick = blocksPerTick / 2;
    private int tickCounter = 0;
    private final int maxBlocksPerTick = 100000;
    private final int fpsThreshold = 45;
    private final int adjustmentInterval = 5;
    private final int blocksPerTickIncrement = 250;

    private AsyncBlockPlacer() {}

    public int getBlocksPerTick() { return blocksPerTick; }
    public int getUpdatesPerTick() { return updatesPerTick; }

    /**
     * Sets the maximum number of blocks to place per tick.
     * @param blocksPerTick The number of blocks (minimum 1)
     */
    public void setBlocksPerTick(int blocksPerTick) {
        this.blocksPerTick = Math.max(1, blocksPerTick);
    }

    /**
     * Sets the maximum number of block updates to send per tick.
     * @param updatesPerTick The number of updates (minimum 1)
     */
    public void setUpdatesPerTick(int updatesPerTick) {
        this.updatesPerTick = Math.max(1, updatesPerTick);
    }

    /**
     * Fills a selection with blocks from a specified pattern.
     * @param selection The selection area to fill
     * @param pattern The block pattern to use for filling
     */
    public void fillSelection(Selection selection, BlockPattern pattern) {
        if (!selection.hasValidSelection()) return;

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Set<BlockPos> positions = selection.getSelectedBlocks();
        BlockPos origin = selection.getPos1();

        // Queue all block placements
        for (BlockPos pos : positions) {
            BlockState state = pattern.getBlockState(pos, origin, random);
            immediatePlacementQueue.offer(new PlacementTask(pos, state, false));
        }

        // Queue block updates if needed
        if (shouldSendUpdates()) {
            for (BlockPos pos : positions) {
                delayedUpdateQueue.offer(new PlacementTask(pos, null, true));
            }
        }
    }

    /**
     * Queues a single block placement operation.
     * @param pos The target position
     * @param state The block state to place
     * @param sendUpdates Whether to send block updates
     */
    public void setBlock(BlockPos pos, BlockState state, boolean sendUpdates) {
        if (sendUpdates) {
            immediatePlacementQueue.offer(new PlacementTask(pos, state, false));
            delayedUpdateQueue.offer(new PlacementTask(pos, null, true));
        } else {
            immediatePlacementQueue.offer(new PlacementTask(pos, state, false));
        }
    }

    /**
     * Handles server tick events to process queued block operations.
     */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        Level playerLevel = player.level();
        Level level = event.getServer().getLevel(playerLevel.dimension());
        if (level == null) return;

        // Skip if no work to do
        if (INSTANCE.immediatePlacementQueue.isEmpty() && INSTANCE.delayedUpdateQueue.isEmpty()) return;

        // Adjust performance parameters based on FPS
        INSTANCE.tickCounter++;
        if (INSTANCE.immediatePlacementQueue.size() > 10000 && INSTANCE.tickCounter % INSTANCE.adjustmentInterval == 0) {
            INSTANCE.adjustPerformanceParameters();
        }

        INSTANCE.processQueues(level);
    }

    /**
     * Adjusts performance parameters based on current FPS.
     */
    private void adjustPerformanceParameters() {
        int currentFps = Minecraft.getInstance().getFps();
        if (currentFps >= fpsThreshold) {
            blocksPerTick = Math.min(blocksPerTick + blocksPerTickIncrement, maxBlocksPerTick);
        } else {
            blocksPerTick = Math.max(blocksPerTick - blocksPerTickIncrement, 1000);
        }
        updatesPerTick = Math.max(1, blocksPerTick / 2);
    }

    /**
     * Processes both placement and update queues.
     */
    private void processQueues(Level level) {
        processImmediatePlacements(level);
        processDelayedUpdates(level);
    }

    /**
     * Processes the immediate block placement queue.
     */
    private void processImmediatePlacements(Level level) {
        if (level == null) return;

        int processed = 0;
        while (processed < blocksPerTick && !immediatePlacementQueue.isEmpty()) {
            PlacementTask task = immediatePlacementQueue.poll();
            if (task != null && isValidPosition(task.position(), level)) {
                level.setBlock(task.position(), task.blockState(), BlockPlacementFlags.UPDATE_CLIENTS.value);
                processed++;
            }
        }
    }

    /**
     * Processes the delayed block update queue.
     */
    private void processDelayedUpdates(Level level) {
        if (level == null) return;

        int processed = 0;
        while (processed < updatesPerTick && !delayedUpdateQueue.isEmpty()) {
            PlacementTask task = delayedUpdateQueue.poll();
            if (task != null && isValidPosition(task.position(), level)) {
                BlockState currentState = level.getBlockState(task.position());
                level.sendBlockUpdated(task.position(), currentState, currentState, BlockPlacementFlags.UPDATE_ALL.value);
                processed++;
            }
        }
    }

    /**
     * Validates if a position is within world bounds.
     */
    private boolean isValidPosition(BlockPos pos, Level level) {
        return level.isInWorldBounds(pos);
    }

    /**
     * Determines whether block updates should be sent.
     */
    private boolean shouldSendUpdates() {
        return true;
    }

    // Queue status methods
    public int getQueuedPlacements() { return immediatePlacementQueue.size(); }
    public int getQueuedUpdates() { return delayedUpdateQueue.size(); }

    /** Clears all pending block operations */
    public void clearQueues() {
        immediatePlacementQueue.clear();
        delayedUpdateQueue.clear();
    }
}