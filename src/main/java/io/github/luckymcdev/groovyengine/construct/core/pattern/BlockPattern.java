/*
 * Copyright 2025 LuckyMcDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.luckymcdev.groovyengine.construct.core.pattern;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Defines a pattern for block placement within a selection.
 */
public interface BlockPattern {

    /**
     * Gets the block state for a specific position within the pattern.
     *
     * @param pos    The target position
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

        /**
         * Gets the block state for a specific position within the pattern.
         * <p>
         * This pattern works by generating a random float between 0 and 1, and then
         * iterating through the block states and their corresponding weights. When the
         * cumulative weight exceeds the random float, the corresponding block state is
         * returned. If the random float exceeds the total weight, the last
         * block state is returned.
         *
         * @param pos    The target position
         * @param origin The origin point of the pattern
         * @param random Random instance for probabilistic patterns
         * @return The block state to place at the given position
         */
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

        /**
         * Gets the block state for a specific position within the pattern.
         * <p>
         * This pattern works by dividing the position coordinates by the scale, and
         * then summing the resulting coordinates. If the sum is even, the primary
         * block state is returned. If the sum is odd, the secondary block state is returned.
         *
         * @param pos    The target position
         * @param origin The origin point of the pattern
         * @param random Random instance for probabilistic patterns
         * @return The block state to place at the given position
         */
        @Override
        public BlockState getBlockState(BlockPos pos, BlockPos origin, Random random) {
            int x = (pos.getX() - origin.getX()) / scale;
            int y = (pos.getY() - origin.getY()) / scale;
            int z = (pos.getZ() - origin.getZ()) / scale;

            return (x + y + z) % 2 == 0 ? primary : secondary;
        }
    }

    /**
     * Pattern that creates horizontal layers alternating between two blocks.
     */
    class LayeredPattern implements BlockPattern {
        private final BlockState primary;
        private final BlockState secondary;
        private final int layerThickness;

        public LayeredPattern(BlockState primary, BlockState secondary, int layerThickness) {
            this.primary = primary;
            this.secondary = secondary;
            this.layerThickness = layerThickness;
        }

        /**
         * Gets the block state for a specific position within the pattern.
         * <p>
         * This pattern works by dividing the relative Y position of the target position
         * by the layer thickness, and then using the resulting layer number to determine
         * which block state to return. If the layer number is even, the primary block state
         * is returned. If the layer number is odd, the secondary block state is returned.
         * <p>
         *
         * @param pos    The target position
         * @param origin The origin point of the pattern
         * @param random Random instance for probabilistic patterns
         * @return The block state to place at the given position
         */
        @Override
        public BlockState getBlockState(BlockPos pos, BlockPos origin, Random random) {
            int relativeY = pos.getY() - origin.getY();
            int layer = relativeY / layerThickness;
            return layer % 2 == 0 ? primary : secondary;
        }
    }

    /**
     * Pattern that creates a gradient between two blocks based on Y position.
     */
    class GradientPattern implements BlockPattern {
        private final BlockState bottomBlock;
        private final BlockState topBlock;
        private BlockPos minPos;
        private BlockPos maxPos;

        public GradientPattern(BlockState bottomBlock, BlockState topBlock) {
            this.bottomBlock = bottomBlock;
            this.topBlock = topBlock;
        }

        /**
         * Gets the block state for a specific position within the pattern.
         * <p>
         * This pattern works by initializing the bounds of the gradient on the first call
         * if they are not already set. The gradient is then calculated based on the Y
         * position of the target position, and a random threshold is used to create a
         * gradient effect. If the random value is less than the normalized Y position,
         * the top block state is returned. Otherwise, the bottom block state is returned.
         * <p>
         *
         * @param pos    The target position
         * @param origin The origin point of the pattern
         * @param random Random instance for probabilistic patterns
         * @return The block state to place at the given position
         */
        @Override
        public BlockState getBlockState(BlockPos pos, BlockPos origin, Random random) {
            // Initialize bounds on first call if needed
            if (minPos == null) {
                minPos = origin;
                maxPos = origin;
            }

            // Calculate the gradient based on Y position
            int minY = minPos.getY();
            int maxY = maxPos.getY();
            int range = maxY - minY;

            if (range == 0) return bottomBlock;

            float normalizedY = (float) (pos.getY() - minY) / range;

            // Use random threshold to create gradient effect
            return random.nextFloat() < normalizedY ? topBlock : bottomBlock;
        }

        /**
         * Sets the bounds for the gradient calculation.
         */
        public void setBounds(BlockPos min, BlockPos max) {
            this.minPos = min;
            this.maxPos = max;
        }
    }

    /**
     * Pattern that randomly selects blocks based on weighted probabilities.
     */
    class WeightedPattern implements BlockPattern {
        private final List<WeightedBlock> weightedBlocks;
        private final float totalWeight;

        public WeightedPattern(Object... blocks) {
            if (blocks.length % 2 != 0) {
                throw new IllegalArgumentException("Must provide pairs of BlockState and weight");
            }

            this.weightedBlocks = new ArrayList<>();
            float sum = 0;

            for (int i = 0; i < blocks.length; i += 2) {
                if (!(blocks[i] instanceof BlockState state)) {
                    throw new IllegalArgumentException("Even arguments must be BlockState instances");
                }
                if (!(blocks[i + 1] instanceof Number)) {
                    throw new IllegalArgumentException("Odd arguments must be numeric weights");
                }

                float weight = ((Number) blocks[i + 1]).floatValue();

                if (weight < 0) {
                    throw new IllegalArgumentException("Weights cannot be negative");
                }

                weightedBlocks.add(new WeightedBlock(state, weight));
                sum += weight;
            }

            this.totalWeight = sum;

            if (Math.abs(totalWeight - 100.0f) > 0.001f) {
                System.out.println("Weights sum to " + totalWeight + ", normalizing to 100%");
            }
        }

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

        /**
         * Creates a WeightedPattern from a list of blocks and weights.
         * The given blocks and weights must be in pairs, with the block at index i and its weight at index i+1.
         * If the given blocks and weights are not in pairs, an IllegalArgumentException is thrown.
         *
         * @param blocks The list of blocks and weights
         * @return A WeightedPattern representing the given blocks and weights
         * @throws IllegalArgumentException If the given blocks and weights are not in pairs
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
         * Gets the block state for a specific position within the pattern.
         * This pattern works by generating a random float between 0 and 1, and then
         * iterating through the block states and their corresponding weights. When the
         * cumulative weight exceeds the random float, the corresponding block state is
         * returned. If the random float exceeds the total weight, the last
         * block state is returned.
         * <p>
         * If no blocks are defined in the weighted pattern, an IllegalStateException is thrown.
         *
         * @param pos    The target position
         * @param origin The origin point of the pattern
         * @param random Random instance for probabilistic patterns
         * @return The block state to place at the given position
         * @throws IllegalStateException If no blocks are defined in the weighted pattern
         */
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

            return weightedBlocks.get(weightedBlocks.size() - 1).blockState;
        }

        /**
         * Gets a list of all the weighted blocks in this pattern.
         * The list is a copy of the internal list, so modifying it will not affect the pattern.
         *
         * @return A list of all the weighted blocks in this pattern
         */
        public List<WeightedBlock> getWeightedBlocks() {
            return new ArrayList<>(weightedBlocks);
        }

        /**
         * Gets the total weight of all the blocks in this weighted pattern.
         * This is the sum of all the individual weights of the blocks in the pattern.
         *
         * @return The total weight of all the blocks in this weighted pattern
         */
        public float getTotalWeight() {
            return totalWeight;
        }

        public static class WeightedBlock {
            public final BlockState blockState;
            public final float weight;
            public final float percentage;

            public WeightedBlock(BlockState blockState, float weight) {
                this.blockState = blockState;
                this.weight = weight;
                this.percentage = weight;
            }

            public float getPercentage(float totalWeight) {
                return (weight / totalWeight) * 100.0f;
            }

            @Override
            public String toString() {
                return String.format("WeightedBlock{blockState=%s, weight=%.1f}", blockState, weight);
            }
        }
    }

    /**
     * Pattern that creates noise-based terrain using Perlin-like noise.
     */
    class NoisePattern implements BlockPattern {
        private final BlockState primary;
        private final BlockState secondary;
        private final double scale;
        private final double threshold;

        public NoisePattern(BlockState primary, BlockState secondary, double scale, double threshold) {
            this.primary = primary;
            this.secondary = secondary;
            this.scale = scale;
            this.threshold = threshold;
        }

        /**
         * Gets the block state for a specific position within the pattern.
         * <p>
         * This pattern works by generating Perlin-like noise at the specified position,
         * scaled by the given scale. If the generated noise is greater than the
         * given threshold, the primary block state is returned. Otherwise, the
         * secondary block state is returned.
         * <p>
         *
         * @param pos    The target position
         * @param origin The origin point of the pattern
         * @param random Random instance for probabilistic patterns
         * @return The block state to place at the given position
         */
        @Override
        public BlockState getBlockState(BlockPos pos, BlockPos origin, Random random) {
            double noise = generateNoise(
                    (pos.getX() - origin.getX()) * scale,
                    (pos.getY() - origin.getY()) * scale,
                    (pos.getZ() - origin.getZ()) * scale
            );
            return noise > threshold ? primary : secondary;
        }

        /**
         * Generates a simple 3D noise value based on the given coordinates.
         * This noise function is a simplified version of Perlin noise.
         * The function takes three double coordinates (x, y, z) and returns a
         * double noise value in the range of -1.0 to 1.0.
         *
         * @param x The x-coordinate of the position
         * @param y The y-coordinate of the position
         * @param z The z-coordinate of the position
         * @return The generated noise value
         */
        private double generateNoise(double x, double y, double z) {
            // Simple 3D noise function (simplified Perlin-like noise)
            double n = Math.sin(x * 12.9898 + y * 78.233 + z * 37.719) * 43758.5453;
            return (n - Math.floor(n)) * 2.0 - 1.0;
        }
    }

    /**
     * Pattern that creates stripes along a specified axis.
     */
    class StripePattern implements BlockPattern {
        private final BlockState primary;
        private final BlockState secondary;
        private final int stripeWidth;
        private final Axis axis;

        public StripePattern(BlockState primary, BlockState secondary, int stripeWidth, Axis axis) {
            this.primary = primary;
            this.secondary = secondary;
            this.stripeWidth = stripeWidth;
            this.axis = axis;
        }

        /* <<<<<<<<<<<<<<  ✨ Windsurf Command ⭐ >>>>>>>>>>>>>>>> */

        /**
         * Gets the block state for a specific position within the pattern.
         * <p>
         * This pattern works by calculating the position's coordinate along the
         * specified axis, relative to the origin point. The coordinate is then
         * divided by the stripe width to determine which block state to return.
         * If the remainder of the division is 0, the primary block state is returned.
         * Otherwise, the secondary block state is returned.
         * <p>
         *
         * @param pos    The target position
         * @param origin The origin point of the pattern
         * @param random Random instance for probabilistic patterns
         * @return The block state to place at the given position
         */
        /* <<<<<<<<<<  c4652452-18b4-4fa0-b482-ce090f7528e0  >>>>>>>>>>> */
        @Override
        public BlockState getBlockState(BlockPos pos, BlockPos origin, Random random) {
            int coord = switch (axis) {
                case X -> pos.getX() - origin.getX();
                case Y -> pos.getY() - origin.getY();
                case Z -> pos.getZ() - origin.getZ();
            };

            return (coord / stripeWidth) % 2 == 0 ? primary : secondary;
        }

        public enum Axis {X, Y, Z}
    }
}