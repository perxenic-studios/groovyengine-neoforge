package io.github.luckymcdev.groovyengine.lens.client.systems.obj;

import net.minecraft.world.phys.Vec2;
import org.joml.Vector3f;

/**
 * A record to keep track of a Vertex.
 *
 * @param position the Position of the Vertex
 * @param normal the Normal of the Vertex
 * @param uv the UV cord of the Vertex
 */
public record Vertex(Vector3f position, Vector3f normal, Vec2 uv) {
}