package io.github.luckymcdev.groovyengine.lens.client.rendering.vertex;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public record CubeVertexData(Vector3f[] bottomVertices, Vector3f[] topVertices, List<Vector3f[]> offsetMap) {

    /**
     * Creates a CubeVertexData object with the given scale applied to the x and y axes.
     *
     * The resulting cube will have its bottom vertices at (-xScale/2, -yScale/2, -xScale/2), (-xScale/2, -yScale/2, xScale/2), (xScale/2, -yScale/2, xScale/2), and (xScale/2, -yScale/2, -xScale/2),
     * and its top vertices at (-xScale/2, yScale/2, -xScale/2), (-xScale/2, yScale/2, xScale/2), (xScale/2, yScale/2, xScale/2), and (xScale/2, yScale/2, -xScale/2).
     * The offset map will be populated with the vertices in the order of bottom left, bottom right, top right, top left.
     *
     * @param scale The scale to apply to the x and y axes
     * @return A CubeVertexData object with the given scale applied to the x and y axes
     */
    public static CubeVertexData makeCubePositions(float scale) {
        return makeCubePositions(scale, scale);
    }

    /**
     * Creates a CubeVertexData object with the given scale applied to the x and y axes.
     * The resulting cube will have its bottom vertices at (-xScale/2, -yScale/2, -xScale/2), (-xScale/2, -yScale/2, xScale/2), (xScale/2, -yScale/2, xScale/2), and (xScale/2, -yScale/2, -xScale/2),
     * and its top vertices at (-xScale/2, yScale/2, -xScale/2), (-xScale/2, yScale/2, xScale/2), (xScale/2, yScale/2, xScale/2), and (xScale/2, yScale/2, -xScale/2).
     * The offset map will be populated with the vertices in the order of bottom left, bottom right, top right, top left.
     *
     * @param xScale The scale to apply to the x-axis
     * @param yScale The scale to apply to the y-axis
     * @return A CubeVertexData object representing the cube with the given scale
     */
    public static CubeVertexData makeCubePositions(float xScale, float yScale) {
        float xOffset = xScale / 2f;
        float yOffset = yScale / 2f;
        return makeCubePositions(-xOffset, xOffset, -yOffset, yOffset);
    }

    /**
     * Create a CubeVertexData object with the given start and end positions in the x and y axes.
     * The resulting cube will have its bottom vertices at (xStart, yStart, xStart), (xStart, yStart, xEnd), (xEnd, yStart, xEnd), and (xEnd, yStart, xStart),
     * and its top vertices at (xStart, yEnd, xStart), (xStart, yEnd, xEnd), (xEnd, yEnd, xEnd), and (xEnd, yEnd, xStart).
     * The offset map will be populated with the vertices in the order of bottom left, bottom right, top right, top left.
     *
     * @param xStart The starting x position of the cube.
     * @param xEnd The ending x position of the cube.
     * @param yStart The starting y position of the cube.
     * @param yEnd The ending y position of the cube.
     * @return A CubeVertexData object representing the cube with the given positions.
     */
    public static CubeVertexData makeCubePositions(float xStart, float xEnd, float yStart, float yEnd) {
        Vector3f[] bottomVertices = new Vector3f[]{new Vector3f(xStart, yStart, xStart), new Vector3f(xStart, yStart, xEnd), new Vector3f(xEnd, yStart, xEnd), new Vector3f(xEnd, yStart, xStart)};
        Vector3f[] topVertices = new Vector3f[]{new Vector3f(xStart, yEnd, xStart), new Vector3f(xStart, yEnd, xEnd), new Vector3f(xEnd, yEnd, xEnd), new Vector3f(xEnd, yEnd, xStart)};
        List<Vector3f[]> offsetMap = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int index = (i * 3 + 3) % 4; //this weird and specific numbering is to tie the vertices to horizontal directions
            offsetMap.add(new Vector3f[]{bottomVertices[(index) % 4], bottomVertices[(index + 1) % 4], topVertices[(index + 1) % 4], topVertices[(index) % 4]});
        }
        return new CubeVertexData(bottomVertices, topVertices, offsetMap);
    }

    /**
     * Applies a vertex wobble to the given offsets.
     * The wobble is calculated based on the game time and the given sine offset and strength.
     * The sine offset is increased by 0.25f for each iteration.
     * The wobble is applied in the x and y axes.
     *
     * @param offsets The offsets to apply the wobble to
     * @param sineOffset The sine offset to use for the wobble calculation
     * @param strength The strength of the wobble
     */
    public static void applyVertexWobble(Vector3f[] offsets, float sineOffset, float strength) {
        float offset = sineOffset;
        for (Vector3f vector3f : offsets) {
            double time = ((Minecraft.getInstance().level.getGameTime() / 40.0F) % Math.PI * 2);
            float angle = (float) (time + (offset * Math.PI * 2));
            float sin = Mth.sin(angle) * strength;
            float cos = Mth.cos(angle) * strength;
            vector3f.add(sin, cos, 0);
            offset += 0.25f;
        }
    }

    /**
     * Returns the vertices of this cube which are aligned with the given direction.
     * If the direction is UP, the top vertices are returned.
     * If the direction is DOWN, the bottom vertices are returned.
     * Otherwise, the vertices are returned based on the 2D data value of the direction.
     *
     * @param direction The direction to get the vertices for
     * @return The vertices aligned with the given direction
     */
    public Vector3f[] getVerticesByDirection(Direction direction) {
        if (direction.equals(Direction.UP)) {
            return topVertices;
        }
        if (direction.equals(Direction.DOWN)) {
            return bottomVertices;
        }
        return getVerticesByIndex(direction.get2DDataValue());
    }

    /**
     * Returns the vertices of this cube which are aligned with the given index.
     * The index is used to retrieve the vertices from the offset map.
     * If the index is out of range, an empty list is returned.
     *
     * @param index The index to retrieve the vertices for
     * @return The vertices aligned with the given index
     */
    public Vector3f[] getVerticesByIndex(int index) {
        return offsetMap.get(index);
    }

    /**
     * Applies a vertex wobble to this cube.
     * The wobble is calculated based on the game time and the given sine offset and strength.
     * The sine offset is increased by 0.25f for each iteration.
     * The wobble is applied in the x and y axes.
     *
     * @param sineOffset The sine offset to use for the wobble calculation
     * @param strength The strength of the wobble
     * @return This cube vertex data with the wobble applied
     */
    public CubeVertexData applyWobble(float sineOffset, float strength) {
        return applyWobble(sineOffset, sineOffset, strength);
    }

    /**
     * Applies a vertex wobble to this cube.
     * The wobble is calculated based on the game time and the given sine offset and strength.
     * The sine offset is increased by 0.25f for each iteration.
     * The wobble is applied in the x and y axes.
     *
     * @param bottomSineOffset The sine offset to use for the wobble calculation on the bottom vertices
     * @param topSineOffset The sine offset to use for the wobble calculation on the top vertices
     * @param strength The strength of the wobble
     * @return This cube vertex data with the wobble applied
     */
    public CubeVertexData applyWobble(float bottomSineOffset, float topSineOffset, float strength) {
        return applyWobble(bottomVertices, bottomSineOffset, strength).applyWobble(topVertices, topSineOffset, strength);
    }

    /**
     * Applies a vertex wobble to the given offsets.
     * The wobble is calculated based on the game time and the given sine offset and strength.
     * The sine offset is increased by 0.25f for each iteration.
     * The wobble is applied in the x and y axes.
     *
     * @param offsets The offsets to apply the wobble to
     * @param sineOffset The sine offset to use for the wobble calculation
     * @param strength The strength of the wobble
     * @return This cube vertex data with the wobble applied
     */
    public CubeVertexData applyWobble(Vector3f[] offsets, float sineOffset, float strength) {
        applyVertexWobble(offsets, sineOffset, strength);
        return this;
    }

    /**
     * Scales this cube vertex data by the given amount in all three axes.
     *
     * @param scale The amount to scale the cube vertex data by
     * @return This cube vertex data with the scale applied
     */
    public CubeVertexData scale(float scale) {
        return scale(scale, scale);
    }

    /**
     * Scales this cube vertex data by the given width and height in the x and y axes, and by the given width in the z axis.
     *
     * @param width The width to scale the cube vertex data by
     * @param height The height to scale the cube vertex data by
     * @return This cube vertex data with the scale applied
     */
    public CubeVertexData scale(float width, float height) {
        return scale(width, height, width);
    }

    /**
     * Scales this cube vertex data by the given amounts in the x, y, and z axes.
     * This method multiplies each vertex by the given scale factors.
     *
     * @param x The scale factor for the x axis
     * @param y The scale factor for the y axis
     * @param z The scale factor for the z axis
     * @return This cube vertex data with the scale applied
     */
    public CubeVertexData scale(float x, float y, float z) {
        for (int i = 0; i < bottomVertices.length; i++) {
            bottomVertices[i].mul(x, y, z);
            topVertices[i].mul(x, y, z);
        }
        return this;
    }

    /**
     * Offsets this cube vertex data by the given amounts in the x, y, and z axes.
     * This method adds the given offset amounts to each vertex.
     *
     * @param x The offset amount for the x axis
     * @param y The offset amount for the y axis
     * @param z The offset amount for the z axis
     * @return This cube vertex data with the offset applied
     */
    public CubeVertexData offset(float x, float y, float z) {
        for (int i = 0; i < bottomVertices.length; i++) {
            bottomVertices[i].add(x, y, z);
            topVertices[i].add(x, y, z);
        }
        return this;
    }
}