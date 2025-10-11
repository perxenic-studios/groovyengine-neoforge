// File: BlockPattern.java
package io.github.luckymcdev.groovyengine.construct.core.pattern;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

/**
 * Defines a pattern for block placement within a selection.
 */
public interface BlockPattern {

    /**
     * Gets the block state for a specific position within the pattern.
     * @param pos The target position
     * @param origin The origin point of the pattern
     * @param random Random instance for probabilistic patterns
     * @return The block state to place at the given position
     */
    BlockState getBlockState(BlockPos pos, BlockPos origin, Random random);

    /**
     * Pattern that places a single block type everywhere.
     */
    class SingleBlockPattern implements BlockPattern {
        private final BlockState blockState;

        public SingleBlockPattern(BlockState blockState) {
            this.blockState = blockState;
        }

        public SingleBlockPattern(Block block) {
            this.blockState = block.defaultBlockState();
        }

        @Override
        public BlockState getBlockState(BlockPos pos, BlockPos origin, Random random) {
            return blockState;
        }
    }

    /**
     * Pattern that randomly selects from multiple block types with equal probability.
     */
    class RandomPattern implements BlockPattern {
        private final BlockState[] blockStates;
        private final float[] weights;

        public RandomPattern(BlockState... blockStates) {
            this.blockStates = blockStates;
            this.weights = new float[blockStates.length];
            float weight = 1.0f / blockStates.length;
            for (int i = 0; i < weights.length; i++) {
                weights[i] = weight;
            }
        }

        public RandomPattern(Block... blocks) {
            this.blockStates = new BlockState[blocks.length];
            for (int i = 0; i < blocks.length; i++) {
                this.blockStates[i] = blocks[i].defaultBlockState();
            }
            this.weights = new float[blocks.length];
            float weight = 1.0f / blocks.length;
            for (int i = 0; i < weights.length; i++) {
                weights[i] = weight;
            }
        }

        @Override
        public BlockState getBlockState(BlockPos pos, BlockPos origin, Random random) {
            float rand = random.nextFloat();
            float cumulative = 0;
            for (int i = 0; i < blockStates.length; i++) {
                cumulative += weights[i];
                if (rand <= cumulative) {
                    return blockStates[i];
                }
            }
            return blockStates[blockStates.length - 1];
        }
    }

    /**
     * Pattern that creates a 3D checkerboard pattern.
     */
    class CheckerboardPattern implements BlockPattern {
        private final BlockState primary;
        private final BlockState secondary;
        private final int scale;

        public CheckerboardPattern(BlockState primary, BlockState secondary, int scale) {
            this.primary = primary;
            this.secondary = secondary;
            this.scale = scale;
        }

        @Override
        public BlockState getBlockState(BlockPos pos, BlockPos origin, Random random) {
            int x = (pos.getX() - origin.getX()) / scale;
            int y = (pos.getY() - origin.getY()) / scale;
            int z = (pos.getZ() - origin.getZ()) / scale;

            return (x + y + z) % 2 == 0 ? primary : secondary;
        }
    }
}