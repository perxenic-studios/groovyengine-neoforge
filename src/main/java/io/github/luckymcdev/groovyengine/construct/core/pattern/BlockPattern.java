// File: BlockPattern.java
package io.github.luckymcdev.groovyengine.construct.core.pattern;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

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

    /**
     * Pattern that randomly selects blocks based on weighted probabilities.
     * Weights are specified as percentages (0-100) and are automatically normalized.
     */
    class WeightedPattern implements BlockPattern {
        private final List<WeightedBlock> weightedBlocks;
        private final float totalWeight;

        /**
         * Creates a weighted pattern from BlockState and weight pairs.
         * @param blocks Varargs of BlockState and weight pairs (e.g., blockState1, 50, blockState2, 30, blockState3, 20)
         */
        public WeightedPattern(Object... blocks) {
            if (blocks.length % 2 != 0) {
                throw new IllegalArgumentException("Must provide pairs of BlockState and weight");
            }

            this.weightedBlocks = new ArrayList<>();
            float sum = 0;

            for (int i = 0; i < blocks.length; i += 2) {
                if (!(blocks[i] instanceof BlockState)) {
                    throw new IllegalArgumentException("Even arguments must be BlockState instances");
                }
                if (!(blocks[i + 1] instanceof Number)) {
                    throw new IllegalArgumentException("Odd arguments must be numeric weights");
                }

                BlockState state = (BlockState) blocks[i];
                float weight = ((Number) blocks[i + 1]).floatValue();

                if (weight < 0) {
                    throw new IllegalArgumentException("Weights cannot be negative");
                }

                weightedBlocks.add(new WeightedBlock(state, weight));
                sum += weight;
            }

            this.totalWeight = sum;

            // Normalize weights to percentages if they don't sum to 100 (within tolerance)
            if (Math.abs(totalWeight - 100.0f) > 0.001f) {
                System.out.println("Weights sum to " + totalWeight + ", normalizing to 100%");
            }
        }

        /**
         * Creates a weighted pattern from Block and weight pairs.
         * @param blocks Varargs of Block and weight pairs (e.g., block1, 50, block2, 30, block3, 20)
         */
        public static WeightedPattern fromBlocks(Object... blocks) {
            if (blocks.length % 2 != 0) {
                throw new IllegalArgumentException("Must provide pairs of Block and weight");
            }

            Object[] blockStates = new Object[blocks.length];
            for (int i = 0; i < blocks.length; i += 2) {
                if (!(blocks[i] instanceof Block)) {
                    throw new IllegalArgumentException("Even arguments must be Block instances");
                }
                blockStates[i] = ((Block) blocks[i]).defaultBlockState();
                blockStates[i + 1] = blocks[i + 1];
            }

            return new WeightedPattern(blockStates);
        }

        /**
         * Creates a weighted pattern from a map of BlockState to weight.
         * @param blockWeights Map containing BlockState to weight mappings
         */
        public WeightedPattern(Map<BlockState, Float> blockWeights) {
            this.weightedBlocks = new ArrayList<>();
            float sum = 0;

            for (Map.Entry<BlockState, Float> entry : blockWeights.entrySet()) {
                if (entry.getValue() < 0) {
                    throw new IllegalArgumentException("Weights cannot be negative");
                }
                weightedBlocks.add(new WeightedBlock(entry.getKey(), entry.getValue()));
                sum += entry.getValue();
            }

            this.totalWeight = sum;

            if (Math.abs(totalWeight - 100.0f) > 0.001f) {
                System.out.println("Weights sum to " + totalWeight + ", normalizing to 100%");
            }
        }

        @Override
        public BlockState getBlockState(BlockPos pos, BlockPos origin, Random random) {
            if (weightedBlocks.isEmpty()) {
                throw new IllegalStateException("No blocks defined in weighted pattern");
            }

            float randomValue = random.nextFloat() * totalWeight;
            float cumulative = 0;

            for (WeightedBlock weightedBlock : weightedBlocks) {
                cumulative += weightedBlock.weight;
                if (randomValue <= cumulative) {
                    return weightedBlock.blockState;
                }
            }

            // Fallback: return the last block (should rarely happen due to floating point precision)
            return weightedBlocks.get(weightedBlocks.size() - 1).blockState;
        }

        /**
         * Gets the list of weighted blocks with their normalized percentages.
         * @return List of weighted blocks with actual percentages
         */
        public List<WeightedBlock> getWeightedBlocks() {
            return new ArrayList<>(weightedBlocks);
        }

        /**
         * Gets the total weight of all blocks in the pattern.
         * @return The sum of all weights
         */
        public float getTotalWeight() {
            return totalWeight;
        }

        /**
         * Represents a block with its associated weight.
         */
        public static class WeightedBlock {
            public final BlockState blockState;
            public final float weight;
            public final float percentage;

            public WeightedBlock(BlockState blockState, float weight) {
                this.blockState = blockState;
                this.weight = weight;
                this.percentage = weight;
            }

            /**
             * Gets the actual percentage this block represents in the pattern.
             * @param totalWeight The total weight to calculate percentage against
             * @return The percentage (0-100)
             */
            public float getPercentage(float totalWeight) {
                return (weight / totalWeight) * 100.0f;
            }

            @Override
            public String toString() {
                return String.format("WeightedBlock{blockState=%s, weight=%.1f}", blockState, weight);
            }
        }
    }
}