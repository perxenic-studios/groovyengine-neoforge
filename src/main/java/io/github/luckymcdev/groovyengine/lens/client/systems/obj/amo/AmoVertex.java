package io.github.luckymcdev.groovyengine.lens.client.systems.obj.amo;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.Vertex;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4i;

/**
 * Extended vertex for animated models with joint influences.
 */
public record AmoVertex(
        Vector3f position,
        Vector3f normal,
        Vec2 uv,
        Vector4i joints,    // Up to 4 joint indices
        Vector4f weights    // Corresponding weights (should sum to 1.0)
) {
    /**
     * Convert to regular Vertex for non-animated rendering.
     */
    public Vertex toVertex() {
        return new Vertex(position, normal, uv);
    }

    /**
     * Get the effective position after applying joint transformations.
     */
    public Vector3f getAnimatedPosition(Joint[] jointArray) {
        Vector3f result = new Vector3f();

        // Apply weighted joint transformations
        // Convert from 1-based to 0-based indices
        if (weights.x > 0 && (joints.x - 1) >= 0 && (joints.x - 1) < jointArray.length) {
            Vector3f transformed = jointArray[joints.x - 1].transformPoint(position);
            result.add(transformed.mul(weights.x));
        }
        if (weights.y > 0 && (joints.y - 1) >= 0 && (joints.y - 1) < jointArray.length) {
            Vector3f transformed = jointArray[joints.y - 1].transformPoint(position);
            result.add(transformed.mul(weights.y));
        }
        if (weights.z > 0 && (joints.z - 1) >= 0 && (joints.z - 1) < jointArray.length) {
            Vector3f transformed = jointArray[joints.z - 1].transformPoint(position);
            result.add(transformed.mul(weights.z));
        }
        if (weights.w > 0 && (joints.w - 1) >= 0 && (joints.w - 1) < jointArray.length) {
            Vector3f transformed = jointArray[joints.w - 1].transformPoint(position);
            result.add(transformed.mul(weights.w));
        }

        // If no joints were applied, use original position
        if (result.lengthSquared() == 0) {
            result.set(position);
        }

        return result;
    }
}